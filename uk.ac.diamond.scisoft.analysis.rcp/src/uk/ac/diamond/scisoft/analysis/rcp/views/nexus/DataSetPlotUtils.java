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

package uk.ac.diamond.scisoft.analysis.rcp.views.nexus;

import org.eclipse.ui.IEditorPart;

import uk.ac.diamond.scisoft.analysis.rcp.results.navigator.DataSetComparisionDialog;
import uk.ac.gda.common.rcp.util.EclipseUtils;

public class DataSetPlotUtils {

	/**
	 * Trys to find an active DataSetPlotView 
	 * @return DataSetPlotView
	 */
	public static DataSetPlotView getActiveView() {
		
		final DataSetComparisionDialog dialog = DataSetComparisionDialog.getActiveDialog();
		DataSetPlotView sets = dialog!=null ? dialog.getDataSetPlotView() : null;
		
		if (sets==null) {
		    sets = (DataSetPlotView)EclipseUtils.getActivePage().findView(DataSetPlotView.ID);
		}
		if (sets == null) {
			IEditorPart editor = EclipseUtils.getActivePage().getActiveEditor();
			if (editor!=null) {
				if (editor instanceof IDataSetPlotViewProvider) {
					sets = ((IDataSetPlotViewProvider)editor).getDataSetPlotView();
				}
				
			}
		}
		
		return sets; // Might still be null
	}
}
