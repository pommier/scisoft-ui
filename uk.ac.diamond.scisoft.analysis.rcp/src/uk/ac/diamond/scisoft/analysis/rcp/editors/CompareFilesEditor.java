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
import java.io.Serializable;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.PlatformObject;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IPersistableElement;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.EditorPart;

import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.AbstractExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.MetadataSelection;
import uk.ac.diamond.scisoft.analysis.rcp.hdf5.HDF5TreeExplorer.HDF5Selection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisChoice;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.MultipleDatasetsSelection;

/**
 * This editor allows a set of files which can be loaded by one type of loader to be compared. It
 * lets the user select the dataset per file and which (metadata) value(s) to use per dataset. This
 * selection is pushed onto the dataset inspector.
 */
public class CompareFilesEditor extends EditorPart implements ISelectionChangedListener, ISelectionProvider {
	/**
	 * Factory to create proper input object for this editor
	 * @param sel
	 * @return compare files editor input
	 */
	public static IEditorInput createComparesFilesEditorInput(IStructuredSelection sel) {
		return new CompareFilesEditorInput(sel);
	}

	public final static String ID = "uk.ac.diamond.scisoft.analysis.rcp.editors.CompareFilesEditor";
	private SashForm sashComp;
	private List<SelectedFile> fileList;
	private TableViewer viewer;
	private Class<? extends AbstractExplorer> expClass = null;
	private AbstractExplorer explorer;
	private String firstFileName;

	private static class BooleanHolder {
		private boolean value = true;

		public void reset() {
			value = false;
		}
		public void set() {
			value = true;
		}

		public boolean isTrue() {
			return value;
		}
	}
	private BooleanHolder useRowIndexAsValue = new BooleanHolder();
	private Menu headerMenu;
	private DatasetSelection currentDatasetSelection;
	private MultipleDatasetsSelection selections;

	@Override
	public void doSave(IProgressMonitor monitor) {
	}

