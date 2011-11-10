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

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

public class JobParameters extends HashMap<String, String> {

	private static final long serialVersionUID = -7318893897906814738L;

	private File parameterFile;

	private long lastModified;
	
	public JobParameters(String fileName) throws IOException {
		parameterFile = new File(fileName);
		loadParameterFile();

		// initialise the last modified flag
		lastModified = parameterFile.lastModified();
	}

	public void refresh() throws IOException {
		long newLastModified = parameterFile.lastModified();
		if (lastModified != newLastModified) {
			loadParameterFile();
			lastModified = newLastModified;
		}
	}
	
	private void loadParameterFile() throws IOException{
		FileInputStream fin = new FileInputStream(parameterFile);
		BufferedInputStream bis = new BufferedInputStream(fin);
		BufferedReader br = new BufferedReader(new InputStreamReader(bis));
		
		String line = null;
		while ((line = br.readLine()) != null) {
			loadParameterString(line);
		}
		
	}
	
	private void loadParameterString(String parameterString) {
		
		if (parameterString.contains("=")) {
			String[] parts = parameterString.split("=");
			put(parts[0].trim(), parts[1].trim());
		}

	}

	public File getParameterFile() {
		return parameterFile;
	}	
	
}
