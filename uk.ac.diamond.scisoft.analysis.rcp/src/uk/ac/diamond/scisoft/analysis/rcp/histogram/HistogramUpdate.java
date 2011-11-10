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

package uk.ac.diamond.scisoft.analysis.rcp.histogram;

import org.eclipse.jface.viewers.ISelection;

import uk.ac.diamond.scisoft.analysis.rcp.histogram.mapfunctions.AbstractMapFunction;

/**
 *
 */
public class HistogramUpdate implements ISelection {

	
	private AbstractMapFunction redFunc;
	private AbstractMapFunction greenFunc;
	private AbstractMapFunction blueFunc;
	private AbstractMapFunction alphaFunc;
	private boolean invertRed;
	private boolean invertGreen;
	private boolean invertBlue;
	private boolean invertAlpha;
	private double  minValue;
	private double  maxValue;
	
	/**
	 * Constructor of a HistogramUpdate
	 * @param redFunc red channel map function
	 * @param greenFunc green channel map function
	 * @param blueFunc blue channel map function
	 * @param alphaFunc alpha channel map function
	 * @param invertRed invert red channel
	 * @param invertGreen invert green channel
	 * @param invertBlue  invert blue channel
	 * @param invertAlpha invert alpha channel
	 * @param minValue minimum value
	 * @param maxValue maximum value
	 */
		
	public HistogramUpdate(AbstractMapFunction redFunc,
						   AbstractMapFunction greenFunc,
						   AbstractMapFunction blueFunc,
						   AbstractMapFunction alphaFunc,
						   boolean invertRed,
						   boolean invertGreen,
						   boolean invertBlue,
						   boolean invertAlpha,
						   double minValue,
						   double maxValue)
	{
		this.redFunc = redFunc;
		this.greenFunc = greenFunc;
		this.blueFunc = blueFunc;
		this.alphaFunc = alphaFunc;
		this.invertRed = invertRed;
		this.invertGreen = invertGreen;
		this.invertBlue = invertBlue;
		this.invertAlpha = invertAlpha;
		this.minValue = minValue;
		this.maxValue = maxValue;
	}
	
	@Override
	public boolean isEmpty() {
		return false;
	}

	/**
	 * Get the green map function
	 * @return the green map function
	 */
	public AbstractMapFunction getGreenMapFunction()
	{
		return greenFunc;
	}
	
	/**
	 * Get the red map function
	 * @return the red map function
	 */
	public AbstractMapFunction getRedMapFunction()
	{
		return redFunc;
	}
	
	/**
	 * Get the blue map function
	 * @return the blue map function
	 */
	public AbstractMapFunction getBlueMapFunction()
	{
		return blueFunc;
	}
	
	/**
	 * Get the alpha map function
	 * @return the alpha map function
	 */
	
	public AbstractMapFunction getAlphaMapFunction()
	{
		return alphaFunc;
	}
	
	/**
	 * Determine if red channel should be inverted
	 * @return true if yes otherwise false
	 */
	public boolean inverseRed()
	{
		return invertRed;
	}
	
	/**
	 * Determine if green channel should be inverted 
	 * @return true if yes otherwise false
	 */
	public boolean inverseGreen()
	{
		return invertGreen;
	}
	
	/**
	 * Determine if blue channel should be inverted
	 * @return true if yes otherwise false
	 */
	public boolean inverseBlue()
	{
		return invertBlue;
	}
	
	/**
	 * Determine if alpha channel should be inverted
	 * @return true if yes otherwise false
	 */
	public boolean inverseAlpha()
	{
		return invertAlpha;
	}
	
	/**
	 * Get the minimum value
	 * @return the minimum value
	 */
	public double getMinValue()
	{
		return minValue;
	}
	
	/**
	 * Get the maximum value
	 * @return the maximum value
	 */
	
	public double getMaxValue()
	{
		return maxValue;
	}
	
}
