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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.IPlotUI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DAppearance;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotColorUtility;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotException;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.OverlayType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.Plot1DStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.PrimitiveType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.VectorOverlayStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.LinearROIData;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.LinearROIHandler;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.LinearROITableViewer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.ROIData;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.ROIDataList;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.IImagePositionEvent;
import uk.ac.diamond.scisoft.analysis.rcp.util.FloatSpinner;
import uk.ac.diamond.scisoft.analysis.roi.LinearROI;
import uk.ac.diamond.scisoft.analysis.roi.LinearROIList;
import uk.ac.diamond.scisoft.analysis.roi.ROIBase;

/**
 * Composite to show line profiles of main plotter
 */
public class LineProfile extends SidePlotProfile {
	private static Logger logger = LoggerFactory.getLogger(LineProfile.class);

	private SidePlotter1D lpPlotter;

	private static final double lineStep = 0.5;

	/**
	 * possible handle states
	 */
	private enum HandleStatus {
		/**
		 * Specifies the handle does nothing
		 */
		NONE,
		/**
		 * Specifies the handle is for moving
		 */
		MOVE,
		/**
		 * Specifies the handle is for resizing
		 */
		RESIZE,
		/**
		 * Specifies the handle is for re-orienting (i.e. move end but preserve length)
		 */
		REORIENT,
		/**
		 * Specifies the handle is for spinning
		 */
		ROTATE
	}

	private HandleStatus hStatus = HandleStatus.NONE;

	private Spinner spsx, spsy;
	private FloatSpinner spex, spey, splen, spang;
	private Text txSum;

	public LineProfile() {
		super();
		roiClass = LinearROI.class;
		roiListClass = LinearROIList.class;
	}

