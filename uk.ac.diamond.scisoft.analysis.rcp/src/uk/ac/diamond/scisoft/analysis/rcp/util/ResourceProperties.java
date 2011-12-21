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

package uk.ac.diamond.scisoft.analysis.rcp.util;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.eclipse.swt.widgets.Composite;

public class ResourceProperties {
	private static ResourceBundle resourceBundle = ResourceBundle.getBundle("sda_resource"); //$NON-NLS-1$

	/**
	 * Creates an instance of a ControlExample embedded inside the supplied parent Composite.
	 * 
	 * @param parent
	 *            the container of the example
	 */
	@SuppressWarnings("unused")
	public ResourceProperties(Composite parent) {
		initResources();
	}

	/**
	 * Gets a string from the resource bundle. We don't want to crash because of a missing String. Returns the key if
	 * not found.
	 */
	public static String getResourceString(String key) {
		try {
			return resourceBundle.getString(key);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Gets a string from the resource bundle and binds it with the given arguments. If the key is not found, return the
	 * key.
	 */
	static String getResourceString(String key, Object[] args) {
		try {
			return MessageFormat.format(getResourceString(key), args);
		} catch (MissingResourceException e) {
			return key;
		} catch (NullPointerException e) {
			return "!" + key + "!"; //$NON-NLS-1$ //$NON-NLS-2$
		}
	}

	/**
	 * Loads the resources
	 */
	void initResources() {
		String error = (resourceBundle != null) ? getResourceString("error.CouldNotLoadResources")
				: "Unable to load resources"; //$NON-NLS-1$
		throw new RuntimeException(error);
	}
}
