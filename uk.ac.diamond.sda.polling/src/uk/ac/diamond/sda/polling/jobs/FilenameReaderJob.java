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

public abstract class FilenameReaderJob extends AbstractPollJob {
	
	public static final String FILE_NAME = "FileName";

	public FilenameReaderJob() {
		super("Filename Reader Job");
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		
		try {
			File file = new File(getJobParameters().get(FILE_NAME));
			FileInputStream fin = new FileInputStream(file);
			BufferedInputStream bis = new BufferedInputStream(fin);
			BufferedReader br = new BufferedReader(new InputStreamReader(bis));
			
			ArrayList<String> filenames = new ArrayList<String>();
			
			String filename = br.readLine();
			// if there is nothing there, throw an exception here to let the user know
			if(filename == null) {
				throw new IOException("No File Specified in drop location");
			}
			
			// otherwise try to load in all the image filenames
			while(filename != null) {
				filenames.add(filename);
				filename = br.readLine();
			}
			
			processFile(filenames);
		
		} catch (Exception e) {
			setStatus(e.getLocalizedMessage());
			return new Status(IStatus.INFO, Activator.PLUGIN_ID, e.getLocalizedMessage());
		}
		
		setStatus("OK");
		return Status.OK_STATUS;
		
	}

	protected abstract void processFile(ArrayList<String> filenames);
}
