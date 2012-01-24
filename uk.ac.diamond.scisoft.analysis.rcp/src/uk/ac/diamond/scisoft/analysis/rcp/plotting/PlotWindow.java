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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import gda.observable.IObservable;
import gda.observable.IObserver;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramDataUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.actions.InjectPyDevConsoleHandler;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;

/**
 * Actual PlotWindow that can be used inside a View- or EditorPart
 */
public class PlotWindow implements IObserver, IObservable, IPlotWindow {
	public static final String RPC_SERVICE_NAME = "PlotWindowManager";
	public static final String RMI_SERVICE_NAME = "RMIPlotWindowManager";

	static private Logger logger = LoggerFactory.getLogger(PlotWindow.class);

	private DataSetPlotter mainPlotter;
	private IPlotUI plotUI = null;
	private boolean isUpdatePlot = false;
	private Composite parentComp;
	private IWorkbenchPage page = null;
	private IActionBars bars;
	private String name;

	private List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());
	private IGuiInfoManager manager = null;
	private IUpdateNotificationListener notifyListener = null;
	private DataBean myBeanMemory;

	protected Action duplicateWindowAction;
	protected Action saveGraphAction;
	protected Action copyGraphAction;
	protected Action printGraphAction;
	protected CommandContributionItem openPyDevConsoleCCI;
	protected CommandContributionItem updateDefaultPlotCCI;
	protected CommandContributionItem getPlotBeanCCI;

	protected String printButtonText = ResourceProperties.getResourceString("PRINT_BUTTON");
	protected String printToolTipText = ResourceProperties.getResourceString("PRINT_TOOLTIP");
	protected String printImagePath = ResourceProperties.getResourceString("PRINT_IMAGE_PATH");
	protected String copyButtonText = ResourceProperties.getResourceString("COPY_BUTTON");
	protected String copyToolTipText = ResourceProperties.getResourceString("COPY_TOOLTIP");
	protected String copyImagePath = ResourceProperties.getResourceString("COPY_IMAGE_PATH");
	protected String saveButtonText = ResourceProperties.getResourceString("SAVE_BUTTON");
	protected String saveToolTipText = ResourceProperties.getResourceString("SAVE_TOOLTIP");
	protected String saveImagePath = ResourceProperties.getResourceString("SAVE_IMAGE_PATH");

	/**
	 * PlotWindow may be given toolbars exclusive to the workbench part. In this case, there is no need to remove
	 * actions from the part.
	 */
	private boolean exclusiveToolars = false;

	/**
	 * Obtain the IPlotWindowManager for the running Eclipse.
	 * 
	 * @return singleton instance of IPlotWindowManager
	 */
	public static IPlotWindowManager getManager() {
		// get the private manager for use only within the framework and
		// "upcast" it to IPlotWindowManager
		return PlotWindowManager.getPrivateManager();
	}

	public PlotWindow(Composite parent, GuiPlotMode plotMode, IActionBars bars, IWorkbenchPage page, String name) {
		this(parent, plotMode, null, null, bars, page, name);
	}

	public PlotWindow(Composite parent, GuiPlotMode plotMode, IGuiInfoManager manager,
			IUpdateNotificationListener notifyListener, IActionBars bars, IWorkbenchPage page, String name) {

		this.manager = manager;
		this.notifyListener = notifyListener;
		this.parentComp = parent;
		this.page = page;

		if (plotMode == null)
			plotMode = GuiPlotMode.ONED;

		// this needs to be started in 1D as later mode changes will not work as plot UIs are not setup
		mainPlotter = new DataSetPlotter(PlottingMode.ONED, parent, true);

		mainPlotter.setAxisModes(AxisMode.LINEAR, AxisMode.LINEAR, AxisMode.LINEAR);

		this.parentComp = parent;
		this.bars = bars;
		this.name = name;
		parent.setLayout(new FillLayout());
		mainPlotter.setXAxisLabel("X-Axis");
		mainPlotter.setYAxisLabel("Y-Axis");
		mainPlotter.setZAxisLabel("Z-Axis");

		if (plotMode.equals(GuiPlotMode.ONED)) {
			setup1D();
		} else if (plotMode.equals(GuiPlotMode.ONED_THREED)) {
			setupMulti1DPlot();
		} else if (plotMode.equals(GuiPlotMode.TWOD)) {
			setup2D();
		} else if (plotMode.equals(GuiPlotMode.SURF2D)) {
			setup2DSurface();
		} else if (plotMode.equals(GuiPlotMode.SCATTER2D)) {
			setupScatter2DPlot();
		} else if (plotMode.equals(GuiPlotMode.SCATTER3D)) {
			setupScatter3DPlot();
		} else if (plotMode.equals(GuiPlotMode.MULTI2D)) {
			setupMulti2D();
		}

		// for some reason, this window does not get repainted
		// when a perspective is switched and the view is resized
		parent.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				if (e.widget.equals(parentComp)) {
					parentComp.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							mainPlotter.refresh(false);
						}
					});
				}
			}

			@Override
			public void controlMoved(ControlEvent e) {
			}
		});

		PlotWindowManager.getPrivateManager().registerPlotWindow(this);
	}

	/**
	 * Return current page.
	 * 
	 * @return current page
	 */
	@Override
	public IWorkbenchPage getPage() {
		return page;
	}

	/**
	 * @return plot UI
	 */
	public IPlotUI getPlotUI() {
		return plotUI;
	}

	/**
	 * Return the name of the Window
	 * 
	 * @return name
	 */
	@Override
	public String getName() {
		return name;
	}

	/**
	 * Process a plot with data packed in bean - remember to update plot mode first if you do not know the current mode
	 * or if it is to change
	 * 
	 * @param dbPlot
	 */
	public void processPlotUpdate(final DataBean dbPlot) {
		// check to see what type of plot this is and set the plotMode to the correct one
		if (dbPlot.getGuiPlotMode() != null) {
			if(parentComp.isDisposed()){
				//this can be caused by the same plot view shown on 2 difference perspectives.
				throw new IllegalStateException("parentComp is already disposed");
			}
			if (parentComp.getDisplay().getThread() != Thread.currentThread())
				updatePlotMode(dbPlot.getGuiPlotMode(), true);
			else
				updatePlotMode(dbPlot.getGuiPlotMode(), false);
		}
		// there may be some gui information in the databean, if so this also needs to be updated
		if (dbPlot.getGuiParameters() != null) {
			processGUIUpdate(dbPlot.getGuiParameters());
		}

		try {
			doBlock();
			// Now plot the data as standard
			plotUI.processPlotUpdate(dbPlot, isUpdatePlot);
			myBeanMemory = dbPlot;
		} finally {
			undoBlock();
		}
	}

	private void removePreviousActions() {

		IContributionItem[] items = bars.getToolBarManager().getItems();
		for (int i = 0; i < items.length; i++)
			items[i].dispose();
		bars.getToolBarManager().removeAll();

		bars.getMenuManager().removeAll();
		bars.getStatusLineManager().removeAll();
	}

	private void cleanUpFromOldMode(final boolean leaveSidePlotOpen) {
		isUpdatePlot = false;
		mainPlotter.unregisterUI(plotUI);
		if (plotUI != null) {
			plotUI.deleteIObservers();
			plotUI.deactivate(leaveSidePlotOpen);
			removePreviousActions();
		}
	}

	private void addCommonActions() {
		if (duplicateWindowAction == null) {
			duplicateWindowAction = new Action("Create Duplicate Plot", IAction.AS_PUSH_BUTTON) {
				@Override
				public void run() {
					getManager().openDuplicateView(page, name);
				}
			};
			duplicateWindowAction.setText("Duplicate Plot");
			duplicateWindowAction.setToolTipText("Open an additional Plot View with a duplicate of this plot's data");
		}
		if (saveGraphAction == null) {
			saveGraphAction = new Action() {		
				// Cache file name otherwise they have to keep
				// choosing the folder.
				private String filename;
				
				@Override
				public void run() {
					
					FileDialog dialog = new FileDialog (parentComp.getShell(), SWT.SAVE);
					
					String [] filterExtensions = new String [] {"*.jpg;*.JPG;*.jpeg;*.JPEG;*.png;*.PNG", "*.ps;*.eps","*.svg;*.SVG"};
					if (filename!=null) {
						dialog.setFilterPath((new File(filename)).getParent());
					} else {
						String filterPath = "/";
						String platform = SWT.getPlatform();
						if (platform.equals("win32") || platform.equals("wpf")) {
							filterPath = "c:\\";
						}
						dialog.setFilterPath (filterPath);
					}
					dialog.setFilterNames (PlotExportUtil.FILE_TYPES);
					dialog.setFilterExtensions (filterExtensions);
					filename = dialog.open();
					if (filename == null)
						return;

					mainPlotter.saveGraph(filename, PlotExportUtil.FILE_TYPES[dialog.getFilterIndex()]);
				}
			};
			saveGraphAction.setText(saveButtonText);
			saveGraphAction.setToolTipText(saveToolTipText);
			saveGraphAction.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(saveImagePath));
		}
		
		if (copyGraphAction == null) {
			copyGraphAction = new Action() {
				@Override
				public void run() {
					mainPlotter.copyGraph();
				}
			};
			copyGraphAction.setText(copyButtonText);
			copyGraphAction.setToolTipText(copyToolTipText);
			copyGraphAction.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(copyImagePath));
		}
		
		if (printGraphAction == null) {
			printGraphAction = new Action() {
				@Override
				public void run() {
					mainPlotter.printGraph();
				}
			};
			printGraphAction.setText(printButtonText);
			printGraphAction.setToolTipText(printToolTipText);
			printGraphAction.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(printImagePath));
		}

		if (openPyDevConsoleCCI == null) {
			CommandContributionItemParameter ccip = new CommandContributionItemParameter(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow(), null, InjectPyDevConsoleHandler.COMMAND_ID,
					CommandContributionItem.STYLE_PUSH);
			ccip.label = "Open New Plot Scripting";
			ccip.icon = AnalysisRCPActivator.getImageDescriptor("icons/application_osx_terminal.png");
			Map<String, String> params = new HashMap<String, String>();
			params.put(InjectPyDevConsoleHandler.CREATE_NEW_CONSOLE_PARAM, Boolean.TRUE.toString());
			params.put(InjectPyDevConsoleHandler.VIEW_NAME_PARAM, getName());
			params.put(InjectPyDevConsoleHandler.SETUP_SCISOFTPY_PARAM,
					InjectPyDevConsoleHandler.SetupScisoftpy.ALWAYS.toString());
			ccip.parameters = params;
			openPyDevConsoleCCI = new CommandContributionItem(ccip);
		}

		if (updateDefaultPlotCCI == null) {
			CommandContributionItemParameter ccip = new CommandContributionItemParameter(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow(), null, InjectPyDevConsoleHandler.COMMAND_ID,
					CommandContributionItem.STYLE_PUSH);
			ccip.label = "Set Current Plot As Scripting Default";
			Map<String, String> params = new HashMap<String, String>();
			params.put(InjectPyDevConsoleHandler.VIEW_NAME_PARAM, getName());
			ccip.parameters = params;
			updateDefaultPlotCCI = new CommandContributionItem(ccip);
		}

		if (getPlotBeanCCI == null) {
			CommandContributionItemParameter ccip = new CommandContributionItemParameter(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow(), null, InjectPyDevConsoleHandler.COMMAND_ID,
					CommandContributionItem.STYLE_PUSH);
			ccip.label = "Get Plot Bean in Plot Scripting";
			Map<String, String> params = new HashMap<String, String>();
			params.put(InjectPyDevConsoleHandler.INJECT_COMMANDS_PARAM, "bean=dnp.plot.getbean('" + getName() + "')");
			ccip.parameters = params;
			getPlotBeanCCI = new CommandContributionItem(ccip);
		}

		if (bars.getMenuManager().getItems().length > 0)
			bars.getMenuManager().add(new Separator());
		bars.getMenuManager().add(saveGraphAction);
		bars.getMenuManager().add(copyGraphAction);
		bars.getMenuManager().add(printGraphAction);
		bars.getMenuManager().add(new Separator());
		bars.getMenuManager().add(openPyDevConsoleCCI);
		bars.getMenuManager().add(updateDefaultPlotCCI);
		bars.getMenuManager().add(getPlotBeanCCI);
		bars.getMenuManager().add(new Separator());
		bars.getMenuManager().add(duplicateWindowAction);
		bars.getMenuManager().add(new Separator());
	}

	private void setup1D() {
		mainPlotter.setMode(PlottingMode.ONED);
		plotUI = new Plot1DUIComplete(this, manager, bars, parentComp, getPage(), name);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setup2D() {
		mainPlotter.setMode(PlottingMode.TWOD);
		plotUI = new Plot2DUI(this, mainPlotter, manager, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setupMulti2D() {
		mainPlotter.setMode(PlottingMode.MULTI2D);
		plotUI = new Plot2DMultiUI(this, mainPlotter, manager, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setup2DSurface() {
		mainPlotter.useWindow(true);
		mainPlotter.setMode(PlottingMode.SURF2D);
		plotUI = new PlotSurf3DUI(this, mainPlotter, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setupMulti1DPlot() {
		mainPlotter.setMode(PlottingMode.ONED_THREED);
		plotUI = new Plot1DStackUI(this, bars, mainPlotter, parentComp, page);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setupScatter2DPlot() {
		mainPlotter.setMode(PlottingMode.SCATTER2D);
		plotUI = new PlotScatter2DUI(this, bars, mainPlotter, parentComp, page, name);
		addCommonActions();
		bars.updateActionBars();
	}

	private void setupScatter3DPlot() {
		mainPlotter.setMode(PlottingMode.SCATTER3D);
		plotUI = new PlotScatter3DUI(this, mainPlotter, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
	}

	/**
	 * @param plotMode
	 */
	public void updatePlotMode(GuiPlotMode plotMode) {
		if (plotMode.equals(GuiPlotMode.ONED) && mainPlotter.getMode() != PlottingMode.ONED) {
			cleanUpFromOldMode(true);
			setup1D();
		} else if (plotMode.equals(GuiPlotMode.ONED_THREED) && mainPlotter.getMode() != PlottingMode.ONED_THREED) {
			cleanUpFromOldMode(true);
			setupMulti1DPlot();
		} else if (plotMode.equals(GuiPlotMode.TWOD) && mainPlotter.getMode() != PlottingMode.TWOD) {
			cleanUpFromOldMode(true);
			setup2D();
		} else if (plotMode.equals(GuiPlotMode.SURF2D) && mainPlotter.getMode() != PlottingMode.SURF2D) {
			cleanUpFromOldMode(true);
			setup2DSurface();
		} else if (plotMode.equals(GuiPlotMode.SCATTER2D) && mainPlotter.getMode() != PlottingMode.SCATTER2D) {
			cleanUpFromOldMode(true);
			setupScatter2DPlot();
		} else if (plotMode.equals(GuiPlotMode.SCATTER3D) && mainPlotter.getMode() != PlottingMode.SCATTER3D) {
			cleanUpFromOldMode(true);
			setupScatter3DPlot();
		} else if (plotMode.equals(GuiPlotMode.MULTI2D) && mainPlotter.getMode() != PlottingMode.MULTI2D) {
			cleanUpFromOldMode(true);
			setupMulti2D();
		} else if (plotMode.equals(GuiPlotMode.EMPTY) && mainPlotter.getMode() != PlottingMode.EMPTY) {
			clearPlot();
		}
	}

	public void clearPlot() {
		if (mainPlotter != null) {
			mainPlotter.emptyPlot();
			mainPlotter.refresh(true);
		}
	}

	private void updatePlotModeAsync(GuiPlotMode plotMode) {
		if (plotMode.equals(GuiPlotMode.ONED) && mainPlotter.getMode() != PlottingMode.ONED) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setup1D();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.ONED_THREED) && mainPlotter.getMode() != PlottingMode.ONED_THREED) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setupMulti1DPlot();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.TWOD) && mainPlotter.getMode() != PlottingMode.TWOD) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setup2D();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.SURF2D) && mainPlotter.getMode() != PlottingMode.SURF2D) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setup2DSurface();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.SCATTER2D) && mainPlotter.getMode() != PlottingMode.SCATTER2D) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setupScatter2DPlot();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.SCATTER3D) && mainPlotter.getMode() != PlottingMode.SCATTER3D) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setupScatter3DPlot();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.MULTI2D) && mainPlotter.getMode() != PlottingMode.MULTI2D) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						cleanUpFromOldMode(true);
						setupMulti2D();
					} finally {
						undoBlock();
					}
				}
			});
		} else if (plotMode.equals(GuiPlotMode.EMPTY) && mainPlotter.getMode() != PlottingMode.EMPTY) {
			doBlock();
			parentComp.getDisplay().asyncExec(new Runnable() {

				@Override
				public void run() {
					try {
						clearPlot();
					} finally {
						undoBlock();
					}
				}
			});
		}
	}

	public void updatePlotMode(GuiBean bean, boolean async) {
		if (bean != null) {
			if (bean.containsKey(GuiParameters.PLOTMODE)) { // bean does not necessarily have a plot mode (eg, it
															// contains ROIs only)
				GuiPlotMode plotMode = (GuiPlotMode) bean.get(GuiParameters.PLOTMODE);
				updatePlotMode(plotMode, async);
			}
		}
	}

	public void updatePlotMode(GuiPlotMode plotMode, boolean async) {
		if (plotMode != null) {
			if (async)
				updatePlotModeAsync(plotMode);
			else
				updatePlotMode(plotMode);
		}
	}

	public boolean isUpdatePlot() {
		return isUpdatePlot;
	}

	public void setUpdatePlot(boolean isUpdatePlot) {
		this.isUpdatePlot = isUpdatePlot;
	}

	public void processGUIUpdate(GuiBean bean) {
		isUpdatePlot = false;
		if (bean.containsKey(GuiParameters.PLOTMODE)) {
			if (parentComp.getDisplay().getThread() != Thread.currentThread())
				updatePlotMode(bean, true);
			else
				updatePlotMode(bean, false);
		}

		if (bean.containsKey(GuiParameters.TITLE)) {
			final String titleStr = (String) bean.get(GuiParameters.TITLE);
			parentComp.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					doBlock();
					try {
						mainPlotter.setTitle(titleStr);
					} finally {
						undoBlock();
					}
					mainPlotter.refresh(true);
				}
			});
		}

		if (bean.containsKey(GuiParameters.PLOTOPERATION)) {
			String opStr = (String) bean.get(GuiParameters.PLOTOPERATION);
			if (opStr.equals("UPDATE")) {
				isUpdatePlot = true;
			}
		}

		if (bean.containsKey(GuiParameters.ROIDATA) || bean.containsKey(GuiParameters.ROIDATALIST)) {
			plotUI.processGUIUpdate(bean);
		}
	}

	public void notifyHistogramChange(HistogramDataUpdate histoUpdate) {
		Iterator<IObserver> iter = observers.iterator();
		while (iter.hasNext()) {
			IObserver listener = iter.next();
			listener.update(this, histoUpdate);
		}
	}

	@Override
	public void update(Object theObserved, Object changeCode) {
		if (theObserved instanceof HistogramView) {
			HistogramUpdate update = (HistogramUpdate) changeCode;
			mainPlotter.applyColourCast(update);

			mainPlotter.refresh(false);
			if (plotUI instanceof Plot2DUI) {
				Plot2DUI plot2Dui = (Plot2DUI) plotUI;
				plot2Dui.getSidePlotView().sendHistogramUpdate(update);
			}
		}

	}

	public DataSetPlotter getMainPlotter() {
		return mainPlotter;
	}

	public void dispose() {
		PlotWindowManager.getPrivateManager().unregisterPlotWindow(this);
		if (plotUI != null) {
			plotUI.deactivate(false);
			plotUI.dispose();
		}
		try {
			if (mainPlotter != null) {
				mainPlotter.cleanUp();
			}
		} catch (Exception ne) {
			logger.debug("Cannot clean up main plotter!", ne);
		}
		deleteIObservers();
		mainPlotter = null;
		plotUI = null;
		System.gc();
	}

	@Override
	public void addIObserver(IObserver observer) {
		observers.add(observer);
	}

	@Override
	public void deleteIObserver(IObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void deleteIObservers() {
		observers.clear();
	}

	public void notifyUpdateFinished() {
		if (notifyListener != null)
			notifyListener.updateProcessed();
	}

	public boolean isExclusiveToolars() {
		return exclusiveToolars;
	}

	public void setExclusiveToolars(boolean exclusiveToolars) {
		this.exclusiveToolars = exclusiveToolars;
	}

	public DataBean getDataBean() {
		return myBeanMemory;
	}

	SimpleLock simpleLock = new SimpleLock();

	private void doBlock() {
		logger.debug("doBlock " + Thread.currentThread().getId());
		synchronized (simpleLock) {
			if (simpleLock.isLocked()) {
				try {
					logger.debug("doBlock  - waiting " + Thread.currentThread().getId());
					simpleLock.wait();
					logger.debug("doBlock  - locking " + Thread.currentThread().getId());
				} catch (InterruptedException e) {
					// do nothing - but return
				}
			} else {
				logger.debug("doBlock  - waiting not needed " + Thread.currentThread().getId());
			}
			simpleLock.lock();
		}
	}

	private void undoBlock() {
		synchronized (simpleLock) {
			logger.debug("undoBlock " + Thread.currentThread().getId());
			simpleLock.unlock();
			simpleLock.notifyAll();
		}
	}

}

class SimpleLock {
	boolean state = false;

	boolean isLocked() {
		return state;
	}

	void lock() {
		state = true;
	}

	void unlock() {
		state = false;
	}
}
