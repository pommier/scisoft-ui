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
 * Specific NCD Blue channel mapping function
 */
public class NCDGamma2BlueFunction extends AbstractMapFunction {

	@Override
	public String getMapFunctionName() {
		return "NCD Gamma II Blue";
	}

	@Override
	public double mapFunction(double input) {
		if (input >= 0.690) return (input-0.690) / (1-0.690);
		if (input <= 0.192) return input/0.192;
		if (input <= 0.373) return 1 - (input - 0.192) / (0.373 - 0.192);
		if (input <= 0.506) return 0;
		if (input >= 0.624) return 0;
		if (input <= 0.569) return ((input-0.506)/(0.569-0.506)) * 0.322;
		if (input >= 0.569) return (1 - ((input-0.569)/(0.624-0.569))) * 0.322;
		return 0;
	}

}
