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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

public class DiffractionViewerPerspective implements IPerspectiveFactory {

	static final String ID = "uk.ac.diamond.scisoft.diffractionviewerperspective";
	@Override
	public void createInitialLayout(IPageLayout layout) {
			
		IFolderLayout toolsLayout = layout.createFolder("toolsFolder", IPageLayout.RIGHT, 0.3f, layout.getEditorArea());
		toolsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView");

		IFolderLayout plotsLayout = layout.createFolder("plotFolder", IPageLayout.RIGHT, 0.4f, "toolsFolder");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.plotViewDP");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView:Dataset Plot");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView");
		
		IFolderLayout explorerLayout = layout.createFolder("explorerFolder", IPageLayout.TOP, 0.5f, "toolsFolder");
		explorerLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView");
		explorerLayout.addPlaceholder("org.dawb.workbench.views.imageMonitorView");
		
		layout.addView("fable.imageviewer.views.HeaderView", IPageLayout.TOP, 0.8f, layout.getEditorArea());
		
		IFolderLayout navigatorLayout = layout.createFolder("navigators", IPageLayout.TOP, 0.6f, "fable.imageviewer.views.HeaderView");
		navigatorLayout.addView("uk.ac.diamond.sda.navigator.views.FileView");
		navigatorLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
		
		
		if (layout.getViewLayout("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView") != null)
			layout.getViewLayout("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView").setCloseable(false);
		
	}

}
