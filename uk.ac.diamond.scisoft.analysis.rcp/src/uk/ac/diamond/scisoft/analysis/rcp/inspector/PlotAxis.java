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

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Class to represent plot axis choice
 */
public class PlotAxis {
	private Map<String, Integer> map = new LinkedHashMap<String, Integer>();
	private String name;   // name of axis
	private boolean inSet; // true if axis is part of set

	@Override
	public PlotAxis clone() {
		PlotAxis n = new PlotAxis();
		n.name = name;
		n.inSet = inSet;
		n.map.putAll(map);
		return n;
	}

	public void putParameter(int dimension, String name) {
		map.put(name, dimension);
	}

	public void clear() {
		map.clear();
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public int getDimension() {
		return map.get(name);
	}

	public boolean containsName(String name) {
		return map.containsKey(name);
	}

	public LinkedList<String> getNames() {
		return new LinkedList<String>(map.keySet());
	}

	public void setInSet(boolean inSet) {
		this.inSet = inSet;
	}

	public boolean isInSet() {
		return inSet;
	}

	public Map<String, Integer> getMap() {
		return map;
	}

	@Override
	public String toString() {
		return map.toString();
	}
}
