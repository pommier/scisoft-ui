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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.legend;

/**
 * Listener that listens on different LegendChangeEvents, e.g. deleting entries
 */
public interface LegendChangeEventListener {

	/**
	 * An entry has been deleted out of the legend
	 * @param evt LegendChangeEvent object that contains all relevant information
	 */
	
	public void legendDeleted(LegendChangeEvent evt);
	
}
