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

package uk.ac.diamond.sda.navigator.actions;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.IPreferenceChangeListener;
import org.eclipse.core.runtime.preferences.IEclipsePreferences.PreferenceChangeEvent;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.menus.IMenuStateIds;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.menus.UIElement;
import org.eclipse.ui.preferences.ScopedPreferenceStore;
import org.eclipse.ui.internal.IPreferenceConstants;

/**
 * Re-use Editor Command handler in the Project Explorer tool bar
 * Enables the user to set on/off the re-use editor functionality
 */
@SuppressWarnings("restriction")
public class ReUseEditorHandler extends AbstractHandler implements IElementUpdater, IPreferenceChangeListener {

	public static String ID = "uk.ac.diamond.sda.navigator.MultipleEditor";
	private State state;

	@Override
	public final Object execute(ExecutionEvent event) throws ExecutionException {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);

		// update toggled state
		state = event.getCommand().getState(IMenuStateIds.STYLE);

		boolean currentState = (Boolean) state.getValue();
		boolean newState = !currentState;
		state.setValue(newState);

		ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.eclipse.ui.workbench");
		store.setValue(IPreferenceConstants.REUSE_EDITORS_BOOLEAN, newState);
		if (newState)
			store.setValue(IPreferenceConstants.REUSE_EDITORS, 1);
		else
			store.setValue(IPreferenceConstants.REUSE_EDITORS, 10);

		commandService.refreshElements(event.getCommand().getId(), null);

		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public void updateElement(UIElement element, Map parameters) {
		ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
		Command command = commandService.getCommand(ID);
		State state = command.getState(IMenuStateIds.STYLE);
		if (state != null)
			element.setChecked((Boolean) state.getValue());

	}

	private boolean reuseEditors = false;

	//not working
	@Override
	public void preferenceChange(PreferenceChangeEvent event) {
		if (event.getKey().equals(IPreferenceConstants.REUSE_EDITORS_BOOLEAN)) {
			ScopedPreferenceStore store = new ScopedPreferenceStore(InstanceScope.INSTANCE, "org.eclipse.ui.workbench");

			reuseEditors = store.getBoolean(IPreferenceConstants.REUSE_EDITORS_BOOLEAN);
			System.out.println(reuseEditors);
			state.setValue(reuseEditors);

		}
	}

}
