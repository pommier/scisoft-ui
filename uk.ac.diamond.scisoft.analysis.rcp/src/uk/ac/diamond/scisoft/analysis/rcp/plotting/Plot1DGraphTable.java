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

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Plot1DGraphColourTable 
 */

public class Plot1DGraphTable {

	
	private List<Plot1DAppearance> legendEntries = 
		Collections.synchronizedList(new LinkedList<Plot1DAppearance>());
	
	/**
	 * Get the number of legend entries
	 * @return the number of legend entries
	 */
	
	public synchronized int getLegendSize()
	{
		return legendEntries.size();
	}
	
	/**
	 * Get the entry / description of a specific position in the legend map
	 * @param nr entry number
	 * @return Description of the entry at the asked position
	 */
	
	public synchronized Plot1DAppearance getLegendEntry(int nr)
	{
		assert nr < legendEntries.size()-1;
		return legendEntries.get(nr);
	}
	
	/**
	 * Push an entry on the top of the list of the legend
	 * @param newEntry name of the new entry
	 */
	public synchronized void pushEntryOnLegend(Plot1DAppearance newEntry)
	{
		legendEntries.add(0,newEntry);
	}
	
	/**
	 * Add an entry on the back of the list of the legend
	 * @param newEntry name of the new entry
	 * @return the index added.
	 */
	
	public synchronized int addEntryOnLegend(Plot1DAppearance newEntry)
	{
		legendEntries.add(newEntry);
		return legendEntries.size()-1;
	}

	/**
	 * Add entry at given index
	 * @param i index
	 * @param newEntry
	 */
	public synchronized void addEntryOnLegend(int i, Plot1DAppearance newEntry)
	{
		legendEntries.add(i, newEntry);
	}

	/**
	 * Delete an entry in the legend map at a specific position
	 * @param nr position in the legend map
	 */
	public synchronized void deleteLegendEntry(int nr)
	{
		assert nr < legendEntries.size() -1 && nr >= 0;
		legendEntries.remove(nr);
	}
	
	public synchronized void deleteLegendEntry(Plot1DAppearance entry)
	{
		legendEntries.remove(entry);
	}

	/**
	 * Clear the whole legend map
	 */
	
	public synchronized void clearLegend()
	{
		legendEntries.clear();
	}
		
}
