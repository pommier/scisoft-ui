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
public class BoxObject extends OverlayObject {

	private double lux,luy,rlx,rly;
	
	public BoxObject(int primID, OverlayProvider provider) 
	{
		super(primID, provider);
	}

	public void setBoxPoints(double lux, double luy, double rlx, double rly) 
	{
		this.lux = lux;
		this.luy = luy;
		this.rlx = rlx;
		this.rly = rly;
	}
	
	public void setBoxUpperLeftPoint(double lux, double luy)  
	{
		this.lux = lux;
		this.luy = luy;
	}
	
	public void setBoxBottomRightPoint(double rlx, double rly) 
	{
		this.rlx = rlx;
		this.rly = rly;
	}
	
	public void setBoxPosition(double lux, double luy) 
	{
		this.lux = lux;
		this.luy = luy;
	}
	
	public void setBoxWidth(double width) 
	{
		this.rlx = lux + width;
	}
	
	public void setBoxHeight(double height) 
	{
		this.rly = luy + height;
	}
	
	@Override
	public void draw() {
		if (provider instanceof Overlay1DProvider) {
			((Overlay1DProvider)provider).drawBox(primID, lux, luy, rlx, rly);
		} else if (provider instanceof Overlay2DProvider)
			((Overlay2DProvider)provider).drawBox(primID, lux, luy, rlx, rly);
	}	
	
}
