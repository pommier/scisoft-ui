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

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;

/**
 * Contains the constants used by the Jython Terminal configuration 
 */
public class PlotViewConstants {

	protected static final String CONFIGURATION_EXTENSION_POINT_ID = AnalysisRCPActivator.PLUGIN_ID + ".plotView"; //$NON-NLS-1$
	protected static final String PLOT_CONFIG = "plot_config"; //$NON-NLS-1$
	protected static final String ID = "id"; //$NON-NLS-1$
	protected static final String NAME = "name"; //$NON-NLS-1$
}
