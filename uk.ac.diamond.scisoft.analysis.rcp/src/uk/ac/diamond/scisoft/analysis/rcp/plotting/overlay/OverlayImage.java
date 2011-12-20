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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay;

/**
 * Overlay image object
 */
public class OverlayImage {

	private byte[] data;
	private int width;
	private int height;
	private boolean isDirty;
	
	public OverlayImage(int width, int height) {
		data = new byte[width*height*4];
		this.width = width;
		this.height = height;
	}
	
	public int getWidth() {
		return width;
	}
	
	public int getHeight() {
		return height;
	}
	
	public byte[] getImageData() {
		return data;
	}
	
	public void zap() {
		data = new byte[width*height*4];
		isDirty = true;
	}
	
	public void clear(short red, short green, short blue, short alpha) {
		for (int y = 0; y < height; y++)
			for (int x = 0; x <width; x++) {
				data[(x+y*width)*4] = (byte)red;
				data[(x+y*width)*4+1] = (byte)green;
				data[(x+y*width)*4+2] = (byte)blue;
				data[(x+y*width)*4+3] = (byte)alpha;
			}
		isDirty = true;
	}
	
	public boolean isDirty() {
		return isDirty;
	}
	
	public void putPixel(int x, int y,
			             short red, 
			             short green, 
			             short blue, 
			             short alpha) {
		
		if (x >= 0 && x < width &&
			y >= 0 && y < height) {
			data[(x+y*width)*4] = (byte)red;
			data[(x+y*width)*4+1] = (byte)green;
			data[(x+y*width)*4+2] = (byte)blue;
			data[(x+y*width)*4+3] = (byte)alpha;
		}
		isDirty = true;
	}
	
	public void clean() {
		isDirty = false;
	}

	public int[] getShape() {
		return new int[] { height, width };
	}
}
