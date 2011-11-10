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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;

/**
 * Job object
 */

public class PlotJob {

	private PlotJobType type;
	private GuiBean guiBean = null;
	
	/**
	 * Constructor for a new PlotJob
	 * @param type
	 */
	
	public PlotJob(PlotJobType type)
	{
		this.type = type;
	}
	
	/**
	 * Get the type of the job
	 * @return type of the job
	 */
	
	public PlotJobType getType()
	{
		return type;
	}

	/**
	 * Set the GuiBean object for this job
	 * @param guiBean
	 */
	public void setGuiBean(GuiBean guiBean) {
		this.guiBean = guiBean;
	}

	/**
	 * Get the GuiBean object for this job
	 * @return the GuiBean object if exist
	 */
	public GuiBean getGuiBean() {
		return guiBean;
	}
}
