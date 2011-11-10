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

import java.util.ArrayList;

import uk.ac.diamond.scisoft.analysis.roi.ROIBase;

/**
 * Abstract class for region of interest handles
 * 
 * Its super class holds the primitive IDs for handle areas
 */
abstract public class ROIHandles extends ArrayList<Integer> {
	protected ROIBase roi;

	/**
	 * @param handle
	 * @param size 
	 * @return handle point
	 */
	abstract public int[] getHandlePoint(int handle, int size);

	/**
	 * @param handle
	 * @param size
	 * @return anchor point for scale invariant display
	 */
	abstract public int[] getAnchorPoint(int handle, int size);

	abstract public ROIBase getROI();


	/**
	 * @param roi The roi to set.
	 */
	public void setROI(ROIBase roi) {
		this.roi = roi;
	}

}
