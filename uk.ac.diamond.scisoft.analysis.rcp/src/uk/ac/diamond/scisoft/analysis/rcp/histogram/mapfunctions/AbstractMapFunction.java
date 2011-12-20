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
 * Abstract class for a general mapping function from one double to another one or a byte
 */
public abstract class AbstractMapFunction {

	/**
	 * Clipped version of mapFunction
	 * @param input original value to map
	 * @return output double
	 */
	final public double clippedMapToDouble(double input) {
		double value = mapFunction(input);
		if (value < 0.0) return 0.0;
		else if (value > 1.0) return 1.0;
		return value;
	}

	/**
	 * @param input original value to map
	 * @return byte (C-style usage) 0..255 but due to stupid Java signed bytes will be 
	 *              mapped to -128..127 in Java we have to use short
	 */
	final public short mapToByte(double input) {
		return (short)(255*clippedMapToDouble(input));
	}

	/**
	 * Get the name of the function so it can be included in GUI components
	 * @return the function name
	 */
	abstract public String getMapFunctionName();

	
	/**
	 * Converts an input value to an output value.
	 * 
	 * @param input the input value (0 to 1)
	 * 
	 * @return the output value (0 to 1)
	 */
	abstract public double mapFunction(double input);
}
