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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.utils;

import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IndexIterator;
import uk.ac.diamond.scisoft.analysis.dataset.RGBDataset;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions.AbstractMapFunction;

/**
 * Helper methods to convert to SWT images from datasets
 */
public class SWTImageUtils {

	
	static private ImageData createImageFromRGBADataSet(RGBDataset rgbdata, long maxv)
	{
		ImageData img;
		final IndexIterator iter = rgbdata.getIterator(true);
		final int[] pos = iter.getPos();
		final int[] shape = rgbdata.getShape();
		final int height = shape[0];
		final int width = shape.length == 1 ? 1 : shape[1]; // allow 1D datasets to be saved

		short[] data = rgbdata.getData();
		if (maxv < 32) { // 555
			img = new ImageData(width, height, 16, new PaletteData(0x7c00, 0x03e0, 0x001f));

			while (iter.hasNext()) {
				final int n = iter.index;
				final int rgb = ((data[n] & 0x1f) << 10) | ((data[n + 1] & 0x1f) << 5) | (data[n + 2] & 0x1f);
				img.setPixel(pos[1], pos[0], rgb);
			}
		} else if (maxv < 64) { // 565
			img = new ImageData(width, height, 16, new PaletteData(0xf800, 0x07e0, 0x001f));

			while (iter.hasNext()) {
				final int n = iter.index;
				final int rgb = (((data[n] >> 1) & 0x1f) << 10) | ((data[n + 1] & 0x3f) << 5) | ((data[n + 2] >> 1) & 0x1f);
				img.setPixel(pos[1], pos[0], rgb);
			}
		} else if (maxv < 256) { // 888
			img = new ImageData(width, height, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));

			while (iter.hasNext()) {
				final int n = iter.index;
				final int rgb = ((data[n] & 0xff) << 16) | ((data[n + 1] & 0xff) << 8) | (data[n + 2] & 0xff);
				img.setPixel(pos[1], pos[0], rgb);
			}
		} else {
			int shift = 0;
			while (maxv >= 256) {
				shift++;
				maxv >>= 2;
			}

			img = new ImageData(width, height, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));

			while (iter.hasNext()) {
				final int n = iter.index;
				final int rgb = (((data[n] >> shift) & 0xff) << 16) | (((data[n + 1] >> shift) & 0xff) << 8) | ((data[n + 2] >> shift) & 0xff);
				img.setPixel(pos[1], pos[0], rgb);
			}
		}
		return img;
	}
	
	static private ImageData createImageFromDataSet(AbstractDataset a, 
													long maxv,
													AbstractMapFunction redFunc,
													AbstractMapFunction greenFunc,
													AbstractMapFunction blueFunc,
													boolean inverseRed,
													boolean inverseGreen,
													boolean inverseBlue) 
	{
		final int[] shape = a.getShape();
		final int height = shape[0];
		final int width = shape.length == 1 ? 1 : shape[1]; // allow 1D datasets to be saved
		ImageData img;
		final IndexIterator iter = a.getIterator(true);
		final int[] pos = iter.getPos();
		img = new ImageData(width, height, 24, new PaletteData(0xff0000, 0x00ff00, 0x0000ff));
		while (iter.hasNext()) {
			double value = a.getElementDoubleAbs(iter.index) / maxv;
			final int red = (inverseRed ? (255-redFunc.mapToByte(value)) : redFunc.mapToByte(value));
			final int green = (inverseGreen ? (255-greenFunc.mapToByte(value)) : greenFunc.mapToByte(value));
			final int blue = (inverseBlue ? (255-blueFunc.mapToByte(value)) : blueFunc.mapToByte(value));
			final int rgb = (red << 16) | green << 8 | blue; 		
			img.setPixel(pos[1], pos[0],rgb); 
		}
		return img;
	}

	/**
	 * Create SWT ImageData from a dataset
	 * <p>
	 * The input dataset can be a RGB dataset in which case the mapping functions
	 * and inversion flags are ignored.
	 * @param a dataset
	 * @param max maximum value of dataset
	 * @param redFunc
	 * @param greenFunc
	 * @param blueFunc
	 * @param inverseRed
	 * @param inverseGreen
	 * @param inverseBlue
	 * @return an ImageData object for SWT
	 */
	static public ImageData createImageData(AbstractDataset a, Number max,
											AbstractMapFunction redFunc,
											AbstractMapFunction greenFunc,
											AbstractMapFunction blueFunc,
											boolean inverseRed,
											boolean inverseGreen,
											boolean inverseBlue) {
		ImageData img;
		long maxv = max.longValue();

		if (a instanceof RGBDataset) {
			img = createImageFromRGBADataSet((RGBDataset)a, maxv);
		} else {
			img = createImageFromDataSet(a, maxv,redFunc,greenFunc,blueFunc,
										 inverseRed,inverseGreen,inverseBlue);
		}

		return img;
	}

	/**
	 * Create RGB dataset from an SWT image
	 * @param image
	 * @return a RGB dataset
	 */
	static public RGBDataset createRGBDataset(final ImageData image) {
		final int[] data = new int[image.width];
		final RGBDataset rgb = new RGBDataset(image.height, image.width);
		final short[] p = new short[3];
		final PaletteData palette = image.palette;
		if (palette.isDirect) {
			for (int i = 0; i < image.height; i++) {
				image.getPixels(0, i, image.width, data, 0);
				for (int j = 0; j < image.width; j++) {
					int value = data[j];
					p[0] = palette.redShift >= 0 ? (short) ((value & palette.redMask) << palette.redShift) :
						(short) ((value & palette.redMask) >>> -palette.redShift);
					p[1] = palette.greenShift >= 0 ? (short) ((value & palette.greenMask) << palette.greenShift) :
						(short) ((value & palette.greenMask) >>> -palette.greenShift);
					p[2] = palette.blueShift >= 0 ? (short) ((value & palette.blueMask) << palette.blueShift) :
						(short) ((value & palette.blueMask) >>> -palette.blueShift);
					rgb.setItem(p, i, j);
				}
			}
		} else {
			final RGB[] table = palette.getRGBs();
			for (int i = 0; i < image.height; i++) {
				image.getPixels(0, i, image.width, data, 0);
				for (int j = 0; j < image.width; j++) {
					RGB value = table[data[j]];
					p[0] = (short) value.red;
					p[1] = (short) value.green;
					p[2] = (short) value.blue;
					rgb.setItem(p, i, j);
				}
			}
		}

		return rgb;
	}

}
