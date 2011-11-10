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
import uk.ac.diamond.scisoft.analysis.dataset.AbstractCompoundDataset;
import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.SRSLoader;
import uk.ac.diamond.sda.polling.jobs.FilenameReaderJob;

public class SRSFilePlotterJob extends FilenameReaderJob {

	public static final String PLOT_VIEW_NAME = "PlotViewName";
	
	@Override
	protected void processFile(ArrayList<String> filenames) {
		// only process the last given file
		SRSLoader srsLoader = new SRSLoader(filenames.get(filenames.size()-1));
		try {
			DataHolder holder = srsLoader.loadFile();
			
			String[] dataPlotNames = getJobParameters().get("YAxis").split(",");
						
			ArrayList<AbstractCompoundDataset> list = new ArrayList<AbstractCompoundDataset>();
		
			for (String name : dataPlotNames) {				
				
				AbstractDataset[] acd = new AbstractDataset[] { holder.getDataset(getJobParameters().get("XAxis")), holder.getDataset(name.trim()) };
				AbstractCompoundDataset cdd = DatasetUtils.cast(acd, acd[0].getDtype());
				list.add(cdd);			
			}
			
			int[] sizes = new int[list.size()];
			for (int i = 0 ; i < sizes.length; i++) {
				sizes[i] = 5;
			}
			
			SDAPlotter.scatter2DPlot(getJobParameters().get(PLOT_VIEW_NAME),
					list.toArray(new AbstractCompoundDataset[0]),
					sizes);
			
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		

	}

}
