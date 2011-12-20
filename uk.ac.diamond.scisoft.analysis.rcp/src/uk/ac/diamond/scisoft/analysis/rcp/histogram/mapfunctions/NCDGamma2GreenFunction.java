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
 * Specific NCD Green channel mapping function
 */
public class NCDGamma2GreenFunction extends AbstractMapFunction {

	@Override
	public String getMapFunctionName() {
		return "NCD Gamma II Green";
	}

	@Override
	public double mapFunction(double input) {
		if (input >= 0.749) return 1;
		if (input <= 0.447) return 0;
		if (input <= 0.569) return 0.639 * (input - 0.447) / (0.569 - 0.447);
		if (input >= 0.690) return 0.639 + (1 - 0.639) * (input - 0.690) / (0.749 - 0.690); 
		return 0.639;
	}

}
