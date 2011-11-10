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
