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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;


import java.awt.Color;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.diffraction.Resolution;
import uk.ac.diamond.scisoft.analysis.fitting.Generic1DFitter;
import uk.ac.diamond.scisoft.analysis.fitting.functions.APeak;
import uk.ac.diamond.scisoft.analysis.fitting.functions.CompositeFunction;
import uk.ac.diamond.scisoft.analysis.fitting.functions.Gaussian;
import uk.ac.diamond.scisoft.analysis.fitting.functions.IdentifiedPeak;
import uk.ac.diamond.scisoft.analysis.fitting.functions.Lorentzian;
import uk.ac.diamond.scisoft.analysis.fitting.functions.PearsonVII;
import uk.ac.diamond.scisoft.analysis.fitting.functions.PseudoVoigt;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DAppearance;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotColorUtility;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotException;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.OverlayType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.Plot1DStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.PrimitiveType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay1DConsumer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay1DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEvent;
import uk.ac.diamond.scisoft.analysis.roi.LinearROI;
import uk.ac.diamond.scisoft.analysis.roi.ROIProfile;

public class DiffractionViewerSpotFit extends Composite implements Overlay1DConsumer {

	private static final Logger logger = LoggerFactory.getLogger(DiffractionViewerSpotFit.class);

	private static final int DECIMAL_PLACES = 3;

	public DataSetPlotter lpPlotter;
	private DiffractionViewer diffView;

	private Overlay1DProvider provider = null;
	// private Overlay1DProvider gaussianCurves = null;

	private AxisValues axis;

	ArrayList<Integer> peakLines = new ArrayList<Integer>();
	// private int[] peakLines;
	private double[] returnPeaks;

	Plot1DGraphTable colourTable;
	Plot1DAppearance plotAppearace;
	int width, step, length;

	private double dataMaxval;

	private Text maxD;
	private Text minD;
	private Text sigma;
	private Text averageDSpacing;

	private APeak peak;
	private int maxNumPeaks;

	private boolean autoStopping;

	private int stoppingThreashold;

	private org.eclipse.swt.widgets.List rawTable;

	private DecimalFormat decimalFormat;

	public DiffractionViewerSpotFit(Composite parent, int style, DiffractionViewer diffViews) {
		super(parent, style);
		this.diffView = diffViews;
		setLayout(new FillLayout(SWT.VERTICAL));

		// GUI creation and layout
		lpPlotter = new DataSetPlotter(PlottingMode.ONED, this, false);
		// Composite plotComp = lpPlotter.getComposite();
		lpPlotter.setAxisModes(AxisMode.CUSTOM, AxisMode.LINEAR, AxisMode.LINEAR);
		lpPlotter.setXAxisLabel("Distance along line");
		lpPlotter.registerOverlay(this);
		// various this needed for plotting

		axis = new AxisValues();

		colourTable = lpPlotter.getColourTable();

		// Results of d space calculation
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		{
			TabItem summarydSpacing = new TabItem(tabFolder, SWT.NONE);
			summarydSpacing.setText("Summary");
			{
				Composite peakFittingResults = new Composite(tabFolder, SWT.NONE);
				GridLayout gridLayout = new GridLayout(6, false);
				peakFittingResults.setLayout(gridLayout);
				summarydSpacing.setControl(peakFittingResults);
				{
					Label lblAverageDSpacing = new Label(peakFittingResults, SWT.NONE);
					lblAverageDSpacing.setText("Average d spacing");
				}
				{
					averageDSpacing = new Text(peakFittingResults, SWT.READ_ONLY);
					averageDSpacing.setBackground(peakFittingResults.getBackground());
				}
				new Label(peakFittingResults, SWT.NONE).setText("\u00C5");
				{
					Label lblSigma = new Label(peakFittingResults, SWT.NONE);
					lblSigma.setText("Standatd deviation");
				}
				{
					sigma = new Text(peakFittingResults, SWT.READ_ONLY);
					sigma.setBackground(peakFittingResults.getBackground());
				}
				new Label(peakFittingResults, SWT.NONE).setText("\u00C5");
				{
					Label lblmaxD = new Label(peakFittingResults, SWT.NONE);
					lblmaxD.setText("Maximum d spacing");
				}
				{
					maxD = new Text(peakFittingResults, SWT.READ_ONLY);
					maxD.setBackground(peakFittingResults.getBackground());
				}
				new Label(peakFittingResults, SWT.NONE).setText("\u00C5");
				{
					Label lblMaxD = new Label(peakFittingResults, SWT.NONE);
					lblMaxD.setText("Minimum d spacing");
				}
				{
					minD = new Text(peakFittingResults, SWT.READ_ONLY);
					minD.setBackground(peakFittingResults.getBackground());
				}
				new Label(peakFittingResults, SWT.NONE).setText("\u00C5");
				
			}
			TabItem rawDSpacing = new TabItem(tabFolder, SWT.NONE);
			rawDSpacing.setText("Raw Data");
			{
				Composite rawComposite = new Composite(tabFolder, SWT.NONE);
				rawDSpacing.setControl(rawComposite);
				rawComposite.setLayout(new FillLayout(SWT.HORIZONTAL));
				rawTable = new org.eclipse.swt.widgets.List(rawComposite, SWT.V_SCROLL);
			}
		}
		decimalFormat = new DecimalFormat();
		decimalFormat.setMaximumFractionDigits(DECIMAL_PLACES);
	}

