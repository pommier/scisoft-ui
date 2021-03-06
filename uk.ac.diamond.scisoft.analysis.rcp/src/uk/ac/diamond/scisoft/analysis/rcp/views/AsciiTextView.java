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

package uk.ac.diamond.scisoft.analysis.rcp.views;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Method;
import java.util.Timer;
import java.util.TimerTask;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.handlers.AsciiMonitorAction;

import com.swtdesigner.SWTResourceManager;
import org.apache.commons.io.FileUtils;

/**
 *
 */
public class AsciiTextView extends ViewPart {

	private static final Logger logger = LoggerFactory.getLogger(AsciiTextView.class);
	
	/**
	 * 
	 */
	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.views.AsciiTextView"; //$NON-NLS-1$
	
	private Text    text;
	private boolean monitoringFile = false;
	private File    file;
	private Timer   timer;

	private Action saveAction;

	/**
	 * 
	 */
	public AsciiTextView() {
	}

	/**
	 * Create contents of the view part.
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout(SWT.HORIZONTAL));
		{
			text = new Text(container, SWT.BORDER | SWT.READ_ONLY | SWT.H_SCROLL | SWT.V_SCROLL | SWT.CANCEL | SWT.MULTI | SWT.DOUBLE_BUFFERED);
			text.setFont(SWTResourceManager.getFont("Courier New", 10, SWT.NORMAL));
		}

		createActions();
		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Create the actions.
	 */
	private void createActions() {
		// Create the actions
		saveAction = new Action() {
			@Override
			public void run() {
				saveText();
			}
		};
		saveAction.setToolTipText("Save text");
		saveAction.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/script_save.png"));
	}

	/**
	 * Initialize the toolbar.
	 */
	private void initializeToolBar() {
		IToolBarManager toolbarManager = getViewSite().getActionBars()
				.getToolBarManager();
		toolbarManager.add(saveAction);
	}

	/**
	 * Initialize the menu.
	 */
	private void initializeMenu() {
		getViewSite().getActionBars().getMenuManager();
	}

	@Override
	public void setFocus() {
		text.setFocus();
	}

	/**
	 * DO NOT load large files with this method. It is all read to memory.
	 * @param file
	 */
	public void load(File file) {
		
		monitoringFile = false;
		updateMonitoring();
		setPartName(file.getName());
		this.file = file;
		refreshFile();
	}

	/**
	 * Set text data to display
	 */
	public void setData(String textData) {
		text.setText(textData);
	}

	/**
	 * Save text to file
	 */
	public void saveText() {
		FileDialog dialog = new FileDialog(getSite().getShell(), SWT.SAVE);
		dialog.setOverwrite(true);
		dialog.setFileName("text.txt");
		dialog.setFilterExtensions(new String[] { ".txt" });
		dialog.setFilterNames(new String[] { "Ascii text" });

		String fileName = dialog.open();
		if (fileName == null) {
			return;
		}

		try {
			final PrintStream stream = new PrintStream(fileName);

			getSite().getShell().getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					stream.append(text.getText());
					stream.close();
				}
			});
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	private String refreshFile() {
		try {
			final String allText = FileUtils.readFileToString(file);
			text.setText(allText);
			return allText;
		} catch (IOException e) {
			logger.error("Cannot read file "+file, e);
			return "";
		}
	}

	/**
	 * Can switch on / off monitoring here
	 */
	public void toggleMonitor() {
		this.monitoringFile = !monitoringFile;
		updateMonitoring();
	}

	private void updateMonitoring() {
		final IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
		final IContributionItem[] items = toolbarManager.getItems();
		
		// TODO Find out how to do this properly. Looked many times but never seem to find out
		// something good. Paul may have not on XYPlot.
		for (int i = 0; i < items.length; i++) {
			if (items[i] instanceof CommandContributionItem) {
				final CommandContributionItem c = (CommandContributionItem)items[i];
				if (c.getId().equals(AsciiMonitorAction.ID)) {
					try {
						final Method method = CommandContributionItem.class.getDeclaredMethod("setChecked", boolean.class);
						method.setAccessible(true);
						method.invoke(c, monitoringFile);
					} catch (Exception ignored) {
						// Not critical to do
					}
				}
			}
		}
		
		if (monitoringFile) {
			this.timer = new Timer("Update text timer", false);		
			this.timer.schedule(new TimerTask(){
				@Override
				public void run() {
					getSite().getShell().getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							final String allText = refreshFile();
							text.setSelection(allText.lastIndexOf('\n')+1);
						}
					});
					// TODO System preference needed one day.
				}}, 0, 5000);
		} else {
			if (timer!=null) this.timer.cancel();
		}
	}

	@Override
	public void dispose() {
		if (timer!=null) this.timer.cancel();
		super.dispose();
	}
	
}
