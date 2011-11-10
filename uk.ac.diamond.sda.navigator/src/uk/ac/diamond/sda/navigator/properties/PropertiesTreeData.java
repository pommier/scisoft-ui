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
package uk.ac.diamond.sda.navigator.properties;

import org.eclipse.core.resources.IFile;

/**
 * Provides a simple model of a name=value pair from a *.properties file.
 * 
 */
public class PropertiesTreeData {

	private IFile container;
	private String name;
	private String value;

	/**
	 * Create a property with the given name and value contained by the given file.
	 * 
	 * @param aName
	 *            The name of the property.
	 * @param aValue
	 *            The value of the property.
	 * @param aFile
	 *            The file that defines this property.
	 */
	public PropertiesTreeData(String aName, String aValue, IFile aFile) {
		name = aName;
		value = aValue;
		container = aFile;
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
	 * Return the value of the property in the file.
	 * 
	 * @return The value of the property in the file.
	 */
	public String getValue() {
		return value;
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
		return obj instanceof PropertiesTreeData && ((PropertiesTreeData) obj).getName().equals(name);
	}

	@Override
	public String toString() {
		StringBuffer toString = new StringBuffer(getName()).append(":").append(getValue()); //$NON-NLS-1$
		return toString.toString();
	}

}
