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

package uk.ac.diamond.scisoft.analysis.rcp.views;


import java.util.HashMap;
import java.util.Set;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.Platform;

/**
 * @author tjs15132
 *
 */
public class PlotViewRegistry {

	/** 
	 * This class is used to store an Individual Config element.
	 * It also defines the default values.
	 */
	protected class IndividualConfig {
		public String id; /* No Default, ID is required */
		public String name = "Plot View";
	}

    // static reference to the Singleton Configuration Manager instance.
	private static PlotViewRegistry instance;
	// a set of all the configs, keyed by ID.  
	private HashMap<String, IndividualConfig> configs;
	

	
	private PlotViewRegistry() {
		initializeConfig();
	}
	
	/**
	 * Create and get the singleton object.
	 * @return the singleton
	 */
	public static PlotViewRegistry getDefault() {
		if(instance == null) {
			instance = new PlotViewRegistry();
		}
		return instance;
	}	
	
	/**
	 * Discover and create all the categories based
	 * on the configuration extension point.
	 */
	private void initializeConfig() {
		IExtension[] extensions = Platform. getExtensionRegistry().getExtensionPoint(PlotViewConstants.CONFIGURATION_EXTENSION_POINT_ID).getExtensions();
		configs = new HashMap<String, IndividualConfig>();
		for(int i=0; i<extensions.length; i++) {
			IExtension extension = extensions[i];
			IConfigurationElement[] configElements = extension.getConfigurationElements();	
			for(int j=0; j<configElements.length; j++) {
				IConfigurationElement config = configElements[j];
				if (config.getName().equals(PlotViewConstants.PLOT_CONFIG)) {
					IndividualConfig plotConfig = new IndividualConfig();

					String id = config.getAttribute(PlotViewConstants.ID);
					configs.put(id, plotConfig);
					
					plotConfig.id = id;
					
					String name = config.getAttribute(PlotViewConstants.NAME);
					if (name != null) {
						plotConfig.name = name;
					}
				}
			}
		}	
	}


	/**
	 * @return Returns the configs.
	 */
	public HashMap<String, IndividualConfig> getConfigs() {
		return configs;
	}
	
	/**
	 * Obtain a list of all the IDs that are associated with the jython terminal view
	 * @return array of terminal config ids
	 */
	public String[] getConfigIds() {
		Set<String> keySet = configs.keySet();
		String[] array = keySet.toArray(new String[keySet.size()]);
		return array;
	}
}

