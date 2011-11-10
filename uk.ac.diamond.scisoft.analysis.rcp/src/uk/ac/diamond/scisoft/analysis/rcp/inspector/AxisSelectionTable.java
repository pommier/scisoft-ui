/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.util.ArrayList;
import java.util.List;

//import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
//import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;

/**
 * Class for data axis selection GUI
 */
public class AxisSelectionTable extends Composite {
	private List<AxisSelection> axes = null;
	private TableViewer axesSelector;
	private ArrayList<TableColumn> asColumns;
	private ICellEditorListener cListener;
	private static final int asWidth = 60;
	private AxisSelectionLabelProvider lProvider = null;
//	private TableColumnLayout tcLayout;

	/**
	 * @param parent
	 */
	public AxisSelectionTable(Composite parent) {
		this(parent, null);
	}

	/**
	 * @param parent
	 * @param listener 
	 */
	public AxisSelectionTable(Composite parent, ICellEditorListener listener) {
		super(parent, SWT.NONE);

		cListener = listener;
		axesSelector = new TableViewer(this, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL | SWT.H_SCROLL | SWT.FULL_SELECTION | SWT.HIDE_SELECTION);
//		tcLayout = new TableColumnLayout();
//		setLayout(tcLayout);
		setLayout(new FillLayout());

		asColumns = new ArrayList<TableColumn>();
		TableViewerColumn tVCol = new TableViewerColumn(axesSelector, SWT.NONE);
		TableColumn tCol = tVCol.getColumn();
		asColumns.add(tCol);
		tCol.setText("Dim");
		tCol.setToolTipText("Dimension of dataset array");
		tCol.setWidth(40);
		tCol.setResizable(false);
//		tcLayout.setColumnData(tCol, new ColumnWeightData(0, 40, false));

		int columns = 5;
		for (int i = 1; i < columns; i++) {
			tVCol = new TableViewerColumn(axesSelector, SWT.NONE);
			tCol = tVCol.getColumn();
			asColumns.add(tCol);
			tCol.setText(String.valueOf(i));
			tCol.setWidth(asWidth);
			tCol.setMoveable(false);
//			tcLayout.setColumnData(tCol, new ColumnWeightData(10, asWidth));
			tVCol.setEditingSupport(new AxisSelectionEditing(axesSelector, i, cListener));
		}

		final Table tab = axesSelector.getTable();
		tab.setHeaderVisible(true);

		axesSelector.setContentProvider(new AxisSelectionContentProvider());
		lProvider = new AxisSelectionLabelProvider(axes);
		axesSelector.setLabelProvider(lProvider);
	}

	/**
	 * @param axes
	 */
	public void setInput(List<AxisSelection> axes) {
		this.axes = axes;
		lProvider.setAxes(axes);
		axesSelector.setInput(axes);
	}

	/**
	 * 
	 */
	public void redrawASTable() {
		int maxColumns = 0;
		for (AxisSelection a: axes) {
			if (maxColumns < a.size())
				maxColumns = a.size();
		}
		maxColumns++; // compensate for first column

		int curColumns = asColumns.size();
		int diff = maxColumns - curColumns;

		for (int i = 0; i < curColumns; i++) { // make all columns visible
			if (asColumns.get(i).getWidth() == 0) {
				asColumns.get(i).setWidth(asWidth);
//				asColumns.get(i).setResizable(true);
			}
		}
		if (diff > 0) {
			for (int i = curColumns; i < maxColumns; i++) {
				TableViewerColumn tVCol = new TableViewerColumn(axesSelector, SWT.NONE, i);
				TableColumn tCol = tVCol.getColumn();
				asColumns.add(tCol);
				tCol.setText(String.valueOf(i));
				tCol.setWidth(asWidth);
//				tCol.setResizable(true);
				tCol.setMoveable(false);
//				tcLayout.setColumnData(tCol, new ColumnWeightData(10, asWidth));
				tVCol.setEditingSupport(new AxisSelectionEditing(axesSelector, i, cListener));
				tVCol.setLabelProvider(axesSelector.getLabelProvider(0)); // fix for NPE as new column doesn't seem to inherit label provider!
			}
		} else {
			for (int i = curColumns-1; i >= maxColumns; i--) {
				asColumns.get(i).setWidth(0);
//				asColumns.get(i).setResizable(false);
				// above code does not hide columns when using a TableColumnLayout
//				asColumns.get(i).dispose(); 
//				asColumns.remove(i);
			}
		}
		axesSelector.refresh();
	}

	/**
	 * Refresh table
	 */
	public void refresh() {
		axesSelector.refresh();
	}
}

class AxisSelectionContentProvider implements IStructuredContentProvider {
	@Override
	public Object[] getElements(Object inputElement) {
		if (inputElement == null) {
			return null;
		}
		return ((List<?>) inputElement).toArray();
	}

	@Override
	public void dispose() {
	}

	@Override
	public void inputChanged(Viewer arg0, Object arg1, Object arg2) {
	}
}

class AxisSelectionLabelProvider implements ITableLabelProvider {
	private final Image CHECKED = AnalysisRCPActivator.getImageDescriptor("icons/tick.png").createImage();
	private final Image UNCHECKED = AnalysisRCPActivator.getImageDescriptor("icons/cross.png").createImage();

	List<AxisSelection> axes;

	public AxisSelectionLabelProvider(List<AxisSelection> axes) {
		this.axes = axes;
	}

	public void setAxes(List<AxisSelection> axes) {
		this.axes = axes;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		if (axes == null) return null;
		if (columnIndex == 0) return null;
		AxisSelection axis = (AxisSelection) element;
		columnIndex--; // compensate for first column
		if (columnIndex >= axis.size()) return null;
		return axis.isSelected(columnIndex) ? CHECKED : UNCHECKED;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (axes == null) return null;
		AxisSelection axis = (AxisSelection) element;
		if (columnIndex == 0) {
			return String.valueOf(axes.indexOf(axis)+1);
		}
		columnIndex--; // compensate for first column
		if (columnIndex >= axis.size()) return null;
		return axis.getName(columnIndex);
	}

	@Override
	public void addListener(ILabelProviderListener arg0) {
	}

	@Override
	public void dispose() {
	}

	@Override
	public boolean isLabelProperty(Object arg0, String arg1) {
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener arg0) {
	}
}

/**
 * Contains GUI logic to manipulate axis selection
 */
class AxisSelectionEditing extends EditingSupport {
	private CheckboxCellEditor editor;
	private int index;

	public AxisSelectionEditing(ColumnViewer viewer, int column, ICellEditorListener cListener) {
		super(viewer);
		editor = new CheckboxCellEditor(null, SWT.CHECK);
		if (cListener != null)
			editor.addListener(cListener);
		this.index = column - 1; // shift as first column in table is axis number
	}

	@Override
	protected boolean canEdit(Object arg0) {
		return true;
	}

	@Override
	protected CellEditor getCellEditor(Object arg0) {
		return editor;
	}

	@Override
	protected Object getValue(Object element) {
		AxisSelection axis = (AxisSelection) element;
		if (index >= axis.size())
			return null;
		return axis.isSelected(index);
	}

	@Override
	protected void setValue(Object element, Object value) {
		AxisSelection axis = (AxisSelection) element;
		if (axis.isSelected(index))
			return; // do nothing if already true

		if ((Boolean) value) {
			assert index < axis.size();
			axis.selectAxis(index);

			getViewer().update(element, null);
		}
	}
}
