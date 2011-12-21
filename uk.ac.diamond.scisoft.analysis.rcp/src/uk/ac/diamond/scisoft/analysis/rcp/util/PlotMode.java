/*
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
