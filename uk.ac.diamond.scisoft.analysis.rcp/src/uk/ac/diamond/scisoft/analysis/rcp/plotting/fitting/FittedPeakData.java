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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.fitting;

import java.awt.Color;

import org.eclipse.swt.graphics.RGB;

import uk.ac.diamond.scisoft.analysis.fitting.functions.APeak;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.IRowData;

public class FittedPeakData implements IRowData {

	private APeak fittedPeak;
	private Color peakColour;
	private boolean plot = true;
	
	public FittedPeakData(APeak peak, Color colour) {
		fittedPeak = peak;
		peakColour = colour;
	}
	
	@Override
	public boolean isPlot() {
		return plot;
	}

	@Override
	public void setPlot(boolean require) {
		plot = require;
	}

	public Color getPeakColour() {
		return peakColour;
	}

	public void setPeakColour(Color peakColour) {
		this.peakColour = peakColour;
	}

	public APeak getFittedPeak() {
		return fittedPeak;
	}

	@Override
	public RGB getPlotColourRGB() {
		return new RGB(peakColour.getRed(), peakColour.getGreen(), peakColour.getBlue());
	}
}