	@Override
	public void registerProvider(OverlayProvider overlay) {
		provider = (Overlay1DProvider) overlay;
	}

	@Override
	public void unregisterProvider() {
		provider = null;

	}

	@Override
	public void dispose() {
		if (lpPlotter != null)
			lpPlotter.cleanUp();
	}

	@Override
	public void areaSelected(AreaSelectEvent event) {
		// clicking on plot not implemented

	}

	public void updatePlot() {
		lpPlotter.updateAllAppearance();
		lpPlotter.refresh(false);
	}

	@Override
	public void removePrimitives() {

		if (provider == null)
			return;
		if (peakLines == null || peakLines.size() < 1)
			return;
		for (int i = peakLines.size() - 1; i >= 0; i--) {
			provider.setPrimitiveVisible(peakLines.get(i), false);
			peakLines.remove(i);
		}

	}

	/**
	 * Takes an the data from the plot view and the ROI and controls the plotting, peak fitting and d space calculations
	 * 
	 * @param roi
	 * @param data
	 */
	public void processROI(IDataset data, LinearROI roi) {
		if (roi.getLength() <= 1 || data.getSize() < 1)
			return;
		AbstractDataset[] dataSets = ROIProfile.line(data, roi, DiffractionViewer.lineStep);
		// DataSet[] dataSets = ROIProfile.line(AbstractDataset.toDataSet(data), roi, DiffractionViewer.lineStep);
		dataMaxval = dataSets[0].max().doubleValue();
		plotDataSets(dataSets);

		List<IdentifiedPeak> fitterCurves = fitPeaks(dataSets[0]);
		Collections.sort(fitterCurves, new Compare());
		drawPeakLines(peakPosition(fitterCurves), dataMaxval);
		//plotFittedCurves(fitterCurves, dataSets[0]);
		dSpacingBetweenPeaks(fitterCurves, roi);
	}

	private static class Compare implements Comparator<IdentifiedPeak> {
		@Override
		public int compare(IdentifiedPeak arg0, IdentifiedPeak arg1) {
			if (arg0.getPos() > arg1.getPos())
				return 1;
			if (arg0.getPos() < arg1.getPos())
				return -1;
			return 0;
		}

	}

	/**
	 * @param peakPositionsForPlotting
	 *            x positions of the peaks
	 * @param datamax
	 *            max value of the lroi
	 */
	public void drawPeakLines(double[] peakPositionsForPlotting, double datamax) {
		if (provider == null || peakPositionsForPlotting.length == 0) {
			return;
		}
		clearOverlays();
		provider.begin(OverlayType.VECTOR2D);
		returnPeaks = new double[4];
		for (int i = 0; i < peakPositionsForPlotting.length; i++) {
			returnPeaks[0] = peakPositionsForPlotting[i] * DiffractionViewer.lineStep;
			returnPeaks[1] = 0;
			returnPeaks[2] = peakPositionsForPlotting[i] * DiffractionViewer.lineStep;
			returnPeaks[3] = datamax;
			int primID = provider.registerPrimitive(PrimitiveType.LINE);
			provider.setColour(primID, java.awt.Color.BLUE);
			provider.drawLine(primID, returnPeaks[0], returnPeaks[1], returnPeaks[2], returnPeaks[3]);
			peakLines.add(primID);
		}
		provider.end(OverlayType.VECTOR2D);
	}

	private void clearOverlays() {
		provider.begin(OverlayType.VECTOR2D);
		for (Integer i : peakLines) {
			provider.unregisterPrimitive(i);
		}
		provider.end(OverlayType.VECTOR2D);
	}