	/**
	 * @param parent
	 */
	@Override
	public void createPartControl(Composite parent) {
		container = new Composite(parent, SWT.NONE);
		container.setLayout(new FillLayout());

		final SashForm ss = new SashForm(container, SWT.VERTICAL);

		lpPlotter = new SidePlotter1D(ss, "Line profile");
		lpPlotter.setAxisModes(AxisMode.CUSTOM, AxisMode.LINEAR, AxisMode.LINEAR);
		lpPlotter.setXAxisLabel("Distance along line");

		// GUI creation and layout
		final ScrolledComposite scomp = new ScrolledComposite(ss, SWT.VERTICAL | SWT.HORIZONTAL);

		final Composite controls = new Composite(scomp, SWT.NONE);
		controls.setLayout(new FillLayout(SWT.VERTICAL));
		{
			final Group groupCurrent = new Group(controls, SWT.NONE);
			groupCurrent.setLayout(new GridLayout(6, false));
			groupCurrent.setText("Current ROI");
			{
				// 1st row
				new Label(groupCurrent, SWT.NONE).setText("Start x:");
				spsx = new Spinner(groupCurrent, SWT.BORDER);
				spsx.setMinimum(-10000);
				spsx.setMaximum(10000);
				spsx.setIncrement(1);
				spsx.setPageIncrement(5);
				spsx.addSelectionListener(startPosListener);

				new Label(groupCurrent, SWT.NONE).setText("End x:");
				spex = new FloatSpinner(groupCurrent, SWT.BORDER, 6, 2);
				spex.addSelectionListener(endPosListener);

				new Label(groupCurrent, SWT.NONE).setText("Length:");
				splen = new FloatSpinner(groupCurrent, SWT.BORDER, 7, 2);
				splen.addSelectionListener(lenAngListener);

				// 2nd row
				new Label(groupCurrent, SWT.NONE).setText("Start y:");
				spsy = new Spinner(groupCurrent, SWT.BORDER);
				spsy.setMinimum(-10000);
				spsy.setMaximum(10000);
				spsy.setIncrement(1);
				spsy.setPageIncrement(5);
				spsy.addSelectionListener(startPosListener);

				new Label(groupCurrent, SWT.NONE).setText("End y:");
				spey = new FloatSpinner(groupCurrent, SWT.BORDER, 6, 2);
				spey.addSelectionListener(endPosListener);

				new Label(groupCurrent, SWT.NONE).setText("Angle:");
				spang = new FloatSpinner(groupCurrent, SWT.BORDER, 5, 2);
				spang.setMinimum(0.0);
				spang.setMaximum(360.0);
				spang.addSelectionListener(lenAngListener);

				// 3rd row
				new Label(groupCurrent, SWT.NONE).setText("Sum:");
				txSum = new Text(groupCurrent, SWT.READ_ONLY | SWT.BORDER);
				txSum.setTextLimit(12);

				new Label(groupCurrent, SWT.NONE).setText("");
				new Label(groupCurrent, SWT.NONE).setText("");
				new Label(groupCurrent, SWT.NONE).setText("");
				new Label(groupCurrent, SWT.NONE).setText("");

				// 4th row
				GridData gda = new GridData();
				gda.horizontalSpan = 2;
				final Button invert = new Button(groupCurrent, SWT.CHECK);
				invert.setLayoutData(gda);
				invert.setText("Invert brightness");
				invert.setToolTipText("Invert overlay brightness");
				invert.addSelectionListener(brightnessButtonListener);

				GridData gdb = new GridData();
				gdb.horizontalSpan = 2;
				final Button cross = new Button(groupCurrent, SWT.CHECK);
				cross.setLayoutData(gdb);
				cross.setText("Cross hair");
				cross.setToolTipText("Add line 90 degrees to current line");
				cross.addSelectionListener(crossButtonListener);

				new Label(groupCurrent, SWT.NONE).setText("");
				new Label(groupCurrent, SWT.NONE).setText("");

				// 5th row
				GridData gdc = new GridData();
				gdc.horizontalSpan = 2;
				Button copyToTable = new Button(groupCurrent, SWT.PUSH);
				copyToTable.setLayoutData(gdc);
				copyToTable.setText("Copy current to table");
				copyToTable.addSelectionListener(copyButtonListener);

				GridData gde = new GridData();
				gde.horizontalSpan = 2;
				Button deleteCurrent = new Button(groupCurrent, SWT.PUSH);
				deleteCurrent.setLayoutData(gde);
				deleteCurrent.setText("Delete current");
				deleteCurrent.addSelectionListener(deleteButtonListener);
			}
		}

		final Composite table = new Composite(ss, SWT.NONE);
		ss.setWeights(new int[] {50, 30, 20});

		table.setLayout(new FillLayout());

		tViewer = new LinearROITableViewer(table, this, this);

		scomp.setContent(controls);
		controls.setSize(controls.computeSize(SWT.DEFAULT, SWT.DEFAULT));
		// end of GUI creation

		lpPlotter.refresh(false);

		// initialize ROI and data
		updateAllSpinnersInt((LinearROI) roi);
		roiIDs = new ArrayList<Integer>();
		dragIDs = new ArrayList<Integer>();

		// initialize ROIs
		if (roiDataList == null)
			roiDataList = new ROIDataList();
		roisIDs = new ArrayList<Integer>();

		tViewer.setInput(roiDataList);

		// handle areas
		roiHandler = new LinearROIHandler((LinearROI) roi);

		// default colour: cyan
		dColour = new Color(0, 255, 255);

		// invert overlay colour
		float[] hsb;
		hsb = Color.RGBtoHSB(dColour.getRed(), dColour.getGreen(), dColour.getBlue(), null);
		cColour = Color.getHSBColor(hsb[0], hsb[1], (float) (1.0 - 0.7 * hsb[2]));
		oColour = dColour;
	}

	@Override
	public void dispose() {
		super.dispose();
		if (lpPlotter != null) lpPlotter.cleanUp();
	}

