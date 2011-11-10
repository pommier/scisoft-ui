/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.histogram;

/**
 * A small image data container with the minimum set of functionality
 */
public class ColourImageData {
	
	private int imgWidth;
	private int imgHeight;
	private int[] dataContainer;
	
	public ColourImageData(final int width, final int height) {
		imgWidth = width;
		imgHeight = height;
		dataContainer = new int[width * height];
		for (int y = 0; y < height; y++)
			for (int x = 0; x < width; x++) dataContainer[x+y*width] = 0;
	}

	public void set(int value, int pos) {
		if (pos < imgWidth * imgHeight && pos >= 0)
			dataContainer[pos] = value;
	}
	
	public void set(int value, int x, int y) {
		if (x >= 0 && x < imgWidth && y >=0 && y < imgHeight) {
			dataContainer[x+y*imgWidth] = value;
		}
	}
	
	public int getHeight() {
		return imgHeight;
	}
	
	public int getWidth() {
		return imgWidth;
	}
	
	public int get(int pos) {
		if (pos >= 0 && pos < imgWidth * imgHeight)
			return dataContainer[pos];
		return -1;
	}

	public int get(int x, int y) {
		return get(x+y*imgWidth);
	}
}
