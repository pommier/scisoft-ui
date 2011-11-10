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

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.results.navigator.GDADataNature;
import uk.ac.gda.ui.utils.ProjectUtils;

/**
 * This is a sample new wizard. Its role is to create a new file 
 * resource in the provided container. If the container resource
 * (a folder or a project) is selected in the workspace 
 * when the wizard is opened, it will accept it as the target
 * container. The wizard creates one file with the extension
 * "png". If a sample multi-page editor (also available
 * as a template) is registered for the same extension, it will
 * be able to open it.
 */

public class DataWizard extends Wizard implements INewWizard {
	private static final String DATA_WIZARD = "DataWizard";
	public static final String DIALOG_SETTING_KEY_DIRECTORY = "directory";
	public static final String DIALOG_SETTING_KEY_FOLDER = "folder";
	public static final String DIALOG_SETTING_KEY_PROJECT = "project";
	private static final Logger logger = LoggerFactory.getLogger(DataWizard.class);	
	private DataWizardPage page;
	private ISelection selection;

	/**
	 * Constructor for TestWizard.
	 */
	public DataWizard() {
		super();
		setNeedsProgressMonitor(true);
		IDialogSettings dialogSettings = AnalysisRCPActivator.getDefault().getDialogSettings();
		IDialogSettings section = dialogSettings.getSection(DATA_WIZARD);
		if(section == null){
			section = dialogSettings.addNewSection(DATA_WIZARD);
		}
		setDialogSettings(section);
	}

	/**
	 * Adding the page to the wizard.
	 */
	@Override
	public void addPages() {
		String prevProject = null , prevFolder = null, prevDirectory = null;
		IDialogSettings  settings = getDialogSettings();
		if( settings != null){
			prevProject = settings.get(DIALOG_SETTING_KEY_PROJECT);
			prevFolder = settings.get(DIALOG_SETTING_KEY_FOLDER);
			prevDirectory = settings.get(DIALOG_SETTING_KEY_DIRECTORY);
		}
		page = new DataWizardPage(selection, prevProject, prevFolder, prevDirectory);
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in
	 * the wizard. We will create an operation and run it
	 * using wizard as execution context.
	 */
	@Override
	public boolean performFinish() {
	
		final String project = page.getProject();
		final String directory = page.getDirectory();
		final String folder = page.getFolder();

		final Job loadDataProject = new Job("Load data project") {

			@Override
			protected IStatus run(IProgressMonitor monitor) {
				monitor.beginTask("Importing data", 100);
				try {
					ProjectUtils.createImportProjectAndFolder(project, folder, directory, GDADataNature.ID, null, monitor);
				} catch (CoreException e) {
					logger.error("Error creating project " + project, e);
					return new Status(IStatus.ERROR, AnalysisRCPActivator.PLUGIN_ID, "Error creating project " + project);
				}
				return new Status(IStatus.OK, AnalysisRCPActivator.PLUGIN_ID, "Project " + project + " created");
			}
		};

		loadDataProject.setUser(true);
		loadDataProject.setPriority(Job.DECORATE);
		loadDataProject.schedule(100);
		

		IDialogSettings settings = getDialogSettings();
		if( settings != null){
			settings.put(DIALOG_SETTING_KEY_PROJECT, project);
			settings.put(DIALOG_SETTING_KEY_FOLDER, folder);
			settings.put(DIALOG_SETTING_KEY_DIRECTORY, directory);
		}
		return true;
	}

	/**
	 * We will accept the selection in the workbench to see if
	 * we can initialize from it.
	 * @see IWorkbenchWizard#init(IWorkbench, IStructuredSelection)
	 */
	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		this.selection = selection;
	}
}