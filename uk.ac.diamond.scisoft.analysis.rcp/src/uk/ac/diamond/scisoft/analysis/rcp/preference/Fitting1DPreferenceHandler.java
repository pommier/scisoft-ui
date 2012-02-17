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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PreferencesUtil;

public class Fitting1DPreferenceHandler extends AbstractHandler implements IHandler {

	private static final String FITTING1D_PAGE_ID = "uk.ac.diamond.scisoft.analysis.rcp.fitting1DPreferencePage";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		PreferenceDialog pref = PreferencesUtil.createPreferenceDialogOn(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(), FITTING1D_PAGE_ID, null, null);	
		if (pref != null) pref.open();
		return Boolean.TRUE;
	}
}