	@Override
	public void doSaveAs() {
	}

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof CompareFilesEditorInput))
			throw new PartInitException("Invalid input for comparison");

		setSite(site);
		try {
			setInput(input);
		} catch (Exception e) {
			throw new PartInitException("Invalid input for comparison", e);
		}
	}

	@Override
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setInput(IEditorInput input) {
		if (!(input instanceof CompareFilesEditorInput)) {
			return;
		}
		super.setInput(input);
		CompareFilesEditorInput filesInput = (CompareFilesEditorInput) input;
		fileList = new ArrayList<SelectedFile>();

		int n = 0;
		for (Object o: filesInput.list) {
			if (o instanceof IFile) {
				IFile f = (IFile) o;
				fileList.add(new SelectedFile(n++, f));
			}
		}

		firstFileName = fileList.get(0).getAbsolutePath();
		List<String> eList = getEditorCls(firstFileName);
		String edName = null;
		for (String e : eList) {
			try {
				Class edClass = Class.forName(e);
				Method m = edClass.getMethod("getExplorerClass");
				edName = e;
				expClass  = (Class) m.invoke(null);
				break;
			} catch (Exception e1) {
			}
		}
		if (expClass == null) {
			throw new IllegalArgumentException("No explorer available to read " + firstFileName);
		}

		for (int i = 1; i < n; i++) {
			String name = fileList.get(i).getAbsolutePath();
			if (!getEditorCls(name).contains(edName)) {
				throw new IllegalArgumentException("Editor cannot read file: " + fileList.get(i).getAbsolutePath());
			}
		}

		setPartName(input.getToolTipText());
	}

	@Override
	public boolean isDirty() {
		return false;
	}

	@Override
	public boolean isSaveAsAllowed() {
		return false;
	}

	private enum Column {
		TICK, PATH, VALUE;
	}

	private static class TickLabelProvider extends CellLabelProvider {
		private final Image TICK = AnalysisRCPActivator.getImageDescriptor("icons/tick.png").createImage();
		private Display display;
		private BooleanHolder useRowIndex;

		public TickLabelProvider(Display display, BooleanHolder useRowIndexAsValue) {
			this.display = display;
			useRowIndex = useRowIndexAsValue;
		}

		@Override
		public void update(ViewerCell cell) {
			SelectedFile sf = (SelectedFile) cell.getElement();
			if (sf.doUse()) {
				cell.setImage(TICK);
			} else {
				cell.setImage(null);
			}
			Color colour = null;
			if (!useRowIndex.isTrue() && !(sf.hasMetaValue() && sf.hasData())) {
				if (!sf.hasMetaValue())
					colour = display.getSystemColor(SWT.COLOR_RED);
				else
					colour = display.getSystemColor(SWT.COLOR_YELLOW);
			}
			cell.setForeground(colour);
		}

		@Override
		public String getToolTipText(Object element) {
			System.out.println(element.toString());
			return super.getToolTipText("Hello");
		}
	}

	private static class PathLabelProvider extends CellLabelProvider {
		private Display display;

		public PathLabelProvider(Display display) {
			this.display = display;
		}


		@Override
		public void update(ViewerCell cell) {
			SelectedFile sf = (SelectedFile) cell.getElement();
			cell.setText(sf.getAbsolutePath());
			cell.setForeground(sf.doUse() ? null : display.getSystemColor(SWT.COLOR_GRAY));
		}

	}

	private static class ValueLabelProvider extends CellLabelProvider {
		private Display display;
		private BooleanHolder useRowIndex;

		public ValueLabelProvider(Display display, BooleanHolder useRowIndexAsValue) {
			this.display = display;
			useRowIndex = useRowIndexAsValue;
		}

		@Override
		public void update(ViewerCell cell) {
			SelectedFile sf = (SelectedFile) cell.getElement();
			if (useRowIndex.isTrue()) {
				cell.setText(sf.getIndex());
			} else {
				cell.setText(sf.toString());
			}
			cell.setForeground(sf.doUse() ? null : display.getSystemColor(SWT.COLOR_GRAY));
		}
	}

	@Override
	public void createPartControl(Composite parent) {
		Display display = parent.getDisplay();
		sashComp = new SashForm(parent, SWT.VERTICAL);
		sashComp.setLayout(new FillLayout(SWT.VERTICAL));
		viewer = new TableViewer(sashComp, SWT.V_SCROLL);

		TableViewerColumn tVCol;
		TableColumn tCol;

		tVCol = new TableViewerColumn(viewer, SWT.NONE);
		tCol = tVCol.getColumn();
		tCol.setText("Use");
		tCol.setToolTipText("Toggle to use in dataset inspector");
		tCol.setWidth(40);
		tCol.setMoveable(false);
		tVCol.setEditingSupport(new FCEditingSupport(viewer, Column.TICK, null));
		tVCol.setLabelProvider(new TickLabelProvider(display, useRowIndexAsValue));

		tVCol = new TableViewerColumn(viewer, SWT.NONE);
		tCol = tVCol.getColumn();
		tCol.setText("File name");
		tCol.setToolTipText("Name of resource");
		tCol.setWidth(100);
		tCol.setMoveable(false);
		tVCol.setEditingSupport(new FCEditingSupport(viewer, Column.PATH, null));
		tVCol.setLabelProvider(new PathLabelProvider(display));

		tVCol = new TableViewerColumn(viewer, SWT.NONE);
		tCol = tVCol.getColumn();
		tCol.setText("Value");
		tCol.setToolTipText("Value of resource");
		tCol.setWidth(40);
		tCol.setMoveable(false);
		tVCol.setEditingSupport(new FCEditingSupport(viewer, Column.VALUE, null));
		tVCol.setLabelProvider(new ValueLabelProvider(display, useRowIndexAsValue));

		viewer.setContentProvider(new IStructuredContentProvider() {
			
			@Override
			public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
			}
			
			@Override
			public void dispose() {
			}
			
			@Override
			public Object[] getElements(Object inputElement) {
				return fileList == null ? null : fileList.toArray();
			}
		});


		final Table table = viewer.getTable();
		table.setHeaderVisible(true);

		headerMenu = new Menu(sashComp.getShell(), SWT.POP_UP);
		headerMenu.addListener(SWT.Show, new Listener() {
			@Override
			public void handleEvent(Event event) {
				// get selection and decide
				for (MenuItem m : headerMenu.getItems()) {
					m.setEnabled(!useRowIndexAsValue.isTrue());
				}
			}
		});

		MenuItem item = new MenuItem(headerMenu, SWT.PUSH);
		item.setText("Use row index as value");
		item.addListener(SWT.Selection, new Listener() {
			
			@Override
			public void handleEvent(Event event) {
				useRowIndexAsValue.set();
				viewer.refresh();
			}
		});

		final Menu tableMenu = null;
		table.addListener(SWT.MenuDetect, new Listener() {
			@Override
			public void handleEvent(Event event) {
				Point pt = sashComp.getDisplay().map(null, table, new Point(event.x, event.y));
				Rectangle clientArea = table.getClientArea();
				boolean header = clientArea.y <= pt.y && pt.y < (clientArea.y + table.getHeaderHeight());
				table.setMenu(header ? headerMenu : tableMenu);
			}
		});

		if (fileList != null) {
			viewer.setInput(fileList);
			for (TableColumn tc: viewer.getTable().getColumns()) {
				tc.pack();
			}
		}

		try {
			explorer = expClass.getConstructor(Composite.class, IWorkbenchPartSite.class, ISelectionChangedListener.class).newInstance(sashComp, getSite(), this);
		} catch (Exception e) {
			throw new IllegalArgumentException("Cannot make explorer", e);
		}

		try {
			explorer.loadFileAndDisplay(firstFileName, null);
		} catch (Exception e) {
			throw new IllegalArgumentException("Explorer cannot load file", e);
		}
		explorer.addSelectionChangedListener(this);
		getSite().setSelectionProvider(this);
	}

	final private class FCEditingSupport extends EditingSupport {
		private CheckboxCellEditor editor = null;
		private Column column;

		public FCEditingSupport(TableViewer viewer, Column column, ICellEditorListener listener) {
			super(viewer);
			if (column == Column.TICK) {
				editor = new CheckboxCellEditor(viewer.getTable(), SWT.CHECK);
				if (listener != null)
					editor.addListener(listener);
			}
			this.column = column;
		}

		@Override
		protected boolean canEdit(Object element) {
			return column == Column.TICK;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			SelectedFile sf = (SelectedFile) element;
			if (column == Column.TICK) {
				return sf.doUse();
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			SelectedFile sf = (SelectedFile) element;
			if (column == Column.TICK) {
				sf.setUse((Boolean) value);
			}
			getViewer().update(element, null);
		}
	}

	/**
	 * Get editor classes that can handle given file 
	 * @param fileName
	 * @return list of editor class names
	 */
	public static List<String> getEditorCls(final String fileName) {
		IEditorRegistry reg = PlatformUI.getWorkbench().getEditorRegistry();
		IEditorDescriptor[] eds = reg.getEditors(fileName);
		List<String> edId = new ArrayList<String>();
		for (IEditorDescriptor e : eds) {
			if (e.isInternal()) {
				edId.add(e.getId());
			}
		}

		List<String> edCls = new ArrayList<String>();
		IExtensionPoint ept = Platform.getExtensionRegistry().getExtensionPoint("org.eclipse.ui.editors");
		IConfigurationElement[] cs = ept.getConfigurationElements();
		for (IConfigurationElement l : cs) {
			String id = l.getAttribute("id");
			String cls = l.getAttribute("class");
			if (id != null && cls != null) {
				if (edId.contains(id))
					edCls.add(cls);
			}
		}
		return edCls;
	}

	@Override
	public void setFocus() {
	}

	@Override
	public void selectionChanged(SelectionChangedEvent e) {
		ISelection sel = e.getSelection();

		if (sel instanceof DatasetSelection) {
			currentDatasetSelection = (DatasetSelection) sel;

			processDatasets(currentDatasetSelection);
		} else if (sel instanceof MetadataSelection) {
			processMetaValues(((MetadataSelection) sel).getPathname());
			if (currentDatasetSelection != null)
				processDatasets(currentDatasetSelection);
			useRowIndexAsValue.reset();
		}

		if (selections != null)
			setSelection(selections);
	}

	private void processMetaValues(String key) {
		System.out.println("Selected metadata = " + key);
		// TODO change column title
		/*
		 * iterate through selected files and find metadata values 
		 */
		// TODO async outside GUI thread
		for (SelectedFile f : fileList) {
			if (!f.hasMetadata() && !f.hasData()) {
				try {
					DataHolder holder = explorer.loadFile(f.getAbsolutePath(), null);
					f.setDataHolder(holder);
				} catch (Exception e) {
					continue;
				}
			}
			f.setMetaValue(key);
		}

		useRowIndexAsValue.reset();
		// TODO sync in GUI thread
		viewer.refresh();
	}

	private void processDatasets(DatasetSelection selection) {
		/*
		 * iterate through selected files and find datasets 
		 */
		String name = selection.getFirstElement().getName();
		String node = null;
		if (selection instanceof HDF5Selection) {
			node = ((HDF5Selection) selection).getNode() + HDF5Node.SEPARATOR;
		}
		System.out.println("Selected data = " + (node != null ? node + name : name));

		boolean isFirst = true;
		selections = new MultipleDatasetsSelection();

		List<AxisSelection> axes = selection.getAxes();

		for (SelectedFile f : fileList) {
			if (!f.hasData()) {
				try {
					DataHolder holder = explorer.loadFile(f.getAbsolutePath(), null);
					f.setDataHolder(holder);
				} catch (Exception e) {
					continue;
				}
			}
			f.setData(name);
			if (isFirst) {
				isFirst = false;
				if (f.doUse())
					selections.addDatasetSelection(f.getData(), axes);
			} else if (f.doUse()) {
				selections.addDatasetSelection(f.getData(), makeAxes(axes, f, node));
			}
		}

		viewer.refresh();
	}

	private List<AxisSelection> makeAxes(List<AxisSelection> oldAxes, SelectedFile file, String node) {
		ArrayList<AxisSelection> newAxes = new ArrayList<AxisSelection>();
		for (AxisSelection a : oldAxes) {
			AxisSelection n;
			try {
				n = a.clone();
				for (int i = 0, imax = n.size(); i < imax; i++) {
					AxisChoice c = n.getAxis(i);
					String name = c.getName();
					c.setValues(file.getAxis(node != null ? node + name : name));
				}
				newAxes.add(n);
			} catch (CloneNotSupportedException e) {
			}
		}
		return newAxes;
	}

	private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
	private MultipleDatasetsSelection mSelection = null;

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return mSelection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof MultipleDatasetsSelection)
			mSelection = (MultipleDatasetsSelection) selection;

		SelectionChangedEvent e = new SelectionChangedEvent(this, mSelection);
		for (ISelectionChangedListener listener : listeners)
			listener.selectionChanged(e);
	}
}

