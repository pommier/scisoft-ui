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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

/**
 * This is a region of interest selection object for the 3D surface plotting 
 * 
 */
public class SurfacePlotROI {

	private int startX;
	private int startY;
	private int endX;
	private int endY;
	private int xSamplingMode;
	private int ySamplingMode;
	private int xAspect;
	private int yAspect;
	
	public SurfacePlotROI(int startX, int startY,
			              int endX, int endY,
			              int xSamplingMode, int ySamplingMode,
			              int xAspect, int yAspect) {
		
		this.startX = startX;
		this.startY = startY;
		this.endX = endX;
		this.endY = endY;
		this.xSamplingMode = xSamplingMode;
		this.ySamplingMode = ySamplingMode;
		this.xAspect = xAspect;
		this.yAspect = yAspect;
	}
	
	public int getStartX() {
		return startX; 
	}
	
	public int getStartY() {
		return startY;
	}
	
	public int getEndX() {
		return endX;
	}
	
	public int getEndY() {
		return endY;
	}
	
	public int getXSamplingMode() {
		return xSamplingMode;
	}
	
	public int getYSamplingMode() {
		return ySamplingMode;
	}
	
	public int getXAspect() {
		return xAspect;
	}
	
	public int getYAspect() {
		return yAspect;
	}
}
