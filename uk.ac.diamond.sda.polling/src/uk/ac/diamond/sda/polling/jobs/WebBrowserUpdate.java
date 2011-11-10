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

package uk.ac.diamond.sda.polling.jobs;

import java.util.ArrayList;

import uk.ac.diamond.sda.polling.jobs.FilenameReaderUpdateOnlyJob;
import uk.ac.diamond.sda.polling.views.URLPollView;

public class WebBrowserUpdate extends FilenameReaderUpdateOnlyJob {
	
	public static final String URL_VIEW_NAME = "URLViewName";
	
	public WebBrowserUpdate() {
		super();
	}
	
	@Override
	protected void processFile(ArrayList<String> filenames) {
		try {			
			// only process the first file
			URLPollView.setURL(filenames.get(0),getJobParameters().get(URL_VIEW_NAME));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
