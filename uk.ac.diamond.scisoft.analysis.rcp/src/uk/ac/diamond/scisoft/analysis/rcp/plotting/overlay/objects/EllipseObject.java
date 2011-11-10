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

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;

/**
 *
 */
public class EllipseObject extends OverlayObject {

	private double cx, cy;
	private double a = 1.0;
	private double b = 1.0;
	private double omega = 0.0;
	
	public EllipseObject(int primID, OverlayProvider provider) {
		super(primID, provider);
	}

	public void setEllipsePoint(double cx, double cy) {
		this.cx = cx;
		this.cy = cy;
	}
	
	public void setEllipseParams(double a, double b, double omega) {
		this.a = a;
		this.b = b;
		this.omega = omega;
	}
	
	@Override
	public void draw() {
		if (provider instanceof Overlay2DProvider)
			((Overlay2DProvider)provider).drawEllipse(primID, cx, cy, a, b, omega);
	}		
}
