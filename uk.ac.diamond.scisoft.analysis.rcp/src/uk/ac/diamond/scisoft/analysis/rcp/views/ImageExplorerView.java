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

import gda.observable.IObserver;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IExtensionPoint;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.PlotServer;
import uk.ac.diamond.scisoft.analysis.PlotServerProvider;
import uk.ac.diamond.scisoft.analysis.plotserver.FileOperationBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GridImageEntry;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.imagegrid.ImagePlayBack;
import uk.ac.diamond.scisoft.analysis.rcp.imagegrid.SWTGridEntry;
import uk.ac.diamond.scisoft.analysis.rcp.imagegrid.SWTImageGrid;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.actions.ImageExplorerDirectoryChooseAction;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.GlobalColourMaps;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;
import uk.ac.diamond.scisoft.analysis.rcp.util.CommandExecutor;

public class ImageExplorerView extends ViewPart implements IObserver, SelectionListener {

	// Adding in some logging to help with getting this running
	transient private static final Logger logger = LoggerFactory.getLogger(ImageExplorerView.class);

	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView";

	public static final Object FOLDER_UPDATE_MARKER = new Object();

	private String plotViewName = "ImageExplorer View";
	private PlotServer plotServer = null;
	private Canvas canvas;
	private Group compHUD = null;
	private Composite locationRow;
	private Button btnHistoryBack;
	private Button btnHistoryForw;
	private Button btnPlay;
	private Button btnRewind;
	private Button btnStop;
	private Button btnForward;
	private Button btnPlayLoop;
	private Slider sldProgress;
	private Combo cmbDirectoryLocation;
	private Image imgPlay;
	private Image imgStill;
	private Label lblLocation;
	private SWTImageGrid imageGrid;
	private GuiBean guiBean = null;
	private UUID plotID = null;
	private final Semaphore locker = new Semaphore(1);
	private Action[] imgExtenions;
	private List<String> filter = new ArrayList<String>();
	private List<String> filesToLoad = null;
	private List<String> history = new ArrayList<String>();
	private int historyPointer = -1;
	private String currentDir = null;
	private ImagePlayBack playback = null;
	private ExecutorService execSvc = null;
	private boolean isLive = false;
	private boolean liveActive = false;
	private boolean monActive = false;
	private int curPosition = -1;

	/**
	 * The last dir path is saved so that when the user
	 * uses this view again, it initiates to the last known folder.
	 */
	private String dirPath;

	private boolean firstBack = false;

	private boolean isDisposed = true;

	private Composite parent;

	private boolean stopLoading = false;

	public ImageExplorerView() {
		plotServer = PlotServerProvider.getPlotServer();
		plotID = UUID.randomUUID();
		logger.info("Image explorer view uuid: {}", plotID);
		execSvc = Executors.newFixedThreadPool(2);
	}

	/**
	 * Get a list of plot views from extension register
	 * 
	 * @return list of views
	 */
	public static List<String> getRegisteredViews() {
		List<String> plotViews = new LinkedList<String>();

		IExtensionRegistry registry = Platform.getExtensionRegistry();

		// Add the default point first, this is where the double click action will go
		IExtensionPoint point = registry.getExtensionPoint("uk.ac.diamond.scisoft.analysis.rcp.ExplorerViewDefault");
		IExtension[] extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			for (int i1 = 0; i1 < elements.length; i1++) {
				if (elements[i1].getName().equals("ViewDefaultRegister")) {
					if (!plotViews.contains(elements[i1].getAttribute("ViewName")))
						plotViews.add(elements[i1].getAttribute("ViewName"));
				}
			}
		}

