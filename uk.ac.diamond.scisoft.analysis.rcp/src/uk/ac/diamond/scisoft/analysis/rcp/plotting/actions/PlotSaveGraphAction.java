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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.actions;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.handlers.HandlerUtil;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.views.PlotView;
//import uk.ac.diamond.scisoft.analysis.rcp.views.plot.AbstractPlotView;

/**
 *
 */
public class PlotSaveGraphAction extends AbstractHandler {

	private String filename;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		String viewID = event.getParameter("uk.ac.diamond.scisoft.analysis.command.sourceView");
//		if (viewID == null)
//			return Boolean.FALSE;
//
//		final AbstractPlotView apv = (AbstractPlotView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().findView(viewID);
//		DataSetPlotter plotter = apv.getPlotter();
//		if (plotter == null)
//			return Boolean.FALSE;
		final PlotView pv = (PlotView)HandlerUtil.getActiveWorkbenchWindow(event).getActivePage().getActivePart();
		DataSetPlotter plotter = pv.getMainPlotter();


		FileDialog dialog = new FileDialog(pv.getSite().getShell(), SWT.SAVE);

		String [] filterExtensions = new String [] {"*.jpg;*.JPG;*.jpeg;*.JPEG;*.png;*.PNG", "*.ps;*.eps","*.svg;*.SVG"};
		if (filename != null) {
			dialog.setFilterPath((new File(filename)).getParent());
		} else {
			String filterPath = "/";
			String platform = SWT.getPlatform();
			if (platform.equals("win32") || platform.equals("wpf")) {
				filterPath = "c:\\";
			}
			dialog.setFilterPath(filterPath);
		}
		dialog.setFilterNames(PlotExportUtil.FILE_TYPES);
		dialog.setFilterExtensions(filterExtensions);
		filename = dialog.open();
		if (filename == null)
			return Boolean.FALSE;

		plotter.saveGraph(filename, PlotExportUtil.FILE_TYPES[dialog.getFilterIndex()]);

		return Boolean.TRUE;
	}

}
