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

	private ServiceTracker plotServerTracker;

	/**
	 * The constructor
	 */
	public AnalysisRCPActivator() {
	}

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

