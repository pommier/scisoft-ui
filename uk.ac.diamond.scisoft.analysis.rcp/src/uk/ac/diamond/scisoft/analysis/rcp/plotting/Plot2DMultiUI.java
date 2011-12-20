/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import java.io.File;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuCreator;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.printing.PrintDialog;
import org.eclipse.swt.printing.PrinterData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractCompoundDataset;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.RGBDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramDataUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.TickFormatting;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;

/**
 *
 */
public class Plot2DMultiUI extends AbstractPlotUI {

	private DataSetPlotter mainPlotter;
	private Composite compParent;
	private PlotWindow plotWindow;
	private IWorkbenchPage page;
	private String plotViewID;
//	private IGuiInfoManager manager;
	private AxisValues xAxis;
	private AxisValues yAxis;
	private HistogramView histogramView;
	private Action xLabelTypeRound;
	private Action xLabelTypeFloat;
	private Action xLabelTypeExponent;
	private Action xLabelTypeSI;
	private Action yLabelTypeRound;
	private Action yLabelTypeFloat;
	private Action yLabelTypeExponent;
	private Action yLabelTypeSI;
	private Action resetView;
	private Action saveGraph;
	private Action copyGraph;
	private Action printGraph;
	private Action canvasAspect;
	private HistogramDataUpdate histoUpdate;
	private static final Logger logger = LoggerFactory.getLogger(Plot2DMultiUI.class);
	
	private static String[] listPrintScaleText = { ResourceProperties.getResourceString("PRINT_LISTSCALE_0"),
		ResourceProperties.getResourceString("PRINT_LISTSCALE_1"), ResourceProperties.getResourceString("PRINT_LISTSCALE_2"),
		ResourceProperties.getResourceString("PRINT_LISTSCALE_3"), ResourceProperties.getResourceString("PRINT_LISTSCALE_4"),
		ResourceProperties.getResourceString("PRINT_LISTSCALE_5"), ResourceProperties.getResourceString("PRINT_LISTSCALE_6") };
	private String printButtonText = ResourceProperties.getResourceString("PRINT_BUTTON");
	private String printToolTipText = ResourceProperties.getResourceString("PRINT_TOOLTIP");
	private String printImagePath = ResourceProperties.getResourceString("PRINT_IMAGE_PATH");
	private String copyButtonText = ResourceProperties.getResourceString("COPY_BUTTON");
	private String copyToolTipText = ResourceProperties.getResourceString("COPY_TOOLTIP");
	private String copyImagePath = ResourceProperties.getResourceString("COPY_IMAGE_PATH");
	private String saveButtonText = ResourceProperties.getResourceString("SAVE_BUTTON");
	private String saveToolTipText = ResourceProperties.getResourceString("SAVE_TOOLTIP");
	private String saveImagePath = ResourceProperties.getResourceString("SAVE_IMAGE_PATH");
	
	/**
	 * @param window 
	 * @param plotter
	 * @param parent
	 * @param page 
	 * @param id 
	 */
	public Plot2DMultiUI(PlotWindow window, 
					     final DataSetPlotter plotter,
					     @SuppressWarnings("unused") final IGuiInfoManager manager,
					      Composite parent, IWorkbenchPage page, 
					      IActionBars bars, String id)
	{
		this.mainPlotter = plotter;
		this.compParent = parent;
		this.plotWindow = window;
		this.page = page;
		plotViewID = id;
//		this.manager = manager;
		initHistogramView(plotViewID);
		mainPlotter.registerUI(this);
		initSidePlotView();
		buildMenuActions(bars.getMenuManager(), plotter); 
		buildToolActions(bars.getToolBarManager(), plotter, parent.getShell());										 
		xAxis = new AxisValues();
		yAxis = new AxisValues();		
		bars.updateActionBars();
	}

