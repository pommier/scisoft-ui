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
 *
 */
public class UserCustomFunction extends AbstractMapFunction {

	double[] mapTable;
	String funcname;
	
	public UserCustomFunction(String funcname, int tableSize) {
		mapTable = new double[tableSize];
		this.funcname = funcname;
	}
	@Override
	public String getMapFunctionName() {
		return funcname;
	}

	public void setValues(double[] values) {
		mapTable = values.clone();
	}
	
	public void setValue(int pos, double value) {
		if (pos >= 0 && pos < mapTable.length)
			mapTable[pos] = value;
	}
	
	@Override
	public double mapFunction(double input) {
		int lowMapEntry = (int)(input * mapTable.length);
		int upMapEntry = (int)Math.ceil(input * mapTable.length);
		
		if (upMapEntry > mapTable.length-1)
			upMapEntry = mapTable.length-1;
		
		double a = input * mapTable.length - lowMapEntry;
		// linear interpolation
		return mapTable[lowMapEntry] * (1.0 - a) +  a * mapTable[upMapEntry];
	}

}
