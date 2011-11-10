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
