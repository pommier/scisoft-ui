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

import uk.ac.diamond.scisoft.analysis.plotserver.GuiPlotMode;

/**
 *
 */
public enum PlottingMode {
	/**
	 * 
	 */
	ONED(GuiPlotMode.ONED),
	/**
	 * 
	 */
	ONED_THREED(GuiPlotMode.ONED_THREED), 
	/**
	 * 
	 */
	TWOD(GuiPlotMode.TWOD), 
	/**
	 * 
	 */
	SURF2D(GuiPlotMode.SURF2D),
	/**
	 * 
	 */
	MULTI2D(GuiPlotMode.MULTI2D), 
	/**
	 * 
	 */
	BARCHART(null),
	/**
	 * 
	 */
    SCATTER2D(GuiPlotMode.SCATTER2D),
    /**
     * 
     */
	SCATTER3D(GuiPlotMode.SCATTER3D),
	/**
	 * NULL MODE 
	 */
	EMPTY(null);
	
	private GuiPlotMode plotMode;

	private PlottingMode(GuiPlotMode plotMode) {
		this.plotMode = plotMode;
	}

	/**
	 * Get GUI plot mode from plotting mode
	 * @return GUI plot mode
	 */
	public GuiPlotMode getGuiPlotMode() {
		return plotMode;
	}

	/**
	 * Find PlottingMode corresponding to a GUI plot mode
	 * @param guiMode
	 * @return plotting mode
	 */
	public static PlottingMode plottingModeFromGui(GuiPlotMode guiMode) {
		for (PlottingMode p : PlottingMode.values())
			if (p.plotMode.equals(guiMode))
				return p;

		return null;
	}
}
