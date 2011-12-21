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

