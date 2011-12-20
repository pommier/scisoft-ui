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

/**
 * Class for pooling medium sized coordinate and edge arrays to prevent
 * excessive amount of GC and memory fragmentation and also memory stall 
 * request these pools can be used for frequently used access like plots
 */

public class ArrayPoolUtility {

	private static final int INITSIZE = 4096;
	private static final float EXTENSIONFACTOR = 1.25f;
	private static double[][] doubleDoubleArray = null;
	private static int[][] intIntArray = null;
	
	/**
	 * Constructor 
	 */
	public ArrayPoolUtility()
	{
		
	}
	
	/**
	 * Generate and pool a larger DoubleDoubleArray that can be used
	 * for storing coordinates in 3D space
	 * @param length of the array
	 * @return a doubleDouble array that has at least the desired length
	 */
	
	public static double[][] getDoubleArray(int length) {
	   assert length > 0;
	   if (doubleDoubleArray == null)
	   {
		   doubleDoubleArray = new double[Math.max(INITSIZE,length)][3];
	   } else {
		   if (length > doubleDoubleArray.length)
			   doubleDoubleArray = new double[(int)(length*EXTENSIONFACTOR)][3];
	   }
	   return doubleDoubleArray;
	}
	
	/**
	 * Generate and pool a larger IntIntArray that can be used for
	 * storing edges in 3D space
	 * @param length of the array
	 * @return a intInt array that has at least the desired length
	 */
	
	public static int[][] getIntArray(int length) {
		assert length > 0;
		if (intIntArray == null)
		{
			intIntArray = new int[Math.max(INITSIZE,length)][2];
		} else {
		   if (length > intIntArray.length)
			   intIntArray = new int[(int)(length*EXTENSIONFACTOR)][2];
		}
		return intIntArray;
	}
}
