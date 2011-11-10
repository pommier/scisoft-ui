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

package uk.ac.diamond.scisoft.analysis.rcp.wizards;


import junit.framework.Assert;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.utils.PluginTestHelpers;

public class DataWizardPluginTest {

	@Before
	public void setUp(){
		PluginTestHelpers.waitForJobs();
	}

	@After
	public void tearDown() {
		PluginTestHelpers.waitForJobs();
	}
	
	@Test
	public final void testWizard() throws Exception {
		
		final IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		DataWizard wizard = new DataWizard();
		wizard.init(window.getWorkbench(), StructuredSelection.EMPTY);
		WizardDialog dialog = new WizardDialog(window.getShell(), wizard);
		int open = dialog.open();
		{
			//open again to check if setting tranferred across instantiations
			DataWizard wizard1 = new DataWizard();
			wizard1.init(window.getWorkbench(), StructuredSelection.EMPTY);
			WizardDialog dialog1 = new WizardDialog(window.getShell(), wizard1);
			dialog1.open();
		}
		if ( open == Window.OK){
			PluginTestHelpers.delay(1000); //give time for project to be created
			IDialogSettings settings = wizard.getDialogSettings();
			final IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();

			final IProject project = root.getProject(settings.get(DataWizard.DIALOG_SETTING_KEY_PROJECT));
			project.open(null);
			IFolder folder = project.getFolder(settings.get(DataWizard.DIALOG_SETTING_KEY_FOLDER));
			IPath location = folder.getLocation();
			Assert.assertEquals(settings.get(DataWizard.DIALOG_SETTING_KEY_DIRECTORY),location.toString());
		}
	}
}
