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
