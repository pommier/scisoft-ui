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

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.PlatformUI;
import org.python.pydev.debug.core.PydevDebugPlugin;
import org.python.pydev.debug.newconsole.PydevConsoleConstants;

import uk.ac.diamond.scisoft.analysis.AnalysisRpcServerProvider;
import uk.ac.diamond.scisoft.analysis.RMIServerProvider;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rpc.FlatteningService;
import uk.ac.gda.ui.preferences.LabelFieldEditor;

public class AnalysisRpcAndRmiPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {

	@Override
	public void init(IWorkbench workbench) {
		// setDescription("RMI and Analysis RPC Preferences");
		setPreferenceStore(AnalysisRCPActivator.getDefault().getPreferenceStore());
	}

	@Override
	protected void createFieldEditors() {
		addField(new LabelFieldEditor(
				"Analysis RPC Temporary File Location (blank for default of system temp directory)",
				getFieldEditorParent()));

		addField(new DirectoryFieldEditor(PreferenceConstants.ANALYSIS_RPC_TEMP_FILE_LOCATION, "Directory:",
				getFieldEditorParent()));

		addField(new IntegerFieldEditor(PreferenceConstants.ANALYSIS_RPC_SERVER_PORT,
				"Analysis RPC Port: (0 for default, requires restart)", getFieldEditorParent()));
		addField(new IntegerFieldEditor(PreferenceConstants.RMI_SERVER_PORT,
				"RMI Port: (0 for default, requires restart)", getFieldEditorParent()));

		addField(new LabelFieldEditor(
				"Any changes here also affect scisoftpy connecting to this instance of SDA.\n"
						+ "Therefore, if you are not using the defaults, add this Python snippet to your Python scripts and consoles.\n\n"
						+ "import scisoftpy as dnp\n"
						+ "dnp.rpc.settemplocation(<path>)\n"
						+ "dnp.plot.setremoteport(rmiport=<rmiport>, rpcport=<rpcport>)\n\n"
						+ "If you have those lines in PyDev's 'Initial Interpretter Commands' pressing OK will give you the option of updating them.",
				getFieldEditorParent()));

	}

	private boolean performOk(boolean applying) {
		boolean performOk = super.performOk();
		if (performOk) {
			int runningRmiPort = RMIServerProvider.getInstance().getPort();
			int runningRpcPort = AnalysisRpcServerProvider.getInstance().getPort();
			int newRpcPort = getAnalysisRpcPort();
			int newRmiPort = getRmiPort();
			boolean rpcPortChanged = (newRpcPort == 0 && runningRpcPort != AnalysisRpcServerProvider.DEFAULT_RPCPORT)
					|| (newRpcPort != 0 && runningRpcPort != newRpcPort);
			boolean rmiPortChanged = (newRmiPort == 0 && runningRmiPort != RMIServerProvider.DEFAULT_REGISTRYSERVERPORT)
					|| (newRmiPort != 0 && runningRmiPort != newRmiPort);

			File tempLocation = FlatteningService.getFlattener().getTempLocation();
			String runningTempLocation = tempLocation == null ? null : tempLocation.toString();
			String newTempLocation = getAnalysisRpcTempFileLocation();
			boolean tempChanged = runningTempLocation != newTempLocation
					|| (runningTempLocation != null && !runningTempLocation.equals(newTempLocation));
			FlatteningService.getFlattener().setTempLocation(getAnalysisRpcTempFileLocation());

			if (rpcPortChanged || rmiPortChanged || tempChanged) {
				String currentCmds = PydevDebugPlugin.getDefault().getPreferenceStore()
						.getString(PydevConsoleConstants.INITIAL_INTERPRETER_CMDS);

				CalcDialogReturn calcDialogReturn = calcDialogAndNewCommands(newRpcPort, newRmiPort, newTempLocation,
						currentCmds, true);

				if (calcDialogReturn.update) {
					if (calcDialogReturn.dialog.open() == Window.OK) {
						PydevDebugPlugin
								.getDefault()
								.getPreferenceStore()
								.setValue(PydevConsoleConstants.INITIAL_INTERPRETER_CMDS,
										calcDialogReturn.potentialNewCmds);

						if (applying) {
							MessageDialog warningDialog = new MessageDialog(null, "Settings May Not Be Visible", null,
									"Changed settings may not be reflected in PyDev Interactive "
											+ "Console tab until Preferences Window is closed and re-opened.",
									MessageDialog.WARNING, new String[] { "OK" }, 0);
							warningDialog.open();
						}
					}
				} else {
					calcDialogReturn.dialog.open();
				}
			}

			if (rpcPortChanged || rmiPortChanged) {
				if (new MessageDialog(
						null,
						"Restart Required",
						null,
						"The ports have changed and will not take effect until the workbench is restarted. Restart now?",
						MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1).open() == Window.OK) {
					PlatformUI.getWorkbench().restart();
				}
			}
		}
		return performOk;
	}

	/* package */static class CalcDialogReturn {
		/**
		 * Whether {@link #potentialNewCmds} differs from the input commands
		 */
		boolean update;
		/**
		 * Dialog to show.
		 */
		ChangeInteractiveConsoleDialog dialog;
		/**
		 * New commands
		 */
		String potentialNewCmds;
	}

