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

package uk.ac.diamond.scisoft.analysis.rcp.wizards;

import org.eclipse.jface.dialogs.IDialogPage;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

/**
 * The "New" wizard page allows setting the container for the new file as well as the file name. The page will only
 * accept file name without the extension OR with the extension that matches the expected one (png).
 */

public class DataWizardPage extends WizardPage implements KeyListener {

	private Text txtDirectory;
	private Text txtProject;
	private Text txtFolder;
	private final String initProject;
	private final String initDirectory;
	private final String initFolder;

	/**
	 * Constructor for SampleNewWizardPage.
	 * 
	 * @param prevDirectory
	 * @param prevFolder
	 * @param prevProject
	 */
	public DataWizardPage(@SuppressWarnings("unused") ISelection selection, String prevProject,
			String prevFolder, String prevDirectory) {
		super("DataWizardPage");
		this.initProject = prevProject != null ? prevProject : "Data";
		this.initFolder = prevFolder != null ? prevFolder : "data";
		this.initDirectory = prevDirectory != null ? prevDirectory : "";
		setTitle("Data Project Wizard - creates a link to a directory of data files");
		setDescription("Wizard to create a link to a set of data files");
	}

	/**
	 * @see IDialogPage#createControl(Composite)
	 */
	@SuppressWarnings("unused")
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container.setLayout(layout);
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		Label lblProjectName = new Label(container, SWT.NULL);
		lblProjectName.setText("&Project:");
		txtProject = new Text(container, SWT.BORDER);
		txtProject.setText(initProject);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		txtProject.setLayoutData(gd);
		txtProject.addKeyListener(this);
		new Composite(container, SWT.NULL);

		Label lblFolderName = new Label(container, SWT.NULL);
		lblFolderName.setText("&Folder:");
		txtFolder = new Text(container, SWT.BORDER);
		txtFolder.setText(initFolder);
		txtFolder.setEditable(true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		txtFolder.setLayoutData(gd);
		txtFolder.addKeyListener(this);
		new Composite(container, SWT.NULL);

		Label label = new Label(container, SWT.NULL);
		label.setText("&Directory:");
		txtDirectory = new Text(container, SWT.BORDER);
		txtDirectory.setText(initDirectory);
		txtDirectory.setEditable(true);
		txtDirectory.setEnabled(true);
		gd = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		txtDirectory.setLayoutData(gd);

		Button button = new Button(container, SWT.PUSH);
		button.setText("Browse...");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				handleBrowse();
			}
		});
		dialogChanged();
		setControl(container);
	}

	/**
	 * Uses the standard container selection dialog to choose the new value for the container field.
	 */

	private void handleBrowse() {
		DirectoryDialog dirDialog = new DirectoryDialog(getShell(), SWT.OPEN);
		dirDialog.setFilterPath(getDirectory());
		final String filepath = dirDialog.open();
		if (filepath != null) {
			txtDirectory.setText(filepath);
			dialogChanged();
		}
	}

	/**
	 * Ensures that both text fields are set.
	 */

	private void dialogChanged() {
		if (getProject().length() == 0) {
			updateStatus("Project name must be specified");
			return;
		}

		if (getFolder().length() == 0) {
			updateStatus("Folder name must be specified. e.g. data");
			return;
		}

		if (getDirectory().length() == 0) {
			updateStatus("Directory containing files must be specified.");
			return;
		}
		updateStatus(null);
	}

	private void updateStatus(String message) {
		setErrorMessage(message);
		setPageComplete(message == null);
	}

	public String getProject() {
		return txtProject.getText();
	}

	public String getDirectory() {
		return txtDirectory.getText();
	}

	public String getFolder() {
		return txtFolder.getText();
	}

	@Override
	public void keyPressed(KeyEvent e) {
	}

	@Override
	public void keyReleased(KeyEvent e) {
		if (e.getSource().equals(txtProject)) {
			dialogChanged();
		}
		if (e.getSource().equals(txtFolder)) {
			dialogChanged();
		}
	}
}
