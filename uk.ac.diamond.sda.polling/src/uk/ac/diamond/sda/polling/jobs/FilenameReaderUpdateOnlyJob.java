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

package uk.ac.diamond.sda.polling.jobs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import uk.ac.diamond.sda.polling.Activator;

public abstract class FilenameReaderUpdateOnlyJob extends FilenameReaderJob {

	ArrayList<String>  oldFilenames = new ArrayList<String>();
	
	@Override
	protected IStatus run(IProgressMonitor monitor) {
		try {
			File file = new File(getJobParameters().get(FILE_NAME));
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			
			ArrayList<String> filenames = new ArrayList<String>();
			
			String filename = br.readLine();
			if(filename == null) {
				throw new IOException("No File Specified in drop location");
			}
			
			// otherwise try to load in all the image filenames
			while(filename != null) {
				filenames.add(filename);
				filename = br.readLine();
			}
			
			if(newFilesPresent(oldFilenames, filenames)) {
				processFile(filenames);
				oldFilenames.clear();
				for (String string : filenames) {
					oldFilenames.add(string);
				}
			}
		
		} catch (Exception e) {
			setStatus(e.getLocalizedMessage());
			return new Status(IStatus.INFO, Activator.PLUGIN_ID, e.getLocalizedMessage());
		}
		
		setStatus("OK");
		return Status.OK_STATUS;
	}

	private boolean newFilesPresent(ArrayList<String> oldFilenames, ArrayList<String> filenames) {
		if(oldFilenames.size() != filenames.size()) return true;
		for(int i = 0; i < oldFilenames.size(); i++) {
			if(filenames.get(i).compareTo(oldFilenames.get(i)) != 0) {
				return true;
			}
		}
		return false;
	}

}