	/**
	 * Exposed for Unit Test only
	 * <p>
	 * Calculate new commands for Interactive Console Startup, and make a dialog box to prompt user of change
	 * 
	 * @param newRpcPort
	 *            new value for RPC Port
	 * @param newRmiPort
	 *            new value for RMI Port
	 * @param newTempLocation
	 *            new Temp Location
	 * @param currentCmds
	 *            commands to update
	 * @return see {@link CalcDialogReturn}
	 */
	/* package */static CalcDialogReturn calcDialogAndNewCommands(int newRpcPort, int newRmiPort,
			String newTempLocation, String currentCmds, boolean makeDialog) {
		String tempLocPython;
		if (newTempLocation == null) {
			tempLocPython = "None";
		} else {
			tempLocPython = "\"" + newTempLocation.replaceAll("\\\\", "\\\\\\\\") + "\"";
		}
		String newTemp1 = "dnp.rpc.settemplocation(" + tempLocPython + ")";
		String newTemp = newTemp1;
		String newRemotePort = "dnp.plot.setremoteport(rmiport=" + newRmiPort + ", rpcport=" + newRpcPort + ")";

		// Replace set remote port
		Pattern setRemotePortPattern = Pattern.compile("dnp\\.plot\\.setremoteport\\(rmiport=\\d+, rpcport=\\d+\\)");
		Matcher portMatch = setRemotePortPattern.matcher(currentCmds);
		boolean portSettingsFound = portMatch.find();
		String potentialNewCmds = portMatch.replaceAll(newRemotePort);

		// Replace set temp location
		Pattern setTempPattern = Pattern.compile("dnp\\.rpc\\.settemplocation\\(([^\\)]+)\\)");
		Matcher tempMatcher = setTempPattern.matcher(potentialNewCmds);
		boolean tempSettingsFound = tempMatcher.find();
		potentialNewCmds = tempMatcher.replaceAll(newTemp.replaceAll("\\\\", "\\\\\\\\"));

		boolean update = !potentialNewCmds.equals(currentCmds);

		CalcDialogReturn calcDialogReturn = new CalcDialogReturn();
		calcDialogReturn.update = update;
		calcDialogReturn.potentialNewCmds = potentialNewCmds;

		if (makeDialog) {
			ChangeInteractiveConsoleDialog dialog;
			if (!potentialNewCmds.equals(currentCmds)) {
				dialog = new ChangeInteractiveConsoleDialog(null, "Automatically Update", null,
						"Settings have changed that should be reflected in Python and Jython's use of scisoftpy. "
								+ "Automatically update PyDev's 'Initial Interpretter Commands'?",
						MessageDialog.QUESTION, new String[] { "Yes", "No" }, 1);
				dialog.addTextBox("Current:", currentCmds);
				dialog.addTextBox("Updated:", potentialNewCmds);
			} else {
				dialog = new ChangeInteractiveConsoleDialog(null, "Manual Update Required", null,
						"Settings have changed that should be reflected in Python and Jython's use of scisoftpy.",
						MessageDialog.INFORMATION, new String[] { "OK" }, 1);
			}

			if (!portSettingsFound) {
				dialog.addTextBox(
						"No suitable dnp.plot.setremoteport call was found in PyDev's 'Initial Interpretter Commands', "
								+ "use this Python snippet to set your port:", newRemotePort);
			} else if (!update) {
				dialog.addTextBox(
						"PyDev's 'Initial Interpretter Commands' already reflect the new settings for port selection.",
						newRemotePort);
			}

			if (!tempSettingsFound) {
				dialog.addTextBox(
						"No suitable dnp.plot.settemplocation call was found in PyDev's 'Initial Interpretter Commands', "
								+ "use this Python snippet to set your temporary location:", newTemp);
			} else if (!update) {
				dialog.addTextBox(
						"PyDev's 'Initial Interpretter Commands' already reflect the new settings for temporary directory selection.",
						newTemp);
			}

			dialog.addTextBox("Don't forget to update your standalone scripts too!", null);
			calcDialogReturn.dialog = dialog;
		} else {
			calcDialogReturn.dialog = null;
		}
		return calcDialogReturn;
	}

	@Override
	protected void performApply() {
		performOk(true);
	}

	@Override
	public boolean performOk() {
		return performOk(false);
	}

	/**
	 * Retrieve current setting for Analysis RPC Port.
	 * 
	 * @return port number, 0 for use default
	 */
	public static int getAnalysisRpcPort() {
		return AnalysisRCPActivator.getDefault().getPreferenceStore()
				.getInt(PreferenceConstants.ANALYSIS_RPC_SERVER_PORT);
	}

	/**
	 * Retrieve current setting for Analysis RPC Temporary File Location.
	 * 
	 * @return temp file location, or <code>null</code> for use default
	 */
	public static String getAnalysisRpcTempFileLocation() {
		String loc = AnalysisRCPActivator.getDefault().getPreferenceStore()
				.getString(PreferenceConstants.ANALYSIS_RPC_TEMP_FILE_LOCATION);
		if (loc == null || "".equals(loc))
			return null;
		return loc;
	}

	/**
	 * Retrieve current setting for RMI Port.
	 * 
	 * @return port number, 0 for use default
	 */
	public static int getRmiPort() {
		return AnalysisRCPActivator.getDefault().getPreferenceStore().getInt(PreferenceConstants.RMI_SERVER_PORT);
	}
}
