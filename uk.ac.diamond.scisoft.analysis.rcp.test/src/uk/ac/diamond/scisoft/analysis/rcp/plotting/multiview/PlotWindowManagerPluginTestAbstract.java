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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview;

import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.plotserver.IPlotWindowManagerRMI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotWindowManager;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotWindow;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotWindowManager;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.RMIPlotWindowManger;

/**
 * Integration test for {@link PlotWindowManager}, also tests {@link RMIPlotWindowManger} and PlotWindowManager over
 * RPC. These tests test the public functionality, i.e. that defined by {@link IPlotWindowManager} and its close cousin
 * {@link IPlotWindowManagerRMI}.
 * <p>
 * Each transport method is handled in its own subclass of this abstract test. The unimplemented methods are those from
 * {@link IPlotWindowManager} allowing each derived class to implement in its own way.
 * <p>
 * See {@link PlotWindowManagerTest} for most of the detailed tests, such as making sure that names really are unique,
 * etc
 */
abstract public class PlotWindowManagerPluginTestAbstract extends MultiPlotViewTestBase implements IPlotWindowManager {

	private static int viewCount = 1;
	private String VIEW_NAME;

	@Before
	public void incrementName() {
		// don't end view name in a number otherwise the duplicate one is likely to clash with what incrementName does
		VIEW_NAME = "Plot Window Manager " + viewCount + " View";
		viewCount++;
	}

	@Test
	public void testOpenView() {
		// make sure views aren't already open
		Assert.assertFalse(isMultiplePlotViewReferenced(VIEW_NAME));
		Assert.assertFalse(isPlotWindowManagerHave(VIEW_NAME));

		// open new view
		String openView = openView(null, VIEW_NAME);
		Assert.assertEquals(VIEW_NAME, openView);

		// make sure it is open
		Assert.assertTrue(isMultiplePlotViewReferenced(VIEW_NAME));
		Assert.assertTrue(isPlotWindowManagerHave(VIEW_NAME));
	}

	@Test
	public void testOpenUniqueView() {
		// we don't know the name of the view until we open it
		// so save all the views ahead of time and make sure after
		// the fact
		Set<String> viewRefsBefore = getAllMultiPlotViews();
		String[] openViewsBefore = PlotWindow.getManager().getOpenViews();

		// open new view with unique name
		String uniqueViewName = openView(null, null);

		// make sure it is open
		Assert.assertTrue(isMultiplePlotViewReferenced(uniqueViewName));
		Assert.assertTrue(isPlotWindowManagerHave(uniqueViewName));
		// and make sure it wasn't open before we asked it to be open
		Assert.assertFalse(viewRefsBefore.contains(uniqueViewName));
		Assert.assertTrue(ArrayUtils.indexOf(openViewsBefore, uniqueViewName) == -1);
	}

	@Test
	public void testDuplicateView() {
		// Open a view to duplicate
		testOpenView();

		String dupViewName = openDuplicateView(null, VIEW_NAME);
		Assert.assertFalse(VIEW_NAME.equals(dupViewName));

		// make sure original and dup is now open
		Assert.assertTrue(isMultiplePlotViewReferenced(VIEW_NAME));
		Assert.assertTrue(isPlotWindowManagerHave(VIEW_NAME));
		Assert.assertTrue(isMultiplePlotViewReferenced(dupViewName));
		Assert.assertTrue(isPlotWindowManagerHave(dupViewName));
	}

	@Test
	public void testGetOpenViews() {
		// make sure the view is not reported as open
		String[] openViewsBefore = getOpenViews();
		Assert.assertTrue(ArrayUtils.indexOf(openViewsBefore, VIEW_NAME) == -1);

		// Open an additional view
		testOpenView();

		// make sure the view is now reported as open
		String[] openViewsAfter = getOpenViews();
		Assert.assertTrue(ArrayUtils.indexOf(openViewsAfter, VIEW_NAME) >= 0);

	}

}