		// now add all the other contributions to the list
		point = registry.getExtensionPoint("uk.ac.diamond.scisoft.analysis.rcp.ExplorerViewRegister");
		extensions = point.getExtensions();
		for (int i = 0; i < extensions.length; i++) {
			IConfigurationElement[] elements = extensions[i].getConfigurationElements();
			for (int i1 = 0; i1 < elements.length; i1++) {
				if (elements[i1].getName().equals("ViewRegister")) {
					if (!plotViews.contains(elements[i1].getAttribute("ViewName")))
						plotViews.add(elements[i1].getAttribute("ViewName"));
				}
			}
		}
		return plotViews;
	}

	@Override
	public void createPartControl(Composite parent) {
		plotViewName = getViewSite().getRegisteredName();
		plotServer.addIObserver(this);
		setPartName(plotViewName);
		this.parent = parent;
		parent.setLayout(new GridLayout(1, true));

		locationRow = new Composite(parent, SWT.NONE);
		locationRow.setLayout(new GridLayout(4, false));
		{
			btnHistoryBack = new Button(locationRow, SWT.LEFT);
			btnHistoryBack.setEnabled(false);
			btnHistoryBack.setToolTipText("Go back in history");
			btnHistoryBack.setImage(AnalysisRCPActivator.getImageDescriptor("icons/arrow_left.png").createImage());
			btnHistoryBack.addSelectionListener(this);
			btnHistoryForw = new Button(locationRow, SWT.LEFT);
			btnHistoryForw.setEnabled(false);
			btnHistoryForw.setToolTipText("Go forward in history");
			btnHistoryForw.addSelectionListener(this);
			btnHistoryForw.setImage(AnalysisRCPActivator.getImageDescriptor("icons/arrow_right.png").createImage());
			lblLocation = new Label(locationRow, SWT.LEFT);
			lblLocation.setText("Location:");

			cmbDirectoryLocation = new Combo(locationRow, SWT.SINGLE | SWT.LEFT | SWT.BORDER);
			cmbDirectoryLocation.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
			cmbDirectoryLocation.addSelectionListener(this);
		}
		locationRow.setLayoutData(new GridData(SWT.FILL, SWT.NONE, false, false));

		canvas = new Canvas(parent, SWT.DOUBLE_BUFFERED | SWT.V_SCROLL | SWT.H_SCROLL);
		canvas.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		compHUD = new Group(parent, SWT.NONE);
		compHUD.setLayout(new GridLayout(6, false));
		compHUD.setText("Image Playback");
		{
			btnRewind = new Button(compHUD, SWT.PUSH);
			btnRewind.setImage(AnalysisRCPActivator.getImageDescriptor("icons/control_start.png").createImage());
			btnRewind.addSelectionListener(this);
			btnRewind.setToolTipText("Rewind");
			btnPlay = new Button(compHUD, SWT.TOGGLE);
			imgPlay = AnalysisRCPActivator.getImageDescriptor("icons/control_play.png").createImage();
			imgStill = AnalysisRCPActivator.getImageDescriptor("icons/control_pause.png").createImage();
			btnPlay.setImage(imgPlay);
			btnPlay.addSelectionListener(this);
			btnPlay.setToolTipText("Play/Pause");
			btnStop = new Button(compHUD, SWT.PUSH);
			btnStop.setImage(AnalysisRCPActivator.getImageDescriptor("icons/control_stop.png").createImage());
			btnStop.addSelectionListener(this);
			btnStop.setToolTipText("Stop playback");
			btnForward = new Button(compHUD, SWT.PUSH);
			btnForward.setImage(AnalysisRCPActivator.getImageDescriptor("icons/control_end.png").createImage());
			btnForward.addSelectionListener(this);
			btnForward.setToolTipText("Forward");
			btnPlayLoop = new Button(compHUD, SWT.TOGGLE);
			btnPlayLoop.setImage(AnalysisRCPActivator.getImageDescriptor("icons/control_repeat.png").createImage());
			btnPlayLoop.setToolTipText("Playback loop (On/off)");
			btnPlayLoop.addSelectionListener(this);
			sldProgress = new Slider(compHUD, SWT.HORIZONTAL);
			sldProgress.setPageIncrement(1);
			sldProgress.setThumb(1);
			sldProgress.addSelectionListener(this);
			sldProgress.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		}
		compHUD.setLayoutData(new GridData(SWT.FILL, SWT.NONE, true, false));

		imageGrid = new SWTImageGrid(canvas, plotViewName);
		GlobalColourMaps.InitializeColourMaps();
		retainStateFromServer();

		if (dirPath != null && dirPath.length() > 0) {
			dirPath.trim();
			final Job updateDirectory = new Job("Update directory") {

				@Override
				protected IStatus run(IProgressMonitor monitor) {
					ImageExplorerDirectoryChooseAction.setImageFolder(dirPath, filter);
					return Status.OK_STATUS;
				}
			};
			updateDirectory.setUser(true);
			updateDirectory.setPriority(Job.DECORATE);
			updateDirectory.schedule(1000);
			cmbDirectoryLocation.setText(dirPath);
			currentDir = dirPath;
		}
		playback = new ImagePlayBack(parent, getPreferencePlaybackView(), getViewSite().getPage(), sldProgress,
				getPreferenceTimeDelay(), getPreferencePlaybackRate());

		isDisposed = false;
	}

	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		init(site);
		String storedExtensions = "";
		if (memento != null) { // Open dir path
			this.dirPath = memento.getString("DIR");
			storedExtensions = memento.getString("FILTERS");
			if (storedExtensions == null)
				storedExtensions = "";
		}
		imgExtenions = new Action[ImageExplorerDirectoryChooseAction.LISTOFSUFFIX.length];
		MenuManager filterMenu = new MenuManager("File Filters");
		for (int i = 0; i < ImageExplorerDirectoryChooseAction.LISTOFSUFFIX.length; i++) {
			final int number = i;
			imgExtenions[i] = new Action("", IAction.AS_CHECK_BOX) {
				@Override
				public void run() {
					if (this.isChecked()) {
						filter.remove(ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[number]);
					} else {
						filter.add(ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[number]);
					}
				}
			};
			imgExtenions[i].setText(ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[i]);
			imgExtenions[i].setDescription("Filter " + ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[i] + " on/off");
			if (storedExtensions.contains(ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[i])) {
				imgExtenions[i].setChecked(false);
				filter.add(ImageExplorerDirectoryChooseAction.LISTOFSUFFIX[i]);
			} else
				imgExtenions[i].setChecked(true);
			filterMenu.add(imgExtenions[i]);
		}

		site.getActionBars().getMenuManager().add(filterMenu);

	}

	@Override
	public void saveState(IMemento memento) {
		if (dirPath != null && memento != null) {// save dir path
			memento.putString("DIR", dirPath);
		}
		// save filter settings from last session
		if (memento != null) {
			String storeExtensions = "";
			Iterator<String> filterIter = filter.iterator();
			while (filterIter.hasNext()) {
				if (storeExtensions.length() == 0)
					storeExtensions += filterIter.next();
				else
					storeExtensions += "|" + filterIter.next();
			}
			if (storeExtensions.length() > 0)
				memento.putString("FILTERS", storeExtensions);
		}
	}

	public String getPlotViewName() {
		return plotViewName;
	}

	public void setDirPath(final String filepath) {
		this.dirPath = filepath;
	}

	public String getDirPath() {
		return dirPath;
	}

	@Override
	public void setFocus() {

	}

	public void setOverviewMode(boolean overview) {
		if (imageGrid != null)
			imageGrid.setOverviewMode(overview);
	}

	public boolean getOverviewMode() {
		if (imageGrid != null)
			return imageGrid.getOverviewMode();
		return false;
	}

	public ArrayList<String> getSelection() {
		return imageGrid.getSelection();
	}

	private void processNewFile(GuiBean bean) {
		try {
			locker.acquire();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		playback.addFile((String) bean.get(GuiParameters.FILENAME));
		SWTGridEntry entry = new SWTGridEntry((String) bean.get(GuiParameters.FILENAME), null, canvas,
				getPreferenceColourMapChoice(), getPreferenceAutoContrastLo(), getPreferenceAutoContrastHi());
		Integer xPos = (Integer) bean.get(GuiParameters.IMAGEGRIDXPOS);
		Integer yPos = (Integer) bean.get(GuiParameters.IMAGEGRIDYPOS);
		if (xPos != null && yPos != null)
			imageGrid.addEntry(entry, xPos, yPos);
		else
			imageGrid.addEntry(entry);
		locker.release();
		if (liveActive)
			playback.moveToLast();
	}

	private void processNewGrid(GuiBean bean) {
		final Integer[] gridDims = (Integer[]) bean.get(GuiParameters.IMAGEGRIDSIZE);

		if (gridDims != null && gridDims.length > 0) {
			try {
				locker.acquire();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			canvas.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					imageGrid.dispose();
					if (gridDims.length == 1)
						imageGrid = new SWTImageGrid(gridDims[0], gridDims[0], canvas, plotViewName);
					else
						imageGrid = new SWTImageGrid(gridDims[1], gridDims[0], canvas, plotViewName);
					locker.release();
					canvas.redraw();
				}
			});
		}
		cleanUpOnServer();
	}

	private void processGUIUpdate(GuiUpdate gu) {
		if (gu.getGuiName().contains(plotViewName)) {
			GuiBean bean = gu.getGuiData();
			UUID id = (UUID) bean.get(GuiParameters.PLOTID);
			if (id == null || plotID.compareTo(id) != 0) { // filter out own beans
				if (guiBean == null)
					guiBean = bean.copy(); // cache a local copy
				else
					guiBean.merge(bean); // or merge it

				if (bean.containsKey(GuiParameters.FILEOPERATION))
					return;

				if (bean.containsKey(GuiParameters.PLOTMODE)) {
					GuiPlotMode plotMode = (GuiPlotMode) bean.get(GuiParameters.PLOTMODE);
					if (plotMode.equals(GuiPlotMode.IMGEXPL)) {
						if (bean.containsKey(GuiParameters.FILENAME)) {
							if (filesToLoad == null)
								filesToLoad = new ArrayList<String>();
							filesToLoad.add((String) bean.get(GuiParameters.FILENAME));
							processNewFile(bean);
						} else if (bean.containsKey(GuiParameters.IMAGEGRIDSIZE)) {
							if (filesToLoad == null)
								filesToLoad = new ArrayList<String>();
							else
								filesToLoad.clear();
							btnPlay.getDisplay().asyncExec(new Runnable() {

								@Override
								public void run() {
									resetPlaying(true);
								}
							});
							processNewGrid(bean);
						}
					}
				} else if (bean.containsKey(GuiParameters.IMAGEGRIDLIVEVIEW)) {
					String directory = (String) bean.get(GuiParameters.IMAGEGRIDLIVEVIEW);
					spawnLoadJob(directory);
					currentDir = directory;
					isLive = true;
					liveActive = true;
				}
			}
		}
	}

	private void processClientLocalUpdate() {
		GuiBean bean = new GuiBean();
		int gridDim = (int) Math.ceil(Math.sqrt(filesToLoad.size()));
		if (imageGrid != null)
			imageGrid.dispose();

		imageGrid = new SWTImageGrid(gridDim, gridDim, canvas, plotViewName);
		Iterator<String> iter = filesToLoad.iterator();
		while (iter.hasNext()) {
			String filename = iter.next();
			bean.put(GuiParameters.FILENAME, filename);
			processNewFile(bean);
		}
		if (liveActive) {
			sldProgress.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					playback.moveToLast();
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void update(Object source, Object changeCode) {
		if (stopLoading)
			return;

		if (source == ImageExplorerView.FOLDER_UPDATE_MARKER) { // Folder Update
			if (changeCode instanceof List<?>) {
				playback.clearPlayback();
				btnPlay.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						resetPlaying(false);
						if (isLive) {
							playback.setDelay(50);
							playback.setStepping(1);
							playback.start();
							btnPlay.setSelection(true);
							btnPlay.setImage(imgStill);
							execSvc.execute(playback);
							if (!monActive)
								CommandExecutor.executeCommand(getViewSite(),
										"uk.ac.diamond.scisoft.analysis.rcp.MontorDirectoryAction");
							cmbDirectoryLocation.setText(currentDir);
							isLive = false;
						}
					}
				});
				filesToLoad = (List<String>) changeCode;
				processClientLocalUpdate();
			}
		} else {
			if (changeCode instanceof GuiUpdate) {
				GuiUpdate gu = (GuiUpdate) changeCode;
				processGUIUpdate(gu);
			}
		}
	}

	private void cleanUpOnServer() {
		FileOperationBean fopBean = new FileOperationBean(FileOperationBean.DELETEGRIDIMGTEMPDIR);
		GuiBean bean = new GuiBean();
		bean.put(GuiParameters.FILEOPERATION, fopBean);
		// plotServer.deleteIObserver(this);
		try {
			plotServer.updateGui(plotViewName, bean);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	private void retainStateFromServer() {
		try {
			GuiBean guiBean = plotServer.getGuiState(plotViewName);
			if (guiBean != null && guiBean.containsKey(GuiParameters.IMAGEGRIDSTORE)) {
				ArrayList<GridImageEntry> entries = (ArrayList<GridImageEntry>) guiBean
						.get(GuiParameters.IMAGEGRIDSTORE);
				Iterator<GridImageEntry> iter = entries.iterator();
				while (iter.hasNext()) {
					GridImageEntry entry = iter.next();
					SWTGridEntry gridEntry = new SWTGridEntry(entry.getFilename(), null, canvas, getPreferenceColourMapChoice(),
							getPreferenceAutoContrastLo(), getPreferenceAutoContrastHi());
					imageGrid.addEntry(gridEntry, entry.getGridColumnPos(), entry.getGridRowPos());
				}
			}
		} catch (Exception e) {
			logger.warn("Problem with getting GUI data from plot server");
		}
	}

	private GuiBean saveStateToServer() {
		plotServer.deleteIObserver(this);
		if (imageGrid == null)
			return null;
		ArrayList<GridImageEntry> entries = imageGrid.getListOfEntries();
		GuiBean bean = new GuiBean();
		bean.put(GuiParameters.PLOTMODE, GuiPlotMode.IMGEXPL);
		bean.put(GuiParameters.IMAGEGRIDSTORE, entries);
		return bean;
	}

	@Override
	public void dispose() {
		isDisposed = true;
		GuiBean finalBean = saveStateToServer();

		if (imageGrid != null) {
			imageGrid.dispose();
		}
		if (finalBean != null) {
			try {
				plotServer.updateGui(plotViewName, finalBean);
			} catch (Exception e) {
				logger.warn("Problem with updating plot server with GUI data");
				e.printStackTrace();
			}
		}

		playback.dispose();
	}

	/**
	 * @return true if it has been disposed
	 */
	public boolean isDisposed() {
		return isDisposed;
	}

	/**
	 * Push gui information back to plot server
	 * @param key 
	 * @param value 
	 */
	public void pushGUIUpdate(GuiParameters key, Serializable value) {
		if (guiBean == null) {
			try {
				guiBean = plotServer.getGuiState(plotViewName);
			} catch (Exception e) {
				logger.warn("Problem with getting GUI data from plot server");
			}
			if (guiBean == null)
				guiBean = new GuiBean();
		}

		guiBean.put(GuiParameters.PLOTID, plotID); // put plotID in bean

		guiBean.put(key, value);

		try {
			logger.info("Pushing bean to server: {}", guiBean);
			plotServer.updateGui(plotViewName, guiBean);
		} catch (Exception e) {
			logger.warn("Problem with updating plot server with GUI data");
			e.printStackTrace();
		}
	}

	/**
	 * Push a list of filenames back to server
	 * @param selectedFiles
	 */
	public void pushSelectedFiles(ArrayList<String> selectedFiles) {
		pushGUIUpdate(GuiParameters.FILESELECTEDLIST, selectedFiles);
	}

	private void spawnLoadJob(final String newDirectory) {
		final Job updateDirectory = new Job("Update directory") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				if (!ImageExplorerDirectoryChooseAction.setImageFolder(newDirectory,filter)) {
					return new Status(IStatus.ERROR,"ERROR","Couldn't load directory it either doesn't exist or has no files");
				}
				return Status.OK_STATUS;
			}
		};
		updateDirectory.setUser(true);
		updateDirectory.setPriority(Job.DECORATE);
		updateDirectory.schedule(100);
	}

	private void addToHistory() {
		if (currentDir != null)
			history.add(0, new String(currentDir));
	}

	private void resetHistory() {
		historyPointer = 0;
		firstBack = true;
		btnHistoryBack.setEnabled(true);
		btnHistoryForw.setEnabled(false);
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		if (e.getSource().equals(cmbDirectoryLocation)) {
			final String newDirectory = cmbDirectoryLocation.getText();
			if (newDirectory != null && newDirectory.length() > 0) {
				newDirectory.trim();
				addToHistory();
				resetHistory();
				String[] oldDirs = cmbDirectoryLocation.getItems();
				boolean foundEntry = false;
				for (int i = 0; i < oldDirs.length; i++) {
					if (newDirectory.equals(oldDirs[i])) {
						foundEntry = true;
						break;
					}
				}
				if (!foundEntry) {
					if (cmbDirectoryLocation.getItemCount() > 0)
						cmbDirectoryLocation.add(newDirectory, 0);
					else
						cmbDirectoryLocation.add(newDirectory);
				}
				resetPlaying(true);
				spawnLoadJob(newDirectory);
				currentDir = newDirectory;
			}
		}
	}

	public void setLocationText(String location) {
		if (cmbDirectoryLocation != null && !cmbDirectoryLocation.isDisposed())
			cmbDirectoryLocation.setText(location);
	}

	public List<String> getExtensionsFilter() {
		return filter;
	}

	private void resetPlaying(boolean reset) {
		playback.stop();
		if (reset)
			playback.clearPlayback();
		btnPlay.setSelection(false);
		btnPlay.setImage(imgPlay);
		sldProgress.setSelection(0);
		curPosition = -1;
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (e.getSource().equals(cmbDirectoryLocation)) {
			final String newDirectory = cmbDirectoryLocation.getItem(cmbDirectoryLocation.getSelectionIndex());
			if (newDirectory != null && newDirectory.length() > 0) {
				addToHistory();
				resetHistory();
				resetPlaying(true);
				spawnLoadJob(newDirectory);
				currentDir = newDirectory;
			}
		} else if (e.getSource().equals(btnHistoryBack)) {
			String newDir = history.get(historyPointer);
			btnHistoryForw.setEnabled(true);
			cmbDirectoryLocation.setText(newDir);
			resetPlaying(true);
			spawnLoadJob(newDir);
			historyPointer++;
			if (historyPointer >= history.size()) {
				btnHistoryBack.setEnabled(false);
				historyPointer = history.size() - 1;
			}
			if (firstBack) {
				addToHistory();
				firstBack = false;
				historyPointer++;
			}
		} else if (e.getSource().equals(btnHistoryForw)) {
			historyPointer--;
			String newDir = history.get(historyPointer);
			resetPlaying(true);
			spawnLoadJob(newDir);
			cmbDirectoryLocation.setText(newDir);
			btnHistoryBack.setEnabled(true);
			if (historyPointer <= 0) {
				btnHistoryForw.setEnabled(false);
				historyPointer = 1;
			}
		} else if (e.getSource().equals(btnPlay)) {
			if (btnPlay.getSelection()) {
				boolean isPaused = playback.isPaused();
				btnPlay.setImage(imgStill);
				playback.start();
				if (!isPaused) {
					playback.setSelection(getSelection());
					playback.setDelay(getPreferenceTimeDelay());
					playback.setStepping(getPreferencePlaybackRate());
					execSvc.execute(playback);
				}
			} else {
				playback.pause();
				btnPlay.setImage(imgPlay);
			}
		} else if (e.getSource().equals(btnStop)) {
			sldProgress.setSelection(0);
			curPosition = -1;
			btnPlay.setSelection(false);
			btnPlay.setImage(imgPlay);
			playback.stop();
		} else if (e.getSource().equals(btnRewind)) {
			sldProgress.setSelection(0);
			curPosition = -1;
			playback.rewind();
		} else if (e.getSource().equals(btnForward)) {
			sldProgress.setSelection(sldProgress.getMaximum());
			playback.forward();
		} else if (e.getSource().equals(sldProgress)) {
			int p = sldProgress.getSelection();
			if (p != curPosition) {
				playback.setPlayPos(p);
				curPosition = p;
			}
		} else if (e.getSource().equals(btnPlayLoop)) {
			playback.setAutoRewind(btnPlayLoop.getSelection());
		}
	}

	public void setLocationBarVisible(boolean visible) {
		locationRow.setVisible(visible);
		((GridData) locationRow.getLayoutData()).exclude = !visible;
		parent.layout();
	}

	public void setImageGridVisible(boolean visible) {
		canvas.setVisible(visible);
		((GridData) canvas.getLayoutData()).exclude = !visible;
		parent.layout();
	}

	public void stopLoading(boolean stop) {
		if (stop) {
			imageGrid.stopLoading();
		}
		stopLoading  = stop;
	}

	public boolean isStopped() {
		return stopLoading;
	}

	public List<String> getLoadedFiles() {
		return filesToLoad;
	}

	private int getPreferenceColourMapChoice() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_COLOURMAP)
				? preferenceStore.getDefaultInt(PreferenceConstants.IMAGEEXPLORER_COLOURMAP)
				: preferenceStore.getInt(PreferenceConstants.IMAGEEXPLORER_COLOURMAP);
	}

	private int getPreferenceTimeDelay() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES)
				? preferenceStore.getDefaultInt(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES)
				: preferenceStore.getInt(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES);
	}

	private int getPreferencePlaybackRate() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE)
				? preferenceStore.getDefaultInt(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE)
				: preferenceStore.getInt(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE);
	}

	private double getPreferenceAutoContrastLo() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		int v = preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_LOTHRESHOLD)
				? preferenceStore.getDefaultInt(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_LOTHRESHOLD)
				: preferenceStore.getInt(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_LOTHRESHOLD);
		return v/100.0;
	}

	private double getPreferenceAutoContrastHi() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		int v = preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_HITHRESHOLD)
				? preferenceStore.getDefaultInt(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_HITHRESHOLD)
				: preferenceStore.getInt(PreferenceConstants.IMAGEEXPLORER_AUTOCONTRAST_HITHRESHOLD);
				return v/100.0;
	}

	/**
	 * @return plot name that playback uses
	 */
	public String getPreferencePlaybackView() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW)
				? preferenceStore.getDefaultString(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW)
				: preferenceStore.getString(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW);
	}

	public void setMonitorActive(boolean monitorActive) {
		monActive = monitorActive;
	}

	/**
	 * Play button action of ImageExplorer
	 */
	public void play(){
		boolean isPaused = playback.isPaused();
		btnPlay.setImage(imgStill);
		playback.start();
		if (!isPaused) {
			playback.setSelection(getSelection());
			playback.setDelay(getPreferenceTimeDelay());
			playback.setStepping(getPreferencePlaybackRate());
			execSvc.execute(playback);
		}
	}
}
