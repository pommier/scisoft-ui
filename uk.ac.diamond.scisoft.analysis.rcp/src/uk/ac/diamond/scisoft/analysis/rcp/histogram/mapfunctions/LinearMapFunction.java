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

package uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions;

/**
 * General linear map function with offset
 * 
 * This will scale the input by a specified scaling factor and adds an
 * offset to it, optional it is possible to get the absolute value 
 */
public class LinearMapFunction extends AbstractMapFunction {
	private String functionName;
	private double scaleFactor;
	private double offset;
	private boolean useAbsolute;

	/**
	 * Linear: x
	 */
	public LinearMapFunction()
	{
		this("x", 1.0, 0.0, false);
	}

	/**
	 * Scaled linear: scaleFactor * x
	 * @param functionName
	 * @param scaleFactor
	 * @param useAbs
	 */
	public LinearMapFunction(String functionName,
										double scaleFactor,
										boolean useAbs)
	{
		this(functionName, scaleFactor, 0.0, useAbs);
	}

	/**
	 * Scaled linear: scaleFactor * x + offset
	 *
	 * @param functionName name of the function
	 * @param scaleFactor scaling factor
	 * @param offset offset
	 * @param useAbs use absolute value?
	 */
	public LinearMapFunction(String functionName,
										double scaleFactor,
										double offset,
										boolean useAbs)
	{
		this.functionName = functionName;
		this.scaleFactor = scaleFactor;
		this.offset = offset;
		this.useAbsolute = useAbs;
	}

	@Override
	public String getMapFunctionName() {
		return functionName;
	}

	@Override
	public double mapFunction(double input) {
		double returnValue = input * scaleFactor + offset;
		if (useAbsolute)
			returnValue = Math.abs(returnValue);
		return returnValue;
	}
}
