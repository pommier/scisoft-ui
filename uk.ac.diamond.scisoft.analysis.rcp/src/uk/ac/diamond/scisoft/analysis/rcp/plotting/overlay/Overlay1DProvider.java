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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay;

/**
 * 1D Overlay provider
 */
public interface Overlay1DProvider extends OverlayProvider {

	/**
	 * Draw a line primitive
	 * @param primID id of the primitive
	 * @param sx start x position
	 * @param sy start y position
	 * @param ex end x position
	 * @param ey end y position
	 * @return if the draw was successful (true) otherwise (false)
	 */
	
	public boolean drawLine(int primID, double sx,  double sy, double ex, double ey);
	
	/**
	 * Draw a box primitive
	 * @param primID id of the primitive
	 * @param lux left upper x coordinate
	 * @param luy left upper y coordinate
	 * @param rlx right lower x coordinate
	 * @param rly right lower y coordinate
	 * @return if the draw was successful (true) otherwise (false)
	 */
	public boolean drawBox(int primID, double lux, double luy, double rlx, double rly);
	
	
	/**
	 * Draw a label primitive 
	 * @param primID id of the primitive
	 * @param sx x coordinate of the label
	 * @param sy y coordinate of the label
	 * @return if the translation was successful (true) otherwise (false)
	 */
	
	public boolean drawLabel(int primID, double sx, double sy);
	
	
}
