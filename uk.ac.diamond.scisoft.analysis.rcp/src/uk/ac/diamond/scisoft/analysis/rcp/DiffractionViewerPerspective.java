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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.IFolderLayout;

import uk.ac.diamond.sda.meta.views.MetadataPageView;

public class DiffractionViewerPerspective implements IPerspectiveFactory {

	static final String ID = "uk.ac.diamond.scisoft.diffractionviewerperspective";
	@Override
	public void createInitialLayout(IPageLayout layout) {
			
		IFolderLayout toolsLayout = layout.createFolder("toolsFolder", IPageLayout.RIGHT, 0.3f, layout.getEditorArea());
		toolsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView");

		IFolderLayout plotsLayout = layout.createFolder("plotFolder", IPageLayout.RIGHT, 0.4f, "toolsFolder");
		plotsLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.plotViewDP");
		
		IFolderLayout sidefolder = layout.createFolder("sidefolder", IPageLayout.TOP, 0.8f, "toolsFolder");
		sidefolder.addView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView:Dataset Plot");
		sidefolder.addView("uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView:Dataset Plot");
		
		IFolderLayout explorerLayout = layout.createFolder("explorerFolder", IPageLayout.TOP, 0.5f, "sidefolder");
		explorerLayout.addView("uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView");
		explorerLayout.addPlaceholder("org.dawb.workbench.views.imageMonitorView");
		
		layout.addView(MetadataPageView.ID, IPageLayout.TOP, 0.8f, layout.getEditorArea());
		
		IFolderLayout navigatorLayout = layout.createFolder("navigators", IPageLayout.TOP, 0.6f, "uk.ac.diamond.sda.meta.MetadataPageView");
		navigatorLayout.addView("uk.ac.diamond.sda.navigator.views.FileView");
		navigatorLayout.addView("org.eclipse.ui.navigator.ProjectExplorer");
		
		
		if (layout.getViewLayout("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView") != null)
			layout.getViewLayout("uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView").setCloseable(false);
		
	}

}
