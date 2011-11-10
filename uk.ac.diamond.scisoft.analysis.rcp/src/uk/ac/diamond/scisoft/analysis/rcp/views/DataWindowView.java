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

package uk.ac.diamond.scisoft.analysis.rcp.views;

import gda.observable.IObserver;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CCombo;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSet3DPlot3D;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotException;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot.DataWindowOverlay;

/**
 *
 */
public class DataWindowView extends ViewPart implements IObserver, SelectionListener {

	private AxisValues xAxis;
	private AxisValues yAxis;
	private DataSetPlotter plotter;
	private DataWindowOverlay overlay;
	private String sId;
	private String id;
	private final static int MAXDISPLAYDIM = 1024;
	private Label lblStartX;
	private Label lblStartY;
	private Label lblXSampling;
	private Label lblYSampling;
	private Label lblEndX;
	private Label lblDelimiter;
	private Button btnOverwriteAspect;
	private Spinner spnStartX;
	
	private Spinner spnEndX;
	private Spinner spnStartY;
	private Spinner spnEndY;
	private Spinner spnXAspect;
	private Spinner spnYAspect;
	private Action allowUSampling;
	private CCombo ccXsampling;
	private CCombo ccYsampling;
	
		
	/**
	 * Default constructor of DataWindowView
	 */
	public DataWindowView() {
		super();
	}

	/**
	 * @return Returns the id.
	 */
	public String getId() {
		return id;
	}

	@Override
	public void init(IViewSite site) throws PartInitException {
		super.init(site);
		id = site.getId();
		sId = site.getSecondaryId();
		setPartName("SurfacePlotROI: " + sId);
	}
	
	private void buildActions(IToolBarManager manager) {
		allowUSampling = new Action("",IAction.AS_CHECK_BOX) {
			@Override
			public void run()
			{
				overlay.setAllowUndersampling(allowUSampling.isChecked());				
				ccXsampling.setEnabled(allowUSampling.isChecked());
				ccYsampling.setEnabled(allowUSampling.isChecked());
				if (ccXsampling.getSelectionIndex() == 0 && 
					ccXsampling.isEnabled()) {
					ccXsampling.select(1);
					overlay.setSamplingMode(1, ccYsampling.getSelectionIndex());
				}
				if (ccYsampling.getSelectionIndex() == 0 && 
						ccYsampling.isEnabled()) {
						ccYsampling.select(1);
						overlay.setSamplingMode(ccXsampling.getSelectionIndex(),1);
				}
			}
		};
		allowUSampling.setText("Allow undersampling");
		allowUSampling.setDescription("Allow select area to be undersampled");
		allowUSampling.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/arrow_in.png"));
		manager.add(allowUSampling);
	}
	
