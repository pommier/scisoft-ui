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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

import java.awt.Color;

import org.eclipse.swt.graphics.RGB;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;

/**
 * Base class to contain bare essentials of region of interest data
 */
public class ROIData implements IRowData {
	protected ROIBase roi;
	protected AbstractDataset[] profileData;
	protected double profileSum;
	protected RGB plotColourRGB;
	protected AxisValues[] xAxes;

	/**
	 * @param index
	 * @param xAxis The xAxis to set.
	 */
	public void setXAxis(int index, AxisValues xAxis) {
		this.xAxes[index] = xAxis;
	}

	/**
	 * @return Returns the xAxes.
	 */
	public AxisValues[] getXAxes() {
		return xAxes;
	}

	/**
	 * @param index
	 * @return Returns the xAxis.
	 */
	public AxisValues getXAxis(int index) {
		return xAxes[index];
	}

	/**
	 * @return plot colour
	 */
	@Override
	public RGB getPlotColourRGB() {
		return plotColourRGB;
	}

	/**
	 * @return plot colour
	 */
	public Color getPlotColour() {
		return new Color(plotColourRGB.red, plotColourRGB.green, plotColourRGB.blue);
	}

	/**
	 * @param rgb
	 */
	public void setPlotColourRGB(RGB rgb) {
		plotColourRGB = rgb;
	}

	/**
	 * @param index
	 * @param profileData The profileData to set.
	 */
	public void setProfileData(int index, AbstractDataset profileData) {
		this.profileData[index] = profileData;
	}

	/**
	 * @param index
	 * @return Returns the profileData.
	 */
	public AbstractDataset getProfileData(int index) {
		return profileData[index];
	}

	/**
	 * @return Returns the profileData.
	 */
	public AbstractDataset[] getProfileData() {
		return profileData;
	}

	/**
	 * @param profileSum The profileSum to set.
	 */
	public void setProfileSum(double profileSum) {
		this.profileSum = profileSum;
	}

	/**
	 * @return Returns the profileSum.
	 */
	public double getProfileSum() {
		return profileSum;
	}

	/**
	 * @param require set true if plot required 
	 */
	@Override
	public void setPlot(boolean require) {
		roi.setPlot(require);
	}

	/**
	 * @return true if plot is enabled
	 */
	@Override
	public boolean isPlot() {
		return roi.isPlot();
	}


	/**
	 * @param roi
	 */
	public void setROI(ROIBase roi) {
		this.roi = roi;
	}

	/**
	 * @return region of interest
	 */
	public ROIBase getROI() {
		return roi;
	}
}
