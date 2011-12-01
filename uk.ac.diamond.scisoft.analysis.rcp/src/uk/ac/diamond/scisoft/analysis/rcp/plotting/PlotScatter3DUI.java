/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.plotserver.AxisMapBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataBean;
import uk.ac.diamond.scisoft.analysis.plotserver.DataSetWithAxisInformation;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramDataUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.TickFormatting;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.PlotExportUtil;
import uk.ac.diamond.scisoft.analysis.rcp.util.ResourceProperties;
import uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView;

/**
 *
 */
public class PlotScatter3DUI extends AbstractPlotUI {

	private Composite parent;
	private PlotWindow plotWindow;
	private AxisValues xAxis;
	private AxisValues yAxis;
	private AxisValues zAxis;
	private DataSetPlotter mainPlotter;
	private Action boundingBox;
	private Action saveGraph;
	private Action copyGraph;
	private Action printGraph;
	private Action useTransparency;
	private Action renderEdgeOnly;
	private Action uniformSize;
	private Action xLabelTypeRound = null;
	private Action xLabelTypeFloat = null;
	private Action xLabelTypeExponent = null;
	private Action xLabelTypeSI = null;
	private Action yLabelTypeRound = null;
	private Action yLabelTypeFloat = null;
	private Action yLabelTypeExponent = null;
	private Action yLabelTypeSI = null;	
	private Action zLabelTypeRound = null;
	private Action zLabelTypeFloat = null;
	private Action zLabelTypeExponent = null;
	private Action zLabelTypeSI = null;		
	private HistogramView histogramView;
	private Action resetView;
	private IWorkbenchPage page;
	
	private String[] listPrintScaleText = { ResourceProperties.getResourceString("PRINT_LISTSCALE_0"),
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

	public PlotScatter3DUI(PlotWindow window, 
			 			   final DataSetPlotter plotter,
			 			   Composite parent, 
			 			   IWorkbenchPage page, 
			 			   IActionBars bars,
			 			   String id) {
		
		this.parent = parent;
		this.plotWindow = window;
		xAxis = new AxisValues();
		yAxis = new AxisValues();
		zAxis = new AxisValues();
		this.mainPlotter = plotter;
		this.page = page;
		buildMenuActions(bars.getMenuManager(), plotter); 
		buildToolActions(bars.getToolBarManager(), 
		         plotter, parent.getShell());								 

		try {
			 histogramView = (HistogramView) page.showView("uk.ac.diamond.scisoft.analysis.rcp.views.HistogramView",
					id, IWorkbenchPage.VIEW_CREATE);
			plotWindow.addIObserver(histogramView);
			histogramView.addIObserver(plotWindow);
		} catch (PartInitException e) {
			e.printStackTrace();
		}		
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
		boundingBox = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run()
			{
				plotter.enableBoundingBox(boundingBox.isChecked());
				plotter.refresh(false);
			}
		};
		boundingBox.setText("Bounding box on/off");
		boundingBox.setToolTipText("Bounding box on/off");
		boundingBox.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/box.png"));		
		boundingBox.setChecked(true);
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
		manager.add(resetView);
		manager.add(boundingBox);
		manager.add(new Separator(getClass().getName()+"Print"));
		manager.add(saveGraph);
		manager.add(copyGraph);
		manager.add(printGraph);
		
		// Needed when toolbar is attached to an editor
		// or else the bar looks empty.
		manager.update(true);

	}
	
