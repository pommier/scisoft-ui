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

package uk.ac.diamond.scisoft.analysis.rcp.views;

import gda.analysis.io.ScanFileHolderException;

import java.io.File;
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchPartSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.IProgressService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.hdf5.HDF5TreeExplorer;
import uk.ac.gda.monitor.ProgressMonitorWrapper;

public class HDF5TreeView extends ViewPart {
	HDF5TreeExplorer hdfxp;
	Display display;
	FileDialog fileDialog = null;
	
	/**
	 * 
	 */
	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.views.HDF5TreeView"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(HDF5TreeView.class);

	@Override
	public void createPartControl(Composite parent) {
		display = parent.getDisplay();
		IWorkbenchPartSite site = getSite();
		hdfxp = new HDF5TreeExplorer(parent, site, SWT.NONE);
		site.setSelectionProvider(hdfxp);

		// set up the help context
		PlatformUI.getWorkbench().getHelpSystem().setHelp(hdfxp, "uk.ac.diamond.scisoft.analysis.rcp.hdf5View");

		createActions();

		initializeToolBar();
		initializeMenu();
	}

	/**
	 * Load file given by path into view
	 * @param path
	 */
	public void loadTree(final String path) {
		IProgressService service = (IProgressService) getSite().getService(IProgressService.class);

		try {
			// Changed to cancellable as sometimes loading the tree takes ages and you
			// did not mean to choose the file.
			service.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					try {
						loadTree(path, monitor);
					} catch (ScanFileHolderException e) {
						logger.error("Could not load HDF5 file: {}", e);
					} catch (Exception ee) {
						logger.error("Problem with loader: is the library path set correctly?", ee);
					} finally {
						monitor.done();
					}
				}
			});
		} catch (Exception e) {
			logger.error("Could not open HDF5 file", e);
		}
	}

	public void loadTree(final String path, IProgressMonitor monitor) throws Exception {
		monitor.beginTask("Opening HDF5 file " + path, 10);
		monitor.worked(1);
		if (monitor.isCanceled()) return;

		long start = System.nanoTime();
		hdfxp.loadFile(path, new ProgressMonitorWrapper(monitor));
		if (hdfxp.getHDF5Tree() != null) {
			display.syncExec(new Runnable() {
				@Override
				public void run() {
					setPartName((new File(path)).getName());
				}
			});
		}
		logger.info("Setting tree took {}s", (System.nanoTime() - start)*1e-9);
	}

	/**
	 * call to bring up a file dialog
	 */
	public void loadTreeUsingFileDialog() {
		if (fileDialog == null) {
			fileDialog = new FileDialog(getSite().getShell(), SWT.OPEN);
		}

		String [] filterNames = new String [] {"HDF5 files", "All Files (*)"};
		String [] filterExtensions = new String [] {"*.nxs;*.h5;*.hdf5", "*"};
		fileDialog.setFilterNames(filterNames);
		fileDialog.setFilterExtensions(filterExtensions);
		final String path = fileDialog.open();

		if (path != null) {
			loadTree(path);
		}
	}
	

	@Override
	public void setFocus() {
		hdfxp.setFocus();
	}

	/**
	 * Create the actions
	 */
	private void createActions() {
//		IToolBarManager tbManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the toolbar
	 */
	private void initializeToolBar() {
		@SuppressWarnings("unused")
		IToolBarManager toolbarManager = getViewSite().getActionBars().getToolBarManager();
	}

	/**
	 * Initialize the menu
	 */
	private void initializeMenu() {
		@SuppressWarnings("unused")
		IMenuManager menuManager = getViewSite().getActionBars().getMenuManager();
	}

	public void expandAll() {
		hdfxp.expandAll();
	}

}
