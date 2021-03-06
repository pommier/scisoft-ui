/*-
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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.dawb.common.ui.plot.AbstractPlottingSystem;
import org.dawb.common.ui.plot.AbstractPlottingSystem.ColorOption;
import org.dawb.common.ui.plot.PlotType;
import org.dawb.common.ui.plot.PlottingFactory;
import org.dawb.common.ui.plot.region.IROIListener;
import org.dawb.common.ui.plot.region.IRegion;
import org.dawb.common.ui.plot.region.IRegionListener;
import org.dawb.common.ui.plot.region.ROIEvent;
import org.dawb.common.ui.plot.region.RegionEvent;
import org.dawb.common.ui.plot.tool.IProfileToolPage;
import org.dawb.common.ui.plot.tool.IToolPageSystem;
import org.dawb.common.ui.plot.tool.ToolPageFactory;
import org.dawb.common.ui.plot.trace.IImageTrace;
import org.dawb.common.ui.plot.trace.ILineTrace;
import org.dawb.common.ui.plot.trace.ITrace;
import org.dawnsci.plotting.jreality.core.AxisMode;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.ControlListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramDataUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.actions.DuplicatePlotAction;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.actions.InjectPyDevConsoleHandler;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;
import uk.ac.diamond.scisoft.analysis.rcp.views.PlotView;
import uk.ac.diamond.scisoft.analysis.roi.LinearROI;
import uk.ac.diamond.scisoft.analysis.roi.LinearROIList;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;
import uk.ac.diamond.scisoft.analysis.roi.ROIProfile.BoxLineType;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROIList;
import uk.ac.diamond.scisoft.analysis.roi.SectorROI;
import uk.ac.diamond.scisoft.analysis.roi.SectorROIList;

/**
 * Actual PlotWindow that can be used inside a View- or EditorPart
 */
public class PlotWindow implements IObserver, IObservable, IPlotWindow, IROIListener {
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

	private AbstractPlottingSystem plottingSystem;
	private IProfileToolPage sideProfile1;
	private IProfileToolPage sideProfile2;

	private List<IObserver> observers = Collections.synchronizedList(new LinkedList<IObserver>());
	private IGuiInfoManager manager = null;
	private IUpdateNotificationListener notifyListener = null;
	private DataBean myBeanMemory;

	protected Action saveGraphAction;
	protected Action copyGraphAction;
	protected Action printGraphAction;
	protected CommandContributionItem duplicateWindowCCI;
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

	private Composite plotSystemComposite;
	private SashForm sashForm;
	private SashForm sashForm2;
	private SashForm sashForm3;
	private Composite mainPlotterComposite;

	private GuiPlotMode previousPlotMode;
	
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

