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

package uk.ac.diamond.scisoft.analysis.rcp.preference;

import static org.junit.Assert.assertEquals;
import static uk.ac.diamond.scisoft.analysis.rcp.preference.AnalysisRpcAndRmiPreferencePage.calcDialogAndNewCommands;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import uk.ac.diamond.scisoft.analysis.rcp.preference.AnalysisRpcAndRmiPreferencePage.CalcDialogReturn;

/**
 * Getting all the back slashes right was a real nuisance, hopefully this test will help verify
 */
@RunWith(Parameterized.class)
public class AnalysisRpcAndRmiPreferencePageWinPathTest {

	private String input;
	private String newval;
	private String output;

	@Parameters
	public static Collection<Object[]> configs() {
		String[] inputs = new String[] { "'c:\\\\'", "None", "\"C:\\\\temp\\\\\"" };
		String[] newvals = new String[] { "c:\\", null, "C:\\temp\\" };
		String[] outputs = new String[] { "\"c:\\\\\"", "None", "\"C:\\\\temp\\\\\"" };
		List<Object[]> params = new LinkedList<Object[]>();
		for (int i = 0; i < inputs.length; i++) {
			for (int j = 0; j < outputs.length; j++) {
				params.add(new Object[] { inputs[i], newvals[j], outputs[j] });
			}
		}
		return params;
	}

	public AnalysisRpcAndRmiPreferencePageWinPathTest(String input, String newval, String output) {
		this.input = "dnp.rpc.settemplocation(" + input + ")";
		this.newval = newval;
		this.output = "dnp.rpc.settemplocation(" + output + ")";
	}

	@Test
	public void test() {
		CalcDialogReturn ret = calcDialogAndNewCommands(0, 0, newval, input, false);
		assertEquals(output, ret.potentialNewCmds);
	}

}
