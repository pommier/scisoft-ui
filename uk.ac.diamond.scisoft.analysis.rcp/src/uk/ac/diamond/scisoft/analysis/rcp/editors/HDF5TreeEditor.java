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

import java.io.File;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5File;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.AbstractExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.hdf5.HDF5TreeExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection.InspectorType;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;
import uk.ac.gda.common.rcp.util.EclipseUtils;

public class HDF5TreeEditor extends EditorPart implements IPageChangedListener {

	private HDF5TreeExplorer hdfxp;
	private File file;

	private ISelectionListener selectionListener;

	public HDF5TreeEditor() {
	}

	/**
	 * 
	 */
	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.editors.HDF5TreeEditor"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(HDF5TreeEditor.class);

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		setSite(site);
		file = EclipseUtils.getFile(input);
		if (file == null || !file.exists()) {
			logger.warn("File does not exist: {}", input.getName());
			throw new PartInitException("Input is not a file or file does not exist");
		}
		setInput(input);
	}

	protected void loadHDF5Tree() {
		if (getHDF5Tree() != null)
			return;

		final String fileName = file.getAbsolutePath();
		try {
			if (hdfxp != null) {
				hdfxp.loadFileAndDisplay(fileName, null);
			}
		} catch (Exception e) {
			if (e.getCause() != null)
				logger.warn("Could not load NeXus file {}: {}", fileName, e.getCause().getMessage());
			else
				logger.warn("Could not load NeXus file {}: {}", fileName, e.getMessage());
		}
		return;
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
		registerSelectionListener();
		IWorkbenchPartSite site = getSite();
		hdfxp = new HDF5TreeExplorer(parent, site, null);
		site.setSelectionProvider(hdfxp);

		setPartName(file.getName());
		loadHDF5Tree();
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) { // Just selected this page
			loadHDF5Tree();
		}
	}

	@Override
	public void doSave(IProgressMonitor monitor) {
		// do nothing
	}

	@Override
	public void doSaveAs() {
		// do nothing
	}

	@Override
	public void setFocus() {
		hdfxp.setFocus();
	}

	public void expandAll() {
		hdfxp.expandAll();
	}

	@Override
	public void dispose() {
		file = null;
		unregisterSelectionListener();
		if (hdfxp != null)
			hdfxp.dispose();
		super.dispose();
	}

	public HDF5TreeExplorer getHDF5TreeExplorer() {
		return hdfxp;
	}
	
	public HDF5File getHDF5Tree() {
		if (hdfxp == null)
			return null;
		return hdfxp.getHDF5Tree();
	}

	/**
	 * This editor uses a HDF5 explorer
	 * @return explorer class
	 */
	public static Class<? extends AbstractExplorer> getExplorerClass() {
		return HDF5TreeExplorer.class;
	}

	/* Setting up of the editor as a Selection listener on the navigator selectionProvider */
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
						if (element instanceof TreeNode) {
							TreeNode hdf5Data = (TreeNode) element;
							String filename = hdf5Data.getFile().getName();
							//update only the relevant hdf5editor
							if(filename.equals(getSite().getPart().getTitle()))
								update(part, hdf5Data, structuredSelection);
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

	public void update(final IWorkbenchPart original, final TreeNode treeData, IStructuredSelection structuredSelection) {

		/**
		 * TODO Instead of selecting the editor, firing the selection and then selecting the navigator again, better to
		 * have one object type selected by both the editor and navigator which the plot view listens to using eclipse
		 * selection events.
		 */
		if(!EclipseUtils.getActivePage().getActivePart().getTitle().equals(this.getPartName()))
			EclipseUtils.getActivePage().activate(this);

		//System.out.println(treeData.getData().toString());
		//selection of hedf5 tree element no working
		
		
		final Cursor cursor = hdfxp.getCursor();
		Cursor tempCursor = hdfxp.getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
		if (tempCursor != null)
			hdfxp.setCursor(tempCursor);

		try {
			
			
			
			TreePath navTreePath1 = new TreePath(structuredSelection.toArray());
			//System.out.println(navTreePath1.getFirstSegment().toString());			
			hdfxp.expandToLevel(navTreePath1,2);
			hdfxp.setSelection(structuredSelection);
			//TreePath[] editorTreePaths=hdfxp.getExpandedTreePaths();

			//hdfxp.setSelection(structuredSelection);
			//HDF5TableTree tableTree=hdfxp.getTableTree();
			//tableTree.setSelection(structuredSelection);
			//HDF5TableTree tableTree=hdfxp.getTableTree();
			//tableTree.setInput(link);
			
		//	hdfxp.expandToLevel(navTreePath1,0);
//			for (int i = 0; i < hdfxp.getTabList().; i++) {
//				String name = viewer.getTable().getItem(i).getText();
//				if (name.equals(srsData.getName())) {
//					viewer.getTable().setSelection(i);
//					selectItemAction.run();
//					break;
//				}
//			}
			
			//hdfxp.getTableTree().getViewer().setSelection(structuredSelection);
			
			HDF5NodeLink link = ((HDF5NodeLink) treeData.getData());
			hdfxp.selectHDF5Node(link, InspectorType.LINE);
		} catch (Exception e) {
			logger.error("Error processing selection: {}", e.getMessage());
		} finally {
			if (tempCursor != null)
				hdfxp.setCursor(cursor);
		}

		//if(!original.getTitle().equals(this.getPartName()))
		EclipseUtils.getActivePage().activate(original);

	}
}
