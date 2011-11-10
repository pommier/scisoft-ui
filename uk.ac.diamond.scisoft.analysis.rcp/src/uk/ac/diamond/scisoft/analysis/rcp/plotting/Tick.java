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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

/**
 *
 */
public class Tick {

	private String tickName;
	private double tickValue;
	private double xCoord;
	private double yCoord;
	private double zCoord;
	
	/**
	 * @param tickName
	 */
	public void setTickName(String tickName) {
		this.tickName = tickName;
	}
	
	/**
	 * @return the tick name
	 */
	public String getTickName() {
		return tickName;
	}
	
	/**
	 * @param value
	 */
	public void setTickValue(double value) {
		this.tickValue = value;
	}
	
	/**
	 * @return the tick value
	 */
	public double getTickValue() {
		return tickValue;
	}
	
	/**
	 * @param xCoord
	 */
	public void setXCoord(double xCoord) {
		this.xCoord = xCoord;
	}
	
	/**
	 * @return the x-coordinate
	 */
	public double getXCoord() {
		return xCoord;
	}
	
	/**
	 * @param yCoord
	 */
	public void setYCoord(double yCoord) {
		this.yCoord = yCoord;
	}
	
	/**
	 * @return the y-coordinate 
	 */
	public double getYCoord() {
		return yCoord;
	}
	
	/**
	 * @param zCoord
	 */
	public void setZCoord(double zCoord) {
		this.zCoord = zCoord;
	}
	
	/**
	 * @return the z-coordinate
	 */
	public double getZCoord() {
		return zCoord;
	}
	
}
