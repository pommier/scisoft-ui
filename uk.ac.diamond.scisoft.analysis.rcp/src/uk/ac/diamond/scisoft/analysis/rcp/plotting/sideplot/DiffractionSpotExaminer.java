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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.rcp.histogram.HistogramUpdate;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlotException;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.PlottingMode;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.AxisMode;
import uk.ac.diamond.scisoft.analysis.roi.RectangularROI;

public class DiffractionSpotExaminer extends Composite {
	private DataSetPlotter plotter;
	private HistogramUpdate update;

	public DiffractionSpotExaminer(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.VERTICAL));
		plotter = new DataSetPlotter(PlottingMode.TWOD, this, false);
		plotter.setAxisModes(AxisMode.LINEAR_WITH_OFFSET, AxisMode.LINEAR_WITH_OFFSET, AxisMode.LINEAR);
	}

	public void processROI(IDataset data, RectangularROI rectROI) {
		if (rectROI.getLengths()[0] <= 1 || rectROI.getLengths()[1] <= 1 || data.getSize() <= 1)
			return;
		int[] startPoint = rectROI.getIntPoint();
		int[] stopPoint = rectROI.getIntPoint(1, 1);
		IDataset ROIdata = data.getSlice(new int[] { startPoint[1], startPoint[0] }, new int[] { stopPoint[1],
				stopPoint[0] }, new int[] { 1, 1 });
		plotter.setAxisOffset(rectROI.getPoint()[0], rectROI.getPoint()[1], 0.0);

		try {
			plotter.replaceCurrentPlot(ROIdata);
		} catch (PlotException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (update != null) {
			plotter.applyColourCast(update.getRedMapFunction(), update.getGreenMapFunction(),
					update.getBlueMapFunction(), update.getAlphaMapFunction(), update.inverseRed(),
					update.inverseGreen(), update.inverseBlue(), update.inverseAlpha(), update.getMinValue(),
					update.getMaxValue());
		}
		plotter.refresh(true);
	}

	@Override
	public void dispose() {
		if (plotter != null)
			plotter.cleanUp();
	}

	public void sendHistogramUpdate(HistogramUpdate update) {
		this.update = update;
		if (plotter == null)
			return;
		plotter.applyColourCast(update.getRedMapFunction(), update.getGreenMapFunction(), update.getBlueMapFunction(),
				update.getAlphaMapFunction(), update.inverseRed(), update.inverseGreen(), update.inverseBlue(),
				update.inverseAlpha(), update.getMinValue(), update.getMaxValue());
		plotter.refresh(true);
	}
}
