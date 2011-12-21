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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.actions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.util.FileComparator;
import uk.ac.diamond.scisoft.analysis.rcp.util.FileCompareMode;
import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;

/**
 *
 */
public class MonitorDirectoryAction extends AbstractHandler {

	boolean monitorActive = false;
	
	private static final Logger logger = LoggerFactory.getLogger(MonitorDirectoryAction.class);
	private static final int TIMEOUT = 5000; // in ms
	private HashMap<String, Boolean> compare = new HashMap<String, Boolean>();
	
	private void fillUpCompareHash(List<String> list) {
		Iterator<String> iter = list.iterator();
		while (iter.hasNext()) {
			compare.put(iter.next(), true);
		}
	}
	
	public MonitorDirectoryAction() {
		super();
	}
	
	public boolean isMonitorActive() {
		return monitorActive;
	}
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		final ImageExplorerView view = (ImageExplorerView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(ImageExplorerView.ID);
		if (view != null) {
			Command command = event.getCommand();
			boolean oldValue = HandlerUtil.toggleCommandState(command);
			if (!oldValue) {
				final String directory = view.getDirPath();
				if (directory != null &&
				    directory.length() > 0) {
					monitorActive = true;
					view.setMonitorActive(monitorActive);
					Job scanDir = new Job("Scan directory") {

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							if (monitorActive) {
								List<String> alreadyLoaded = view.getLoadedFiles();
								// check if there are any files in this view
								// if not do nothing and wait
								if (alreadyLoaded != null &&
									alreadyLoaded.size() > 0) {
									compare.clear();
									fillUpCompareHash(alreadyLoaded);
									
									java.io.File file = new java.io.File(directory);
									java.io.File[] files = file.listFiles();
									
									if (files.length > 0) {
										ArrayList<java.io.File> actualFiles = 
											ImageExplorerDirectoryChooseAction.filterImages(files,view.getExtensionsFilter());
										Collections.sort(actualFiles,new FileComparator(FileCompareMode.datetime));
										Iterator<java.io.File> iter = actualFiles.iterator();
										while (iter.hasNext()) {
											String filename = iter.next().getAbsolutePath();
											if (compare.get(filename) == null) {
												GuiBean bean = new GuiBean();
												bean.put(GuiParameters.PLOTID,java.util.UUID.randomUUID());
												bean.put(GuiParameters.FILENAME, filename);
												bean.put(GuiParameters.PLOTMODE,GuiPlotMode.IMGEXPL);
												GuiUpdate gu = new GuiUpdate(view.getPartName(), bean);
												view.update(this, gu);
											}
										}
									}
								}
								this.schedule(TIMEOUT);
							}
							return Status.OK_STATUS;
						}
						
					};
					scanDir.setUser(false);
					scanDir.setPriority(Job.DECORATE);
					scanDir.schedule(TIMEOUT);
				} else {
					// set state back
					command.getState("org.eclipse.ui.commands.toggleState").setValue(Boolean.FALSE);
					logger.info("No active directory selected reverse monitor state");
				}
			} else { 
				monitorActive = false;
				view.setMonitorActive(monitorActive);				
			}
		} else {
			logger.info("Couldn't find view to load for");
		}

		return Boolean.FALSE;
	}

}