	public void initHistogramView(String id) {
		try {
			 histogramView = (HistogramView) page.showView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView",
					id, IWorkbenchPage.VIEW_CREATE);			 
			 plotWindow.addIObserver(histogramView);
			 histogramView.addIObserver(plotWindow);
		} catch (PartInitException e) {
			logger.error("Failed to initialized histogram View");
		}		
	}	
	
	private void buildMenuActions(IMenuManager manager, final DataSetPlotter plotter)
	{
		xLabelTypeRound = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.roundAndChopMode);
				plotter.refresh(true);
			}
		};
		xLabelTypeRound.setText("X-Axis labels integer");
		xLabelTypeRound.setToolTipText("Change the labelling on the x-axis to integer numbers");
		xLabelTypeRound.setChecked(true);
		xLabelTypeFloat = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.plainMode);
				plotter.refresh(true);
			}
		};
		xLabelTypeFloat.setText("X-Axis labels real");
		xLabelTypeFloat.setToolTipText("Change the labelling on the x-axis to real numbers");

		xLabelTypeExponent = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.useExponent);
				plotter.refresh(true);
			}
		};
		xLabelTypeExponent.setText("X-Axis labels exponents");
		xLabelTypeExponent.setToolTipText("Change the labelling on the x-axis to using exponents");

		xLabelTypeSI = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.useSIunits);	
				plotter.refresh(true);
			}
		};
		xLabelTypeSI.setText("X-Axis labels SI units");
		xLabelTypeSI.setToolTipText("Change the labelling on the x-axis to using SI units");
		yLabelTypeRound = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setYTickLabelFormat(TickFormatting.roundAndChopMode);
				plotter.refresh(true);
			}
		};
		yLabelTypeRound.setText("Y-Axis labels integer");
		yLabelTypeRound.setToolTipText("Change the labelling on the y-axis to integer numbers");
		yLabelTypeRound.setChecked(true);
		yLabelTypeFloat = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setYTickLabelFormat(TickFormatting.plainMode);
				plotter.refresh(true);
			}
		};
		yLabelTypeFloat.setText("Y-Axis labels real");
		yLabelTypeFloat.setToolTipText("Change the labelling on the y-axis to real numbers");

		yLabelTypeExponent = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setYTickLabelFormat(TickFormatting.useExponent);
				plotter.refresh(true);
			}
		};
		yLabelTypeExponent.setText("Y-Axis labels exponents");
		yLabelTypeExponent.setToolTipText("Change the labelling on the y-axis to using exponents");

		yLabelTypeSI = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setYTickLabelFormat(TickFormatting.useSIunits);	
				plotter.refresh(true);
			}
		};
		yLabelTypeSI.setText("Y-Axis labels SI units");
		yLabelTypeSI.setToolTipText("Change the labelling on the y-axis to using SI units");

/*		colourCastLinear = new Action("",IAction.AS_RADIO_BUTTON) 
		{
			@Override
			public void run() 
			{
				plotter.setZAxisScaling(ScaleType.LINEAR);
				plotter.refresh(true);
				histogramView.setScaling(ScaleType.LINEAR);
				logScaleSettings.put(histogramView.getPartName(), 0);
			}
		};
		colourCastLinear.setChecked(getPreferenceColourScaleChoice() == 0);
		colourCastLinear.setText("Linear mappig colours");
		colourCastLinear.setToolTipText("Apply linear colour mapping to image");

		colourCastLog = new Action("",IAction.AS_RADIO_BUTTON) 
		{
			@Override
			public void run() 
			{
				plotter.setZAxisScaling(ScaleType.LN);
				plotter.refresh(true);
				histogramView.setScaling(ScaleType.LN);
				logScaleSettings.put(histogramView.getPartName(), 1);
			}
		};
		colourCastLog.setText("Logarithmic mappig colours");
		colourCastLog.setToolTipText("Apply logarithmic colour mapping to image");
		colourCastLog.setChecked(getPreferenceColourScaleChoice() != 0);
		
		if (colourCastLog.isChecked()) {
			plotter.setZAxisScaling(ScaleType.LN);
			histogramView.setScaling(ScaleType.LN);
		}
		
		gradientMode = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run()
			{
				plotter.setGradientImageMode(gradientMode.isChecked());
			}
		};
		gradientMode.setText("Switch to gradient mode");
		gradientMode.setToolTipText("Switches display to gradient mode shows magnitude of gradients");
*/		
		MenuManager xAxisMenu = new MenuManager("X-Axis");
		MenuManager yAxisMenu = new MenuManager("Y-Axis");
		manager.add(xAxisMenu);
		manager.add(yAxisMenu);
		xAxisMenu.add(xLabelTypeFloat);
		xAxisMenu.add(xLabelTypeRound);
		xAxisMenu.add(xLabelTypeExponent);
		xAxisMenu.add(xLabelTypeSI);
		yAxisMenu.add(yLabelTypeFloat);
		yAxisMenu.add(yLabelTypeRound);
		yAxisMenu.add(yLabelTypeExponent);
		yAxisMenu.add(yLabelTypeSI);
