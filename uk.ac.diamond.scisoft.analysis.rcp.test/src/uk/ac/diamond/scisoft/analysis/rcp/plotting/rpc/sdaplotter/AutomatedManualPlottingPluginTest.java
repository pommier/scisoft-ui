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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.rpc.sdaplotter;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.PlotServerProvider;
import uk.ac.diamond.scisoft.analysis.PythonHelper;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.RcpPlottingTestBase;

/**
 * There are a couple of manual plotting tests in python test. Runs these automatically here. This doesn't remove
 * requirement to run them manually because this test is only able to check for exceptions and unexpected output, not
 * for everything.
 */
public class AutomatedManualPlottingPluginTest extends RcpPlottingTestBase {

	@Test
	public void testManualPlotTestOverRpcPython() throws Exception {
		// Launch the AnalysisRpc server that receives our requests and sends them back to us
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 RPC Python") == -1);
		PythonHelper.runPythonFile("../uk.ac.diamond.scisoft.python/test/scisoftpy/manual_plot_test_over_rpc.py", true);
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 RPC Python") != -1);
	}

	@Test
	public void testManualPlotTestPython() throws Exception {
		// Launch the AnalysisRpc server that receives our requests and sends them back to us
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 DNP Python") == -1);
		PythonHelper.runPythonFile("../uk.ac.diamond.scisoft.python/test/scisoftpy/manual_plot_test.py", true);
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 DNP Python") != -1);
	}

}
