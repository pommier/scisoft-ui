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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class DiffractionViewerPerspective implements IPerspectiveFactory {

	static final String ID = "uk.ac.diamond.scisoft.diffractionviewerperspective";
	@Override
	public void createInitialLayout(IPageLayout layout) {
			
		IFolderLayout toolsLayout = layout.createFolder("toolsFolder", IPageLayout.RIGHT, 0.2f, layout.getEditorArea());
		toolsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView:Dataset Plot");
		toolsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView:Dataset Plot");
		toolsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView");

		IFolderLayout plotsLayout = layout.createFolder("plotFolder", IPageLayout.RIGHT, 0.4f, "toolsFolder");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.plotViewDP");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView:Dataset Plot");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView");
		
		IFolderLayout explorerLayout = layout.createFolder("explorerFolder", IPageLayout.TOP, 0.5f, "toolsFolder");
		explorerLayout.addView("org.dawb.workbench.views.imageMonitorView");
		explorerLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView");
		
		layout.addView("uk.ac.diamond.sda.navigator.views.FileView", IPageLayout.TOP, 0.8f, layout.getEditorArea());
		
	}

}