	@Override
	protected void updatePlot(ROIBase roib) {
		final LinearROI lroi = (LinearROI) roib;

		updateDataList();

		if (data == null) {
			logger.warn("No data");
			return;
		}

		if (lroi != null) {
			roiData = new LinearROIData(lroi, data, lineStep);

			if (!roiData.isPlot())
				return;

			getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					txSum.setText(String.format("%.2f", roiData.getProfileSum()));
				}
			});
		}

		Plot1DGraphTable colourTable;
		Plot1DAppearance newApp;
		int p, l;
		int nHistory;
		List<IDataset> plots = new ArrayList<IDataset>();
		List<AxisValues> paxes = new ArrayList<AxisValues>();

		l = p = 0;
		colourTable = lpPlotter.getColourTable();
		nHistory = lpPlotter.getNumHistory();

		if (lroi != null) {
			if (l + nHistory >= colourTable.getLegendSize()) {
				newApp = new Plot1DAppearance(PlotColorUtility.getDefaultColour(p), Plot1DStyles.SOLID, "Line 1");
				colourTable.addEntryOnLegend(l, newApp);
			} else {
				newApp = colourTable.getLegendEntry(l);
				newApp.setColour(PlotColorUtility.getDefaultColour(p));
				newApp.setStyle(Plot1DStyles.SOLID);
				newApp.setName("Line 1");
			}

			plots.add(roiData.getProfileData(0));
			paxes.add(roiData.getXAxis(0));

			if (lroi.isCrossHair()) {
				l++;
				if (l + nHistory >= colourTable.getLegendSize()) {
					newApp = new Plot1DAppearance(PlotColorUtility.getDefaultColour(p), Plot1DStyles.DASHED,
							"Cross Line 1");
					colourTable.addEntryOnLegend(l, newApp);
				} else {
					newApp = colourTable.getLegendEntry(l);
					newApp.setColour(PlotColorUtility.getDefaultColour(p));
					newApp.setStyle(Plot1DStyles.DASHED);
					newApp.setName("Cross Line 1");
				}

				plots.add(roiData.getProfileData(1));
				paxes.add(roiData.getXAxis(1));
			}
			l++;
			p++;
		}

		for (int i = 0, imax = roiDataList.size(); i < imax; i++) {
			LinearROIData rd = (LinearROIData) roiDataList.get(i);
			Color colour = PlotColorUtility.getDefaultColour(i+1);
			RGB rgb = new RGB(colour.getRed(), colour.getGreen(), colour.getBlue());
			rd.setPlotColourRGB(rgb);

			if (rd.isPlot()) {
				plots.add(rd.getProfileData(0));
				paxes.add(rd.getXAxis(0));
				if (l + nHistory >= colourTable.getLegendSize()) {
					newApp = new Plot1DAppearance(colour, Plot1DStyles.SOLID, "Line " + (p + 1));
					colourTable.addEntryOnLegend(l, newApp);
				} else {
					newApp = colourTable.getLegendEntry(l);
					newApp.setColour(colour);
					newApp.setStyle(Plot1DStyles.SOLID);
					newApp.setName("Line " + (p + 1));
				}

				if (rd.getROI().isCrossHair()) {
					l++;
					plots.add(rd.getProfileData(1));
					paxes.add(rd.getXAxis(1));
					if (l + nHistory  >= colourTable.getLegendSize()) {
						newApp = new Plot1DAppearance(colour, Plot1DStyles.DASHED, "Cross Line " + (p + 1));
						colourTable.addEntryOnLegend(l, newApp);
					} else {
						newApp = colourTable.getLegendEntry(l);
						newApp.setColour(colour);
						newApp.setStyle(Plot1DStyles.DASHED);
						newApp.setName("Cross Line " + (p + 1));
					}
				}
				l++;
				p++;
			}
		}

		while (nHistory-- > 0) { // tidy up history colours
			newApp = colourTable.getLegendEntry(l++);
			newApp.setColour(PlotColorUtility.getDefaultColour(p++));
		}

		try {
			lpPlotter.replaceAllPlots(plots, paxes);
		} catch (PlotException e) {
			e.printStackTrace();
		}

		lpPlotter.updateAllAppearance();
		lpPlotter.refresh(false);
	}

	/**
	 * Draw dragged out overlay for given region of interest
	 * @param roib
	 */
	private void drawDraggedOverlay(ROIBase roib) {
		if (oProvider == null)
			return;

		final LinearROI lroi = (LinearROI) roib;
		double[] spt = lroi.getPoint();
		double[] ept = lroi.getEndPoint();

		if (dragIDs.isEmpty()) {
			dragIDs.add(-1);
			dragIDs.add(-1);
		}

		int id, index;
		index = 0;

		// box
		id = dragIDs.get(index);
		if (id == -1) {
			id = oProvider.registerPrimitive(PrimitiveType.LINE);
			dragIDs.set(index, id);
			if (id == -1)
				return;
		} else
			oProvider.setPrimitiveVisible(id, true);
		index++;

		oProvider.begin(OverlayType.VECTOR2D);

		oProvider.drawLine(id, spt[0], spt[1], ept[0], ept[1]);
		oProvider.setColour(id, oColour);
		oProvider.setTransparency(id, oTransparency);

		// bisector
		id = dragIDs.get(index);
		if (id == -1) {
			id = oProvider.registerPrimitive(PrimitiveType.LINE);
			dragIDs.set(index, id);
			if (id == -1)
				return;
		}
		index++;

		if (lroi.isCrossHair()) {
			spt = lroi.getPerpendicularBisectorPoint(0.0);
			ept = lroi.getPerpendicularBisectorPoint(1.0);

			oProvider.setPrimitiveVisible(id, true);
			oProvider.drawLine(id, spt[0], spt[1], ept[0], ept[1]);
			oProvider.setColour(id, oColour);
			oProvider.setTransparency(id, oTransparency);
		} else {
			oProvider.setPrimitiveVisible(id, false);
		}

		oProvider.end(OverlayType.VECTOR2D);
	}

	@Override
	protected void drawCurrentOverlay() {
		if (oProvider == null || roi == null)
			return;

		if (roiIDs.isEmpty()) {
			roiIDs.add(-1); // arrow
			roiIDs.add(-1); // bisector
		}

		int id, index;
		index = 0;

		final LinearROI lroi = (LinearROI) roi;
		double[] spt = roi.getPoint();
		double[] ept = lroi.getEndPoint();
		oProvider.begin(OverlayType.VECTOR2D);

		// arrow
		id = roiIDs.get(index);
		if (id == -1) {
			id = oProvider.registerPrimitive(PrimitiveType.ARROW);
			roiIDs.set(index, id);
			if (id == -1)
				return;
		} else
			oProvider.setPrimitiveVisible(id, true);
		index++;

		oProvider.drawArrow(id, spt[0], spt[1], ept[0], ept[1], 2./3);
		oProvider.setColour(id, oColour);
		oProvider.setTransparency(id, oTransparency);

		// bisector
		id = roiIDs.get(index);
		if (id == -1) {
			id = oProvider.registerPrimitive(PrimitiveType.LINE);
			roiIDs.set(index, id);
			if (id == -1)
				return;
		}
		index++;

		if (lroi.isCrossHair()) {
			double[] mpt = lroi.getPerpendicularBisectorPoint(0.0);
			double[] bpt = lroi.getPerpendicularBisectorPoint(1.0);

			oProvider.setPrimitiveVisible(id, true);
			oProvider.drawLine(id, mpt[0], mpt[1], bpt[0], bpt[1]);
			oProvider.setColour(id, oColour);
			oProvider.setTransparency(id, oTransparency);
		} else {
			oProvider.setPrimitiveVisible(id, false);
		}

		// image size dependent handle size
		getDataset();
		int hsize = calcHandleSize(data.getShape());

		// handle areas
		for (int h = 0, hmax = roiHandler.size(); h < hmax; h++) {
			int hid = roiHandler.get(h);
			if (hid == -1) {
				hid = oProvider.registerPrimitive(PrimitiveType.BOX, true);
				roiHandler.set(h, hid);
			} else
				oProvider.setPrimitiveVisible(hid, true);

			int[] pt = roiHandler.getHandlePoint(h, hsize);
			if (pt == null)
				continue;
			oProvider.drawBox(hid, pt[0], pt[1], pt[0] + hsize, pt[1] + hsize);
			pt = roiHandler.getAnchorPoint(h, hsize);
			oProvider.setAnchorPoints(hid, pt[0], pt[1]);
			oProvider.setStyle(hid, VectorOverlayStyles.FILLED_WITH_OUTLINE);
			oProvider.setColour(hid, oColour);
			oProvider.setOutlineColour(hid, oColour);
			oProvider.setLineThickness(hid, oThickness);
			oProvider.setTransparency(hid, 0.9);
			oProvider.setOutlineTransparency(hid, oTransparency);
		}

		oProvider.end(OverlayType.VECTOR2D);
	}

	@Override
	protected void drawOverlays() {
		if (oProvider == null)
			return;

		if (roiDataList.size() == 0)
			return;

		if (roisIDs.size() != roiDataList.size()) {
			logger.warn("Mismatch in number of primitives and ROIs!");
		}

		oProvider.begin(OverlayType.VECTOR2D);

		for (int r = 0, rmax = roiDataList.size(); r < rmax; r++) {
			int id = -1;
			try {
				id = roisIDs.get(r);
			} catch (IndexOutOfBoundsException e) {
				roisIDs.add(r, -1);
			}
			if (id == -1) {
				id = oProvider.registerPrimitive(PrimitiveType.ARROW);
				roisIDs.set(r, id);
			} else
				oProvider.setPrimitiveVisible(id, true);

			final LinearROI lroi = (LinearROI) roiDataList.get(r).getROI();
			double[] spt = lroi.getPoint();
			double[] ept = lroi.getEndPoint();

			oProvider.drawArrow(id, spt[0], spt[1], ept[0], ept[1]);
			oProvider.setColour(id, oColour);
			oProvider.setTransparency(id, oTransparency);
		}

		oProvider.end(OverlayType.VECTOR2D);
	}

	@Override
	public void imageStart(IImagePositionEvent event) {
		hStatus = HandleStatus.NONE;

		if (roi == null) {
			roi = new LinearROI();
			roi.setPlot(true);
			roiHandler.setROI(roi);
		}

		int id = event.getPrimitiveID();
		short flags = event.getFlags();
		cpt = event.getImagePosition();

		if ((flags & IImagePositionEvent.LEFTMOUSEBUTTON) != 0) {
			if (id == -1 || !roiHandler.contains(id)) {
				// new ROI mode
				roi.setPoint(cpt);
				if (oProvider!=null) oProvider.setPlotAreaCursor(SWT.CURSOR_CROSS);
				hideCurrent();
				getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						spsx.setSelection(roi.getIntPoint()[0]);
						spsy.setSelection(roi.getIntPoint()[1]);
					}
				});
				dragging = true;
			} else if (roiHandler.contains(id)) {
				int h = roiHandler.indexOf(id);

				if (h == 0 || h == 2) {
					if ((flags & IImagePositionEvent.SHIFTKEY) != 0) {
						hStatus = HandleStatus.REORIENT;
						oProvider.setPlotAreaCursor(SWT.CURSOR_IBEAM);
					} else {
						hStatus = HandleStatus.RESIZE;
						oProvider.setPlotAreaCursor(SWT.CURSOR_SIZEALL);
					}
				} else if (h == 1) {
					if ((flags & IImagePositionEvent.SHIFTKEY) != 0) {
						hStatus = HandleStatus.ROTATE;
						oProvider.setPlotAreaCursor(SWT.CURSOR_APPSTARTING);
					} else {
						hStatus = HandleStatus.MOVE;
						oProvider.setPlotAreaCursor(SWT.CURSOR_HAND);
					}
				}
				hideCurrent();
				drawDraggedOverlay(roi);
				dragging = true;
				dragHandle = h; // store dragged handle
			}
		} else if ((flags & IImagePositionEvent.RIGHTMOUSEBUTTON) != 0) {
			if (roiHandler.contains(id)) {
				int h = roiHandler.indexOf(id);

				if (h == 0 || h == 2) {
					hStatus = HandleStatus.REORIENT;
					oProvider.setPlotAreaCursor(SWT.CURSOR_IBEAM);
				} else if (h == 1) {
					hStatus = HandleStatus.ROTATE;
					oProvider.setPlotAreaCursor(SWT.CURSOR_APPSTARTING);
				}
				hideCurrent();
				drawDraggedOverlay(roi);
				dragging = true;
				dragHandle = h; // store dragged handle
			}
		}
	}

	private LinearROI interpretMouseDragging(int[] pt) {
		final LinearROI lroi = (LinearROI) roi;
		LinearROI croi = null; // return null if not a valid event

		switch (hStatus) {
		case MOVE:
			croi = lroi.copy();
			croi.addPoint(pt);
			croi.subPoint(cpt);
			break;
		case NONE:
			croi = lroi.copy();
			croi.setEndPoint(pt);
			break;
		case REORIENT:
			croi = ((LinearROIHandler) roiHandler).reorient(dragHandle, pt);
			break;
		case RESIZE:
			croi = ((LinearROIHandler) roiHandler).resize(dragHandle, pt);
			break;
		case ROTATE:
			croi = lroi.copy();
			double ang = croi.getAngleRelativeToMidPoint(pt);
			double[] mpt = croi.getMidPoint();
			croi.setAngle(ang);
			croi.setMidPoint(mpt);
			break;
		}

		return croi;
	}

	@Override
	public void imageDragged(IImagePositionEvent event) {
		if (dragging) {
			final LinearROI croi = interpretMouseDragging(event.getImagePosition());

			if (croi != null) {
				drawDraggedOverlay(croi);
				if (System.currentTimeMillis() >= nextTime) {
					nextTime = System.currentTimeMillis() + updateInterval;
					updatePlot(croi);
					sendCurrentROI(croi);

					updateAllSpinners(croi);
				}
			}
		}
	}

	@Override
	public void imageFinished(IImagePositionEvent event) {
		if (dragging) {
			dragging = false;
			hideIDs(dragIDs);
			oProvider.restoreDefaultPlotAreaCursor();

			roi = interpretMouseDragging(event.getImagePosition());
			roiHandler.setROI(roi);

			dragHandle = -1;
			hStatus = HandleStatus.NONE;
			drawCurrentOverlay();
			sendCurrentROI(roi);

			updateAllSpinners(roi);
		}
	}

	// more GUI listeners
	private SelectionListener crossButtonListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final LinearROI lroi = (LinearROI) roi;

			if (((Button) e.widget).getSelection()) {
				lroi.setCrossHair(true);
			} else {
				lroi.setCrossHair(false);
			}
			getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					drawCurrentOverlay();
					updatePlot();
				}
			});
		}
	};

	@Override
	protected void updateAllSpinners(ROIBase roib) {
		final LinearROI lroi = (LinearROI) roib;
		if (lroi == null)
			return;

		getControl().getDisplay().asyncExec(new Runnable() {
			@Override
			public void run() {
				updateAllSpinnersInt(lroi);
			}
		});
	}

	private void updateAllSpinnersInt(final LinearROI lroi) {
		if (lroi == null)
			return;
		isBulkUpdate = true;
		spsx.setSelection(lroi.getIntPoint()[0]);
		spsy.setSelection(lroi.getIntPoint()[1]);
		splen.setDouble(lroi.getLength());
		spang.setDouble(lroi.getAngleDegrees());
		spex.setDouble(lroi.getEndPoint()[0]);
		isBulkUpdate = false;
		spey.setDouble(lroi.getEndPoint()[1]);
	}

	// spinner listeners
	private SelectionListener startPosListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final LinearROI lroi = (LinearROI) roi;
			double[] ept = lroi.getEndPoint();

			lroi.setPoint(spsx.getSelection(), spsy.getSelection());
			lroi.setEndPoint(ept);

			if (isBulkUpdate)
				return;

			sendCurrentROI(roi);

			getControl().getDisplay().asyncExec(new Runnable() {
				@Override
				public void run() {
					updatePlot();
					drawCurrentOverlay();
				}
			});
		}
	};

	private SelectionListener endPosListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final LinearROI lroi = (LinearROI) roi;

			if (lroi!=null) {
				lroi.setEndPoint(new double[] { spex.getDouble(), spey.getDouble() });

				if (!isBulkUpdate)
					sendCurrentROI(roi);
	
				getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						splen.setDouble(lroi.getLength());
						spang.setDouble(lroi.getAngleDegrees());
						if (isBulkUpdate)
							return;
						updatePlot();
						drawCurrentOverlay();
					}
				});
			}
		}
	};

	private SelectionListener lenAngListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			final LinearROI lroi = (LinearROI) roi;

			if (lroi!=null) {
				lroi.setLength(splen.getDouble());
				lroi.setAngleDegrees(spang.getDouble());
				if (!isBulkUpdate)
					sendCurrentROI(roi);
	
				getControl().getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						spex.setDouble(lroi.getEndPoint()[0]);
						spey.setDouble(lroi.getEndPoint()[1]);
						if (isBulkUpdate)
							return;
						updatePlot();
						drawCurrentOverlay();
					}
				});
			}
		}
	};

	@Override
	public LinearROIList createROIList() {
		LinearROIList list = new LinearROIList();
		if (roiDataList != null) {
			for (ROIData rd: roiDataList) {
				list.add((LinearROI) rd.getROI());
			}
		}
		return list;
	}

	@Override
	public ROIData createNewROIData(ROIBase roi) {
		return new LinearROIData((LinearROI) roi, data, lineStep);
	}

	@Override
	public Action createSwitchAction(final int index, final IPlotUI plotUI) {
		Action action = super.createSwitchAction(index, plotUI);
		action.setText("Line profile");
		action.setToolTipText("Switch side plot to line profile mode");
		action.setImageDescriptor(AnalysisRCPActivator.getImageDescriptor("icons/ProfileLine.png"));

		return action;
	}

	@Override
	public void addToHistory() {
		Plot1DAppearance plotApp = 
			new Plot1DAppearance(PlotColorUtility.getDefaultColour(lpPlotter.getColourTable().getLegendSize()),
					             Plot1DStyles.SOLID, "History " + lpPlotter.getNumHistory());
		lpPlotter.getColourTable().addEntryOnLegend(plotApp);
		lpPlotter.pushGraphOntoHistory();
	}

	@Override
	public void removeFromHistory() {
		if (lpPlotter.getNumHistory() > 0) {
			lpPlotter.getColourTable().deleteLegendEntry(lpPlotter.getColourTable().getLegendSize()-1);					
		    lpPlotter.popGraphFromHistory();
		    lpPlotter.refresh(true);
		}	
	}
}
