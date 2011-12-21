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
