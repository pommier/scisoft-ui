/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
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

public class ImageGridViewPathSetterAction extends AbstractHandler implements IObjectActionDelegate {

	private static Logger logger = LoggerFactory.getLogger(ImageGridViewPathSetterAction.class);
	
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
					if(selObjects[0] instanceof IFolder) {
						try {
							IFolder folder = (IFolder)selObjects[0];
							String path = folder.getLocation().toString();
							
							SDAPlotter.scanForImages("ImageExplorer View", path);
						} catch (Exception e) {
							logger.debug("Problem with SDAPlotter.setupNewImageGrid",e);
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
