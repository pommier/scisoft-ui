/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

import uk.ac.diamond.scisoft.analysis.roi.LinearROI;

/**
 * Wrapper class for a LinearROI that adds handles
 */
public class LinearROIHandler extends ROIHandles {

	/**
	 * Number of handle areas
	 */
	private final static int NHANDLE = 3;
	
	/**
	 * Handler for LinearROI
	 * @param roi
	 */
	public LinearROIHandler(LinearROI roi) {
		super();
		for (int h = 0; h < NHANDLE; h++) {
			add(-1);
		}
		this.roi = roi;
	}

	/**
	 * @return Returns the roi.
	 */
	@Override
	public LinearROI getROI() {
		return (LinearROI) roi;
	}

	@Override
	public int[] getHandlePoint(int handle, int size) {
		int[] pt = getAnchorPoint(handle, size);
		
		if (pt != null) {
			pt[0] -= size/2;
			pt[1] -= size/2;
		}
		return pt;
	}

	@Override
	public int[] getAnchorPoint(int handle, int size) {
		final LinearROI oroi = (LinearROI) roi;
		int[] pt = null;

		switch (handle) {
		case 0:
			pt = oroi.getIntPoint();
			break;
		case 1:
			pt = new int[] { (int) oroi.getMidPoint()[0], (int) oroi.getMidPoint()[1] };
			break;
		case 2:
			pt = oroi.getIntEndPoint();
			break;
		}

		return pt;
	}

	/**
	 * @param handle
	 * @param pt
	 * @return reoriented ROI
	 */
	public LinearROI reorient(int handle, int[] pt) {
		final LinearROI oroi = (LinearROI) roi;
		LinearROI croi = null;
		double len;

		switch (handle) {
		case 0:
			croi = oroi.copy();
			len = croi.getLength();
			croi.setPointKeepEndPoint(pt);
			croi.translateAlongLength(croi.getLength()-len);
			croi.setLength(len);
			break;
		case 2:
			croi = oroi.copy();
			len = croi.getLength();
			croi.setEndPoint(pt);
			croi.setLength(len);
			break;
		}
		return croi;
	}

	/**
	 * @param handle
	 * @param pt
	 * @return resized ROI
	 */
	public LinearROI resize(int handle, int[] pt) {
		final LinearROI oroi = (LinearROI) roi;
		LinearROI croi = null;

		switch (handle) {
		case 0:
			croi = oroi.copy();
			((LinearROI) roi).setPointKeepEndPoint(pt);
			break;
		case 2:
			croi = oroi.copy();
			croi.setEndPoint(pt);
			break;
		}
		return croi;
	}
}
