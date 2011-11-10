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
 * Constant map function this will always return a constant value no matter what the
 * input value is
 */

public class ConstMapFunction extends AbstractMapFunction {
	private double constValue;
	private String functionName;
	
	/**
	 * Constant colour map function
	 * @param constValue constant value to be returned
	 * @param functionName name of the function
	 */
	
	public ConstMapFunction(double constValue, String functionName)
	{
		this.functionName = functionName;
		this.constValue = constValue;
	}

	@Override
	public String getMapFunctionName() {
		return functionName;
	}

	@Override
	public double mapFunction(double input) {
		return constValue;
	}

}
