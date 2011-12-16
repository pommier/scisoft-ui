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

package uk.ac.diamond.scisoft.analysis.rcp.editors;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;

import uk.ac.diamond.scisoft.analysis.rcp.explorers.AbstractExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.SRSExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.srs.SRSTreeData;
import uk.ac.gda.common.rcp.util.EclipseUtils;

public class SRSEditor extends EditorPart {

	private SRSExplorer srsxp;
	private String fileName;

	public SRSEditor() {
	}

	@Override
	public void doSave(IProgressMonitor monitor) {

	}

	@Override
	public void doSaveAs() {

	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
        setSite(site);
		fileName = EclipseUtils.getFilePath(input);
		if (fileName == null) {
			throw new PartInitException("Input is not a file or file does not exist");
		}
        setInput(input);
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		IWorkbenchPartSite site = getSite();
		srsxp = new SRSExplorer(parent, site, null);
		site.setSelectionProvider(srsxp);

		setPartName(fileName);
		try {
			srsxp.loadFileAndDisplay(fileName, null);
		} catch (Exception e) {
		}

		// Register selection listener
		registerSelectionListener();
	}

	@Override
	public void setFocus() {
	}

	private ISelectionListener selectionListener;
	
	/* Setting up of the SRSEditor as a Selection listener on the navigator selectionProvider */
	private void registerSelectionListener() {

		final ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		if (selectionService == null)
			throw new IllegalStateException("Cannot acquire the selection service!");
		selectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(final IWorkbenchPart part, final ISelection selection) {
				if (selection instanceof IStructuredSelection) {
					if (!selection.isEmpty()) {
						final IStructuredSelection structuredSelection = (IStructuredSelection) selection;
						final Object element = structuredSelection.getFirstElement();
						if (element instanceof SRSTreeData) {
							SRSTreeData srsData = (SRSTreeData) element;
							String filename = srsData.getFile().getLocation().toString();
							//update only the relevant srseditor
							String editorName= getSite().getPart().getTitle();
							if(filename.equals(editorName))
								update(part, srsData);			
						}
					}

				}
			}
		};
		selectionService.addSelectionListener(selectionListener);
	}

	private void unregisterSelectionListener() {

		final ISelectionService selectionService = getSite().getWorkbenchWindow().getSelectionService();
		if (selectionService == null)
			throw new IllegalStateException("Cannot acquire the selection service!");

		selectionService.removeSelectionListener(selectionListener);
	}

	@Override
	public void dispose() {

		// Unregister selection listener
		unregisterSelectionListener();

		super.dispose();
	}

	/**
	 * This editor uses an SRS explorer
	 * @return explorer class
	 */
	public static Class<? extends AbstractExplorer> getExplorerClass() {
		return SRSExplorer.class;
	}

	public void update(final IWorkbenchPart original, final SRSTreeData srsData) {

		/**
		 * TODO Instead of selecting the editor, firing the selection and then selecting the naigator again, better to
		 * have one object type selected by both the editor and navigator which the plot view listens to using eclipse
		 * selection events.
		 */

		EclipseUtils.getActivePage().activate(this);

		TableViewer viewer = srsxp.getViewer();
		for (int i = 0; i < viewer.getTable().getItemCount(); i++) {
			String name = viewer.getTable().getItem(i).getText();
			if (name.equals(srsData.getName())) {
				viewer.getTable().setSelection(i);
				//selectItemAction.run();
				srsxp.selectItemSelection();
				break;
			}
		}

		EclipseUtils.getActivePage().activate(original);

	}

}
