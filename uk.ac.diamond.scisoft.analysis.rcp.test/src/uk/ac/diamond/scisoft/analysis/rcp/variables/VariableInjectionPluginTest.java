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

package uk.ac.diamond.scisoft.analysis.rcp.variables;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;
import org.python.pydev.core.IInterpreterInfo;
import org.python.pydev.core.IInterpreterManager;
import org.python.pydev.plugin.PydevPlugin;

public class VariableInjectionPluginTest {

	public List<IInterpreterInfo> collectAllInterpreterInfos() {
		List<IInterpreterInfo> infos = new ArrayList<IInterpreterInfo>();
		IInterpreterManager[] allInterpreterManagers = PydevPlugin.getAllInterpreterManagers();
		for (IInterpreterManager iInterpreterManager : allInterpreterManagers) {
			IInterpreterInfo[] interpreterInfos = iInterpreterManager.getInterpreterInfos();
			infos.addAll(Arrays.asList(interpreterInfos));
		}
		return infos;
	}
	
	public void checkInfo(IInterpreterInfo info) {
		String[] envVariables = info.getEnvVariables();
		Set<String> hashSet = new HashSet<String>();
		hashSet.addAll(Arrays.asList(envVariables));
		
		Assert.assertTrue("SCISOFT_RPC_PORT", hashSet.contains("SCISOFT_RPC_PORT=${scisoft_rpc_port}"));
		Assert.assertTrue("SCISOFT_RMI_PORT", hashSet.contains("SCISOFT_RMI_PORT=${scisoft_rmi_port}"));
		Assert.assertTrue("SCISOFT_RPC_TEMP", hashSet.contains("SCISOFT_RPC_TEMP=${scisoft_rpc_temp}"));
	}
	
	@Test
	public void testVariablesArePresent() throws InterruptedException {
		// A bit of a race condition here, let JythonCreator create a configuration
		// and fail if none shows up within 10 seconds
		int retries = 10;
		List<IInterpreterInfo> infos = collectAllInterpreterInfos();
		while (infos.size() == 0 && retries > 0) {
			retries--;
			Thread.sleep(1000);
			infos = collectAllInterpreterInfos();
		}
		for (IInterpreterInfo iInterpreterInfo : infos) {
			checkInfo(iInterpreterInfo);
		}
		Assert.assertTrue("At least one info found", infos.size() >= 1);
	}

}
