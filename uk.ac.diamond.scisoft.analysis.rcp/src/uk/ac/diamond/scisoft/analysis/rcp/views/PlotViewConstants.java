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
