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

package uk.ac.diamond.scisoft.analysis.rcp.views.plot;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;

public class DataSetPlotData implements IPlotData {

	private Map<String, AbstractDataset> data;

	private DataSetPlotData() {
		this.data = new HashMap<String,AbstractDataset>(3);
	}
	
	public DataSetPlotData(String name, AbstractDataset value) {
		this();
		data.put(name, value);
	}


	@Override
	public IPlotData clone() {
		final DataSetPlotData copy = new DataSetPlotData();
		for (String name : data.keySet()) {
			copy.data.put(name, data.get(name).clone());
		}
		return copy;
	}
	
	@Override
	public void clear() {
		data.clear();
	}

//	@Override
//	public List<Double> getData() {
// 	    final double[] da = getDataSet().getData();
// 	    final List<Double> ret = new ArrayList<Double>(da.length);
// 	    for (int i = 0; i < da.length; i++) {
// 	    	ret.add(da[i]);
//		}
// 	    return ret;
//	}

	@Override
	public Map<String, AbstractDataset> getDataMap() {
		return data;
	}

	@Override
	public AbstractDataset getDataSet() {
		return data.values().iterator().next();
	}

	@Override
	public Collection<AbstractDataset> getDataSets() {
		return data.values();
	}

	@Override
	public boolean isDataSetValid() {
		for (String name : data.keySet()) {
			final AbstractDataset set = data.get(name);
			if (set.containsInfs()) return false;
			if (set.containsNans()) return false;
		}
		return true;
	}

	@Override
	public boolean isDataSetsValid() {
		return true;
	}

	@Override
	public boolean isMulti() {
		return data.size()>1;
	}

	@Override
	public int size() {
		return data.values().iterator().next().getSize();
	}

}
