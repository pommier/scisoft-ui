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

package uk.ac.diamond.scisoft.analysis.rcp.util;

import org.eclipse.swt.graphics.ImageData;

import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;

public class SWTImageDataConverter {
	// weights from NTSC formula aka ITU-R BT.601 for mapping RGB to luma
	private static final double Wr = 0.299, Wg = 0.587, Wb = 0.114;
	ImageData imageData;
	IDataset idataset;

	public SWTImageDataConverter(ImageData imageData) {
		this.imageData = imageData;
	}

	public IDataset toIDataset() {
		if (imageData == null) {
			return null;
		}
		if( idataset == null){
			int[] data;
			double[] dataDbl;
			int index = 0;
			data = new int[imageData.width];
			dataDbl = new double[imageData.height * imageData.width];

			for (int i = 0; i < imageData.height; i++) {
				imageData.getPixels(0, i, imageData.width, data, 0);
				for (int j = 0; j < imageData.width; j++) {
					dataDbl[index++] = mapToLuma(data[j]);
				}
			}
			idataset = new DoubleDataset(dataDbl, imageData.height, imageData.width);
		}
		return idataset;

	}

	/**
	 * Unpack RGB to obtain luma value
	 * 
	 * @param value
	 *            RGB pixel
	 * @return double luma value of a pixel
	 */
	private double mapToLuma(int value) {
		double luma = 0;
		luma += (value & 0xff) * Wb;
		value >>= 8;
		luma += (value & 0xff) * Wg;
		value >>= 8;
		luma += (value & 0xff) * Wr;

		return luma;

	}
}
