/*
 * Copyright (c) 2012 European Synchrotron Radiation Facility,
 *                    Diamond Light Source Ltd.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */ 

package uk.ac.diamond.sda.meta.views;

import java.io.Serializable;
import java.util.Collection;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;

/**
 * @author suchet + gerring
 * 
 */
public class MetadataTableView extends ViewPart {

	public static final String ID = "fable.imageviewer.views.HeaderView";

	private static final Logger logger = LoggerFactory.getLogger(MetadataTableView.class);

	private IMetaData meta;
	private TableViewer table;

	/**
	 * 
	 */
	public MetadataTableView() {
	}

	@Override
	public void createPartControl(final Composite parent) {

		final Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new GridLayout(1, false));
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// GridUtils.removeMargins(container);

		final Text searchText = new Text(container, SWT.BORDER | SWT.SEARCH | SWT.ICON_SEARCH | SWT.ICON_CANCEL);
		searchText.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL));
		searchText.setToolTipText("Search on data set name or expression value.");

		this.table = new TableViewer(container, SWT.FULL_SELECTION | SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL
				| SWT.BORDER);

		table.getTable().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.getTable().setLinesVisible(true);
		table.getTable().setHeaderVisible(true);

		final TableViewerColumn key = new TableViewerColumn(table, SWT.NONE, 0);
		key.getColumn().setText("Key");
		key.getColumn().setWidth(200);
		key.setLabelProvider(new HeaderColumnLabelProvider(0));

		final TableViewerColumn value = new TableViewerColumn(table, SWT.NONE, 1);
		value.getColumn().setText("Value");
		value.getColumn().setWidth(200);
		value.setLabelProvider(new HeaderColumnLabelProvider(1));

		table.setColumnProperties(new String[] { "Key", "Value" });
		table.setUseHashlookup(true);

		final HeaderFilter filter = new HeaderFilter();
		table.addFilter(filter);
		searchText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				if (parent.isDisposed())
					return;
				filter.setSearchText(searchText.getText());
				table.refresh();
			}
		});
	}

	UIJob updateTable = new UIJob("Updating Metadata Table") {

		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {

			if (table.getControl().isDisposed()) {
				logger.warn("The header table is disposed, cannot update table");
				return Status.CANCEL_STATUS;
			}
			table.setContentProvider(new IStructuredContentProvider() {
				@Override
				public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
				}

				@Override
				public void dispose() {
				}

				@Override
				public Object[] getElements(Object inputElement) {
					try {
						Collection<String> names = meta == null ? null : meta.getMetaNames();
						return names == null ? new Object[] { "" } : names.toArray(new Object[names.size()]);
					} catch (Exception e) {
						return new Object[] { "" };
					}
				}
			});

			if (table.getControl().isDisposed())
				return Status.CANCEL_STATUS;
			table.setInput(new String());
			return Status.OK_STATUS;
		}
	};

	@Override
	public void setFocus() {
		table.getControl().setFocus();
	}

	private class HeaderColumnLabelProvider extends ColumnLabelProvider {
		private int column;

		public HeaderColumnLabelProvider(int col) {
			this.column = col;
		}

		@Override
		public String getText(final Object element) {
			if (column == 0)
				return element.toString();
			if (column == 1)
				try {
					Serializable value = meta == null ? null : meta.getMetaValue(element.toString());
					return value == null ? "" : value.toString();
				} catch (Exception ignored) {
					// Null allowed
				}
			return "";
		}
	}

	class HeaderFilter extends ViewerFilter {

		private String searchString;

		public void setSearchText(String s) {
			if (s == null)
				s = "";
			this.searchString = ".*" + s.toLowerCase() + ".*";
		}

		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (searchString == null || searchString.length() == 0) {
				return true;
			}

			final String name = (String) element;

			if (name == null || "".equals(name))
				return true;

			if (name.toLowerCase().matches(searchString)) {
				return true;
			}
			if (name.toLowerCase().matches(searchString)) {
				return true;
			}

			return false;
		}
	}

	public void setMeta(IMetaData meta) {
		this.meta = meta;
		updateTable.schedule();
	}

}
