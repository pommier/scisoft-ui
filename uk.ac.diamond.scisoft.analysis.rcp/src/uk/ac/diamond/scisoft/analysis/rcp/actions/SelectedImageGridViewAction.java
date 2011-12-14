/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.actions;

import java.io.File;
import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.SDAPlotter;

public class SelectedImageGridViewAction extends AbstractHandler implements IObjectActionDelegate {

	private static Logger logger = LoggerFactory.getLogger(SelectedImageGridViewAction.class);
	
	@Override
	public void run(IAction action) {
		doAction();		
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return doAction();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		// TODO Auto-generated method stub
		
	}
	
	private Object doAction() {
		
		final IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
		final IStructuredSelection sel = (IStructuredSelection)page.getSelection();
		
		Job job = new Job("Updating image Explorer View") {
			
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				if (sel != null) {
					
					Object[] selObjects = sel.toArray();
					ArrayList<String> files = new ArrayList<String>();
					for (Object obj : selObjects) {
						if(obj instanceof IFile) {
							IFile file = (IFile)obj;
							String path = file.getLocation().toString();
							files.add(path);
						} else if (obj instanceof File) {
							File file = (File)obj;
							String path = file.getAbsolutePath();
							files.add(path);
						}
					}
					
					try {
						SDAPlotter.setupNewImageGrid("ImageExplorer View", files.size());
					} catch (Exception e1) {
						logger.debug("Problem with SDAPlotter.setupNewImageGrid",e1);
						return Status.CANCEL_STATUS;
					}
					for (String path : files) {						
						try {
							SDAPlotter.plotImageToGrid("ImageExplorer View", path);
						} catch (Exception e) {
							logger.debug("Problem with SDAPlotter.plotImageToGrid()",e);
							return Status.CANCEL_STATUS;
						}
					}
				} else
					return Status.CANCEL_STATUS;
				return Status.OK_STATUS;
			}
		};
		
		job.schedule();
		
		return Boolean.TRUE;
	}

}
