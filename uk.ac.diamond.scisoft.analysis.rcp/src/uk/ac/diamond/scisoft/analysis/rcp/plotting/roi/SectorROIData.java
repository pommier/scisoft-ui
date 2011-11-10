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
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.roi.ROIProfile;
import uk.ac.diamond.scisoft.analysis.roi.SectorROI;

/**
 * Class to aggregate information associated with a ROI
 */
public class SectorROIData extends ROIData {
	/**
	 * Construct new object from given roi and data
	 * @param sroi
	 * @param data 
	 */
	public SectorROIData(SectorROI sroi, AbstractDataset data) {
		this(sroi, data, null, 1.);
	}

	public SectorROIData(SectorROI sroi, AbstractDataset data, AbstractDataset mask) {
		this(sroi, data, mask, 1.);
	}

	/**
	 * Construct new object from given roi and data
	 * @param sroi
	 * @param data
	 * @param subFactor
	 */
	public SectorROIData(SectorROI sroi, AbstractDataset data, double subFactor) {
		this(sroi, data, null, subFactor);
	}

	public SectorROIData(SectorROI sroi, AbstractDataset data, AbstractDataset mask, double subFactor) {
		super();setROI(sroi.copy());
		roi.downsample(subFactor);
		profileData = ROIProfile.sector(data, mask, (SectorROI) roi);
		if (profileData != null && profileData[0].getShape()[0] > 1 && profileData[1].getShape()[0] > 1) {
			AbstractDataset pdata;
			for (int i = 0; i < 4; i++) {
				pdata = profileData[i];
				if (pdata instanceof AbstractCompoundDataset) // use first element
					profileData[i] = ((AbstractCompoundDataset) pdata).getElements(0);
			}
			Number sum = (Number) profileData[0].sum();
			profileSum = sum.doubleValue();

			xAxes = new AxisValues[] { null, null, null, null };
			xAxes[0] = new AxisValues();
			xAxes[1] = new AxisValues();
			xAxes[2] = new AxisValues();
			xAxes[3] = new AxisValues();

			AbstractDataset axis;
			axis = DatasetUtils.linSpace(sroi.getRadius(0), sroi.getRadius(1), profileData[0].getSize(), AbstractDataset.FLOAT64);//profileData[0].getIndices().squeeze();
			xAxes[0].setValues(axis);

			if (sroi.getSymmetry() != SectorROI.FULL)
				axis = DatasetUtils.linSpace(sroi.getAngleDegrees(0), sroi.getAngleDegrees(1), profileData[1].getSize(), AbstractDataset.FLOAT64);
			else
				axis = DatasetUtils.linSpace(sroi.getAngleDegrees(0), sroi.getAngleDegrees(0) + 360., profileData[1].getSize(), AbstractDataset.FLOAT64);
			xAxes[1].setValues(axis);

			if (sroi.hasSeparateRegions()) {
				axis = DatasetUtils.linSpace(sroi.getRadius(0), sroi.getRadius(1), profileData[2].getSize(), AbstractDataset.FLOAT64);//profileData[0].getIndices().squeeze();
				xAxes[2].setValues(axis);
				axis = DatasetUtils.linSpace(sroi.getAngleDegrees(0), sroi.getAngleDegrees(1), profileData[3].getSize(), AbstractDataset.FLOAT64);
				xAxes[3].setValues(axis);
			}
		} else {
			setPlot(false);
		}
	}

	/**
	 * Aggregate a copy of ROI data to this object
	 * @param sroi
	 * @param profileData
	 * @param axes
	 * @param profileSum
	 */
	public SectorROIData(SectorROI sroi, AbstractDataset[] profileData, AxisValues[] axes, double profileSum) {
		super();
		setROI(sroi.copy());
		this.profileData = profileData.clone();
		for (int i = 0; i < profileData.length; i++) {
			this.profileData[i] = profileData[i].clone();
		}
		xAxes = axes.clone();
		for (int i = 0; i < axes.length; i++) {
			xAxes[i] = axes[i].clone();
		}
		this.profileSum = profileSum;
		setPlot(false);
	}

	/**
	 * @return linear region of interest
	 */
	@Override
	public SectorROI getROI() {
		return (SectorROI) roi;
	}
}
