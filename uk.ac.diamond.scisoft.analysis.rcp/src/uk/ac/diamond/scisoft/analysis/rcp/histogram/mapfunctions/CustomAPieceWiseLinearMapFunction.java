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
 * A custom piece-wise linear mapping function
 */
public class CustomAPieceWiseLinearMapFunction extends AbstractMapFunction {
	@Override
	public String getMapFunctionName() {
		return "4x;1;-2x+1.84;12.5x-11.5";
	}

	@Override
	public double mapFunction(double input) {
		double returnValue;
		if (input < 0.25) {
			returnValue = 4.0 * input;
		} else if (input < 0.42) {
			returnValue = 1.0;
		} else if (input < 0.92) {
			returnValue = -2.0 * input + 1.84;
		} else {
			returnValue = 12.5 * input - 11.5;
		}
		return returnValue;
	}
}
