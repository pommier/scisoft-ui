/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

import org.eclipse.ui.IWorkbenchPart;

import uk.ac.diamond.scisoft.analysis.dataset.IMetadataProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;

public interface ISidePlotPart extends IMetadataProvider, IWorkbenchPart{

	/**
	 * Different side plot choices which may be made by the user.
	 */
	public enum SidePlotPreference {
		LINE,
		PROFILE,
		DIFFRACTION_3D,
		IMAGE
	}
	public SidePlotPreference getSidePlotPreference();
	
	/**
	 * Returns the plotter used on the part
	 * @return DataSetPlotter
	 */
	public DataSetPlotter getMainPlotter();
}
