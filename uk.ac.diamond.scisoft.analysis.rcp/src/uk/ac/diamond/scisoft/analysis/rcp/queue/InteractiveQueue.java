/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.queue;

import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A queue for interactive jobs. This is used where jobs can take some time and a conventional queue
 * would grow with more user interactions. The queue drops all but the last job added.
 */
public class InteractiveQueue {
	transient private static final Logger logger = LoggerFactory.getLogger(InteractiveQueue.class);

	private final BlockingDeque<InteractiveJob> jobQueue;
	private Job handlerJob;
	private boolean isDisposed;
	private Control control;

	public InteractiveQueue(Control c) {
		control = c;
		jobQueue = new LinkedBlockingDeque<InteractiveJob>();
		isDisposed = false;
		createHandlerJob();
	}

	/**
	 * Create queue to protect against too many invocations of interactive jobs at too fast a rate
	 */
	private void createHandlerJob() {
		if (handlerJob != null)
			return;
		/**
		 * Tricky to get right thread stuff here. Want to make slice fast to change
		 * but also leave last slice plotted. Change only after testing and running
		 * the regression tests. The use of a queue also minimizes threads (there's only
		 * one) and multiple threads break nexus and are inefficient.
		 */
		handlerJob = new Job("Interactive queue handling") {
			private Cursor tempCursor;

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				try {
					logger.debug("Interactive queue service started");
					while (!isDisposed) {
						if (monitor.isCanceled()) return Status.CANCEL_STATUS;

						final InteractiveJob obj = jobQueue.take(); // Blocks when null job
						if (obj.isNull()) return Status.OK_STATUS; // Any null results in stopping the queue

						final Display display = control != null ? control.getDisplay() : null;
						if (display != null) {
							display.syncExec(new Runnable() {

								@Override
								public void run() {
									if (control != null && !control.isDisposed()) {
										tempCursor = control.getCursor();
										control.setCursor(display.getSystemCursor(SWT.CURSOR_WAIT));
									}
								}
							});
						}
						try {
							obj.run(monitor);
						} catch (Exception e) {
							logger.error("Cannot run job", e);
						}
						if (display != null) {
							display.syncExec(new Runnable() {
								@Override
								public void run() {
									if (control != null && !control.isDisposed())
										control.setCursor(tempCursor);
									tempCursor = null;
								}
							});
						}
					}
					return Status.OK_STATUS;
				} catch (InterruptedException ne) {
					logger.error("Interactive queue exiting...", ne);
					return Status.CANCEL_STATUS;
				} finally {
					logger.debug("Interactive queue service ended");
				}
			}

		};
		handlerJob.setPriority(Job.LONG);
		handlerJob.setUser(false); // Popup form not allowed but job appears in status bar.
		handlerJob.setSystem(true);
		handlerJob.schedule();
	}

	/**
	 * Add an interactive job to queue
	 * @param job
	 */
	public void addJob(InteractiveJob job) {
		if (isDisposed || handlerJob == null) {
			throw new IllegalStateException("handler job gone");
		}
		jobQueue.clear();
		jobQueue.add(job);
	}

	private void interrupt() {
		if (jobQueue != null)
			jobQueue.clear();
		if (handlerJob != null) {
			if (jobQueue != null)
				jobQueue.add(new InteractiveJobAdapter());
			if (handlerJob != null)
				handlerJob.cancel();
			try {
				if (handlerJob != null)
					handlerJob.join();
			} catch (InterruptedException e) {
				logger.error("Cannot join", e);
			}
		}
		handlerJob = null;
	}

	/**
	 * Call to finish queue
	 */
	public void dispose() {
		interrupt();
		isDisposed = true;
	}
}

