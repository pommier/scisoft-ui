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

import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;

/**
 * Wrapper class for a RectangularROI that adds handles
 */
public class RectangularROIHandler extends ROIHandles {
	/**
	 * Number of handle areas
	 */
	private final static int NHANDLE = 9;
	
	/**
	 * Handler for RectangularROI
	 * @param roi
	 */
	public RectangularROIHandler(RectangularROI roi) {
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
	public RectangularROI getROI() {
		return (RectangularROI) roi;
	}

	@Override
	public int[] getHandlePoint(int handle, int size) {
		final RectangularROI oroi = (RectangularROI) roi;
		int[] pt = null;

		switch (handle) {
		case 0:
			pt = oroi.getIntPoint();
			break;
		case 1:
			pt = oroi.getIntPoint(0.5, 0);
			pt[0] -= size/2;
			break;
		case 2:
			pt = oroi.getIntPoint(1.0, 0);
			pt[0] -= size;
			break;
		case 3:
			pt = oroi.getIntPoint(0.0, 0.5);
			pt[1] -= size/2;
			break;
		case 4:
			pt = oroi.getIntPoint(0.5, 0.5);
			pt[0] -= size/2;
			pt[1] -= size/2;
			break;
		case 5:
			pt = oroi.getIntPoint(1.0, 0.5);
			pt[0] -= size;
			pt[1] -= size/2;
			break;
		case 6:
			pt = oroi.getIntPoint(0.0, 1.0);
			pt[1] -= size;
			break;
		case 7:
			pt = oroi.getIntPoint(0.5, 1.0);
			pt[0] -= size/2;
			pt[1] -= size;
			break;
		case 8:
			pt = oroi.getIntPoint(1.0, 1.0);
			pt[0] -= size;
			pt[1] -= size;
			break;
		}
		return pt;
	}

	@Override
	public int[] getAnchorPoint(int handle, int size) {
		final RectangularROI oroi = (RectangularROI) roi;
		int[] pt = null;

		switch (handle) {
		case 0:
			pt = oroi.getIntPoint();
			break;
		case 1:
			pt = oroi.getIntPoint(0.5, 0);
			break;
		case 2:
			pt = oroi.getIntPoint(1.0, 0);
			break;
		case 3:
			pt = oroi.getIntPoint(0.0, 0.5);
			break;
		case 4:
			pt = oroi.getIntPoint(0.5, 0.5);
			break;
		case 5:
			pt = oroi.getIntPoint(1.0, 0.5);
			break;
		case 6:
			pt = oroi.getIntPoint(0.0, 1.0);
			break;
		case 7:
			pt = oroi.getIntPoint(0.5, 1.0);
			break;
		case 8:
			pt = oroi.getIntPoint(1.0, 1.0);
			break;
		}
		return pt;
	}

	/**
	 * @param handle
	 * @param spt starting point 
	 * @param pt 
	 * @return resized ROI
	 */
	public RectangularROI resize(int handle, int[] spt, int[] pt) {
		RectangularROI rroi = null;
		double[] ept;

		if (handle == 4)
			return rroi;

		rroi = (RectangularROI) roi.copy();
		ept = rroi.getEndPoint();

		switch (handle) {
		case -1: // new definition
			rroi.setPoint(spt);
			rroi.setEndPoint(pt);
			break;
		case 0:
			pt[0] -= spt[0];
			pt[1] -= spt[1];
			rroi.setPointKeepEndPoint(pt, true, true);
			break;
		case 1:
			pt[0] -= spt[0];
			pt[1] -= spt[1];
			rroi.setPointKeepEndPoint(pt, false, true);
			break;
		case 2:
			rroi.adjustKeepDiagonalPoint(spt, ept, pt, true);
			break;
		case 3:
			pt[0] -= spt[0];
			pt[1] -= spt[1];
			rroi.setPointKeepEndPoint(pt, true, false);
			break;
		case 5:
			pt[0] += ept[0] - spt[0];
			pt[1] += ept[1] - spt[1];
			rroi.setEndPoint(pt, true, false);
			break;
		case 6:
			rroi.adjustKeepDiagonalPoint(spt, ept, pt, false);
			break;
		case 7:
			pt[0] += ept[0] - spt[0];
			pt[1] += ept[1] - spt[1];
			rroi.setEndPoint(pt, false, true);
			break;
		case 8:
			pt[0] += ept[0] - spt[0];
			pt[1] += ept[1] - spt[1];
			rroi.setEndPoint(pt, true, true);
			break;
		default:
			break;
		}

		return rroi;
	}

	/**
	 * @param handle
	 * @param pt
	 * @return reoriented ROI
	 */
	public RectangularROI reorient(int handle, int[] pt) {
		RectangularROI rroi = null;

		if (handle == 4 || (handle%2) == 1)
			return rroi;

		final RectangularROI oroi = (RectangularROI) roi;

		rroi = oroi.copy();
		double nang, oang;

		switch (handle) {
		case 0: // keep end point
			oang = oroi.getAngleRelativeToPoint(1.0, 1.0, oroi.getIntPoint());
			nang = oroi.getAngleRelativeToPoint(1.0, 1.0, pt);
			rroi.addAngle(nang-oang);
			rroi.setEndPointKeepLengths(oroi.getEndPoint());
			break;
		case 2:
			oang = oroi.getAngleRelativeToPoint(0.0, 1.0, oroi.getIntPoint(1.0, 0.0));
			nang = oroi.getAngleRelativeToPoint(0.0, 1.0, pt);
			rroi.translate(0.0, 1.0);
			rroi.addAngle(nang-oang);
			rroi.translate(0.0, -1.0);
			break;
		case 6:
			oang = oroi.getAngleRelativeToPoint(1.0, 0.0, oroi.getIntPoint(0.0, 1.0));
			nang = oroi.getAngleRelativeToPoint(1.0, 0.0, pt);
			rroi.translate(1.0, 0.0);
			rroi.addAngle(nang-oang);
			rroi.translate(-1.0, 0.0);
			break;
		case 8: // keep start point
			oang = oroi.getAngleRelativeToPoint(0, 0, oroi.getIntPoint(1.0, 1.0));
			nang = oroi.getAngleRelativeToPoint(0, 0, pt);
			rroi.addAngle(nang-oang);
			break;
		}
		return rroi;
	}
}
