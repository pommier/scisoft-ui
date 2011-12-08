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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.roi;

import java.util.List;

import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.CheckboxCellEditor;
import org.eclipse.jface.viewers.ColumnViewer;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.PaletteData;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;

/**
 * Class that extends a table viewer for regions of interests
 */
public abstract class ROITableViewer {
	private TableViewer tViewer;

	private static final int CHECKCOLNUM = 0;

	private Menu contextMenu;

	abstract public String[] getTitles();
	abstract public String[] getTipTexts();
	abstract public  int[] getWidths();
	abstract public  String content(Object element, int columnIndex);

	/**
	 * Edit selection in menu
	 */
	public static final int ROITABLEMENU_EDIT = 0;
	/**
	 * Copy selection in menu
	 */
	public static final int ROITABLEMENU_COPY = 1;
	/**
	 * Delete selection in menu
	 */
	public static final int ROITABLEMENU_DELETE = 2;
	/**
	 * Delete all selection in menu
	 */
	public static final int ROITABLEMENU_DELETE_ALL = 3;

	/**
	 * @return context menu
	 */
	public Menu getContextMenu() {
		return contextMenu;
	}

	/**
	 * Table viewer for ROIs A selection listener and a cell editor listener are required for responding to context menu
	 * and plot enablement events
	 * 
	 * @param parent
	 * @param slistener
	 *            selection listener for context menu
	 * @param clistener
	 *            cell editor listener
	 */
	public ROITableViewer(Composite parent, SelectionListener slistener, ICellEditorListener clistener) {
		tViewer = new TableViewer(parent, SWT.BORDER | SWT.SINGLE | SWT.V_SCROLL);
		String[] titles = getTitles();
		String[] tiptext = getTipTexts();
		int[] widths = getWidths();

		for (int i = 0; i < titles.length; i++) {
			TableViewerColumn tVCol = new TableViewerColumn(tViewer, SWT.NONE);
			TableColumn tCol = tVCol.getColumn();
			tCol.setText(titles[i]);
			tCol.setToolTipText(tiptext[i]);
			tCol.setWidth(widths[i]);
			tCol.setMoveable(true);
			tVCol.setEditingSupport(new ROIEditingSupport(tViewer, i, clistener));
		}

		final Table table = tViewer.getTable();

		table.setHeaderVisible(true);
		tViewer.setContentProvider(new ROIContentProvider());
		tViewer.setLabelProvider(new ROILabelProvider());


		contextMenu = new Menu(tViewer.getControl());
		// NB preserve this order according to constants above
		MenuItem editItem = new MenuItem(contextMenu, SWT.PUSH);
		editItem.addSelectionListener(slistener);
		editItem.setText("Edit");
		MenuItem copyItem = new MenuItem(contextMenu, SWT.PUSH);
		copyItem.addSelectionListener(slistener);
		copyItem.setText("Copy to current");
		MenuItem deleteItem = new MenuItem(contextMenu, SWT.PUSH);
		deleteItem.addSelectionListener(slistener);
		deleteItem.setText("Delete");
		MenuItem deleteAllItem = new MenuItem(contextMenu, SWT.PUSH);
		deleteAllItem.addSelectionListener(slistener);
		deleteAllItem.setText("Delete all");
		table.setMenu(contextMenu);
	}

	public void addLeftClickListener(ISelectionChangedListener scListener){
		tViewer.addSelectionChangedListener(scListener);
	}
	/**
	 * Refresh table viewer
	 */
	final public void refresh() {
		tViewer.refresh();
	}

	/**
	 * @param obj
	 */
	final public void setInput(Object obj) {
		tViewer.setInput(obj);
	}

	/**
	 * @return a selection
	 */
	final public ISelection getSelection() {
		return tViewer.getSelection();
	}

	final protected class ROIEditingSupport extends EditingSupport {
		private CheckboxCellEditor editor;
		private int column;

		public ROIEditingSupport(ColumnViewer viewer, int column, ICellEditorListener listener) {
			super(viewer);
			if (column == CHECKCOLNUM) {
				editor = new CheckboxCellEditor(null, SWT.CHECK);
				editor.addListener(listener);
			}
			this.column = column;
		}

		@Override
		protected boolean canEdit(Object element) {
			return true;
		}

		@Override
		protected CellEditor getCellEditor(Object element) {
			return editor;
		}

		@Override
		protected Object getValue(Object element) {
			IRowData lrdata = (IRowData) element;
			if (column == CHECKCOLNUM) {
				return lrdata.isPlot();
			}
			return null;
		}

		@Override
		protected void setValue(Object element, Object value) {
			IRowData lrdata = (IRowData) element;
			if (column == CHECKCOLNUM) {
				lrdata.setPlot((Boolean) value);
			}
			getViewer().update(element, null);
		}
	}

	final protected class ROIContentProvider implements IStructuredContentProvider {
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
		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}
	}

	final protected class ROILabelProvider implements ITableLabelProvider {
		private final Image CROSSED = AnalysisRCPActivator.getImageDescriptor("icons/cross.png").createImage();

		private final int height = CROSSED.getImageData().height;
		private final int width = CROSSED.getImageData().width;
		private final int linehalfthickness = 2;
		private final RGB white = new RGB(255, 255, 255);

		private Image drawImage(RGB rgb) {
			PaletteData palette = new PaletteData(new RGB[] { white, rgb });
			ImageData data = new ImageData(width, height, 2, palette);
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data.setAlpha(x, y, 0);
				}
			}
			for (int y = -linehalfthickness; y < linehalfthickness; y++) {
				for (int x = 0; x < width; x++) {
					data.setPixel(x, height / 2 + y, 1);
					data.setAlpha(x, height / 2 + y, 255);
				}
			}
			Image image = new Image(CROSSED.getDevice(), data);
			return image;
		}

		@Override
		public Image getColumnImage(Object element, int columnIndex) {
			IRowData cROIData = (IRowData) element;
			if (cROIData != null) {
				if (columnIndex == CHECKCOLNUM) {
					if (cROIData.isPlot()) {
						return drawImage(cROIData.getPlotColourRGB());
					}
					return CROSSED;
				}
			}
			return null;
		}

		@Override
		public String getColumnText(Object element, int columnIndex) {
			return content(element, columnIndex);
		}

		@Override
		public void addListener(ILabelProviderListener listener) {
		}

		@Override
		public void dispose() {
		}

		@Override
		public boolean isLabelProperty(Object element, String property) {
			return false;
		}

		@Override
		public void removeListener(ILabelProviderListener listener) {
		}
	}
}