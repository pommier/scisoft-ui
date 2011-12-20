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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.fitting;

import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.fitting.functions.APeak;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.ROITableViewer;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;

public final class FittedPeakTableViewer extends ROITableViewer {
	
	
	
	public FittedPeakTableViewer(Composite parent, SelectionListener slistener,
			ICellEditorListener clistener, ISelectionChangedListener scListener) {
		super(parent, slistener, clistener);
		this.addLeftClickListener(scListener);
	}
	
	@Override
	public
	String content(Object element, int columnIndex) {
		String msg = null;

		FittedPeakData peakData = (FittedPeakData) element;
		if (peakData != null) {
			APeak apeak = peakData.getFittedPeak();
			switch (columnIndex) {
			case 1:
				msg = formatCellToCorrectDP(apeak.getPosition());
				break;
			case 2 :
				msg = formatCellToCorrectDP(apeak.getFWHM());
				break;
			case 3:
				msg = formatCellToCorrectDP(apeak.getArea());
				break;
			case 4:
				msg = apeak.getClass().getSimpleName();
				break;
			}
		}
		return msg;
	}

	@Override
	public
	String[] getTitles() {
		return new String[] { "Visible", "Peak Position", "FWHM", "Area", "Name"};
	}

	@Override
	public
	int[] getWidths() {
		return new int[] { 60, 100, 100, 100, 150 };
	}

	@Override
	public
	String[] getTipTexts() {
		return new String[] { "Visible", "Peak position", "Full width at half maximum", "Area under peak",
				"Name of distribution"};
	}
	
	private String formatCellToCorrectDP(double tableValue){
		int decimalPlaces = AnalysisRCPActivator.getDefault().getPreferenceStore().getInt(PreferenceConstants.FITTING_1D_DECIMAL_PLACES);
		return String.format("%."+decimalPlaces+"f", tableValue);
		
	}
	
}
