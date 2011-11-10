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

package uk.ac.diamond.scisoft.analysis.rcp.util;

import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;

public enum PlotMode {

	/**
	 * 1D
	 */
	PM1D(GuiPlotMode.ONED), 
	/**
	 * 3D but the axis in z is the index of Y
	 */
	PMSTACKED(GuiPlotMode.ONED_THREED), 
	/**
	 * 3D, z is a data set not the indices of Y
	 */
	PM3D(GuiPlotMode.ONED_THREED);

	private GuiPlotMode plotMode;

	private PlotMode(GuiPlotMode plotMode) {
		this.plotMode = plotMode;
	}

	public GuiPlotMode getGuiPlotMode() {
		return plotMode;
	}

}