class CompareFilesEditorInput extends PlatformObject implements IEditorInput {

	Object[] list;
	private String name;

	public CompareFilesEditorInput(IStructuredSelection selection) {
		list = selection.toArray();
		name = createName();
	}

	@Override
	public boolean exists() {
		return false;
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public IPersistableElement getPersistable() {
		return null;
	}

	@Override
	public String getToolTipText() {
		return name;
	}

	private String createName() {
		StringBuilder s = new StringBuilder();
		if (list == null || list.length < 1) {
			s.append("Invalid list");
		} else {
			s.append("Comparing ");
			for (Object o : list) {
				if (o instanceof IFile) {
					IFile f = (IFile) o;
					s.append(f.getFullPath().toString());
					s.append(", ");
				}
			}
			int end = s.length();
			s.delete(end-2, end);
		}
		return s.toString();
	}
}

class SelectedFile {
	boolean use = true;
	int i;
	File f;
	boolean hasMetaValue = true;
	boolean hasData = true;
	DataHolder h;
	IMetaData m;
	ILazyDataset d;
	Serializable mv;

	public SelectedFile(int index, IFile file) {
		i = index;
		f = new File(file.getLocationURI());
	}

	public String getAbsolutePath() {
		return f.getAbsolutePath();
	}

	public String getName() {
		return f.getName();
	}

	public boolean doUse() {
		return use;
	}

	@Override
	public String toString() {
		if (mv == null)
			return Integer.toString(i);
		return mv.toString();
	}

	public String getIndex() {
		return Integer.toString(i);
	}

	public void setUse(boolean doUse) {
		use = doUse;
	}

	public boolean hasMetaValue() {
		return m != null && mv != null;
	}

	public boolean hasMetadata() {
		return m != null;
	}

	public boolean hasData() {
		return h != null;
	}

	public void setMetadata(IMetaData metadata) {
		m = metadata;
	}

	public void setDataHolder(DataHolder holder) {
		h = holder;
		if (h != null)
			m = h.getMetadata();
	}

	public void setData(String key) {
		d = h.getLazyDataset(key);
	}

	public ILazyDataset getData() {
		return d;
	}

	public ILazyDataset getAxis(String key) {
		return h.getLazyDataset(key);
	}

	public boolean setMetaValue(String key) {
		if (m == null)
			return false;

		try {
			mv = m.getMetaValue(key);
			if (mv == null && h != null) {
				mv = h.getDataset(key);
			}
		} catch (Exception e) {
		}
		return mv != null;
	}
}
