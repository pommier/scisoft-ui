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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import org.apache.commons.beanutils.BeanUtils;

/**
 * This class is an auto-generated bean to use with
 * diamond widgets (saving and remembering state).
 */
public class FitData {
	
	private int peakSelection,numberOfPeaks,smoothing,algType;
	private double accuracy;

	@Override
	public String toString() {
		try {
			return BeanUtils.describe(this).toString();
		} catch (Exception e) {
			return e.getMessage();
		}
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(accuracy);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + algType;
		result = prime * result + numberOfPeaks;
		result = prime * result + peakSelection;
		result = prime * result + smoothing;
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FitData other = (FitData) obj;
		if (Double.doubleToLongBits(accuracy) != Double.doubleToLongBits(other.accuracy))
			return false;
		if (algType != other.algType)
			return false;
		if (numberOfPeaks != other.numberOfPeaks)
			return false;
		if (peakSelection != other.peakSelection)
			return false;
		if (smoothing != other.smoothing)
			return false;
		return true;
	}
	public int getPeakSelection() {
		return peakSelection;
	}
	public void setPeakSelection(int peakSelection) {
		this.peakSelection = peakSelection;
	}
	public int getNumberOfPeaks() {
		return numberOfPeaks;
	}
	public void setNumberOfPeaks(int numberOfPeaks) {
		this.numberOfPeaks = numberOfPeaks;
	}
	public int getSmoothing() {
		return smoothing;
	}
	public void setSmoothing(int smoothing) {
		this.smoothing = smoothing;
	}
	public int getAlgType() {
		return algType;
	}
	public void setAlgType(int algType) {
		this.algType = algType;
	}
	public double getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(double accuracy) {
		this.accuracy = accuracy;
	}

}
