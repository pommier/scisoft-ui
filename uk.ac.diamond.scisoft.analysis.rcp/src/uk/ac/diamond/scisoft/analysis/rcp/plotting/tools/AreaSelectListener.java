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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.tools;

import java.util.EventListener;

/**
 * AreaSelect listener that can listen into areaSelect events
 */
public interface AreaSelectListener extends EventListener {

	/**
	 * Area selection has started
	 * @param e AreaSelectEvent that contains the information 
	 *          necessary to handle this event
	 */
	public void areaSelectStart(AreaSelectEvent e);
	
	/**
	 * Area selection is dragged
	 * @param e AreaSelectEvent that contains the information
	 * 			necessary to handle this event
	 */
	public void areaSelectDragged(AreaSelectEvent e);
		
	/**
	 * Area selection is finished
	 * @param e AreaSelectEvent that contains the information
	 * 			necessary to handle this event
	 */
	public void areaSelectEnd(AreaSelectEvent e);
	
}
