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

package uk.ac.diamond.scisoft.analysis.rcp.preference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static uk.ac.diamond.scisoft.analysis.rcp.preference.AnalysisRpcAndRmiPreferencePage.calcDialogAndNewCommands;

import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.rcp.preference.AnalysisRpcAndRmiPreferencePage.CalcDialogReturn;

public class AnalysisRpcAndRmiPreferencePageTest {

	@Test
	public void testNoReplacementsFound() {
		CalcDialogReturn ret = calcDialogAndNewCommands(0, 0, null, "", false);
		assertEquals("", ret.potentialNewCmds);
		assertEquals(false, ret.update);

	}

	@Test
	public void testBothFoundNothingToDo() {
		String input = "dnp.rpc.settemplocation(None)\n" + "dnp.plot.setremoteport(rmiport=0, rpcport=0)";
		CalcDialogReturn ret = calcDialogAndNewCommands(0, 0, null, input, false);
		assertEquals(input, ret.potentialNewCmds);
		assertFalse(ret.update);
	}

	@Test
	public void testBothFoundChangeBoth() {
		String input = "dnp.rpc.settemplocation('/tmp')\n" + "dnp.plot.setremoteport(rmiport=1, rpcport=2)";
		String output = "dnp.rpc.settemplocation(None)\n" + "dnp.plot.setremoteport(rmiport=0, rpcport=0)";
		CalcDialogReturn ret = calcDialogAndNewCommands(0, 0, null, input, false);
		assertEquals(output, ret.potentialNewCmds);
		assertTrue(ret.update);
	}

}
