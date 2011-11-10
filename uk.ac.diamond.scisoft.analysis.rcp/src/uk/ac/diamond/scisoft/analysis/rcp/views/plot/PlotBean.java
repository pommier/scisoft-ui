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

import java.util.LinkedHashMap;
import java.util.Map;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.AxisValues;
import uk.ac.gda.util.list.PrimitiveArrayEncoder;

/**
 * Stores information required to restore a plot. Should serialise to 
 * XML easily. Resolves plot data to primitive and serializable types.
 * 
 * NOTE: Does not work with large datasets. Perhaps should compress data or 
 * better still, just write the bean as instructions to load the data in from
 * the data folder instead of reproducing it. Another alternative is to save
 * less points for the graph which would give unexpected results when zooming
 * in.
 */
public class PlotBean {

	private String              secondId;
	private String              currentPlotName;
	private String              partName;
	// NOTE name<->data mapping where data is string as it serialises smaller.
	private Map<String,String>  data;
    private String              xAxisValues;
    private String              xAxis,yAxis;
	private int                 xAxisMode,yAxisMode;
    /**
	 * @return Returns the data.
	 */
	public Map<String, String> getData() {
		return data;
	}

	/**
	 * @param data The data to set.
	 */
	public void setData(Map<String, String> data) {
		this.data = data;
	}
	
	/**
	 * Convenience method for transferring the double[]'s to DataSets.
	 * @return h
	 */
	public Map<String, ? extends AbstractDataset> getDataSets() {
		if (data==null) return null;
		final Map<String, AbstractDataset> ret = new LinkedHashMap<String,AbstractDataset>(data.size());
		for (String name : data.keySet()) {
			ret.put(name,new DoubleDataset(PrimitiveArrayEncoder.getDoubleArray(data.get(name))));
		}
		return ret;
	}
	
	/**
	 * 
	 * @param ds
	 */
	public void setDataSets(final Map<String,? extends AbstractDataset> ds) {
		if (data==null) data = new LinkedHashMap<String,String>(ds.size());
		data.clear();
		for (String name : ds.keySet()) {
			data.put(name, PrimitiveArrayEncoder.getString(new DoubleDataset(ds.get(name)).getData()));
		}		
	}

	/**
	 * @return Returns the xAxisValues.
	 */
	public String getXAxisValues() {
		return xAxisValues;
	}

	/**
	 * @param axisValues The xAxisValues to set.
	 */
	public void setXAxisValues(String axisValues) {
		xAxisValues = axisValues;
	}
	
	/**
	 * 
	 * @param values
	 */
	public void setXAxisValues(AxisValues values) {
		this.xAxisValues = values.getValues().toString();
	}

	/**
	 * 
	 * @return a
	 */
	public AxisValues getXAxisValues2() {
		if (xAxisValues==null)     return null;
		if (xAxisValues.isEmpty()) return null;
		final double [] da = PrimitiveArrayEncoder.getDoubleArray(xAxisValues);
		if (da==null) return null;
		return new AxisValues(da);
	}
	/**
	 * @return Returns the xAxis.
	 */
	public String getXAxis() {
		return xAxis;
	}

	/**
	 * @param axis The xAxis to set.
	 */
	public void setXAxis(String axis) {
		xAxis = axis;
	}

	/**
	 * @return Returns the yAxis.
	 */
	public String getYAxis() {
		return yAxis;
	}

	/**
	 * @param axis The yAxis to set.
	 */
	public void setYAxis(String axis) {
		yAxis = axis;
	}

	/**
	 * @return Returns the xAxisMode.
	 */
	public int getXAxisMode() {
		return xAxisMode;
	}

	/**
	 * @param axisMode The xAxisMode to set.
	 */
	public void setXAxisMode(int axisMode) {
		xAxisMode = axisMode;
	}

	/**
	 * @return Returns the yAxisMode.
	 */
	public int getYAxisMode() {
		return yAxisMode;
	}

	/**
	 * @param axisMode The yAxisMode to set.
	 */
	public void setYAxisMode(int axisMode) {
		yAxisMode = axisMode;
	}

	/**
	 * @return Returns the partName.
	 */
	public String getPartName() {
		return partName;
	}

	/**
	 * @param partName The partName to set.
	 */
	public void setPartName(String partName) {
		this.partName = partName;
	}

	/**
	 * @return Returns the currentPlotName.
	 */
	public String getCurrentPlotName() {
		return currentPlotName;
	}

	/**
	 * @param currentPlotName The currentPlotName to set.
	 */
	public void setCurrentPlotName(String currentPlotName) {
		this.currentPlotName = currentPlotName;
	}

	/**
	 * @return Returns the secondId.
	 */
	public String getSecondId() {
		return secondId;
	}

	/**
	 * @param secondId The secondId to set.
	 */
	public void setSecondId(String secondId) {
		this.secondId = secondId;
	}
	
}
