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
 * Specific NCD Red channel mapping function
 */
public class NCDGamma2RedFunction extends AbstractMapFunction {

	@Override
	public String getMapFunctionName() {
		return "NCD Gamma II Red";
	}

	@Override
	public double mapFunction(double input) {
		if (input >= 0.753) return 1;
		if (input <= 0.188) return 0;
		if (input <= 0.251) return 0.316 * (input - 0.188) / (0.251 - 0.188);
		if (input <= 0.306) return 0.316;
		if (input <= 0.431) return 0.316 + (1 - 0.319) * (input - 0.306) / (0.431 - 0.306);
		if (input >= 0.682) return 0.639 + (1 - 0.639) * (input - 0.682) / (0.753 - 0.682);
		if (input >= 0.635) return 1 - (1 - 0.639) * (input - 0.635) / (0.682 - 0.635);
		return 1;					
	}

}
