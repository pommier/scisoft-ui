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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.enums;

/**
 * Defines the different axis mode
 */
public enum AxisMode {
	/**
	 * Linear mode so a 1:1 mapping between data point and its x axis
	 */
	LINEAR(1), 
	/**
	 *  Just like linear mode but with an initial offset
	 */
	LINEAR_WITH_OFFSET(2), 
	
	/**
	 * Completely custom each data point mode might have a specific x value
	 */
	CUSTOM(3);

	private int code;
	
	private AxisMode(int code) {
		this.code = code;
	}
	
	/**
	 * Used for saving enum to file.
	 * @return int code
	 */
	public int asInt() {
		return code;
	}
	
	/**
	 * Used for saving enum to file
	 * @param code
	 * @return AxisMode
	 */
	public static AxisMode asEnum(int code) {
		switch (code) {
			case 1: return LINEAR;
			case 2: return LINEAR_WITH_OFFSET;
			case 3: return CUSTOM;
		}
		return null;
	}
}
