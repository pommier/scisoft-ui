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

package uk.ac.diamond.scisoft.analysis.rcp.editors;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartReference;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotWindow;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.ISidePlotView;
import uk.ac.gda.common.rcp.util.EclipseUtils;
import uk.ac.gda.ui.event.PartAdapter2;

public class EditorUtils {

	public static void addSidePlotActivator(final IWorkbenchPart part,
			                                final PlotWindow     window,
			                                final String         partName) {


		// Add part listener in asyncExec so that does not get fired
		// while part being created. 
		part.getSite().getShell().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				part.getSite().getPage().addPartListener((IPartListener2)new PartAdapter2() {

					@Override
					public void partActivated(IWorkbenchPartReference partRef) {

						// activating the view stops the rogue toolbars appearing
						// these could also be avoided by moving the toolbars to
						// eclipse configured things.
						if (partRef.getPartName().equals(partName)) {
							try {
								// Select the respective side plots
								final IWorkbenchPage activePage = EclipseUtils.getActivePage();
								if (activePage!=null) {
									final ISidePlotView side = window.getPlotUI().getSidePlotView();
									if (side instanceof IWorkbenchPart) {
										part.getSite().getShell().getDisplay().asyncExec(new Runnable() {
											@Override
											public void run() {
												IWorkbenchPart part = (IWorkbenchPart)side;
												activePage.bringToTop(part);
											}
										});
									}
								}
							} catch (Exception ignored) {
								// We do our best to activate and ignore exceptions.
							} 
						}

					}
				});
			}
		});

	}

}
