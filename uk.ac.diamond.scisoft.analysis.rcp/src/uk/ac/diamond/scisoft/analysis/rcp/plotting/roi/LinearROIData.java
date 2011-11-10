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

import uk.ac.diamond.scisoft.analysis.dataset.AbstractCompoundDataset;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.roi.LinearROI;
import uk.ac.diamond.scisoft.analysis.roi.ROIProfile;

/**
 * Class to aggregate information associated with a ROI
 */
public class LinearROIData extends ROIData {
	/**
	 * Construct new object from given roi and data
	 * @param roi
	 * @param data
	 * @param step
	 */
	public LinearROIData(LinearROI roi, AbstractDataset data, double step) {
		super();

		setROI(roi.copy());
		profileData = ROIProfile.line(data, roi, step);
		if (profileData != null && profileData[0].getShape()[0] > 1) {
			AbstractDataset pdata;
			for (int i = 0; i < 2; i++) {
				pdata = profileData[i];
				if (pdata instanceof AbstractCompoundDataset) // use first element
					profileData[i] = ((AbstractCompoundDataset) pdata).getElements(0);
			}
			Number sum = (Number) profileData[0].sum();
			profileSum = sum.doubleValue() * step;

			xAxes = new AxisValues[] { null, null };
			xAxes[0] = new AxisValues();
			xAxes[1] = new AxisValues();

			AbstractDataset axis;
			axis = profileData[0].getIndices().squeeze();
			axis.imultiply(step);
			xAxes[0].setValues(axis);

			if (roi.isCrossHair()) {
				axis = profileData[1].getIndices().squeeze();
				axis.imultiply(step);
				xAxes[1].setValues(axis);
			}
		} else {
			roi.setPlot(false);
		}
	}

	/**
	 * Aggregate a copy of ROI data to this object
	 * @param roi
	 * @param profileData
	 * @param axes
	 * @param profileSum
	 */
	public LinearROIData(LinearROI roi, AbstractDataset[] profileData, AxisValues[] axes, double profileSum) {
		super();
		setROI(roi.copy());
		this.profileData = profileData.clone();
		for (int i = 0; i < profileData.length; i++) {
			this.profileData[i] = profileData[i].clone();
		}
		xAxes = axes.clone();
		for (int i = 0; i < axes.length; i++) {
			xAxes[i] = axes[i].clone();
		}
		this.profileSum = profileSum;
		roi.setPlot(false);
	}

	/**
	 * @return linear region of interest
	 */
	@Override
	public LinearROI getROI() {
		return (LinearROI) roi;
	}
}
