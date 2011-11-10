/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.objects;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay1DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;

/**
 *
 */
public class LineObject extends OverlayObject {

	private double sx,sy,ex,ey;
	
	public LineObject(int primID, OverlayProvider provider) {
		super(primID, provider);
	}
	
	public void setLinePoints(double sx,  double sy, double ex, double ey) {
		this.sx = sx;
		this.ex = ex;
		this.sy = sy;
		this.ey = ey;
	}
	
	public void setLineStart(double sx, double sy) {
		this.sx = sx;
		this.sy = sy;
	}
	
	public void setLineEnd(double ex, double ey) {
		this.ex = ex;
		this.ey = ey;
	}

	@Override
	public void draw() {
		if (provider instanceof Overlay1DProvider) {
			((Overlay1DProvider)provider).drawLine(primID, sx, sy, ex, ey);
		} else if (provider instanceof Overlay2DProvider)
			((Overlay2DProvider)provider).drawLine(primID, sx, sy, ex, ey);
	}
}