/*		manager.add(colourCastLinear);
		manager.add(colourCastLog);
		manager.add(gradientMode);*/
		manager.update(true);
	}
	

	
	private void buildToolActions(IToolBarManager manager, final DataSetPlotter plotter,
			  final Shell shell)
	{

		
		resetView = new Action() {
			@Override
			public void run()
			{
				mainPlotter.resetView();
				mainPlotter.refresh(false);
			}
		};
		resetView.setText("Reset view");
		resetView.setToolTipText("Reset panning and zooming");
		resetView.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/house_go.png"));
/*		
		monitorValues = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				plotter.setPlotActionEnabled(monitorValues.isChecked());
			}
		};
		monitorValues.setText("Monitor position");
		monitorValues.setToolTipText("Monitor the position in the image");
		monitorValues.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/monitor.png"));
*/
		saveGraph = new Action() {
			
			// Cache file name otherwise they have to keep
			// choosing the folder.
			private String filename;
			
			@Override
			public void run() {
				
				FileDialog dialog = new FileDialog (shell, SWT.SAVE);
				
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

				plotter.saveGraph(filename, PlotExportUtil.FILE_TYPES[dialog.getFilterIndex()]);
			}
		};
		saveGraph.setText(saveButtonText);
		saveGraph.setToolTipText(saveToolTipText);
		saveGraph.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(saveImagePath));

		copyGraph = new Action() {
			@Override
			public void run() {
				plotter.copyGraph();
			}
		};
		copyGraph.setText(copyButtonText);
		copyGraph.setToolTipText(copyToolTipText);
		copyGraph.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(copyImagePath));

		printGraph = new Action() {
			@Override
			public void run() {
//				PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//				PrinterData printerData = dialog.open();
				plotter.printGraph();
			}
		};
		printGraph.setText(printButtonText);
		printGraph.setToolTipText(printToolTipText);
		printGraph.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor(printImagePath));
