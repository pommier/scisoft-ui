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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview;

import org.eclipse.ui.IWorkbenchPage;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.ac.diamond.scisoft.analysis.AnalysisRpcServerProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotWindowManager;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotWindow;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview.MultiPlotViewTestBase.ThreadRunner.ThreadRunnable;
import uk.ac.diamond.scisoft.analysis.rpc.AnalysisRpcException;

/**
 * Concrete class that tests RPC connection from within same JVM
 */
public class PlotWindowManagerRPCPluginTest extends PlotWindowManagerPluginTestAbstract {

	private static IPlotWindowManager manager;

	@BeforeClass
	public static void setupRMIClient() {

		manager = new IPlotWindowManager() {

			@Override
			public String openView(IWorkbenchPage page, String viewName) {
				try {
					return (String) AnalysisRpcServerProvider.getInstance().request(PlotWindow.RPC_SERVICE_NAME,
							"openView", null, viewName);
				} catch (AnalysisRpcException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String openDuplicateView(IWorkbenchPage page, String viewName) {
				try {
					return (String) AnalysisRpcServerProvider.getInstance().request(PlotWindow.RPC_SERVICE_NAME,
							"openDuplicateView", null, viewName);
				} catch (AnalysisRpcException e) {
					throw new RuntimeException(e);
				}
			}

			@Override
			public String[] getOpenViews() {
				try {
					return (String[]) AnalysisRpcServerProvider.getInstance().request(PlotWindow.RPC_SERVICE_NAME,
							"getOpenViews");
				} catch (AnalysisRpcException e) {
					throw new RuntimeException(e);
				}
			}
		};
	}

	@AfterClass
	public static void dropReference() {
		manager = null;
	}

	@Override
	public String openDuplicateView(IWorkbenchPage page, final String viewName) {
		ThreadRunner threadRunner = new ThreadRunner(new ThreadRunnable() {

			@Override
			public Object run() throws Exception {
				return manager.openDuplicateView(null, viewName);
			}

		});
		return (String) threadRunner.run();
	}

	@Override
	public String openView(IWorkbenchPage page, final String viewName) {
		ThreadRunner threadRunner = new ThreadRunner(new ThreadRunnable() {

			@Override
			public Object run() throws Exception {
				return manager.openView(null, viewName);
			}

		});
		return (String) threadRunner.run();
	}

	@Override
	public String[] getOpenViews() {
		ThreadRunner threadRunner = new ThreadRunner(new ThreadRunnable() {

			@Override
			public Object run() throws Exception {
				return manager.getOpenViews();
			}

		});
		return (String[]) threadRunner.run();
	}

}
