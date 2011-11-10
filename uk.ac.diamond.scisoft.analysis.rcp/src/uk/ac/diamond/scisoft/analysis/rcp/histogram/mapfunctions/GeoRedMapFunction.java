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
 * Geographical Red channel mapping
 */
public class GeoRedMapFunction extends AbstractMapFunction {

	private static final double region1 = 1.0/7.0;
	private static final double region2 = 2.0/5.0;
	private static final double region3 = 3.0/4.0;
	@Override
	public String getMapFunctionName() {
		return "GeoRed";
	}

	@Override
	public double mapFunction(double input) {
		if (input < region1)
			return 0;
		if (input >= region1 && input < region2)
			return (input-region1) * 4;
		else if (input >= region2 && input < region3)
			return 1.0 - 0.5 * (input-region2);
		else if (input >= region3)
			return 1.0 - 0.5 * (region3-region2) + 3.75 * (input-region3) * (input-region3);
		return 1;
	}

}
