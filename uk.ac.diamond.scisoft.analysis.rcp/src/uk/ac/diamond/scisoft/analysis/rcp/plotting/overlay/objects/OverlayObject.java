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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.objects;

import java.awt.Color;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.VectorOverlayStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;

/**
 *
 */
public class OverlayObject {

	protected int primID = -1;
	protected OverlayProvider provider;
	
	public void draw() {
		// Leave this to sub classes to get this right
	}
	
	public OverlayObject(int primID, OverlayProvider provider) 
	{
		this.provider = provider;
		this.primID = primID;
	}
	
	public void setPrimID(int primID) 
	{
		this.primID = primID;
	}
	
	public int getPrimID() 
	{
		return primID;
	}
	
	public void translate(double tx, double ty) 
	{
		if (provider != null)
			provider.translatePrimitive(primID, tx, ty);
	}
	
	public void rotate(double angle, double rcx, double rcy) 
	{
		if (provider != null)
			provider.rotatePrimitive(primID, angle, rcx, rcy);
	}
	
	public void setOutlineColour(Color colour) 
	{
		if (provider != null)
			provider.setOutlineColour(primID, colour);
	}
	
	public void setColour(Color colour) 
	{
		if (provider != null)
			provider.setColour(primID,colour);
	}
	
	public void setTransparency(double transparency) 
	{
		if (provider != null)
			provider.setTransparency(primID, transparency);
	}
	
	public void setStyle(VectorOverlayStyles newStyle) 
	{
		if (provider != null)
			provider.setStyle(primID, newStyle);
	}
	
	public void setAnchorPoints(double x, double y) 
	{
		if (provider != null)
			provider.setAnchorPoints(primID, x, y);
	}
	
	public void setVisible(boolean visible) {
		if (provider != null)
			provider.setPrimitiveVisible(primID, visible);
	}
	
	public void dispose() {
		if (provider != null)
			provider.unregisterPrimitive(primID);
		primID = -1;
	}
	
	@Override
	public void finalize() {
		if (provider != null && primID != -1) {
			provider.unregisterPrimitive(primID);
		}
	}
}
