/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.roi.LinearROI;

/**
 * Class that extends a table viewer for linear regions of interests
 */
public final class LinearROITableViewer extends ROITableViewer {
	/**
	 * @param parent
	 * @param slistener
	 * @param clistener
	 */
	public LinearROITableViewer(Composite parent, SelectionListener slistener,
			ICellEditorListener clistener) {
		super(parent, slistener, clistener);
	}

	@Override
	public String content(Object element, int columnIndex) {
		String msg = null;
		
		LinearROIData cROIData = (LinearROIData) element;
		if (cROIData != null) {
			LinearROI cROI = cROIData.getROI();
			switch (columnIndex) {
			case 1:
				msg = Integer.toString(cROI.getIntPoint()[0]);
				break;
			case 2:
				msg = Integer.toString(cROI.getIntPoint()[1]);
				break;
			case 3:
				msg = String.format("%.2f", cROI.getEndPoint()[0]);
				break;
			case 4:
				msg = String.format("%.2f", cROI.getEndPoint()[1]);
				break;
			case 5:
				msg = String.format("%.2f", cROI.getLength());
				break;
			case 6:
				msg = String.format("%.2f", cROI.getAngleDegrees());
				break;
			case 7:
				if (cROI.isCrossHair())
					msg = "Y";
				else
					msg = "N";
				break;
			case 8:
				msg = String.format("%.2f", cROIData.getProfileSum());
				break;
			}
		}
		return msg;
	}

	@Override
	public String[] getTitles() {
		return new String[] { "Plot", "x_s", "y_s", "x_e", "y_e", "len", "phi", "Cross", "Sum" };
	}

	@Override
	public int[] getWidths() {
		return new int[] { 40, 50, 50, 70, 70, 70, 70, 40, 80 };
	}

	@Override
	public String[] getTipTexts() {
		return new String[] { "Plot", "Start x", "Start y", "End x", "End y", "Length", "Angle", "Cross hair", "Sum of profile" };
	}
}
