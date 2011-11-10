/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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
 * Geographical Green channel mapping
 */
public class GeoGreenMapFunction extends AbstractMapFunction {

	private static final double region1 = 1.0/7.0;
	private static final double region2 = 1.0/5.0;
	private static final double region3 = 1.0/3.0;
	private static final double region4 = 2.0/5.0;
	private static final double region5 = 2.0/3.0;
	private static final double region6 = 5.0/6.0;
	
	@Override
	public String getMapFunctionName() {
		return "GeoGreen";
	}

	@Override
	public double mapFunction(double input) {
		if (input < region1)
			return 0;
		else if (input >= region1 && input < region2) 
			return 3.75 * Math.sqrt(input - region1);
		else if (input >= region2 && input < region3)
			return 0.9;
		else if (input >= region3 && input < region4)
			return 0.9 + (input-region3);
		else if (input >= region4 && input < region5)
			return 0.9742  - 1.25 * (input-region4);
		else if (input >= region5 && input < region6)
			return 0.9742 - 1.25 * (region5-region4) - 0.75 * Math.sqrt(input - region5);
		else
			return 0.9742 - 1.25 * (region5-region4) - 0.75 * Math.sqrt(region6 - region5) + 28 * (input-region6) * (input-region6);
	}

}
