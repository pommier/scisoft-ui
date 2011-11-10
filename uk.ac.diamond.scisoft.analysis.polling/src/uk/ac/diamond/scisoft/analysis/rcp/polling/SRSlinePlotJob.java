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

package uk.ac.diamond.scisoft.analysis.rcp.polling;

import java.util.ArrayList;

import uk.ac.diamond.scisoft.analysis.SDAPlotter;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.SRSLoader;
import uk.ac.diamond.sda.polling.jobs.FilenameReaderJob;

public class SRSlinePlotJob extends FilenameReaderJob {

	public static final String PLOT_VIEW_NAME = "PlotViewName";

	private void swapItems(int a, int b, AbstractDataset dataSet) {
		Object temp = dataSet.getObject(a);
		dataSet.set(dataSet.getObject(b), a);
		dataSet.set(temp, b);
	}
	
	
	@Override
	protected void processFile(ArrayList<String> filenames) {
		// only plot the first file
		SRSLoader srsLoader = new SRSLoader(filenames.get(filenames.size()-1));
		try {
			DataHolder holder = srsLoader.loadFile();
			
			// get all the data
			String[] dataPlotNames = getJobParameters().get("YAxis").split(",");
					
			AbstractDataset xAxis = holder.getDataset(getJobParameters().get("XAxis"));			
			
			ArrayList<AbstractDataset> list = new ArrayList<AbstractDataset>();
		
			for (String name : dataPlotNames) {					
				list.add(holder.getDataset(name));
			}
			
			// order the data, simple sorting routine
			boolean sorted = false;
			while(!sorted) {
				
				sorted = true;
				
				for(int i = 0; i < xAxis.getShape()[0]-1; i++) {
					
					if(xAxis.getDouble(i) > xAxis.getDouble(i+1)) {
						sorted = false;
						
						swapItems(i, i+1, xAxis);
						for (AbstractDataset abstractDataset : list) {
							swapItems(i, i+1, abstractDataset);
						}						
					}					
				}
			}
			
			
			// plot the results
			SDAPlotter.plot(getJobParameters().get(PLOT_VIEW_NAME),
					xAxis,list.toArray(new AbstractDataset[0]));			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