	private void buildMenuActions(IMenuManager manager, final DataSetPlotter plotter)
	{	
		useTransparency = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run()
			{
				plotter.useTransparency(useTransparency.isChecked());
				plotter.refresh(false);
			}
		};
		useTransparency.setText("Use transparency");
		useTransparency.setToolTipText("Switch on/off transparency");
		renderEdgeOnly = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run() 
			{
				if (renderEdgeOnly.isChecked())
					plotter.useTransparency(true);
				else
					plotter.useTransparency(useTransparency.isChecked());
				plotter.useDrawOutlinesOnly(renderEdgeOnly.isChecked());
			}
		};
		xLabelTypeRound = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.roundAndChopMode);
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
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
				plotter.refresh(false);
			}
		};
		yLabelTypeSI.setText("Y-Axis labels SI units");
		yLabelTypeSI.setToolTipText("Change the labelling on the y-axis to using SI units");
		zLabelTypeRound = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setZTickLabelFormat(TickFormatting.roundAndChopMode);
				plotter.refresh(false);
			}
		};
		zLabelTypeRound.setText("Z-Axis labels integer");
		zLabelTypeRound.setToolTipText("Change the labelling on the z-axis to integer numbers");
		zLabelTypeFloat = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.plainMode);
				plotter.refresh(false);
			}
		};
		zLabelTypeFloat.setText("Z-Axis labels real");
		zLabelTypeFloat.setToolTipText("Change the labelling on the z-axis to real numbers");
		zLabelTypeFloat.setChecked(true);

		zLabelTypeExponent = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setXTickLabelFormat(TickFormatting.useExponent);
				plotter.refresh(false);
			}
		};
		zLabelTypeExponent.setText("Z-Axis labels exponents");
		zLabelTypeExponent.setToolTipText("Change the labelling on the z-axis to using exponents");

		zLabelTypeSI = new Action("",IAction.AS_RADIO_BUTTON)
		{
			@Override
			public void run()
			{
				plotter.setZTickLabelFormat(TickFormatting.useSIunits);	
				plotter.refresh(false);
			}
		};
		zLabelTypeSI.setText("Z-Axis labels SI units");
		zLabelTypeSI.setToolTipText("Change the labelling on the z-axis to using SI units");
		
		renderEdgeOnly.setText("Draw outlines only");
		renderEdgeOnly.setToolTipText("Switch on/off drawing outlines only");
		
		uniformSize = new Action("",IAction.AS_CHECK_BOX)
		{
			@Override
			public void run() {
				plotter.useUniformSize(uniformSize.isChecked());
				plotter.refresh(false);
			}
		};
		uniformSize.setText("Uniform size");
		uniformSize.setToolTipText("Switch on/off uniform point size");
		manager.add(useTransparency);
		manager.add(renderEdgeOnly);
		manager.add(uniformSize);
		MenuManager xAxisMenu = new MenuManager("X-Axis");
		MenuManager yAxisMenu = new MenuManager("Y-Axis");
		MenuManager zAxisMenu = new MenuManager("Z-Axis");
		manager.add(xAxisMenu);
		manager.add(yAxisMenu);		
		manager.add(zAxisMenu);
		xAxisMenu.add(xLabelTypeFloat);
		xAxisMenu.add(xLabelTypeRound);
		xAxisMenu.add(xLabelTypeExponent);
		xAxisMenu.add(xLabelTypeSI);
		yAxisMenu.add(yLabelTypeFloat);
		yAxisMenu.add(yLabelTypeRound);
		yAxisMenu.add(yLabelTypeExponent);
		yAxisMenu.add(yLabelTypeSI);
		zAxisMenu.add(zLabelTypeFloat);
		zAxisMenu.add(zLabelTypeRound);
		zAxisMenu.add(zLabelTypeExponent);
		zAxisMenu.add(zLabelTypeSI);		
	}

	@Override
	public void processPlotUpdate(DataBean dbPlot, boolean isUpdate) {
		Collection<DataSetWithAxisInformation> plotData = dbPlot.getData();
		if (plotData != null) {
			Iterator<DataSetWithAxisInformation> iter = plotData.iterator();
			final List<IDataset> datasets = Collections.synchronizedList(new LinkedList<IDataset>());
	
			AbstractDataset xAxisValues = dbPlot.getAxis(AxisMapBean.XAXIS);
			AbstractDataset yAxisValues = dbPlot.getAxis(AxisMapBean.YAXIS);
			AbstractDataset zAxisValues = dbPlot.getAxis(AxisMapBean.ZAXIS);
			if (xAxisValues != null && yAxisValues != null && zAxisValues != null) {
				if (!isUpdate) {
					xAxis.clear();
					yAxis.clear();
					zAxis.clear();
					if (xAxisValues.getName() != null && xAxisValues.getName().length() > 0)
						mainPlotter.setXAxisLabel(xAxisValues.getName());
					else
						mainPlotter.setXAxisLabel("X-Axis");
	
					if (yAxisValues.getName() != null && yAxisValues.getName().length() > 0)
						mainPlotter.setYAxisLabel(yAxisValues.getName());
					else
						mainPlotter.setYAxisLabel("Y-Axis");

					if (zAxisValues.getName() != null && zAxisValues.getName().length() > 0)
						mainPlotter.setZAxisLabel(zAxisValues.getName());
					else
						mainPlotter.setZAxisLabel("Z-Axis");

				}
	
				xAxis.setValues(xAxisValues);
				mainPlotter.setXAxisValues(xAxis, 1);
				
				yAxis.setValues(yAxisValues);
				mainPlotter.setYAxisValues(yAxis);

				zAxis.setValues(zAxisValues);
				mainPlotter.setZAxisValues(zAxis);

				mainPlotter.setYTickLabelFormat(TickFormatting.roundAndChopMode);
				mainPlotter.setXTickLabelFormat(TickFormatting.roundAndChopMode);
				while (iter.hasNext()) {
					DataSetWithAxisInformation dataSetAxis = iter.next();
					AbstractDataset data = dataSetAxis.getData();
					datasets.add(data);
				}
				if (!isUpdate) {
					try {
						mainPlotter.replaceAllPlots(datasets);
					} catch (PlotException e) {
						e.printStackTrace();
					}
				} else {
					IDataset data = datasets.get(0);
					IDataset currentData = mainPlotter.getCurrentDataSet();
					final int length = currentData.getSize();
					final int addLength = data.getSize();
					for (int i = 0; i < addLength; i++) {
						currentData.set(data.getObject(i), i+length);
					}
					datasets.set(0, currentData);
					try {
						mainPlotter.replaceAllPlots(datasets);
					} catch (PlotException e) {
						e.printStackTrace();
					}					
				}
				
				final HistogramDataUpdate histoUpdate = new
				  HistogramDataUpdate(datasets.get(0));
				
				parent.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						mainPlotter.refresh(true);
						plotWindow.notifyHistogramChange(histoUpdate);
						plotWindow.notifyUpdateFinished();
					}
				});	
				
			}
/*			dataWindowView.setData(datasets.get(0),xAxis,yAxis);*/
		}

	}
	
	@Override
	public void deactivate(boolean leaveSidePlotOpen) {
		plotWindow.deleteIObserver(histogramView);
		page.hideView(histogramView);
	}	

}
