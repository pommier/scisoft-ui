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
import uk.ac.diamond.scisoft.analysis.dataset.IDataset;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;
import uk.ac.diamond.sda.polling.jobs.FilenameReaderUpdateOnlyJob;

public class ImagePlotOverlayJob extends FilenameReaderUpdateOnlyJob {

	private static final Object PLOT_VIEW_NAME = "PlotViewName";
	private static final Object MAX_IMAGES_TO_OVERLAY = "MaxImagesToOverlay";
	
	@Override
	protected void processFile(ArrayList<String> filenames) {
		try {	
			// get the end of the list
			int listEnd = filenames.size()-1;

			ArrayList<IDataset> images = new ArrayList<IDataset>();
			
			int position = 0;
			while (listEnd-position >= 0 && position < Integer.parseInt(getJobParameters().get(MAX_IMAGES_TO_OVERLAY))) {
				DataHolder data = LoaderFactory.getData(filenames.get(listEnd-position));
				images.add(data.getDataset(0));
				position++;
			}			
				
			SDAPlotter.imagesPlot(getJobParameters().get(PLOT_VIEW_NAME), images.toArray(new IDataset[0]));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}



}