	private void plotDataSets(IDataset[] dataSets) {
		DoubleDataset axis = DoubleDataset.arange(dataSets[0].getSize());
		axis.imultiply(DiffractionViewer.lineStep);
		this.axis.setValues(axis);

		colourTable.clearLegend();
		plotAppearace = new Plot1DAppearance(PlotColorUtility.getDefaultColour(0), Plot1DStyles.SOLID, "Line 1");

		colourTable.addEntryOnLegend(plotAppearace);

		List<IDataset> plots = new ArrayList<IDataset>();
		List<AxisValues> plotAxis = new ArrayList<AxisValues>();

		plots.add(dataSets[0]);
		plotAxis.add(this.axis);

		Color colour = PlotColorUtility.getDefaultColour(1);
		plotAppearace = new Plot1DAppearance(colour, Plot1DStyles.SOLID, "Line 2");
		colourTable.addEntryOnLegend(plotAppearace);

		try {
			lpPlotter.replaceAllPlots(plots, plotAxis);
			updatePlot();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<IdentifiedPeak> fitPeaks(AbstractDataset currentDataSet) {
		if (currentDataSet == null || currentDataSet.getSize() < 1)
			return null;
		length = currentDataSet.getSize();
		return Generic1DFitter.findPeaks(AbstractDataset.arange(length, AbstractDataset.INT), currentDataSet, (int) (length *0.1));

	}

	private double[] peakPosition(List<IdentifiedPeak> fitterCurves) {
		double[] peaksXValues = new double[fitterCurves.size()];
		for (int i = 0; i < peaksXValues.length; i++) {
			peaksXValues[i] = fitterCurves.get(i).getPos();
		}
		Arrays.sort(peaksXValues);
		return peaksXValues;
	}

	
	@SuppressWarnings("unused")
	/**
	 * Since no longer peak fitting then this method is non longer required
	 */
	private void plotFittedCurves(List<APeak> fitterCurves, AbstractDataset dataSets) {
		ArrayList<AbstractDataset> plottingData = new ArrayList<AbstractDataset>();
		CompositeFunction compFunc = new CompositeFunction();
		if (!fitterCurves.isEmpty()) {
			for (APeak fp : fitterCurves) {
				compFunc.addFunction(fp);
			}
			plottingData.add(dataSets);
			plottingData.add(compFunc.makeDataset(DoubleDataset.arange(dataSets.getSize())));

			try {
				lpPlotter.replaceAllPlots(plottingData);
			} catch (PlotException e) {
				e.printStackTrace();
			}
			updatePlot();
		}
	}

	private void dSpacingBetweenPeaks(List<IdentifiedPeak> list, LinearROI roi) {
		if (list == null || list.size() < 1) {
			logger.warn("No peaks found");
			return;
		}

		int[] peakPixVal = new int[list.size() * 2];
		double[] dSpacing = new double[list.size() - 1];

		for (int i = 0; i < list.size(); i++) {
			int[] tempPeakPxLoc = roi.getIntPoint(list.get(i).getPos()
					* DiffractionViewer.lineStep / roi.getLength());
			peakPixVal[i * 2] = tempPeakPxLoc[0];
			peakPixVal[i * 2 + 1] = tempPeakPxLoc[1];
		}
		try {
			dSpacing = Resolution.peakDistances(peakPixVal, diffView.detConfig, diffView.diffEnv);
		} catch (IllegalArgumentException e) {
			logger.warn("pixel values were found to be identical");
		} catch (Exception e) {
			logger.error("Could not calculate d spacing between these peaks");
		}

		// calculate some stats that might or might not be useful
		double mean = 0;
		double StandardDev = 0;
		double minimumD = Double.MAX_VALUE;
		double maximumD = -Double.MAX_VALUE;

		for (double d : dSpacing) {
			mean += d;
			if (d > maximumD)
				maximumD = d;
			if (d < minimumD)
				minimumD = d;

		}
		mean = mean / dSpacing.length;
		for (double d : dSpacing) {
			StandardDev += (d - mean) * (d - mean);
		}

		final double tmpSigma = Math.sqrt((StandardDev / (dSpacing.length - 1)));
		final double tempmean = mean;
		final double tempMin = minimumD;
		final double tempMax = maximumD;
		addDSpacingToList(dSpacing);
		
		getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				averageDSpacing.setText(decimalFormat.format(tempmean));
				sigma.setText(decimalFormat.format(tmpSigma));
				minD.setText(decimalFormat.format(tempMin));
				maxD.setText(decimalFormat.format(tempMax));
			}
		});
	}

	private void addDSpacingToList(final double[] dSpacing) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				rawTable.removeAll();
				int i = 1;
				for (double d : dSpacing) {
					rawTable.add("Peak " + (i++) + " and peak " + i + " has distance of " + d + " \u00C5");
				}

			}
		});

	}

	public void pushPreferences(String peakName, int numPeaks, boolean stopping, int threashold) {

		autoStopping = stopping;
		if (autoStopping)
			maxNumPeaks = -1;
		else
			maxNumPeaks = numPeaks;
		stoppingThreashold = threashold;

		if (peakName.compareToIgnoreCase("Gaussian") == 0) {
			peak = new Gaussian(1, 1, 1, 1);
		} else if (peakName.compareToIgnoreCase("Lorentzian") == 0) {
			peak = new Lorentzian(1, 1, 1, 1);
		} else if (peakName.compareToIgnoreCase("Pearson VII") == 0) {
			peak = new PearsonVII(1, 1, 1, 1);
		} else if (peakName.compareToIgnoreCase("PseudoVoigt") == 0) {
			peak = new PseudoVoigt(1, 1, 1, 1);
		} else {
			peak = new Gaussian(1, 1, 1, 1);
		}
	}
}
