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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.legend;


import gda.observable.IObserver;

import java.util.Iterator;
import java.util.LinkedList;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.TableEditor;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DAppearance;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DUIAdapter;
/**
 *
 */
public class LegendTable extends LegendComponent implements SelectionListener, KeyListener {

	private Table tblLegend;
	private LinkedList<TableEditor> editors;
	private LinkedList<Button> buttons;
	private Plot1DGraphTable plotTable;
	private static final int DEL_KEY = 127;
	
	/**
	 * @param parent
	 * @param style
	 */
	public LegendTable(Composite parent, int style) {
		
		super(parent, style);
		this.setLayout(new GridLayout(1, false));
		editors = new LinkedList<TableEditor>();
		buttons = new LinkedList<Button>();
		observers = new LinkedList<IObserver>();
		tblLegend = new Table(this, SWT.DOUBLE_BUFFERED );
		tblLegend.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		tblLegend.setHeaderVisible(false);
		tblLegend.setLinesVisible(true);
		tblLegend.setEnabled(true);
		tblLegend.addKeyListener(this);
		{
			TableColumn tblclmnColour = new TableColumn(tblLegend, SWT.NONE);
			tblclmnColour.setWidth(82);
			tblclmnColour.setResizable(false);
			tblclmnColour.setText("Colour");
		}
		{
			TableColumn tblclmnDescription = new TableColumn(tblLegend, SWT.NONE);
			tblclmnDescription.setWidth(250);
			tblclmnDescription.setResizable(true);
			tblclmnDescription.setText("Description");
		}
		{
			TableColumn tblclmnVisible = new TableColumn(tblLegend, SWT.NONE);
			tblclmnVisible.setWidth(80);
			tblclmnVisible.setResizable(false);
			tblclmnVisible.setText("Visible");
		}
		
		// The headers have to be visible to resize the column. Sometimes people
		// use long dataset names, therefore need to resize the legend to see
		// what's plotted. We add a right click action to the table to show headers
		// which then allow it to be resized. There may be an easier way to do this.
		final MenuManager man = new MenuManager();
		man.add(new Action("Resizable", IAction.AS_CHECK_BOX) {
			private boolean show = false;
			 @Override
			public void run() {
				show = !show;
				tblLegend.setHeaderVisible(show);
			}
		});
		final Menu menu = man.createContextMenu(tblLegend);
		tblLegend.setMenu(menu);
	}
	
	/**
	 * Update the legend table
	 * @param table
	 */
	@Override
	public void updateTable(Plot1DGraphTable table)
	{
		removeAllTableEntries();
		plotTable = table;
		for (int i = 0; i < table.getLegendSize(); i++)
		{
			Plot1DAppearance plotApp = table.getLegendEntry(i);
			
			TableItem newItem = new TableItem(tblLegend,SWT.DOUBLE_BUFFERED);
			TableEditor editor = new TableEditor(tblLegend);
			editors.add(editor);
			TableCanvas canvas = new TableCanvas(tblLegend,SWT.DOUBLE_BUFFERED);
			plotApp.updateCanvas(canvas);
			canvas.pack();
			editor.minimumWidth = canvas.getSize().x;
			editor.horizontalAlignment = SWT.CENTER;
			editor.setEditor(canvas,newItem,0);
			editor.grabHorizontal = true;
			newItem.setText(1,plotApp.getName());
			TableEditor editor2 = new TableEditor(tblLegend);
			editor2.horizontalAlignment = SWT.CENTER;
			editor2.grabHorizontal = true;
			editor2.grabVertical = true;
			Button checkButton = new Button(tblLegend,SWT.CHECK|SWT.DOUBLE_BUFFERED);
			checkButton.setText("Active");
			checkButton.pack();
			checkButton.addSelectionListener(this);
			checkButton.setBackground(this.getDisplay().getSystemColor(SWT.COLOR_WHITE));
			buttons.add(checkButton);
			if (plotApp.isVisible())
				checkButton.setSelection(true);
			else
				checkButton.setSelection(false);
			
			editor2.setEditor(checkButton,newItem,2);
			editors.add(editor2);
		}
	}
	
	private void removeAllTableEntries()
	{
		tblLegend.removeAll();
		Iterator<TableEditor> iter = editors.iterator();
		while (iter.hasNext()) {
			TableEditor editor = iter.next();
			editor.getEditor().dispose();
			editor.dispose();
		}
		editors.clear();
		buttons.clear();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Nothing to do
	}

	private void notifyAllObservers() {
		Iterator<IObserver> iter = observers.iterator();
		while (iter.hasNext()) {
			iter.next().update(this, null);
		}
	}
	
	private void notifyAllListeners(int selectNr) {
		Iterator<LegendChangeEventListener> iter = listeners.iterator();
		LegendChangeEvent evt = new LegendChangeEvent(this, selectNr);
		while (iter.hasNext()) {
			iter.next().legendDeleted(evt);
		}
	}
	
	@Override
	public void widgetSelected(SelectionEvent e) {
		int index = buttons.indexOf(e.getSource());
		if (index >= 0 && index < plotTable.getLegendSize())
		{
			Plot1DAppearance app = plotTable.getLegendEntry(index);
			Button checkButton = (Button)e.getSource();
			app.setVisible(checkButton.getSelection());
			notifyAllObservers();
		}
	}

	@Override
	public void keyPressed(KeyEvent e) {
		if (e.keyCode == DEL_KEY)
		{
			TableItem item = tblLegend.getItem(tblLegend.getSelectionIndex());
			if (item.getText(1).contains(Plot1DUIAdapter.HISTORYSTRING))
			{
				notifyAllListeners(tblLegend.getSelectionIndex());
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// Nothing to do
		
	}

}
