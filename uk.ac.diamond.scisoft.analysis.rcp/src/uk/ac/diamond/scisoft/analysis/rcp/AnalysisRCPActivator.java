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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import uk.ac.diamond.scisoft.analysis.AnalysisRpcServerProvider;
import uk.ac.diamond.scisoft.analysis.PlotServer;
import uk.ac.diamond.scisoft.analysis.PlotServerProvider;
import uk.ac.diamond.scisoft.analysis.RMIServerProvider;
import uk.ac.diamond.scisoft.analysis.rcp.preference.AnalysisRpcAndRmiPreferencePage;
import uk.ac.diamond.scisoft.analysis.rpc.FlatteningService;

/**
 * The activator class controls the plug-in life cycle
 */
public class AnalysisRCPActivator extends AbstractUIPlugin {

	/**
	 * The plug-in ID
	 */
	public static final String PLUGIN_ID = "uk.ac.diamond.scisoft.analysis.rcp";

	// The shared instance
	private static AnalysisRCPActivator plugin;

	@SuppressWarnings("rawtypes")
	private ServiceTracker plotServerTracker;

	/**
	 * The constructor
	 */
	public AnalysisRCPActivator() {
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;

		plotServerTracker = new ServiceTracker(context, PlotServer.class.getName(), null);
		plotServerTracker.open();
		PlotServer plotServer = (PlotServer)plotServerTracker.getService();
		if( plotServer != null)
			PlotServerProvider.setPlotServer(plotServer);
		
		AnalysisRpcServerProvider.getInstance().setPort(AnalysisRpcAndRmiPreferencePage.getAnalysisRpcPort());
		RMIServerProvider.getInstance().setPort(AnalysisRpcAndRmiPreferencePage.getRmiPort());
		FlatteningService.getFlattener().setTempLocation(
				AnalysisRpcAndRmiPreferencePage.getAnalysisRpcTempFileLocation());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		PlotServer plotServer = (PlotServer)plotServerTracker.getService();
		if( plotServer != null)
			PlotServerProvider.setPlotServer(null);
		plotServerTracker.close();
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static AnalysisRCPActivator getDefault() {
		return plugin;
	}

	/**
	 * Returns an image descriptor for the image file at the given
	 * plug-in relative path
	 *
	 * @param path the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	/**
	 * Creates the image, this should be disposed later.
	 * @param path
	 * @return Image
	 */
	public static Image getImage(String path) {
		ImageDescriptor des = imageDescriptorFromPlugin(PLUGIN_ID, path);
		return des.createImage();
	}
}

