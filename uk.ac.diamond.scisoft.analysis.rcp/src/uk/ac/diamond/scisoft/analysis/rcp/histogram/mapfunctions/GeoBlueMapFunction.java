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
 * Geographical Blue channel mapping
 */
public class GeoBlueMapFunction extends AbstractMapFunction {

	private static final double region1 = 1.0/6.0;
	private static final double region2 = 1.0/5.0;
	private static final double region3 = 1.0/3.0;
	private static final double region4 = 3.0/4.0;
	private static final double region5 = 4.0/5.0;
	
	@Override
	public String getMapFunctionName() {
		// TODO Auto-generated method stub
		return "GeoBlue";
	}

	@Override
	public double mapFunction(double input) {
		if (input < region1) 
			return  Math.sin(0.5 * Math.PI * (input / region1));
		else if (input >= region1 && input < region2)
			return 1;
		else if (input >= region2 && input < region3)
			return 1 - (input - region2) * 6;
		else if (input >= region3 && input < region4)
			return 0.15;
		else if (input >= region4 && input < region5)			
			return 0.15 + 3.125 * Math.sqrt(input-region4);
		else if (input >= region5)
			return 0.15 + 3.125 * Math.sqrt(region5-region4) + 6.25 * (input-region5) * (input-region5);
//			return 20 * (input-region4) * (input - region4);
		return 0;
	}

}
