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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.ScaleType;

/**
 * Utility class for different type of value scaling operations
 */

public class ScalingUtility {

	private static boolean smallLogFlag = true;
	private static final double LOG2 = Math.log10(2.0);
	public static void setSmallLogFlag(boolean flag)
	{
		smallLogFlag = flag;
	}
	
	/**
	 * @param in
	 *            input z value
	 * @param currentScaling
	 *            current scaling type
	 * @return scaled value
	 */
	public static double valueScaler(double in, ScaleType currentScaling) {
		double out = in;
		switch (currentScaling) {
			case LINEAR:
			break;
			case LOG2:
				if (smallLogFlag)
				{
					out = Math.log10(in) / LOG2;
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag) in = -in;
					if (in < 2.0) {
						in += (2.0 -in) / 2.0; 
					}
					out = Math.log10(in) / LOG2;
					out = negFlag ? (-out):out;
				}
			break;
			case LOG10:
				if (smallLogFlag)
				{
					out = Math.log10(in);
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag) in = -in;
					if (in < 10.0) {
						in += (10.0 - in) / 10.0;
					}
					out = Math.log10(in);
					out = negFlag ? (-out):out;
				}
			break;
			case LN:
				if (smallLogFlag)
				{
					out = Math.log(in);
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag) in = -in;
					if (in < Math.E) {
						in += (Math.E - in) / Math.E;
					}
					out = Math.log(in);
					out = negFlag ? (-out):out;
				}
			break;
		}
		return out;
	}

	/**
	 * Compute the inverse operation of the axis scaling
	 * 
	 * @param in
	 *            scaled value
	 * @param currentScaling
	 *            current scaling mode
	 * @return inverse scaled value
	 */

	public static double inverseScaler(double in, ScaleType currentScaling) {
		double out = in;
		switch (currentScaling) {
			case LINEAR:
			break;
			case LOG2:
				if (smallLogFlag)
				{
					out = Math.pow(2.0, in);
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag)
						in = -in;
					if (in < 1.0) {
						out = (Math.pow(2,in+1) - 2.0);
					} else {
						out = Math.pow(2,in);
					}
					out = negFlag?(-out):out;
				}
			break;
			case LOG10:
				if (smallLogFlag)
				{
					out = Math.pow(10.0, in);					
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag)
						in = -in;
					if (in < 1.0) {
						out = (Math.pow(10,in+1) - 10.0) / 9.0;
					} else {
						out = Math.pow(10,in);
					}
					out = negFlag?(-out):out;
				}
			break;
			case LN:
				if (smallLogFlag)
				{
					out = Math.exp(in);
				} else {
					boolean negFlag = (in < 0.0);
					if (negFlag)
						in = -in;
					if (in < 1.0) {
						out = (Math.exp(in+1) - Math.E) / (Math.E-1.0);						
					} else {
						out = Math.exp(in);
					}
					out = negFlag?(-out):out;	
				}
			break;
		}
		return out;
	}
	
}
