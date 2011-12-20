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

package uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions;

/**
 *
 */
public class SpecialExposureFunction extends AbstractMapFunction {

	private char mode;
	private double minThreshold;
	private double maxThreshold;
	private String functionName;
	
	public SpecialExposureFunction(String functionName,
								   double minThreshold,
								   double maxThreshold,
								   char mode)
	{
		this.functionName = functionName;
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThreshold;
		this.mode = mode;
	}
	
	public void setThresholds(double minThreshold,
							  double maxThresHold)
	{
		this.minThreshold = minThreshold;
		this.maxThreshold = maxThresHold;
	}
	
	@Override
	public String getMapFunctionName() {
		// TODO Auto-generated method stub
		return functionName;
	}

	@Override
	public double mapFunction(double input) {
		double returnValue = 0;
		switch (mode) {
			case 'r':
			{
				if (input < minThreshold)
					returnValue = 0;
				else
					returnValue = input;
			}
			break;
			case 'g':
			{
				if (input < minThreshold ||
					input > maxThreshold)
					returnValue = 0;
				else
					returnValue = input;
			}
			break;
			case 'b':
			{
				returnValue = input;
				if (input < minThreshold)
					returnValue =  (minThreshold - input) / minThreshold;
				if (input > maxThreshold)
					returnValue = 0;
			}
			break;
		}
		return returnValue;
	}

}
