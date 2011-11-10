/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview;

import org.eclipse.ui.IWorkbenchPage;
import org.junit.AfterClass;
import org.junit.BeforeClass;

import uk.ac.diamond.scisoft.analysis.plotserver.IPlotWindowManagerRMI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.RMIPlotWindowManger;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview.MultiPlotViewTestBase.ThreadRunner.ThreadRunnable;

/**
 * Concrete class that tests RMI connection from within same JVM
 */
public class PlotWindowManagerRMIPluginTest extends PlotWindowManagerPluginTestAbstract {

	private static IPlotWindowManagerRMI manager;

	@BeforeClass
	public static void setupRMIClient() {
		manager = RMIPlotWindowManger.getManager();
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
				return manager.openDuplicateView(viewName);
			}

		});
		return (String) threadRunner.run();
	}

	@Override
	public String openView(IWorkbenchPage page, final String viewName) {
		ThreadRunner threadRunner = new ThreadRunner(new ThreadRunnable() {

			@Override
			public Object run() throws Exception {
				return manager.openView(viewName);
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
