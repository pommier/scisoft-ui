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
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.ILazyDataset;
import uk.ac.diamond.scisoft.analysis.dataset.StringDataset;
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

	private static final String scanCmdName = "scan_command";
	private static final String titleName = "title";

	private String[][] getHDF5TitleAndScanCmd(String fullpath) throws Exception {
		List<ILazyDataset> list = new HDF5Loader(fullpath).findDatasets(new String[] {scanCmdName, titleName}, 1, null);

		List<String> scans = new ArrayList<String>();
		List<String> titles = new ArrayList<String>();
		for (ILazyDataset d : list) {
			if (d instanceof StringDataset) {
				String n = d.getName();
				if (n == null) {
					continue;
				}
				if (n.contains(scanCmdName)) {
					scans.add(d.toString());
					if (scans.size() > titles.size() + 1)
						titles.add(null); // bulk out list
				} else if (n.contains(titleName)) {
					titles.add(d.toString());
					if (titles.size() > scans.size() + 1)
						scans.add(null);
				}
			}
		}

		int s = scans.size();
		int t = titles.size();
		if (s != t) {
			// correct size of lists
//			logger.warn("Scans and titles not in sync!");
			while (s < t) {
				scans.add(null);
				s++;
			}
			while (t < s) {
				titles.add(null);
				t++;
			}
		}

		String[][] results = new String[2][s];
		for (int i = 0; i < s; i++) {
			String str = scans.get(i);
			if (str != null && str.length() > 100) { // restrict to 100 characters
				str = str.substring(0,  100) + "...";
			}
			results[0][i] = str == null ? "" : "\nScanCmd" + (i+1) + ": " + str;
			str = titles.get(i);
			if (str != null && str.length() > 100) { // restrict to 100 characters
				str = str.substring(0,  100) + "...";
			}
			results[1][i] = str == null ? "" : "\nTitle" + (i+1) + ": " + str;
		}

		return results;
	}
}
