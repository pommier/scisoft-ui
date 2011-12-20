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

package uk.ac.diamond.scisoft.analysis.rcp.histogram;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions.AbstractMapFunction;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.ScalingUtility;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.ScaleType;


/**
 *
 */
public class ColourLookupTable {

	private static final int LOOKUPTABLESIZE = 256;

	/**
	 * Returns a full DataSet to ImageData map 
	 * @param data DataSet that should be converted
	 * @param redFunc red channel mapping function
	 * @param greenFunc green channel mapping function
	 * @param blueFunc blue channel mapping function
	 * @param alphaFunc alpha channel mapping function
	 * @param inverseRed invert red channel
	 * @param inverseGreen invert green channel
	 * @param inverseBlue invert blue channel
	 * @param inverseAlpha invert alpha channel
	 * @param minValue minimum value
	 * @param maxValue maximum value
	 * @param useLogarithmic should the colourtable be logarithmic
	 * @return the result ImageData 
	 */
	static public ColourImageData generateColourTable(IDataset data,
											    AbstractMapFunction redFunc,
											    AbstractMapFunction greenFunc,
											    AbstractMapFunction blueFunc,
											    AbstractMapFunction alphaFunc,
												boolean inverseRed,
												boolean inverseGreen,
												boolean inverseBlue,
												boolean inverseAlpha,
												double minValue,
												double maxValue,
												boolean useLogarithmic)
	{
		int width = data.getShape()[1];
		int height = data.getShape()[0];
		ColourImageData returnImage = new ColourImageData(width,height);
		if (useLogarithmic)
		{
			ScalingUtility.setSmallLogFlag(false);
			minValue = ScalingUtility.valueScaler(minValue,ScaleType.LN);
			maxValue = ScalingUtility.valueScaler(maxValue, ScaleType.LN);
		}
		for (int y = 0; y < height; y++)
		{
			for (int x = 0; x < width; x++)
			{
				
				double value = data.getDouble(y, x);
				if (useLogarithmic)
					value = ScalingUtility.valueScaler(value, ScaleType.LN);
				value = (value - minValue) / (maxValue-minValue);
				value = Math.min(value,1.0);
				value = Math.max(0.0, value);
				short red = redFunc.mapToByte(value);
				if (inverseRed)
					red = (short)(255 - red);
				short green = greenFunc.mapToByte(value);
			    if (inverseGreen)
			    	green = (short)(255 - green);
			    short blue = blueFunc.mapToByte(value);
			    if (inverseBlue)
			    	blue = (short)(255 - blue);
			    short alpha = alphaFunc.mapToByte(value);
			    if (inverseAlpha)
			    	alpha = (short)(255 - alpha);
				int imageValue =  ((alpha&0xff) << 24)+((red&0xff) << 16)+((green&0xff) << 8)+(blue&0xff);
				returnImage.set(imageValue, x+y*width);			    
			}
		}
		return returnImage;
	}

	/**
	 * Return a lookup Table encoded into a ImageData map
	 * @param redFunc red channel mapping function
	 * @param greenFunc green channel mapping function
	 * @param blueFunc blue channel mapping function
	 * @param alphaFunc alpha channel mapping function
	 * @param inverseRed invert red channel
	 * @param inverseGreen invert green channel
	 * @param inverseBlue invert blue channel
	 * @param inverseAlph invert alpha channel
	 * @return the result lookup table
	 */
	static public ColourImageData generateColourLookupTable(AbstractMapFunction redFunc,
													  AbstractMapFunction greenFunc,
													  AbstractMapFunction blueFunc,
													  AbstractMapFunction alphaFunc,
													  boolean inverseRed,
													  boolean inverseGreen,
													  boolean inverseBlue,
													  boolean inverseAlph)
	{
		ColourImageData returnData = new ColourImageData(LOOKUPTABLESIZE,1);
		for (int x = 0; x < LOOKUPTABLESIZE; x++)
		{
			double currentValue = (double)x/(double)LOOKUPTABLESIZE;
			short red = redFunc.mapToByte(currentValue);
			if (inverseRed)
				red = (short) (255 - red);
			short green = greenFunc.mapToByte(currentValue);
			if (inverseGreen)
				green = (short)(255 - green);
			short blue = blueFunc.mapToByte(currentValue);
			if (inverseBlue)
				blue = (short)(255 - blue);
			short alpha = alphaFunc.mapToByte(currentValue);
			if (inverseAlph)
				alpha = (short)(255 - alpha);
			int value =  ((alpha&0xff) << 24)+((red&0xff) << 16)+((green&0xff) << 8)+(blue&0xff);
			returnData.set(value, x);
		}
		return returnData;
	}
}
