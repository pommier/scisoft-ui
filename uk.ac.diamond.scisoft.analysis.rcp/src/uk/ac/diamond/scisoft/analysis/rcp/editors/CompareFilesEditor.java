/*
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.AggregateDataset;
import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.io.Utils;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.AbstractExplorer;
import uk.ac.diamond.scisoft.analysis.rcp.explorers.MetadataSelection;
import uk.ac.diamond.scisoft.analysis.rcp.hdf5.HDF5TreeExplorer.HDF5Selection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisChoice;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection.InspectorType;

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

	private static final Logger logger = LoggerFactory.getLogger(CompareFilesEditor.class);

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
	private DatasetSelection multipleSelection;

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
		int l = 0;
		while (l < filesInput.list.length) {
			Object o = filesInput.list[l++];
			if (o instanceof IFile) {
				IFile f = (IFile) o;
				try {
					fileList.add(new SelectedFile(n, f));
					n++;
					break;
				} catch (IllegalArgumentException e) {
					logger.warn("Problem with selection: ", e);
				}
			}
		}
		if (n == 0) {
			// TODO error
			return;
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

		while (l < filesInput.list.length) {
			Object o = filesInput.list[l++];
			if (o instanceof IFile) {
				IFile f = (IFile) o;
				try {
					SelectedFile sf = new SelectedFile(n, f);
					String name = sf.getAbsolutePath();
					if (!getEditorCls(name).contains(edName)) {
						logger.warn("Editor cannot read file: {}", name);
					}

					fileList.add(sf);
					n++;
				} catch (IllegalArgumentException e) {
					logger.warn("Problem with selection: ", e);
				}
			}
		}

		if (n != filesInput.list.length) {
			// TODO warning
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
			if (!sf.hasMetaValue() && !useRowIndex.isTrue()) {
				colour = display.getSystemColor(SWT.COLOR_RED);
			} else if (!sf.hasData()) {
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
		boolean refresh = false;

		if (sel instanceof MetadataSelection) {
			loadMetaValues(((MetadataSelection) sel).getPathname());
			useRowIndexAsValue.reset();
			refresh = true;
		} else if (sel instanceof DatasetSelection) {
			currentDatasetSelection = (DatasetSelection) sel;
			String name;
			String node;
			if (currentDatasetSelection instanceof HDF5Selection) {
				name = ((HDF5Selection) currentDatasetSelection).getNode();
				node = name.substring(0, name.lastIndexOf(HDF5Node.SEPARATOR)+1);
			} else {
				name = currentDatasetSelection.getFirstElement().getName();
				node = null;
			}
			logger.debug("Selected data = {}", name);
			loadDatasets(name);
			loadAxisSelections(currentDatasetSelection.getAxes(), node);
			refresh = true;
		}

		if (currentDatasetSelection != null) {
			List<ILazyDataset> dataList = new ArrayList<ILazyDataset>();
			List<ILazyDataset> metaList = new ArrayList<ILazyDataset>();
			List<List<AxisSelection>> axesList = new ArrayList<List<AxisSelection>>();

			for (SelectedFile f : fileList) {
				if (f.doUse() && f.hasData() && (useRowIndexAsValue.isTrue() || f.hasMetaValue())) {
					dataList.add(f.getData());
					metaList.add(f.getMetaValue());
					axesList.add(new ArrayList<AxisSelection>(f.getAxisSelections()));
				}
			}

			setSelection(createSelection(dataList, metaList, axesList));
		}

		// TODO sync in GUI thread
		if (refresh)
			viewer.refresh();
	}

	/**
	 * Load metadata values from selected files
	 */
	private void loadMetaValues(String key) {
		logger.debug("Selected metadata = {}", key);

		// TODO change column title
		// TODO async outside GUI thread
		for (SelectedFile f : fileList) {
			if (!f.hasMetadata() && !f.hasDataHolder()) {
				try {
					DataHolder holder = explorer.loadFile(f.getAbsolutePath(), null);
					f.setDataHolder(holder);
				} catch (Exception e) {
					continue;
				}
			}
			f.setMetaValue(key);
		}
	}

	/**
	 * Load datasets from selected files
	 */
	private void loadDatasets(String key) {
		for (SelectedFile f : fileList) {
			if (!f.hasDataHolder()) {
				try {
					DataHolder holder = explorer.loadFile(f.getAbsolutePath(), null);
					f.setDataHolder(holder);
				} catch (Exception e) {
					continue;
				}
			}
			f.setData(key);
		}
	}

	/**
	 * Load axis selections from selected files
	 */
	private void loadAxisSelections(List<AxisSelection> axes, String node) {
		boolean isFirst = true;

		List<AxisSelection> laxes = new ArrayList<AxisSelection>();
		for (AxisSelection as : axes)
			laxes.add(as.clone());

		for (SelectedFile f : fileList) {
			if (f.doUse() && f.hasData() && (useRowIndexAsValue.isTrue() || f.hasMetaValue())) {
				if (isFirst) {
					isFirst = false;
					f.setAxisSelections(laxes);
				} else {
					f.setAxisSelections(makeAxes(laxes, f, node));
				}
			}
		}

		// prune missing choices
		List<String> choices = new ArrayList<String>();
		int rank = axes.size();
		for (int i = 0; i < rank; i++) {
			choices.clear();
			choices.addAll(axes.get(i).getNames());

			for (SelectedFile f : fileList) {
				AxisSelection as = f.getAxisSelections().get(i);
				for (String n : as) {
					if (as.getAxis(n) == null) {
						logger.warn("Removing choice {} as it is missing in {}", n, f.getName());
						choices.remove(n);
					}
				}
			}

			for (SelectedFile f : fileList) {
				AxisSelection as = f.getAxisSelections().get(i);
				for (String n : as) {
					if (!choices.contains(n)) {
						as.removeChoice(n);
					}
				}
			}
		}
	}

	/**
	 * Create axes from file based on other axes
	 * @param oldAxes
	 * @param file
	 * @param node
	 * @return list of axis selections
	 */
	private List<AxisSelection> makeAxes(List<AxisSelection> oldAxes, SelectedFile file, String node) {
		List<AxisSelection> newAxes = new ArrayList<AxisSelection>();
		for (AxisSelection a : oldAxes) {
			AxisSelection n = a.clone();
			for (int i = 0, imax = n.size(); i < imax; i++) {
				AxisChoice c = n.getAxis(i);
				String name = c.getName();
				ILazyDataset d = file.getAxis(node != null ? node + name : name); // can be null (from Index or dim:)
				if (d == null) {
					if (name.startsWith(AbstractExplorer.DIM_PREFIX)) {
						d = c.getValues().clone();
					}
				}
				c.setValues(d);
			}
			newAxes.add(n);
		}
		return newAxes;
	}

	/**
	 * Create a data selection from given lists of datasets, metadata value datasets and axis selection lists  
	 * @param datasets
	 * @param metavalues
	 * @param axisSelectionLists
	 * @return data selection
	 */
	public static DatasetSelection createSelection(List<ILazyDataset> datasets, List<ILazyDataset> metavalues, List<List<AxisSelection>> axisSelectionLists) {
		boolean extend = true;
		for (ILazyDataset m : metavalues) { // if all metadata is multi-valued then do not extend aggregate shape
			if (m.getSize() > 1) {
				extend = false;
				break;
			}
		}

		AggregateDataset allData = new AggregateDataset(extend, datasets.toArray(new ILazyDataset[0]));
		AggregateDataset allMeta = new AggregateDataset(extend, metavalues.toArray(new ILazyDataset[0]));
		List<AxisSelection> newAxes = new ArrayList<AxisSelection>();
		if (extend) { // extra entries as aggregate datasets can have extra dimension
			for (List<AxisSelection> asl : axisSelectionLists) {
				asl.add(0, null);
			}
		}

		// mash together axes
		int[] shape = allData.getShape();
		int rank = shape.length;
		AxisSelection as;
		List<ILazyDataset> avalues = new ArrayList<ILazyDataset>();

		// for each dimension,
		for (int i = 0; i < rank; i++) {
			as = new AxisSelection(rank, i);
			newAxes.add(as);
			if (i == 0) { // add meta values first
				AxisChoice nc = new AxisChoice(allMeta, 1);
				int[] map = new int[allMeta.getRank()];
				for (int j = 0; j < map.length; j++) {
					map[j] = j;
				}
				nc.setIndexMapping(map);
				nc.setAxisNumber(0);
				as.addChoice(nc, 1);
			}

			AxisSelection ias = axisSelectionLists.get(0).get(0); // initial
			if (ias == null)
				ias = axisSelectionLists.get(0).get(1);

			for (int k = 0, kmax = ias.size(); k < kmax; k++) { // for each choice
				avalues.clear();
				for (List<AxisSelection> asl : axisSelectionLists) { // for each file
					AxisSelection a = asl.get(i);
					if (a == null)
						break; // was extended

					AxisChoice ec = a.getAxis(k);
					avalues.add(ec.getValues());
				}
				if (avalues.size() == 0)
					break;

				// consume list for choice
				AggregateDataset allAxis = new AggregateDataset(extend, avalues.toArray(new ILazyDataset[0]));
				final AxisChoice c = ias.getAxis(i);
				AxisChoice nc = new AxisChoice(allAxis, c.getPrimary());
				int[] map = c.getIndexMapping();
				String name = ias.getName(k);
				if (extend) {
					int[] nmap = new int[map.length+1];
					for (int l = 0; l < map.length; l++) {
						nmap[l+1] = map[l] + 1;
					}
					nc.setIndexMapping(nmap);
					if (name.startsWith(AbstractExplorer.DIM_PREFIX)) { // increment dim: number
						int d = Integer.parseInt(name.substring(AbstractExplorer.DIM_PREFIX.length()));
						name = AbstractExplorer.DIM_PREFIX + (d+1);
					}
				} else {
					nc.setIndexMapping(map.clone());
				}
				nc.setAxisNumber(i);
				as.addChoice(name, nc, ias.getOrder(k));
			}

			as.selectAxis(0);
		}

		return new DatasetSelection(InspectorType.LINESTACK, newAxes, allData);
	}

	private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();

	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}

	@Override
	public ISelection getSelection() {
		return multipleSelection;
	}

	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		listeners.remove(listener);
	}

	@Override
	public void setSelection(ISelection selection) {
		if (selection instanceof DatasetSelection)
			multipleSelection = (DatasetSelection) selection;

		SelectionChangedEvent e = new SelectionChangedEvent(this, multipleSelection);
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
	DataHolder h;
	IMetaData m;
	ILazyDataset d;
	Serializable mv;
	List<AxisSelection> asl;

	public SelectedFile(int index, IFile file) {
		i = index;
		f = new File(file.getLocationURI());
		if (f == null || !f.canRead())
			throw new IllegalArgumentException("File '" + file.getName() + "' does not exist or can not be read");
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

	public boolean hasDataHolder() {
		return h != null;
	}

	public boolean hasData() {
		return d != null;
	}

	public void setMetadata(IMetaData metadata) {
		m = metadata;
	}

	public void setDataHolder(DataHolder holder) {
		h = holder;
		if (h != null)
			m = h.getMetadata();
	}

	public boolean setData(String key) {
		d = h.getLazyDataset(key);
		return d != null;
	}

	public void resetData() {
		d = null;
	}

	public ILazyDataset getData() {
		return d;
	}

	public ILazyDataset getAxis(String key) {
		return h != null ? h.getLazyDataset(key) : null;
	}

	private final static String INDEX = "index";
	public ILazyDataset getMetaValue() {
		if (mv == null) {
			AbstractDataset a = AbstractDataset.array(Integer.valueOf(i));
			a.setName(INDEX);
			return a;
		}
		if (mv instanceof ILazyDataset)
			return (ILazyDataset) mv;
		return AbstractDataset.array(mv);
	}

	public boolean setMetaValue(String key) {
		if (m == null)
			return false;

		try {
			mv = m.getMetaValue(key);
			if (mv instanceof String) {
				mv = Utils.parseValue((String) mv); // TODO parse common multiple values string
				if (mv != null) {
					AbstractDataset a = AbstractDataset.array(mv);
					a.setName(key);
					mv = a;
				}
			}
			if (mv == null && h != null) {
				mv = h.getDataset(key);
			}
		} catch (Exception e) {
		}
		return mv != null;
	}

	public void setAxisSelections(List<AxisSelection> axisSelectionList) {
		asl = axisSelectionList;
	}

	public List<AxisSelection> getAxisSelections() {
		return asl;
	}

	public boolean hasAxisSelections() {
		return asl != null;
	}
}
