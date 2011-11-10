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

import java.io.Serializable;

import uk.ac.diamond.scisoft.analysis.plotserver.GuiBean;
import uk.ac.diamond.scisoft.analysis.plotserver.GuiParameters;

/**
 * The <code>IGuiInfoManager</code> interface provides protocol
 * for saving and restoring GUI state typically between a client and multiple
 * subscribers. 
 * 
 * A concrete gui update manager implementation can be found,
 *  {@link uk.ac.diamond.scisoft.analysis.rcp.views.PlotView <code>PlotView</code>}
 * 
 */
public interface IGuiInfoManager {

	/**
	 * This method allows for interested parties to get relevant GUI information
	 * from the client
	 * 
	 * @return The data which specifies the gui state
	 */
	public abstract GuiBean getGUIInfo();

	/**
	 * This method allows interested parties to push relevant GUI information to the 
	 * client. Information can later be retrieved using <code>getGuiState</code>
	 * 
	 * @param key key with which the specified value is to be associated
	 * @param value value to be associated with the specified key
	 */
	public abstract void putGUIInfo(GuiParameters key, Serializable value);

	/**
	 * This method allows interested parties to remove GUI information from the client
	 *  
	 * @param key key with which the specified value is to be associated
	 */
	public abstract void removeGUIInfo(GuiParameters key);

}
