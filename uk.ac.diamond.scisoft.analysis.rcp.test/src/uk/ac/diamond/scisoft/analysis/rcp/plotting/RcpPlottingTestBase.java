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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;

import uk.ac.diamond.scisoft.analysis.PlotServer;
import uk.ac.diamond.scisoft.analysis.PlotServerProvider;
import uk.ac.diamond.scisoft.analysis.PlotService;
import uk.ac.diamond.scisoft.analysis.PlotServiceProvider;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;
import uk.ac.diamond.scisoft.analysis.rcp.views.PlotView;
import uk.ac.diamond.scisoft.analysis.rcp.views.SidePlotView;
import uk.ac.gda.common.rcp.util.EclipseUtils;

abstract public class RcpPlottingTestBase {

	// try and clean up the open plot view windows that are left around
	// and clear out the plot server contents of data
	private static final Set<String> viewIDsToClose = new HashSet<String>();
	static {
		viewIDsToClose.add(PlotView.PLOT_VIEW_MULTIPLE_ID);
		viewIDsToClose.add(SidePlotView.ID);
		viewIDsToClose.add(HistogramView.ID);
	}

	// Make sure there is a plot server and service available before running this test
	@BeforeClass
	public static void beforeClass() throws Exception {
		// give everything a chance to catch up, should return immediately if there is nothing to do
		EclipseUtils.delay(30000, true);

		PlotServer plotServer = PlotServerProvider.getPlotServer();
		Assert.assertNotNull(plotServer);

		PlotService plotService = PlotServiceProvider.getPlotService();
		Assert.assertNotNull(plotService);

		IPlotWindowManager manager = PlotWindow.getManager();
		Assert.assertNotNull(manager);

		clearPlotServer();
		closeAllPlotRelatedViews();

		// give everything a chance to catch up, should return immediately if there is nothing to do
		EclipseUtils.delay(30000, true);
	}

	@AfterClass
	public static void afterClass() throws Exception {
		closeAllPlotRelatedViews();
		clearPlotServer();

		// give everything a chance to catch up, should return immediately if there is nothing to do
		EclipseUtils.delay(30000, true);
	}

	/**
	 * Remote all data from plot server.
	 * <p>
	 * There is actually no way to do this fully today, ie. all the guiNames are left behind. Instead set DataBean and
	 * GuiBean to new empty ones.
	 * 
	 * @throws Exception
	 */
	public static void clearPlotServer() throws Exception {
		PlotServer plotServer = PlotServerProvider.getPlotServer();
		String[] guiNames = plotServer.getGuiNames();
		for (String name : guiNames) {
			plotServer.setData(name, new DataBean());
			plotServer.updateGui(name, new GuiBean());
		}
	}

	/**
	 * Close all Plot related views
	 */
	public static void closeAllPlotRelatedViews() {
		IViewReference[] viewReferences = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
				.getViewReferences();
		for (IViewReference ref : viewReferences) {
			if (viewIDsToClose.contains(ref.getId())) {
				ref.getPage().hideView(ref);
			}
		}
	}

	@Before
	public void before() {
		while (Job.getJobManager().currentJob() != null)
			EclipseUtils.delay(1000);
		// give everything a chance to catch up, should return immediately if there is nothing to do
		EclipseUtils.delay(5000, true);
	}

	@After
	public void after() {
		// give everything a chance to catch up, should return immediately if there is nothing to do
		EclipseUtils.delay(5000, true);
	}

}
