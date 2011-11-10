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

import gda.analysis.io.ScanFileHolderException;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.ExtendedSRSLoader;
import uk.ac.diamond.scisoft.analysis.io.SRSLoader;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisChoice;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.srs.SRSTreeData;
import uk.ac.gda.common.rcp.util.EclipseUtils;

public class SRSEditor extends EditorPart implements ISelectionProvider {

	private TableViewer viewer;
	protected String fileName;
	protected DataHolder data;
	private Action selectItemAction;

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
        setInput(input);
        
        getSite().setSelectionProvider(this);
        
        // now load the data
	    try {
	    	SRSLoader dataLoader = new ExtendedSRSLoader(fileName);
	    	data = dataLoader.loadFile();
		} catch (ScanFileHolderException e) {
			data = new DataHolder();
			data.addDataset("Failed to load File", new DoubleDataset(1));
		}

		setSelection(new DatasetSelection()); // set up null selection to clear plot
	}

	@Override
	public void setInput(final IEditorInput input) {
		super.setInput(input);

		fileName = EclipseUtils.getFilePath(input);
		if (fileName == null) {
			return;
		}

		setPartName(input.getName());
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
		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setHeaderVisible (true);
		
		TableColumn tc = new TableColumn(viewer.getTable(), SWT.LEFT);
		tc.setText("Name");
		tc.setWidth(200);
		tc = new TableColumn(viewer.getTable(), SWT.LEFT);
		tc.setText("min");
		tc.setWidth(100);
		tc = new TableColumn(viewer.getTable(), SWT.LEFT);
		tc.setText("max");
		tc.setWidth(100);
		tc = new TableColumn(viewer.getTable(), SWT.LEFT);
		tc.setText("Class");
		tc.setWidth(100);
		
		viewer.setContentProvider(new ViewContentProvider());
		viewer.setLabelProvider(new ViewLabelProvider());
		viewer.setInput(getEditorSite());

		makeActions();
		hookDataSelectionHandler();
		
		DatasetSelection datasetSelection = new DatasetSelection(getAxes(), getActiveData());
		setSelection(datasetSelection);

		// Register selection listener
		registerSelectionListener();

	}
	
	private void makeActions() {

		selectItemAction = new Action() {
			@Override
			public void run() {				
				DatasetSelection datasetSelection = new DatasetSelection(getAxes(), getActiveData());
				setSelection(datasetSelection);		
			}
		};
	}
	
	
	private List<AxisSelection> getAxes() {
		List<AxisSelection> axes = new ArrayList<AxisSelection>();
		
		AxisSelection axisSelection = new AxisSelection(data.getDataset(0).getShape()[0]);
		
		AbstractDataset autoAxis = AbstractDataset.arange(data.getDataset(0).getShape()[0], AbstractDataset.INT32);
		autoAxis.setName("Index");
		AxisChoice newChoice = new AxisChoice(autoAxis);
		newChoice.setDimension(new int[] {0});
		axisSelection.addSelection(newChoice, 0);
		
		for (int i = 0; i < data.size(); i++) {
			ILazyDataset ldataset = data.getLazyDataset(i);
			if (ldataset instanceof AbstractDataset) {
				newChoice = new AxisChoice(ldataset);
				newChoice.setDimension(new int[] {0});
				axisSelection.addSelection(newChoice, i+1);
			}
		}
		
		axisSelection.selectAxis(0);
		axes.add(axisSelection);
		
		return axes;
	}
	
	
	private void hookDataSelectionHandler() {
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				selectItemAction.run();				
			}
		});

	}

	private ILazyDataset getActiveData() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection)selection).getFirstElement();
		if(obj == null) {
			return data.getLazyDataset(0);
		}
		return (ILazyDataset) obj;		
	}
	
	@Override
	public void setFocus() {
	}
	
	private class ViewContentProvider implements IStructuredContentProvider {
		@Override
		public void inputChanged(Viewer v, Object oldInput, Object newInput) {
		}
		
		@Override
		public void dispose() {
		}
		
		@Override
		public Object[] getElements(Object parent) {
			return data.getList().toArray();
		}
		
	}
	
	class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			if(obj instanceof IDataset) {
				IDataset dataset = (IDataset) obj;
				if (index == 0) return dataset.getName();
				if (index == 1) return dataset.min().toString();
				if (index == 2) return dataset.max().toString();
				if (index == 3) {
					String[] parts = dataset.elementClass().toString().split("\\.");
					return parts[parts.length-1];
				}
			}
			if(obj instanceof ILazyDataset) {
				ILazyDataset dataset = (ILazyDataset) obj;
				if (index == 0) return dataset.getName();
				if (index == 1) return "Not Available";
				if (index == 2) return "Not Available";
				if (index == 3) return "Not Available";
			}
			
			return null;
		}
		
		@Override
		public Image getColumnImage(Object obj, int index) {
			if (index == 0 ) return getImage(obj);
			return null;
		}
		
		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().
			getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
	private DatasetSelection dSelection = null;

	private ISelectionListener selectionListener;
	
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}		

	}

	@Override
	public ISelection getSelection() {

		return dSelection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);

	}

	@Override
	public void setSelection(ISelection selection) {
		
		if (selection instanceof DatasetSelection)
			dSelection = (DatasetSelection) selection;

		SelectionChangedEvent e = new SelectionChangedEvent(this, dSelection);
		for (ISelectionChangedListener listener : listeners)
			listener.selectionChanged(e);

	}

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
							String filename = srsData.getFile().getName();
							//update only the relevant srseditor
							if(filename.equals(getSite().getPart().getTitle()))
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

	public void update(final IWorkbenchPart original, final SRSTreeData srsData) {
		//System.out.println(srsData.getName());

		/**
		 * TODO Instead of selecting the editor, firing the selection and then selecting the naigator again, better to
		 * have one object type selected by both the editor and navigator which the plot view listens to using eclipse
		 * selection events.
		 */

		EclipseUtils.getActivePage().activate(this);

		for (int i = 0; i < viewer.getTable().getItemCount(); i++) {
			String name = viewer.getTable().getItem(i).getText();
			if (name.equals(srsData.getName())) {
				viewer.getTable().setSelection(i);
				selectItemAction.run();
				break;
			}
		}

		EclipseUtils.getActivePage().activate(original);

	}

}
