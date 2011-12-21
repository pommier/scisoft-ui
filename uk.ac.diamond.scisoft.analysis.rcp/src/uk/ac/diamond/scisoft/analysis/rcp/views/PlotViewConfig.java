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



/**
 * Contains all the configuration elements for a JythonTerminalView
 */
public class PlotViewConfig {
	private String name;
	
	/**
	 * Create a config for this Jython Terminal Configuration
	 * @param id The ID of the view that the config is for
	 */
	public PlotViewConfig(String id) {
		// cache the name as it isn't configurable by the preferences
		name = PlotViewRegistry.getDefault().getConfigs().get(id).name;
	
	}

	/**
	 * @return the name of the view
	 */
	public String getName() {
		return name;
	}


}
