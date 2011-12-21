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