	public PlotWindow(final Composite parent, GuiPlotMode plotMode, IGuiInfoManager manager,
			IUpdateNotificationListener notifyListener, IActionBars bars, IWorkbenchPage page, String name) {

		this.manager = manager;
		this.notifyListener = notifyListener;
		this.parentComp = parent;
		this.page = page;
		this.bars = bars;
		this.name = name;

		this.registeredTraces = new HashMap<String,Collection<ITrace>>(7);

		if (plotMode == null)
			plotMode = GuiPlotMode.ONED;

		// this needs to be started in 1D as later mode changes will not work as plot UIs are not setup
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM)
			createDatasetPlotter(PlottingMode.ONED);
		
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_SYSTEM) {
			// if we are in 2D plotting mode with side profiles plot then we create 
			// the MultiPlotting System
			if(!plotMode.equals(GuiPlotMode.TWOD_ROIPROFILES))
				createPlottingSystem();
			else
				createMultiPlottingSystem();
			cleanUpDatasetPlotter();
		}
		// Setting up
		if (plotMode.equals(GuiPlotMode.ONED)) {
			if(getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM)
				setup1D();
			else
				setupPlotting1D();
			previousPlotMode = GuiPlotMode.ONED;
		} else if (plotMode.equals(GuiPlotMode.ONED_THREED)) {
			setupMulti1DPlot();
			previousPlotMode = GuiPlotMode.ONED_THREED;
		} else if (plotMode.equals(GuiPlotMode.TWOD)) {
			if(getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM)
				setup2D();
			else
				setupPlotting2D();
			previousPlotMode = GuiPlotMode.TWOD;
		} else if (plotMode.equals(GuiPlotMode.TWOD_ROIPROFILES)) {
			setupPlotting2DROIProfile();
			previousPlotMode = GuiPlotMode.TWOD_ROIPROFILES;
		} else if (plotMode.equals(GuiPlotMode.SURF2D)) {
			setup2DSurface();
			previousPlotMode = GuiPlotMode.SURF2D;
		} else if (plotMode.equals(GuiPlotMode.SCATTER2D)) {
			if(getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM)
				setupScatter2DPlot();
			else
				setupScatterPlotting2D();
			previousPlotMode = GuiPlotMode.SCATTER2D;
		} else if (plotMode.equals(GuiPlotMode.SCATTER3D)) {
			setupScatter3DPlot();
			previousPlotMode = GuiPlotMode.SCATTER3D;
		} else if (plotMode.equals(GuiPlotMode.MULTI2D)) {
			setupMulti2D();
			previousPlotMode = GuiPlotMode.MULTI2D;
		}

		parentAddControlListener();

		PlotWindowManager.getPrivateManager().registerPlotWindow(this);
	}

	private void parentAddControlListener(){
		// for some reason, this window does not get repainted
		// when a perspective is switched and the view is resized
		parentComp.addControlListener(new ControlListener() {
			@Override
			public void controlResized(ControlEvent e) {
				if (e.widget.equals(parentComp)) {
					parentComp.getDisplay().asyncExec(new Runnable() {
						@Override
						public void run() {
							if(mainPlotter!=null && !mainPlotter.isDisposed())
								mainPlotter.refresh(false);
						}
					});
				}
			}
			@Override
			public void controlMoved(ControlEvent e) {
			}
		});
	}
	
	private void createDatasetPlotter(PlottingMode mode){
		parentComp.setLayout(new FillLayout());
		mainPlotterComposite = new Composite(parentComp, SWT.NONE);
		mainPlotterComposite.setLayout(new FillLayout());
		mainPlotter = new DataSetPlotter(mode, mainPlotterComposite, true);
		mainPlotter.setAxisModes(AxisMode.LINEAR, AxisMode.LINEAR, AxisMode.LINEAR);
		mainPlotter.setXAxisLabel("X-Axis");
		mainPlotter.setYAxisLabel("Y-Axis");
		mainPlotter.setZAxisLabel("Z-Axis");

	}
	
	private void createPlottingSystem(){
		parentComp.setLayout(new FillLayout());
		plotSystemComposite = new Composite(parentComp, SWT.NONE);
		plotSystemComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		plotSystemComposite.setLayout(new FillLayout());
		try {
			plottingSystem = PlottingFactory.createPlottingSystem();
			plottingSystem.setColorOption(ColorOption.NONE);
			
			plottingSystem.createPlotPart(plotSystemComposite, name, bars, PlotType.XY, (IViewPart)manager);
			plottingSystem.repaint();
			
			this.regionListener = getRegionListener();
			this.plottingSystem.addRegionListener(this.regionListener);
			
		} catch (Exception e) {
			logger.error("Cannot locate any Abstract plotting System!", e);
		}
	}

	/**
	 * Create a plotting system layout with a main plotting system and two side plot profiles
	 */
	private void createMultiPlottingSystem(){
		parentComp.setLayout(new FillLayout());
		plotSystemComposite = new Composite(parentComp, SWT.NONE);
		plotSystemComposite.setLayout(new GridLayout(1, true));
		sashForm = new SashForm(plotSystemComposite, SWT.HORIZONTAL);
		sashForm.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		sashForm2 = new SashForm(sashForm, SWT.VERTICAL);
		sashForm2.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		sashForm3 = new SashForm(sashForm, SWT.VERTICAL);
		sashForm3.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		try {
			plottingSystem = PlottingFactory.createPlottingSystem();
			plottingSystem.setColorOption(ColorOption.NONE);
			
			plottingSystem.createPlotPart(sashForm2, name, bars, PlotType.XY, (IViewPart)manager);
			plottingSystem.repaint();
			
			sideProfile1 = (IProfileToolPage)ToolPageFactory.getToolPage("org.dawb.workbench.plotting.tools.boxLineProfileTool");
			sideProfile1.setLineType(BoxLineType.HORIZONTAL_TYPE);
			sideProfile1.setToolSystem(plottingSystem);
			sideProfile1.setPlottingSystem(plottingSystem);
			sideProfile1.setTitle(name+"_profile1");
			sideProfile1.setPart((IViewPart)manager);
			sideProfile1.setToolId(String.valueOf(sideProfile1.hashCode()));
			sideProfile1.createControl(sashForm2);
			sideProfile1.activate();
			
			sideProfile2 = (IProfileToolPage)ToolPageFactory.getToolPage("org.dawb.workbench.plotting.tools.boxLineProfileTool");
			sideProfile2.setLineType(BoxLineType.VERTICAL_TYPE);
			sideProfile2.setToolSystem(plottingSystem);
			sideProfile2.setPlottingSystem(plottingSystem);
			sideProfile2.setTitle(name+"_profile2");
			sideProfile2.setPart((IViewPart)manager);
			sideProfile2.setToolId(String.valueOf(sideProfile2.hashCode()));
			sideProfile2.createControl(sashForm3);
			sideProfile2.activate();
			
			Label metaDataLabel = new Label(sashForm3, SWT.BORDER | SWT.CENTER);
			metaDataLabel.setText("Metadata");
			sashForm.setWeights(new int[]{1, 1});
			this.regionListener = getRegionListener();
			this.plottingSystem.addRegionListener(this.regionListener);
			
		} catch (Exception e) {
			logger.error("Cannot locate any Abstract plotting System!", e);
		}
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

	/**
	 * Cleaning up the plot view according to the current plot mode
	 * 
	 * @param mode
	 */
	private void cleanUp(GuiPlotMode mode){
		if(mode.equals(GuiPlotMode.ONED) || mode.equals(GuiPlotMode.TWOD)
				|| mode.equals(GuiPlotMode.SCATTER2D)){
			cleanUpDatasetPlotter();
			cleanUpMultiPlottingSystem();
			if (plottingSystem == null || plottingSystem.isDisposed())
				createPlottingSystem();
		} else if(mode.equals(GuiPlotMode.TWOD_ROIPROFILES)){
			cleanUpDatasetPlotter();
			cleanUpPlottingSystem();
			if (plottingSystem == null || plottingSystem.isDisposed())
				createMultiPlottingSystem();
		} else if(mode.equals(GuiPlotMode.ONED_THREED)){
			cleanUpPlottingSystem();
			cleanUpMultiPlottingSystem();
			if(mainPlotter==null || mainPlotter.isDisposed())
				createDatasetPlotter(PlottingMode.ONED_THREED);
			cleanUpFromOldMode(true);
		} else if(mode.equals(GuiPlotMode.SURF2D)){
			cleanUpPlottingSystem();
			cleanUpMultiPlottingSystem();
			if(mainPlotter==null || mainPlotter.isDisposed())
				createDatasetPlotter(PlottingMode.SURF2D);
			cleanUpFromOldMode(true);
		} else if(mode.equals(GuiPlotMode.SCATTER3D)){
			cleanUpPlottingSystem();
			cleanUpMultiPlottingSystem();
			if(mainPlotter==null || mainPlotter.isDisposed())
				createDatasetPlotter(PlottingMode.SCATTER3D);
			cleanUpFromOldMode(true);
		} else if(mode.equals(GuiPlotMode.MULTI2D)){
			cleanUpPlottingSystem();
			cleanUpMultiPlottingSystem();
			if(mainPlotter==null || mainPlotter.isDisposed())
				createDatasetPlotter(PlottingMode.MULTI2D);
			cleanUpFromOldMode(true);
		}
		parentComp.layout();
	}

	/**
	 * Cleaning of the DatasetPlotter and its composite
	 * before the setting up of a Plotting System
	 */
	private void cleanUpDatasetPlotter(){
		if (mainPlotter!=null && !mainPlotter.isDisposed()) {
			bars.getToolBarManager().removeAll();
			bars.getMenuManager().removeAll();
			mainPlotter.cleanUp();
			mainPlotterComposite.dispose();
		}
	}
	
	/**
	 * Cleaning of the plotting system and its composite
	 * before the setting up of a datasetPlotter
	 */
	private void cleanUpPlottingSystem(){
		if((previousPlotMode == GuiPlotMode.ONED
				|| previousPlotMode == GuiPlotMode.TWOD
				|| previousPlotMode == GuiPlotMode.SCATTER2D))
			if(!plottingSystem.isDisposed() 
					&& (sideProfile1 == null || sideProfile1.isDisposed())
					&& (sideProfile2 == null || sideProfile2.isDisposed())){
				bars.getToolBarManager().removeAll();
				bars.getMenuManager().removeAll();
				for (Iterator<IRegion> iterator = plottingSystem.getRegions().iterator(); iterator.hasNext();) {
					IRegion region = iterator.next();
					plottingSystem.removeRegion(region);
				}
				plottingSystem.removeRegionListener(regionListener);
				plottingSystem.dispose();
				plotSystemComposite.dispose();
			}
	}

	/**
	 * Cleaning of the plotting system and its composite
	 * before the setting up of a datasetPlotter
	 */
	private void cleanUpMultiPlottingSystem(){
		
		if(previousPlotMode == GuiPlotMode.TWOD_ROIPROFILES){
			bars.getToolBarManager().removeAll();
			bars.getMenuManager().removeAll();
			for (Iterator<IRegion> iterator = plottingSystem.getRegions().iterator(); iterator.hasNext();) {
				IRegion region = iterator.next();
				plottingSystem.removeRegion(region);
			}
			plottingSystem.removeRegionListener(regionListener);
			plottingSystem.dispose();
			plotSystemComposite.dispose();
		
			if(sideProfile1 != null && !sideProfile1.isDisposed()){
				sideProfile1.dispose();
			}
			if(sideProfile2 != null && !sideProfile2.isDisposed()){
				sideProfile2.dispose();
			}
			if(sashForm != null && sashForm2 != null && sashForm3 != null){
				sashForm.dispose();
				sashForm2.dispose();
				sashForm3.dispose();
			}
		}
	}

	private void addCommonActions() {

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

		if (bars.getMenuManager().getItems().length > 0)
			bars.getMenuManager().add(new Separator());
		bars.getMenuManager().add(saveGraphAction);
		bars.getMenuManager().add(copyGraphAction);
		bars.getMenuManager().add(printGraphAction);
		bars.getMenuManager().add(new Separator("scripting.group"));
		addScriptingAction();
		bars.getMenuManager().add(new Separator("duplicate.group"));
		addDuplicateAction();
		
	}

	private void addDuplicateAction(){
		if (duplicateWindowCCI == null) {
			CommandContributionItemParameter ccip = new CommandContributionItemParameter(PlatformUI.getWorkbench()
					.getActiveWorkbenchWindow(), null, DuplicatePlotAction.COMMAND_ID,
					CommandContributionItem.STYLE_PUSH);
			ccip.label = "Create Duplicate Plot";
			ccip.icon = AnalysisRCPActivator.getImageDescriptor("icons/chart_curve_add.png");
			duplicateWindowCCI = new CommandContributionItem(ccip);
		}
		bars.getMenuManager().add(duplicateWindowCCI);
	}

	private void addScriptingAction(){
		
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
		bars.getMenuManager().add(openPyDevConsoleCCI);
		bars.getMenuManager().add(updateDefaultPlotCCI);
		bars.getMenuManager().add(getPlotBeanCCI);
	}

	//Datasetplotter
	private void setup1D() {
		mainPlotter.setMode(PlottingMode.ONED);
		plotUI = new Plot1DUIComplete(this, manager, bars, parentComp, getPage(), name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.ONED;
	}

	//Abstract plotting System
	private void setupPlotting1D() {
		plotUI = new Plotting1DUI(plottingSystem);
		addScriptingAction();
		addDuplicateAction();
		previousPlotMode = GuiPlotMode.ONED;
	}

	//Datasetplotter
	private void setup2D() {
		mainPlotter.setMode(PlottingMode.TWOD);
		plotUI = new Plot2DUI(this, mainPlotter, manager, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.TWOD;
	}

	//Abstract plotting System
	private void setupPlotting2D() {
		plotUI = new Plotting2DUI(this, plottingSystem);
		addScriptingAction();
		addDuplicateAction();
		previousPlotMode = GuiPlotMode.TWOD;
	}

	// AbstractPlottingSystem
	private void setupPlotting2DROIProfile(){
		setupPlotting2D();
		// set the profiles axes
		if(myBeanMemory != null){
			AbstractDataset xAxisValues = myBeanMemory.getAxis(AxisMapBean.XAXIS);
			AbstractDataset yAxisValues = myBeanMemory.getAxis(AxisMapBean.YAXIS);
			List<AbstractDataset> axes1 = Collections.synchronizedList(new LinkedList<AbstractDataset>());
			List<AbstractDataset> axes2 = Collections.synchronizedList(new LinkedList<AbstractDataset>());
			if(xAxisValues!=null){
				axes1.add(0, xAxisValues);
				axes2.add(0, yAxisValues);
			}
			if(yAxisValues!=null){
				axes1.add(1, yAxisValues);
				axes2.add(1, xAxisValues);
			}
			sideProfile1.setAxes(axes1);
			sideProfile2.setAxes(axes2);
		}
		previousPlotMode = GuiPlotMode.TWOD_ROIPROFILES;
	}

	private void setupMulti2D() {
		mainPlotter.setMode(PlottingMode.MULTI2D);
		plotUI = new Plot2DMultiUI(this, mainPlotter, manager, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.MULTI2D;
	}

	private void setup2DSurface() {
		mainPlotter.useWindow(true);
		mainPlotter.setMode(PlottingMode.SURF2D);
		plotUI = new PlotSurf3DUI(this, mainPlotter, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.SURF2D;
	}

	private void setupMulti1DPlot() {
		mainPlotter.setMode(PlottingMode.ONED_THREED);
		plotUI = new Plot1DStackUI(this, bars, mainPlotter, parentComp, page);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.ONED_THREED;
	}

	private void setupScatter2DPlot() {
		mainPlotter.setMode(PlottingMode.SCATTER2D);
		plotUI = new PlotScatter2DUI(this, bars, mainPlotter, parentComp, page, name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.SCATTER2D;
	}

	//Abstract plotting System
	private void setupScatterPlotting2D() {
		plotUI = new PlottingScatter2DUI(plottingSystem);
		addScriptingAction();
		addDuplicateAction();
		previousPlotMode = GuiPlotMode.SCATTER2D;
	}

	private void setupScatter3DPlot() {
		mainPlotter.setMode(PlottingMode.SCATTER3D);
		plotUI = new PlotScatter3DUI(this, mainPlotter, parentComp, getPage(), bars, name);
		addCommonActions();
		bars.updateActionBars();
		previousPlotMode = GuiPlotMode.SCATTER3D;
	}

	/**
	 * @param plotMode
	 */
	public void updatePlotMode(GuiPlotMode plotMode) {
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM) {
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
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_SYSTEM) {
			if(!plotMode.equals(GuiPlotMode.EMPTY)){
				cleanUp(plotMode);
				if (plotMode.equals(GuiPlotMode.ONED)) {
					setupPlotting1D();
				} else if (plotMode.equals(GuiPlotMode.TWOD)) {
					setupPlotting2D();
				} else if(plotMode.equals(GuiPlotMode.TWOD_ROIPROFILES)){
					setupPlotting2DROIProfile();
				} else if (plotMode.equals(GuiPlotMode.SCATTER2D)) {
					setupScatterPlotting2D();
				} else if (plotMode.equals(GuiPlotMode.ONED_THREED)) {
					setupMulti1DPlot();
				} else if (plotMode.equals(GuiPlotMode.SURF2D)) {
					setup2DSurface();
				} else if (plotMode.equals(GuiPlotMode.SCATTER3D)) {
					setupScatter3DPlot();
				} else if (plotMode.equals(GuiPlotMode.MULTI2D)) {
					setupMulti2D();
				} 
			}else if (plotMode.equals(GuiPlotMode.EMPTY)) {
				clearPlot();
			}
		}
	}

	public void clearPlot() {
		if (!mainPlotter.isDisposed()) {
			mainPlotter.emptyPlot();
			mainPlotter.refresh(true);
		}
		if (plottingSystem != null) {
			plottingSystem.clearRegions();
			plottingSystem.reset();
			plottingSystem.repaint();
		}
	}

	private void updatePlotModeAsync(GuiPlotMode plotMode) {
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM) {
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
		if (getDefaultPlottingSystemChoice() == PreferenceConstants.PLOT_VIEW_ABSTRACT_PLOTTING_SYSTEM) {
			if (plotMode.equals(GuiPlotMode.ONED)){
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.ONED);
							setupPlotting1D();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.TWOD)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.TWOD);
							setupPlotting2D();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.TWOD_ROIPROFILES)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.TWOD_ROIPROFILES);
							setupPlotting2DROIProfile();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.SCATTER2D)){
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.SCATTER2D);
							setupScatterPlotting2D();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.ONED_THREED)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.ONED_THREED);
							setupMulti1DPlot();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.SURF2D)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.SURF2D);
							setup2DSurface();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.SCATTER3D)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.SCATTER3D);
							setupScatter3DPlot();
						} finally {
							undoBlock();
						}
					}
				});
			} else if (plotMode.equals(GuiPlotMode.MULTI2D)) {
				doBlock();
				parentComp.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						try {
							cleanUp(GuiPlotMode.MULTI2D);
							setupMulti2D();
						} finally {
							undoBlock();
						}
					}
				});
			}else if (plotMode.equals(GuiPlotMode.EMPTY)) {
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
	}

	//not used
	public PlottingMode getPlottingSystemMode(){
		final Collection<ITrace> traces = plottingSystem.getTraces();
		if (traces==null) return PlottingMode.EMPTY;
		for (ITrace iTrace : traces) {
			if (iTrace instanceof ILineTrace) return PlottingMode.ONED;
			if (iTrace instanceof IImageTrace) return PlottingMode.TWOD;
		}
		return PlottingMode.EMPTY;
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
		if(getDefaultPlottingSystemChoice()==PreferenceConstants.PLOT_VIEW_DATASETPLOTTER_PLOTTING_SYSTEM){
			Iterator<IObserver> iter = observers.iterator();
			while (iter.hasNext()) {
				IObserver listener = iter.next();
				listener.update(this, histoUpdate);
			}
		}
	}

	@Override
	public void update(Object theObserved, Object changeCode) {
		if (theObserved instanceof HistogramView) {
			HistogramUpdate update = (HistogramUpdate) changeCode;
			mainPlotter.applyColourCast(update);

			if(!mainPlotter.isDisposed())
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

	public AbstractPlottingSystem getPlottingSystem() {
		return plottingSystem;
	}

	/**
	 * Required if you want to make tools work with Abstract Plotting System.
	 */
	@SuppressWarnings("rawtypes")
	public Object getAdapter(final Class clazz) {
		if (clazz == IToolPageSystem.class) {
			return plottingSystem;
		}
		return null;
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
			if (plottingSystem != null && !plottingSystem.isDisposed()) {
				plottingSystem.removeRegionListener(regionListener);
				plottingSystem.dispose();
			}
			if(sideProfile1 != null && !sideProfile1.isDisposed()){
				sideProfile1.dispose();
			}
			if(sideProfile2 != null && !sideProfile2.isDisposed()){
				sideProfile2.dispose();
			}
		} catch (Exception ne) {
			logger.debug("Cannot clean up plotter!", ne);
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

	// Make the PlotWindow a RegionListener (new plotting)
	private IRegionListener regionListener;
	private Map<String,Collection<ITrace>> registeredTraces;

	private IRegionListener getRegionListener(){
		return new IRegionListener.Stub() {
			@Override
			public void regionRemoved(RegionEvent evt) {
				IRegion region = evt.getRegion();
				if (region!=null) {
					region.removeROIListener(PlotWindow.this);
					Object obj = evt.getSource();
					//if we delete current ROI
					if(currentRoiPair.getName().equals(obj.toString())){
						currentRoiPair = previousRoiPair;
						if (roiPairList!=null) {
							for (ROIPair<String, ROIBase> roiPair : roiPairList) {
								if(previousRoiPair.getName().equals(roiPair.getName())){
									roiPairList.remove(roiPair);
									break;
								}
							}
							if(roiPairList.size()>0)
								previousRoiPair = roiPairList.get(0);
							else
								previousRoiPair = null;
						} else {
							previousRoiPair = null;
						}
					}
					//if we delete the previous ROI
					else if(previousRoiPair.getName().equals(obj.toString())){
						for (ROIPair<String, ROIBase> roiPair : roiPairList) {
							if(previousRoiPair.getName().equals(roiPair.getName())){
								roiPairList.remove(roiPair);
								break;
							}
						}
						if(roiPairList.size()>0)
							previousRoiPair = roiPairList.get(0);
						else
							previousRoiPair = null;
					}
					//if we delete a ROI which is in the ROIlist and which is not the current nor the previous
					else {
						for (ROIPair<String, ROIBase> roiPair : roiPairList) {
							if(roiPair.getName().equals(obj.toString())){
								roiPairList.remove(roiPair);
								break;
							}
						}
					}
					if(currentRoiPair!=null)
						updateGuiBean(currentRoiPair.getRoi());
					else
						updateGuiBean(null);
					clearTraces(evt.getRegion());
				}
			}
			@Override
			public void regionAdded(RegionEvent evt) {
				if (evt.getRegion()!=null) {
					ROIBase roi = evt.getRegion().getROI();
					if(roi!=null){
						currentRoiPair = new ROIPair<String, ROIBase>(evt.getSource().toString(), roi);
						if(roiPairList.size()>0){
							//Remove the current ROI from ROI List and replace it by previous one
							for (ROIPair<String, ROIBase> roiPair : roiPairList) {
								if(roiPair.getName().equals(currentRoiPair.getName())){
									roiPairList.remove(roiPair);
									roiPairList.add(new ROIPair<String, ROIBase>(previousRoiPair.getName(), previousRoiPair.getRoi()));
								//	break;
								}
							}
						}
						
					}
					updateGuiBean(roi);
				}
			}
			@Override
			public void regionCreated(RegionEvent evt) {
				if (evt.getRegion()!=null) {
					evt.getRegion().addROIListener(PlotWindow.this);
					IRegion region = evt.getRegion();
					ROIBase roi = region.getROI();
					if(roi!=null){
						updateGuiBean(roi);
					}
					
				}
			}
		};
	}

	protected void clearTraces(final IRegion region) {
		final String name = region.getName();
		Collection<ITrace> registered = this.registeredTraces.get(name);
		if (registered!=null) for (ITrace iTrace : registered) {
			plottingSystem.removeTrace(iTrace);
		}
	}

	@Override
	public void roiDragged(ROIEvent evt) {
		ROIBase roi = evt.getROI();
		if(roi!=null){
			// TODO with a timer
			//updateGuiBean(roi);
		}
	}

	@Override
	public void roiChanged(ROIEvent evt) {
		ROIBase roi = evt.getROI();
		if(roi!=null){
			String id = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart().getSite().getId();
			if(id.startsWith(PlotView.ID)){
			
				ROIPair<String, ROIBase> evtPair = new ROIPair<String, ROIBase>(evt.getSource().toString(), roi);
				if(currentRoiPair!=null && !evtPair.getName().equals(currentRoiPair.getName()))
					previousRoiPair = currentRoiPair;
				currentRoiPair = evtPair;
			
				if(previousRoiPair!=null && !roiPairList.contains(previousRoiPair))
					roiPairList.add(new ROIPair<String, ROIBase>(previousRoiPair.getName(), previousRoiPair.getRoi()));
				if (roiPairList.size()>0){
					//Remove the current ROI from ROI List and replace it by previous one
					for (ROIPair<String, ROIBase> roiPair : roiPairList) {
						if(roiPair.getName().equals(currentRoiPair.getName())){
							roiPairList.remove(roiPair);
							break;
						}
					}
				}
				updateGuiBean(roi);
			}
		}
	}

	protected List<ROIPair<String, ROIBase>> roiPairList = new ArrayList<ROIPair<String, ROIBase>>();
	protected ROIPair<String, ROIBase> currentRoiPair;
	protected ROIPair<String, ROIBase> previousRoiPair;
	
	protected void updateGuiBean(ROIBase roib){
		manager.removeGUIInfo(GuiParameters.ROIDATA);
		manager.putGUIInfo(GuiParameters.ROIDATA, roib);
		manager.removeGUIInfo(GuiParameters.ROIDATALIST);
		if(roib instanceof RectangularROI)
			manager.putGUIInfo(GuiParameters.ROIDATALIST, createNewRROIList());
		if(roib instanceof LinearROI)
			manager.putGUIInfo(GuiParameters.ROIDATALIST, createNewLROIList());
		if(roib instanceof SectorROI)
			manager.putGUIInfo(GuiParameters.ROIDATALIST, createNewSROIList());
	}

	public LinearROIList createNewLROIList() {
		LinearROIList list = new LinearROIList();
		if (roiPairList != null) {
			for (ROIPair<String, ROIBase> roiPair: roiPairList) {
				if(roiPair.getRoi() instanceof LinearROI){
					list.add((LinearROI) roiPair.getRoi());
				}
			}
			if(roiPairList.size()==0)
				return null;
		}
		return list;
	}

	public RectangularROIList createNewRROIList() {
		RectangularROIList list = new RectangularROIList();
		if (roiPairList != null) {
			for (ROIPair<String, ROIBase> roiPair: roiPairList) {
				if(roiPair.getRoi() instanceof RectangularROI){
					list.add((RectangularROI) roiPair.getRoi());
				}
			}
			if(roiPairList.size()==0)
				return null;
		}
		return list;
	}

	public SectorROIList createNewSROIList() {
		SectorROIList list = new SectorROIList();
		if (roiPairList != null) {
			for (ROIPair<String, ROIBase> roiPair: roiPairList) {
				if(roiPair.getRoi() instanceof SectorROI){
					list.add((SectorROI) roiPair.getRoi());
				}
			}
			if(roiPairList.size()==0)
				return null;
		}
		return list;
	}

	private int getDefaultPlottingSystemChoice() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		return preferenceStore.isDefault(PreferenceConstants.PLOT_VIEW_PLOTTING_SYSTEM) ? 
				preferenceStore.getDefaultInt(PreferenceConstants.PLOT_VIEW_PLOTTING_SYSTEM)
				: preferenceStore.getInt(PreferenceConstants.PLOT_VIEW_PLOTTING_SYSTEM);
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

/**
 * Class to store key-value (name-RoiBase) pairs for handling ROIs
 */
class ROIPair<A, B> {
	private final A name;
	private final B roi;

	public ROIPair(A name, B roi) {
		super();
		this.name = name;
		this.roi = roi;
	}

	@Override
	public int hashCode() {
		int hashFirst = name != null ? name.hashCode() : 0;
		int hashSecond = roi != null ? roi.hashCode() : 0;
		return (hashFirst + hashSecond) * hashSecond + hashFirst;
	}

	@Override
	public boolean equals(Object other) {
		if (other instanceof ROIPair) {
			ROIPair<?, ?> otherPair = (ROIPair<?, ?>) other;
			return 
					((this.name == otherPair.name ||
					(this.name != null && otherPair.name != null &&
					this.name.equals(otherPair.name))) &&
					(this.roi == otherPair.roi ||
					(this.roi != null && otherPair.roi != null &&
					this.roi.equals(otherPair.roi))) );
		}
		return false;
	}

	@Override
	public String toString(){ 
		return "(" + name + ", " + roi + ")"; 
	}

	public A getName() {
		return name;
	}

	public B getRoi() {
		return roi;
	}
}
