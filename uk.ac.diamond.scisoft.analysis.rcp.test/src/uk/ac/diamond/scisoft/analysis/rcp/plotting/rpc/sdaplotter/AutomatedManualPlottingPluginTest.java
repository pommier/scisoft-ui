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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.rpc.sdaplotter;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.PlotServerProvider;
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
		runPythonFile("manual_plot_test_over_rpc.py", true);
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 RPC Python") != -1);
	}

	@Test
	public void testManualPlotTestPython() throws Exception {
		// Launch the AnalysisRpc server that receives our requests and sends them back to us
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 DNP Python") == -1);
		runPythonFile("manual_plot_test.py", true);
		Assert.assertTrue(ArrayUtils.indexOf(PlotServerProvider.getPlotServer().getGuiNames(), "Plot 1 DNP Python") != -1);
	}

}
