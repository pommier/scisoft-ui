/*-
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
package uk.ac.diamond.sda.exporter;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWizard;

import uk.ac.diamond.sda.exporter.io.CsvWriter;

public class ExportWizard extends Wizard implements IWorkbenchWizard {

	private ExportWizardPage1 convertWizardPage1;

	public ExportWizard() {
		setWindowTitle("Export Data Wizard");
	}

	@Override
	public void addPages() {
		this.convertWizardPage1 = new ExportWizardPage1();
		addPage(convertWizardPage1);
	}

	@Override
	public boolean performFinish() {

		final Object[] sel = convertWizardPage1.getSelected();
		if (sel == null || sel.length < 1)
			return false;

		String delimiter = convertWizardPage1.delimiterText.getText();
		if(!delimiter.equals(""))
			CsvWriter.createCSV(convertWizardPage1.getFile(), sel, delimiter);
		else
			return false;
		return true;
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub
		
	}

}
