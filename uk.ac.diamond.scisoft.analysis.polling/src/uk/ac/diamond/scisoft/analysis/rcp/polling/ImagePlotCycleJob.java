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

package uk.ac.diamond.scisoft.analysis.rcp.polling;

import java.util.ArrayList;

import uk.ac.diamond.scisoft.analysis.SDAPlotter;
import uk.ac.diamond.sda.polling.jobs.FilenameReaderJob;

public class ImagePlotCycleJob extends FilenameReaderJob {

	private static final String MAX_IMAGES_TO_CYCLE = "MaxImagesToCycle";
	private static final Object PLOT_VIEW_NAME = "PlotViewName";
	private int cycle = 0;

	@Override
	protected void processFile(ArrayList<String> filenames) {
		try {	
			// get the end of the list
			int listEnd = filenames.size()-1;
			// check to make sure the cyclepoint is valid
			if((listEnd-cycle < 0) || cycle > Integer.parseInt(getJobParameters().get(MAX_IMAGES_TO_CYCLE))) {
				// otherwise reset it to zero
				cycle = 0;
			}
			SDAPlotter.imagePlot(getJobParameters().get(PLOT_VIEW_NAME), filenames.get(listEnd-cycle));	
			cycle++;
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	


}
