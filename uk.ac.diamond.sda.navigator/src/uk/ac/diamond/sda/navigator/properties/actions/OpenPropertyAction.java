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
package uk.ac.diamond.sda.navigator.properties.actions;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.FindReplaceDocumentAdapter;
import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.texteditor.IDocumentProvider;
import org.eclipse.ui.texteditor.ITextEditor;

import uk.ac.diamond.sda.intro.navigator.NavigatorRCPActivator;
import uk.ac.diamond.sda.navigator.properties.PropertiesTreeData;

public class OpenPropertyAction extends Action {

	private IWorkbenchPage page;
	private PropertiesTreeData data;
	private ISelectionProvider provider;

	/**
	 * Construct the OpenPropertyAction with the given page.
	 * 
	 * @param p
	 *            The page to use as context to open the editor.
	 * @param selectionProvider
	 *            The selection provider
	 */
	public OpenPropertyAction(IWorkbenchPage p, ISelectionProvider selectionProvider) {
		setText("Open Property"); //$NON-NLS-1$
		page = p;
		provider = selectionProvider;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#isEnabled()
	 */
	@Override
	public boolean isEnabled() {
		ISelection selection = provider.getSelection();
		if (!selection.isEmpty()) {
			IStructuredSelection sSelection = (IStructuredSelection) selection;
			if (sSelection.size() == 1 && sSelection.getFirstElement() instanceof PropertiesTreeData) {
				data = ((PropertiesTreeData) sSelection.getFirstElement());
				return true;
			}
		}
		return false;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.jface.action.Action#run()
	 */
	@Override
	public void run() {
		/*
		 * In production code, you should always externalize strings, but this is an example.
		 */
		try {
			if (isEnabled()) {
				IFile propertiesFile = data.getFile();
				IEditorPart editor = IDE.openEditor(page, propertiesFile);

				if (editor instanceof ITextEditor) {
					ITextEditor textEditor = (ITextEditor) editor;

					IDocumentProvider documentProvider = textEditor.getDocumentProvider();
					IDocument document = documentProvider.getDocument(editor.getEditorInput());

					FindReplaceDocumentAdapter searchAdapter = new FindReplaceDocumentAdapter(document);

					try {
						String searchText = data.getName() + "="; //$NON-NLS-1$ 
						IRegion region = searchAdapter.find(0, searchText, true /* forwardSearch */,
								true /* caseSensitive */, false /* wholeWord */, false /* regExSearch */);

						((ITextEditor) editor).selectAndReveal(region.getOffset(), region.getLength());

					} catch (BadLocationException e) {
						NavigatorRCPActivator.logError(0, "Could not open property!", e); //$NON-NLS-1$
						MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Opening Property", //$NON-NLS-1$
								"Could not open property!"); //$NON-NLS-1$
					}
					return;
				}
			}
		} catch (PartInitException e) {
			NavigatorRCPActivator.logError(0, "Could not open property!", e); //$NON-NLS-1$
			MessageDialog.openError(Display.getDefault().getActiveShell(), "Error Opening Property", //$NON-NLS-1$
					"Could not open property!"); //$NON-NLS-1$
		}
	}
}
