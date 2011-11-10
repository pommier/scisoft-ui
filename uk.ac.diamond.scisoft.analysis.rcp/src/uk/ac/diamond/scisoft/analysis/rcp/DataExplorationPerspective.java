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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView;
import uk.ac.diamond.scisoft.analysis.rcp.views.PlotView;
import uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView;

public class DataExplorationPerspective implements IPerspectiveFactory {

	public static final String ID = "uk.ac.diamond.scisoft.dataexplorationperspective";
	@Override
	public void createInitialLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();

		String explorer = "org.eclipse.ui.navigator.ProjectExplorer";
		layout.addView(explorer, IPageLayout.LEFT, 0.15f, editorArea);
		if (layout.getViewLayout(explorer) != null)
			layout.getViewLayout(explorer).setCloseable(false);

		String plot = PlotView.ID + "DP";
		layout.addView(plot, IPageLayout.RIGHT, 0.25f, editorArea);
		if (layout.getViewLayout(plot) != null)
			layout.getViewLayout(plot).setCloseable(false);

		String sidePlot = SidePlotView.ID + ":Dataset Plot";
		layout.addView(sidePlot, IPageLayout.RIGHT, 0.60f, plot);
		if (layout.getViewLayout(sidePlot) != null)
			layout.getViewLayout(sidePlot).setCloseable(false);

		String inspector = DatasetInspectorView.ID;
		layout.addStandaloneView(inspector, false, IPageLayout.BOTTOM, 0.60f, editorArea);
		if (layout.getViewLayout(inspector) != null)
			layout.getViewLayout(inspector).setCloseable(false);

	}

}
