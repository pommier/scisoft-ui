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

package uk.ac.diamond.scisoft.analysis.rcp.explorers;

import java.util.ArrayList;
import java.util.List;

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
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisChoice;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.AxisSelection;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetSelection;
import uk.ac.gda.monitor.IMonitor;

public class SRSExplorer extends AbstractExplorer implements ISelectionProvider {

	private TableViewer viewer;
	private DataHolder data = null;
	private ISelectionChangedListener listener;
	private Display display = null;
	private SelectionAdapter contextListener = null;

	public SRSExplorer(Composite parent, IWorkbenchPartSite partSite, ISelectionChangedListener valueSelect) {
		super(parent, partSite, valueSelect);

		display = parent.getDisplay();
		setLayout(new FillLayout());

		viewer = new TableViewer(this, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.getTable().setHeaderVisible(true);

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
		// viewer.setInput(getEditorSite());

		listener = new ISelectionChangedListener() {
			
			@Override
			public void selectionChanged(SelectionChangedEvent arg0) {
				DatasetSelection datasetSelection = new DatasetSelection(getAxes(), getActiveData());
				setSelection(datasetSelection);
			}
		};

		viewer.addSelectionChangedListener(listener);

		if (metaValueListener != null) {
			final SRSExplorer provider = this;
			contextListener = new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					int i = viewer.getTable().getMenu().indexOf((MenuItem) e.widget);
					SelectionChangedEvent ce = new SelectionChangedEvent(provider, new MetadataSelection(metaNames.get(i)));
					metaValueListener.selectionChanged(ce);
				}
			};
		}
	}

	public TableViewer getViewer() {
		return viewer;
	}

	private List<AxisSelection> getAxes() {
		List<AxisSelection> axes = new ArrayList<AxisSelection>();

		AxisSelection axisSelection = new AxisSelection(data.getDataset(0).getShape()[0]);

		AbstractDataset autoAxis = AbstractDataset.arange(data.getDataset(0).getShape()[0], AbstractDataset.INT32);
		autoAxis.setName("Index");
		AxisChoice newChoice = new AxisChoice(autoAxis);
		newChoice.setDimension(new int[] { 0 });
		axisSelection.addSelection(newChoice, 0);

		for (int i = 0; i < data.size(); i++) {
			ILazyDataset ldataset = data.getLazyDataset(i);
			if (ldataset instanceof AbstractDataset) {
				newChoice = new AxisChoice(ldataset);
				newChoice.setDimension(new int[] { 0 });
				axisSelection.addSelection(newChoice, i + 1);
			}
		}

		axisSelection.selectAxis(0);
		axes.add(axisSelection);

		return axes;
	}

	private ILazyDataset getActiveData() {
		ISelection selection = viewer.getSelection();
		Object obj = ((IStructuredSelection) selection).getFirstElement();
		if (obj == null) {
			return data.getLazyDataset(0);
		}
		return (ILazyDataset) obj;
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

	private class ViewLabelProvider extends LabelProvider implements ITableLabelProvider {
		@Override
		public String getColumnText(Object obj, int index) {
			if (obj instanceof IDataset) {
				IDataset dataset = (IDataset) obj;
				if (index == 0)
					return dataset.getName();
				if (index == 1)
					return dataset.min().toString();
				if (index == 2)
					return dataset.max().toString();
				if (index == 3) {
					String[] parts = dataset.elementClass().toString().split("\\.");
					return parts[parts.length - 1];
				}
			}
			if (obj instanceof ILazyDataset) {
				ILazyDataset dataset = (ILazyDataset) obj;
				if (index == 0)
					return dataset.getName();
				if (index == 1)
					return "Not Available";
				if (index == 2)
					return "Not Available";
				if (index == 3)
					return "Not Available";
			}

			return null;
		}

		@Override
		public Image getColumnImage(Object obj, int index) {
			if (index == 0)
				return getImage(obj);
			return null;
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
		}
	}

	private List<ISelectionChangedListener> listeners = new ArrayList<ISelectionChangedListener>();
	private DatasetSelection dSelection = null;
	private ArrayList<String> metaNames;
	private String fileName;

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
		else return;

		SelectionChangedEvent e = new SelectionChangedEvent(this, dSelection);
		for (ISelectionChangedListener listener : listeners)
			listener.selectionChanged(e);
	}

	@Override
	public void dispose() {
		viewer.removeSelectionChangedListener(listener);
	}

	@Override
	public DataHolder loadFile(String fileName, IMonitor mon) throws Exception {
		if (fileName == this.fileName)
			return data;

		return LoaderFactory.getData(fileName, mon);
	}

	@Override
	public void loadFileAndDisplay(String fileName, IMonitor mon) throws Exception {
		this.fileName = fileName;

		data = LoaderFactory.getData(fileName, mon);
		if (data != null) {
			if (display != null) {
				final IMetaData meta = data.getMetadata();

				display.asyncExec(new Runnable() {
					
					@Override
					public void run() {
						viewer.setInput(data);
						display.update();
						if (metaValueListener != null) {
							addMenu(meta);
						}
					}
				});
			}
			DatasetSelection datasetSelection = new DatasetSelection(getAxes(), getActiveData());
			setSelection(datasetSelection);
		}
	}

	public void selectItemSelection() {
		DatasetSelection datasetSelection = new DatasetSelection(getAxes(), getActiveData());
		setSelection(datasetSelection);
	}

	private void addMenu(IMetaData meta) {
		// create context menu and handling
		if (meta != null) {
			try {
				Menu context = new Menu(viewer.getControl());
				metaNames = new ArrayList<String>();
				for (String n : meta.getMetaNames()) {
					try {
						String v = meta.getMetaValue(n).toString();
						Double.parseDouble(v);
						metaNames.add(n);
						MenuItem item = new MenuItem(context, SWT.PUSH);
						item.addSelectionListener(contextListener);
						item.setText(n + " = " + v);
					} catch (NumberFormatException e) {
						
					}
				}

				viewer.getTable().setMenu(context);
			} catch (Exception e) {
			}
		}
	}
}
