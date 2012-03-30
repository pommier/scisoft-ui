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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.handlers.HandlerUtil;

import uk.ac.gda.common.rcp.util.EclipseUtils;

/**
 * ExportWizard shows a wizard for converting synchrotron data to more common
 * file types.
 **/
public class ExportWizardHandler extends AbstractHandler implements
		IObjectActionDelegate {

	private IWorkbenchPart targetPart;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		openWizard(HandlerUtil.getActiveShell(event));
		return Boolean.FALSE;
	}

	private void openWizard(final Shell shell) {
		WizardDialog dialog = new WizardDialog(shell, new ExportWizard());
		dialog.setPageSize(new Point(400, 300));
		dialog.create();
		dialog.open();
	}

	@Override
	public void run(IAction action) {
		openWizard(targetPart.getSite().getShell());
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {

	}

	@Override
	public boolean isEnabled() {
		final ISelection selection = EclipseUtils.getActivePage()
				.getSelection();
		if (selection instanceof StructuredSelection) {
			StructuredSelection s = (StructuredSelection) selection;
			final Object o = s.getFirstElement();
			if (o instanceof IFile)
				return true;
		}
		return false;
	}

	@Override
	public boolean isHandled() {
		return isEnabled();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
		this.targetPart = targetPart;
	}
}
