/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.util.Arrays;

import org.apache.commons.lang.ArrayUtils;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;

/**
 * Class to hold an axis name, order (or primacy) and dataset
 * for a specified dimension of a dataset
 */
public class AxisChoice {
	private ILazyDataset values = null;
	private int primary;   // possible order in a list of choices (0 signifies leave to end of list) 
	private int dimension; // which dimension does this axis represent for chosen dataset
	private int[] axes;    // list of dimensions needed to retrieve axis value
	private int length;    // length of axis

	/**
	 * @param values
	 */
	public AxisChoice(ILazyDataset values) {
		this(values, 0);
	}

	/**
	 * @param values
	 * @param primary
	 */
	public AxisChoice(ILazyDataset values, int primary) {
		setValues(values);
		setPrimary(primary);
	}

	/**
	 * @return Returns the name.
	 */
	public String getName() {
		return values != null ? values.getName(): null;
	}

	/**
	 * @param values The values to set.
	 */
	public void setValues(ILazyDataset values) {
		this.values = values;
	}

	/**
	 * @return Returns the values.
	 */
	public AbstractDataset getValues() {
		try {
			return DatasetUtils.convertToAbstractDataset(values.getSlice());
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * @param primary The order to set.
	 */
	public void setPrimary(int primary) {
		this.primary = primary;
	}

	/**
	 * @return Returns the order.
	 */
	public int getPrimary() {
		return primary;
	}

	/**
	 * @param axes list of referenced axis.
	 */
	public void setDimension(int[] axes) {
		setDimension(axes, axes[axes.length - 1]);
	}
	
	/**
	 * @param axes list of referenced data dimensions.
	 * @param dimension The dimension to set.
	 */
	public void setDimension(int[] axes, int dimension) {
		int idx = ArrayUtils.indexOf(axes, dimension);
		if (idx == -1) {
			throw new IllegalArgumentException("Invalid dimension: Specified dimension is not in axis list.");
		}
		this.axes = axes;
		this.dimension = dimension;
		if (values != null) {
//			if (values.getRank() != axes.length)
//				throw new IllegalArgumentException("Invalid axes: Axes attribute does not match axis data shape.");
			length = values.getShape()[idx];
		}
	}

	/**
	 * @return Returns the dimension.
	 */
	public int getDimension() {
		return dimension;
	}

	/**
	 * @return Returns the axes.
	 */
	public int[] getAxes() {
		return axes;
	}

	/**
	 * @param obj
	 * @return true if name matches axis name
	 */
	@Override
	public boolean equals(Object obj) {
		if (values == null)
			return false;
		
		if (obj instanceof String)
			return values.getName().equals(obj);
		
		if (obj instanceof AxisChoice) {
			if (!values.getName().equals(((AxisChoice) obj).getValues().getName()))
				return false;
			if (!Arrays.equals(getAxes(), ((AxisChoice) obj).getAxes()))
				return false;
			if (!values.equals(((AxisChoice) obj).getValues()))
				return false;
			
			return true;
		}
		
		return false;
	}

	/**
	 * @return Returns the length.
	 */
	public int getLength() {
		return length;
	}

	@Override
	public int hashCode() {
		int hash = length;
		for (int d : axes)
			hash = hash * 17 + d;
				
		String name = values.getName();
		if (name != null)
			hash = hash * 17 + name.hashCode();
		else 
			hash *= 17;
		
		hash = hash * 17 + values.hashCode();
		return hash;
	}

	/**
	 * Clone everything but values
	 */
	@Override
	protected AxisChoice clone() throws CloneNotSupportedException {
		AxisChoice choice = new AxisChoice(values, primary);
		if (axes != null)
			choice.setDimension(axes.clone(), dimension);
		choice.setPrimary(primary);
		return choice;
	}
}
