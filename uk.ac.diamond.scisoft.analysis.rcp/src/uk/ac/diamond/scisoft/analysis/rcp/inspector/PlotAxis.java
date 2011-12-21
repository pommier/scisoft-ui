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
