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

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.IExtendedMetadata;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class LightweightSRSScanCmdDecorator extends LabelProvider implements ILightweightLabelDecorator {

	private static final String SRS_EXT = "dat"; //$NON-NLS-1$
	private IExtendedMetadata metaData;
	private String decorator = "";
	private static final Logger logger = LoggerFactory.getLogger(LightweightSRSScanCmdDecorator.class);

	public LightweightSRSScanCmdDecorator() {
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
			if (SRS_EXT.equals(modelFile.getFileExtension())) {
				IFile ifile = (IFile) element;
				srsMetaDataLoader(ifile.getLocation().toString());

				try {
					decorator = metaData.getScanCommand();
					if(decorator==null){
						decorator=" * Scan Command: N/A";
						decoration.addSuffix(decorator);
					}else{
						if (decorator.length() > 100) // restrict to 100 characters
							decorator = decorator.substring(0, 100) + "...";
						decorator = " * " + decorator;
						decoration.addSuffix(decorator);
					}
				}catch (Exception e) {
					logger.error("Could not read metadata: ", e);
				}
			}
		}
	}
	
	public IExtendedMetadata srsMyMetaDataLoader(String fullpath){
		srsMetaDataLoader(fullpath);
		return metaData;
	}
	
	private void srsMetaDataLoader(String fullpath) {
		
		try {
			IMetaData metaDataTest=LoaderFactory.getMetaData(fullpath, null);
			if(metaDataTest instanceof IExtendedMetadata)
				metaData = (IExtendedMetadata)LoaderFactory.getMetaData(fullpath, null);
			else{
				decorator=" * Scan Command: N/A";
				logger.warn("Cannot decorate SRS decorator");
			}
		} catch (Exception ne) {
			logger.error("Cannot open dat file", ne);
		}
	}
}