//		printGraph.setMenuCreator(new IMenuCreator() {
//			@Override
//			public Menu getMenu(Control parent) {
//				Menu menu = new Menu(parent);
//				MenuItem item10 = new MenuItem(menu, SWT.None);
//				item10.setText(listPrintScaleText[0]);
//				item10.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.1f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item25 = new MenuItem(menu, SWT.None);
//				item25.setText(listPrintScaleText[1]);
//				item25.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.25f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item33 = new MenuItem(menu, SWT.None);
//				item33.setText(listPrintScaleText[2]);
//				item33.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.33f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item50 = new MenuItem(menu, SWT.None);
//				item50.setText(listPrintScaleText[3]);
//				item50.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.5f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item66 = new MenuItem(menu, SWT.None);
//				item66.setText(listPrintScaleText[4]);
//				item66.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.66f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item75 = new MenuItem(menu, SWT.None);
//				item75.setText(listPrintScaleText[5]);
//				item75.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 0.75f);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				MenuItem item100 = new MenuItem(menu, SWT.None);
//				item100.setText(listPrintScaleText[6]);
//				item100.addSelectionListener(new SelectionListener() {
//					@Override
//					public void widgetSelected(SelectionEvent e) {
//						PrintDialog dialog = new PrintDialog(shell, SWT.NULL);
//						PrinterData printerData = dialog.open();
//						plotter.printGraph(printerData, 1);
//					}
//					@Override
//					public void widgetDefaultSelected(SelectionEvent e) {}
//				});
//				return menu;
//			}
//			@Override
//			public Menu getMenu(Menu parent) {
//				return null;
//			}
//			@Override
//			public void dispose() {}
//		});

		
		canvasAspect = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run() {
				plotter.imagePlotSetCanvasAspectRatio(canvasAspect.isChecked());
				plotter.refresh(false);
			}
		};
		canvasAspect.setText("Canvas");
		canvasAspect.setToolTipText("Switch between canvas/data aspect ratio");
		canvasAspect.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/arrow_out.png"));

	/*	manager.add(new Separator());
		try {
			switchToTabs = getSidePlotView().createSwitchActions(this);

			for (Action action: switchToTabs) {
				manager.add(action);
			}
		} catch (IllegalStateException ex) {}*/
		manager.add(new Separator());
		
		manager.add(resetView);
	//	manager.add(monitorValues);
		manager.add(new Separator(getClass().getName()+printButtonText));
		manager.add(saveGraph);
		manager.add(copyGraph);
		manager.add(printGraph);
		manager.add(new Separator(getClass().getName()+"Canvas"));
		manager.add(canvasAspect);		
		manager.update(true);
	}
	
	
	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate)
	{
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<AbstractDataset> datasets = Collections.synchronizedList(new LinkedList<AbstractDataset>());
			AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS);
			AbstractDataset yAxisValues = dbPlot.getAxis(AxisMapBean.YAXIS);
			xAxis.clear();
			yAxis.clear();
			mainPlotter.setAxisModes((xAxisValues == null ? AxisMode.LINEAR : AxisMode.CUSTOM),
					                 (yAxisValues == null ? AxisMode.LINEAR : AxisMode.CUSTOM),
					                 AxisMode.LINEAR);
			if (xAxisValues != null) {
				if (xAxisValues.getName() != null && xAxisValues.getName().length() > 0)
					mainPlotter.setXAxisLabel(xAxisValues.getName());
				else
					mainPlotter.setXAxisLabel("X-Axis");
				xAxis.setValues(xAxisValues);
				mainPlotter.setXAxisValues(xAxis, 1);
			} else
				mainPlotter.setXAxisLabel("X-Axis");
			if (yAxisValues != null) {
				if (yAxisValues.getName() != null && yAxisValues.getName().length() > 0)
					mainPlotter.setYAxisLabel(yAxisValues.getName());
				else
					mainPlotter.setYAxisLabel("Y-Axis");
				yAxis.setValues(yAxisValues);
				mainPlotter.setYAxisValues(yAxis);
			} else
				mainPlotter.setYAxisLabel("Y-Axis");
			mainPlotter.setYTickLabelFormat(TickFormatting.roundAndChopMode);
			mainPlotter.setXTickLabelFormat(TickFormatting.roundAndChopMode);
			while (iter.hasNext()) {
				DataSetWithAxisInformation dataSetAxis = iter.next();
				AbstractDataset data = dataSetAxis.getData();
				datasets.add(data);
			}
					

			try {
				mainPlotter.replaceAllPlots(datasets);
			} catch (PlotException e) {
				e.printStackTrace();
			}

			mainPlotter.setTitle(datasets.get(0).getName());

			compParent.getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
//					getSidePlotView().processPlotUpdate();
					plotWindow.notifyUpdateFinished();
				}
			});	
			boolean useRGB = true;
			for (AbstractDataset data : datasets) { 
				useRGB &= 
					(data instanceof RGBDataset) ||
	 	  	  	    (data instanceof AbstractCompoundDataset &&
					(((AbstractCompoundDataset)data).getElementsPerItem() == 3 ||
			 		 ((AbstractCompoundDataset)data).getElementsPerItem() == 4));
			}
			if (!useRGB) {
				for (AbstractDataset data : datasets) {
					if (!(data instanceof RGBDataset) &&
						!(data instanceof AbstractCompoundDataset)) {
						histoUpdate = new HistogramDataUpdate(data);
						break;
					}
				}
				plotWindow.notifyHistogramChange(histoUpdate);
			} else
				mainPlotter.refresh(true);
		}
	}	
	
	@Override
	public void deactivate(boolean leaveSidePlotOpen) {
		histoUpdate = null;
		plotWindow.deleteIObserver(histogramView);
//		try {
//			getSidePlotView().deactivate(leaveSidePlotOpen);
//		} catch (IllegalStateException ex) {}
		page.hideView(histogramView);
	}	
}