	@Override
	public void createPartControl(Composite parent) {
		buildActions(getViewSite().getActionBars().getToolBarManager());
		Composite topComposite = new Composite(parent,SWT.NONE);
		GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 1;
		gridLayout.horizontalSpacing = 0;
		gridLayout.verticalSpacing = 2;
		topComposite.setLayout(gridLayout);
		plotter = new DataSetPlotter(PlottingMode.ONED,topComposite,false);
		Composite plotComp = plotter.getComposite();
		GridData gridData = new GridData();
		gridData.grabExcessHorizontalSpace = true;
		gridData.grabExcessVerticalSpace = true;
		gridData.horizontalAlignment = GridData.FILL;
		gridData.verticalAlignment = GridData.FILL;
		plotComp.setLayoutData(gridData);
		Composite bottomComposite = new Composite(topComposite,SWT.NONE);
		bottomComposite.setLayout(new FormLayout());
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		gridData.heightHint = 125;
		bottomComposite.setLayoutData(gridData);
		{
			lblStartX = new Label(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.height = 18;
				formData.width = 69;
				formData.top = new FormAttachment(0, 25);
				formData.left = new FormAttachment(0, 10);
				lblStartX.setLayoutData(formData);
			}
			lblStartX.setText("Start X:");
		}
		{
			spnStartX = new Spinner(bottomComposite, SWT.BORDER);
			{
				FormData formData = new FormData();
				formData.width = 62;
				formData.top = new FormAttachment(lblStartX, 0, SWT.TOP);
				formData.left = new FormAttachment(lblStartX, 6);
				spnStartX.setLayoutData(formData);
				spnStartX.setMinimum(0);
				spnStartX.addSelectionListener(this);
			}
		}
		{
			lblStartY = new Label(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartX, 15);
				formData.left = new FormAttachment(lblStartX, 0, SWT.LEFT);
				lblStartY.setLayoutData(formData);
			}
			lblStartY.setText("Start Y:");
		}
		{
			spnStartY = new Spinner(bottomComposite, SWT.BORDER);
			{
				FormData formData = new FormData();
				formData.left = new FormAttachment(spnStartX, 0, SWT.LEFT);
				formData.right = new FormAttachment(spnStartX, 0, SWT.RIGHT);
				formData.top = new FormAttachment(spnStartX, 6);
				spnStartY.setLayoutData(formData);
				spnStartY.setMinimum(0);
				spnStartY.addSelectionListener(this);
			}
		}
		{
			lblEndX = new Label(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartX, 0, SWT.TOP);
				formData.left = new FormAttachment(spnStartX, 16);
				lblEndX.setLayoutData(formData);
			}
			lblEndX.setText("Width:");
		}
		{
			spnEndX = new Spinner(bottomComposite, SWT.BORDER);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartX, 0, SWT.TOP);
				formData.left = new FormAttachment(lblEndX, 16);
				formData.width = 69;
				spnEndX.setLayoutData(formData);
				spnEndX.setMinimum(0);
				spnEndX.addSelectionListener(this);
			}
		}
		{
			Label lblEndY = new Label(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartY, 0, SWT.TOP);
				formData.right = new FormAttachment(lblEndX, 0, SWT.RIGHT);
				lblEndY.setLayoutData(formData);
			}
			lblEndY.setText("Height:");
		}
		{
			spnEndY = new Spinner(bottomComposite, SWT.BORDER);
			{
				FormData formData = new FormData();
				formData.right = new FormAttachment(spnEndX, 0, SWT.RIGHT);
				formData.top = new FormAttachment(spnStartY, 0, SWT.TOP);
				formData.left = new FormAttachment(spnEndX, 0, SWT.LEFT);
				spnEndY.setLayoutData(formData);
				spnEndY.setMinimum(0);
				spnEndY.addSelectionListener(this);
			}
		}
		{
			lblXSampling = new Label(bottomComposite,SWT.NONE);
			FormData formData = new FormData();
			formData.top = new FormAttachment(lblStartX, 0, SWT.TOP);
			formData.left = new FormAttachment(spnEndX, 16);
			formData.width = 79;
			lblXSampling.setLayoutData(formData);
			lblXSampling.setText("Sampling:");
		}
		{
			lblYSampling = new Label(bottomComposite,SWT.NONE);
			FormData formData = new FormData();
			formData.top = new FormAttachment(lblStartY, 0, SWT.TOP);
			formData.left = new FormAttachment(spnEndY, 16);
			formData.width = 79;
			lblYSampling.setLayoutData(formData);
			lblYSampling.setText("Sampling:");
		}		
		{
			ccXsampling = new CCombo(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartX, 0, SWT.TOP);
				formData.left = new FormAttachment(lblXSampling, 16);
				formData.width = 120;
				ccXsampling.setLayoutData(formData);
			}
			ccXsampling.add("None");
			ccXsampling.add("Point");
			ccXsampling.add("Median");
			ccXsampling.add("Minimum");
			ccXsampling.add("Maximum");
		}
		{
			ccYsampling = new CCombo(bottomComposite, SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblStartY, 0, SWT.TOP);
				formData.left = new FormAttachment(lblYSampling, 16);
				formData.width = 120;
				ccYsampling.setLayoutData(formData);
			}
			ccYsampling.add("None");
			ccYsampling.add("Point");
			ccYsampling.add("Median");
			ccYsampling.add("Minimum");
			ccYsampling.add("Maximum");
		}	
		{
			btnOverwriteAspect = new Button(bottomComposite,SWT.CHECK);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblYSampling,40,SWT.TOP);
				formData.left = new FormAttachment(0, 20);
				formData.width = 175;
				btnOverwriteAspect.setLayoutData(formData);
			}
			btnOverwriteAspect.setText("Overwrite Aspect-Ratio");
			btnOverwriteAspect.addSelectionListener(this);
		}
		{
			spnXAspect = new Spinner(bottomComposite,SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblYSampling,40,SWT.TOP);
				formData.left = new FormAttachment(btnOverwriteAspect,16);
				spnXAspect.setLayoutData(formData);
			}
			spnXAspect.setEnabled(false);
			spnXAspect.setMinimum(1);
			spnXAspect.setMaximum(10);
			spnXAspect.setSelection(1);
			spnXAspect.setIncrement(1);
			spnXAspect.addSelectionListener(this);
		}
		{
			lblDelimiter = new Label(bottomComposite,SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblYSampling,40,SWT.TOP);
				formData.left = new FormAttachment(spnXAspect,5);
				lblDelimiter.setLayoutData(formData);
			}
			lblDelimiter.setText(":");
		}
		{
			spnYAspect = new Spinner(bottomComposite,SWT.NONE);
			{
				FormData formData = new FormData();
				formData.top = new FormAttachment(lblYSampling,40,SWT.TOP);
				formData.left = new FormAttachment(lblDelimiter,5);
				spnYAspect.setLayoutData(formData);
			}
			spnYAspect.setEnabled(false);
			spnYAspect.setMinimum(1);
			spnYAspect.setMaximum(10);
			spnYAspect.setSelection(1);
			spnYAspect.setIncrement(1);
			spnYAspect.addSelectionListener(this);
		}
		ccXsampling.select(0);
		ccYsampling.select(0);
		ccXsampling.setEnabled(false);
		ccYsampling.setEnabled(false);
		ccXsampling.addSelectionListener(this);
		ccYsampling.addSelectionListener(this);
		plotter.setMode(PlottingMode.TWOD);
		plotter.setAxisModes(AxisMode.CUSTOM, AxisMode.CUSTOM, AxisMode.LINEAR);
		overlay = new DataWindowOverlay(1,1,this);
		plotter.registerOverlay(overlay);
		xAxis = new AxisValues();
		yAxis = new AxisValues();
	}

	@Override
	public void setFocus() {
		// Nothing to do

	}

	/**
	 * @param newData
	 * @param inXAxis optional xAxis values
	 * @param inYAxis optional yAxis values
	 */
	public void setData(IDataset newData, AxisValues inXAxis, AxisValues inYAxis) {
		if (newData != null) {
			xAxis.clear();
			yAxis.clear();
			final int xSize = newData.getShape()[1];
			final int ySize = newData.getShape()[0];
			int xSamplingRate = Math.max(1, xSize / MAXDISPLAYDIM);
			int ySamplingRate = Math.max(1, ySize / MAXDISPLAYDIM);
			int[] startPos = new int[]{0,0};
			int[] endPos = new int[]{newData.getShape()[0],newData.getShape()[1]};
			int[] step = new int[]{ySamplingRate,xSamplingRate};
			final IDataset displayData = newData.getSlice(startPos, endPos, step);
			if (inXAxis == null || inXAxis.size() == 0)
			{
				xAxis.setValues(AbstractDataset.arange(0, xSize, xSamplingRate, AbstractDataset.INT32));
			} else {
				xAxis.setValues(inXAxis.subset(0, xSize, xSamplingRate).toDataset());
			}
			if (inYAxis == null || inYAxis.size() == 0)
			{
				yAxis.setValues(AbstractDataset.arange(0, ySize, ySamplingRate, AbstractDataset.INT32));
			} else {
				yAxis.setValues(inYAxis.subset(0, ySize, ySamplingRate).toDataset());
			}
			overlay.removePrimitives();
			overlay.setScaling(xSamplingRate,ySamplingRate);
			plotter.setXAxisValues(xAxis, 1);
			plotter.setYAxisValues(yAxis);
			try {
				plotter.replaceCurrentPlot(displayData);
			} catch (PlotException ex) {}
			plotter.getComposite().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					spnEndX.setMaximum(xSize);
					spnEndY.setMaximum(ySize);
					spnStartX.setMaximum(xSize);
					spnStartY.setMaximum(ySize);
					if (xSize * ySize > DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM)
					{
						float reduceFactor = (float)(DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) / 
											 (float)(xSize * ySize);
						float xAspect = (float)xSize / (float)(xSize+ySize);
						float yAspect = (float)ySize / (float)(xSize+ySize);
						float xReduce = 1.0f - (1.0f - reduceFactor) * xAspect;
						float yReduce = 1.0f - (1.0f - reduceFactor) * yAspect;
						int currentXdim = (int)(xSize * xReduce * 0.75f);
						int currentYdim = (int)(ySize * yReduce * 0.75f);					
						spnEndX.setSelection(currentXdim);
						spnEndY.setSelection(currentYdim);
						overlay.setSelectPosition(0,0,currentXdim,currentYdim);
					} else {
						spnEndX.setSelection(xSize);
						spnEndY.setSelection(ySize);						
					}
					plotter.refresh(false);
					spnStartX.setSelection(0);
					spnStartY.setSelection(0);
				}
			});
		}
	}

	/**
	 * Set the spinner values
	 * @param startX start position in x dimension
	 * @param startY start position in y dimension
	 * @param width
	 * @param height
	 */
	public void setSpinnerValues(final int startX, 
								 final int startY, 
								 final int width, 
								 final int height) {
		plotter.getComposite().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				spnStartX.setSelection(startX);
				spnStartY.setSelection(startY);
				spnEndX.setSelection(width);
				spnEndY.setSelection(height);
			}
		});	
	}
	
	@Override
	public void update(Object theObserved, Object changeCode) {
		HistogramUpdate update = (HistogramUpdate) changeCode;
		plotter
				.applyColourCast(update.getRedMapFunction(), update
						.getGreenMapFunction(),
						update.getBlueMapFunction(), update
								.getAlphaMapFunction(),
						update.inverseRed(), update.inverseGreen(), update
								.inverseBlue(), update.inverseAlpha(),
						update.getMinValue(), update.getMaxValue());
		plotter.refresh(false);		
	}

	/**
	 * Add an Observer 
	 * @param anIObserver the new observer
	 */
	public void addIObserver(IObserver anIObserver) {
		if (overlay != null)
			overlay.addIObserver(anIObserver);
	}


	/**
	 * Delete an observer
	 * @param anIObserver the observer that should be removed
	 */
	public void deleteIObserver(IObserver anIObserver) {
		if (overlay != null)
			overlay.deleteIObserver(anIObserver);
	}


	/**
	 * Delete all observers
	 */
	public void deleteIObservers() {
		if (overlay != null)
			overlay.deleteIObservers();
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {
		// Nothing to do
		
	}

	@Override
	public void widgetSelected(SelectionEvent e) {
		if (overlay != null) {
			if (!e.getSource().equals(ccXsampling) &&
				!e.getSource().equals(ccYsampling) &&
				!e.getSource().equals(btnOverwriteAspect))  {
				int startPosX = spnStartX.getSelection();
				int startPosY = spnStartY.getSelection();
				int width = spnEndX.getSelection();
				int height = spnEndY.getSelection();
				if (startPosX + width > spnEndX.getMaximum()) {
					width = spnEndX.getMaximum() - startPosX;
				}
				if (startPosY + height > spnEndY.getMaximum()) {
					height = spnEndY.getMaximum() - startPosY;
				}
				overlay.setAspects(btnOverwriteAspect.getSelection() ? spnXAspect.getSelection() : 0, 
				           		   btnOverwriteAspect.getSelection() ? spnYAspect.getSelection() : 0);

				overlay.setSelectPosition(startPosX, 
										  startPosY,
										  width,
										  height);
			}
			else if (e.getSource().equals(ccXsampling) ||
					 e.getSource().equals(ccYsampling)) {
				overlay.setSamplingMode(ccXsampling.getSelectionIndex(),
										ccYsampling.getSelectionIndex());
			} else if (e.getSource().equals(btnOverwriteAspect)) {
				spnXAspect.setEnabled(btnOverwriteAspect.getSelection());
				spnYAspect.setEnabled(btnOverwriteAspect.getSelection());
				if (btnOverwriteAspect.getSelection()) 
					overlay.setAspects(spnXAspect.getSelection(),
									   spnYAspect.getSelection());		
				else
					overlay.setAspects(0,0);							
			}
		}
	}
	
	@Override
	public void dispose()
	{
		if (!spnStartX.isDisposed())
			spnStartX.removeSelectionListener(this);
		if (!spnStartY.isDisposed())
			spnStartY.removeSelectionListener(this);
		if (!spnEndX.isDisposed())
			spnEndX.removeSelectionListener(this);
		if (!spnEndY.isDisposed())
			spnEndY.removeSelectionListener(this);
		if (!ccXsampling.isDisposed())
			ccXsampling.removeSelectionListener(this);
		if (!ccYsampling.isDisposed())
			ccYsampling.removeSelectionListener(this);
		
		deleteIObservers();
		if (plotter != null)
			plotter.cleanUp();
	}
}
 
