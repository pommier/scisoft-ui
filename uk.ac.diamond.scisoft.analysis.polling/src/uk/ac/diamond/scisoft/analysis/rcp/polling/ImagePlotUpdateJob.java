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
import uk.ac.diamond.sda.polling.jobs.FilenameReaderUpdateOnlyJob;

public class ImagePlotUpdateJob extends FilenameReaderUpdateOnlyJob {

	public static final String PLOT_VIEW_NAME = "PlotViewName";
	
	public ImagePlotUpdateJob() {
		super();
	}

	@Override
	protected void processFile(ArrayList<String> filenames) {
		// this one will simply plot the last image in the list
		
		try {	
			SDAPlotter.imagePlot(getJobParameters().get(PLOT_VIEW_NAME), filenames.get(filenames.size()-1));		
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

}
