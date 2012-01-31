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

package uk.ac.diamond.sda.navigator.decorator;

import gda.analysis.io.ScanFileHolderException;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.HDF5Loader;

public class LightweightNXSScanCmdDecorator extends LabelProvider implements ILightweightLabelDecorator {

	public static final String ID = "uk.ac.diamond.sda.navigator.nxsScancmdDecorator";
	
	private static final String NXS_EXT = "nxs"; //$NON-NLS-1$
	private String decorator = "";
	private static final Logger logger = LoggerFactory.getLogger(LightweightNXSScanCmdDecorator.class);

	public LightweightNXSScanCmdDecorator() {
		super();
	}
	
	@Override
	public void addListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
	}

	@Override
	public boolean isLabelProperty(Object element, String property) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		decorator = "";
		if (element instanceof IFile) {
			IFile modelFile = (IFile) element;
			if (NXS_EXT.equals(modelFile.getFileExtension())) {
				IFile ifile = (IFile) element;

				try {
					String[][] listTitlesAndScanCmd = getHDF5TitleAndScanCmd(ifile.getLocation().toString());
					for (int i = 0; i < listTitlesAndScanCmd[0].length; i++) {
						decorator = listTitlesAndScanCmd[0][i] + listTitlesAndScanCmd[1][i];
						decoration.addSuffix(decorator);
					}
				} catch (ScanFileHolderException e) {
					logger.error("Could not read Nexus file: ", e);
				}catch (Exception e){
					logger.error("Could not read Nexus metadata: ", e);
				}
			}
		}		
	}

	public String[][] getMyHDF5TitleAndScanCmd(String fullpath) throws Exception{
		return getHDF5TitleAndScanCmd(fullpath);
	}
	
	private String[][] getHDF5TitleAndScanCmd(String fullpath) throws Exception {
		String hdf5scanCommand = "";
		String hdf5Title = "";

		DataHolder dataHolder= new HDF5Loader(fullpath).loadFile();

		List<String> list = getAllRootEntries(dataHolder.getNames());
		String[] scanCmd = new String[list.size()];
		scanCmd=initStringArray(scanCmd);
		String[] titles = new String[list.size()];
		titles=initStringArray(titles);
		
		String[][] listScanCmdAndTitles = new String[2][list.size()];
		int i=0;
		for (Iterator<String> iterator = list.iterator(); iterator.hasNext();) {
			String string = iterator.next();
			// scan command
			if (dataHolder.contains("/" + string + "/scan_command")) {
				hdf5scanCommand = dataHolder.getDataset("/" + string + "/scan_command").toString();
				scanCmd[i] = "\nScanCmd" + (i+1) + ": " + hdf5scanCommand;// display of the string on a new line
			}
			// title
			if (dataHolder.contains("/" + string + "/title")) {
				hdf5Title = dataHolder.getDataset("/" + string + "/title").toString();
				titles[i] = "\nTitle" + (i+1) + ": " + hdf5Title;// display of the string on a new line
			}
			if (titles[i].length() > 100) // restrict to 100 characters
				titles[i] = titles[i].substring(0, 100) + "...";
			if (scanCmd[i].length() > 100) // restrict to 100 characters
				scanCmd[i] = scanCmd[i].substring(0, 100) + "...";
			
			listScanCmdAndTitles[0][i] = titles[i];
			listScanCmdAndTitles[1][i] = scanCmd[i];
			i++;
		}

		return listScanCmdAndTitles;

	}
	
	private String[] initStringArray(String[] array){
		for (int i = 0; i < array.length; i++) {
			array[i]="";
		}
		return array;
	}

	private List<String> getAllRootEntries(String[] oldFullPaths) {
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < oldFullPaths.length; i++) {
			String[] tmp = oldFullPaths[i].split("/");
			if (!list.contains(tmp[1]))
				list.add(tmp[1]);
		}
		return list;
	}
}
