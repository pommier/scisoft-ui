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
package uk.ac.diamond.scisoft.analysis.rcp.navigator.srs;

import org.eclipse.core.resources.IFile;

import uk.ac.diamond.scisoft.analysis.io.DataHolder;

/**
 * Provides an SRS model of a name,min,max and class set from a *.dat file.
 */
public class SRSTreeData {

	private IFile container;
	private String name;
	private String minValue;
	private String maxValue;
	private String classValue;

	protected DataHolder data;

	/**
	 * Create a property with the given name and value contained by the given file.
	 * 
	 * @param name
	 *            The name of the property.
	 * @param minValue
	 *            The minimum value of the property.
	 * @param maxValue
	 *            The maximum value of the property.
	 * @param classValue
	 *            The class value of the property.
	 * @param file
	 *            The file that defines this property.
	 */
	public SRSTreeData(String name, String minValue, String maxValue, String classValue, IFile file) {
		this.name = name;
		this.minValue = minValue;
		this.maxValue = maxValue;
		this.classValue = classValue;
		this.container = file;
	}

	/**
	 * The name of this property.
	 * 
	 * @return The name of this property.
	 */
	public String getName() {
		return name;
	}

	/**
	 * Return the minimum value of the property in the file.
	 * 
	 * @return The minimum value of the property in the file.
	 */
	public String getMinValue() {
		return minValue;
	}

	/**
	 * Return the maximum value of the property in the file.
	 * 
	 * @return The maximum value of the property in the file.
	 */
	public String getMaxValue() {
		return maxValue;
	}

	/**
	 * Return the class value of the property in the file.
	 * 
	 * @return The class value of the property in the file.
	 */
	public String getClassValue() {
		return classValue;
	}

	/**
	 * The IFile that defines this property.
	 * 
	 * @return The IFile that defines this property.
	 */
	public IFile getFile() {
		return container;
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof SRSTreeData && ((SRSTreeData) obj).getName().equals(name);
	}

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer(getName()).append(":").append(getMinValue()) //$NON-NLS-1$
				.append(":").append(getMaxValue()) //$NON-NLS-1$
				.append(":").append(getClassValue()); //$NON-NLS-1$
		return toString.toString();
	}
}
