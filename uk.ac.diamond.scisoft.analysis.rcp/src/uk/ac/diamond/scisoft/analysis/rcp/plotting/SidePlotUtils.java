/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;


import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView;

public class SidePlotUtils {
	
	private static final Logger logger = LoggerFactory.getLogger(SidePlotUtils.class);
	/**
	 * Attempts to find the side plot view and logs errors if not
	 * rather than throwing exceptions.
	 * @param page
	 * @param plotViewID
	 * @return SidePlotView
	 */
	public static SidePlotView getSidePlotView(final IWorkbenchPage page,
			                                   final String         plotViewID) {
		
		
		//if (PlatformUI.getWorkbench().isStarting()) throw new IllegalStateException("Workbench is starting!");
		
		SidePlotView sidePlotView = null;
		// necessary for multiple SPVs
		try {
			// Cannot use the findView(...) because of some side plot initiation madness.
			//sidePlotView = (SidePlotView) page.findView("uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView");

			//if (sidePlotView==null) {
				
				sidePlotView = (SidePlotView) page.showView(SidePlotView.ID,
						                                    plotViewID, IWorkbenchPage.VIEW_CREATE);
			//}
				
			
		} catch (PartInitException e) {
			logger.warn("Could not find side plot ",e);
		}
		if (sidePlotView == null) {
			logger.error("Cannot find side plot");
			throw new IllegalStateException("Cannot find side plot");
		}

		return sidePlotView;
	}
	
	
	public static void bringToTop(final IWorkbenchPage activePage, final IWorkbenchPart part) {
		
		if (part.getSite().getShell().isDisposed()) return;
		
		// activating the view stops the rogue toolbars appearing
		// these could also be avoided by moving the toolbars to
		// eclipse configured things.
		try {
			PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					activePage.bringToTop(part);
				}
			});

		} catch (Exception ne) {
			logger.error("Cannot acivate plot "+part.getTitle(), ne);
		} 
		
	}

}
