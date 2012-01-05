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

package uk.ac.diamond.sda.navigator.hdf5;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.navigator.IDescriptionProvider;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;

/**
 * Provides a label and icon for objects of type {@link Tree}.
 */
public class HDF5LabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider, ILabelDecorator {
	
	@Override
	public Image getImage(Object element) {
		TreeNode data = (TreeNode) element;
		HDF5Node node = ((HDF5NodeLink) data.getData()).getDestination();
		
		String[] str = node.toString().split("\n");
		for (int i = 0; i < str.length; i++) {
			if (str[0].contains("@NX")) {
				return  new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/hdf5/folderopen.gif"));
			}
			else if(str[i].contains("@target")||(str[i].contains("shape"))){
				return  new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/hdf5/dataset.gif"));
			}
		}
		return  new Image(Display.getCurrent(), getClass().getResourceAsStream("/icons/hdf5/text.gif"));
	}

	@Override
	public String getText(Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			return file.getName() + " " + file.getFullPath();
		}
		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			return ((HDF5NodeLink) data.getData()).getName();
		}
		return null;
	}

	@Override
	public String getDescription(Object element) {
		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			return "Property: " + ((HDF5NodeLink) data.getData()).getName();
		}
		return null;
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		return image;
	}

	@Override
	public String decorateText(String label, Object element) {
		TreeNode data = (TreeNode) element;
		HDF5Node node = ((HDF5NodeLink) data.getData()).getDestination();
		return label + " " + getNodeLinkData(node);
	}

	public static String getNodeLinkData(HDF5Node to) {
		String strClass = "";
		String strData = "";
		String[] str = to.toString().split("\n");
		for (int i = 0; i < str.length; i++) {
			if (str[0].contains("@NX_class")) {
				String[] temp = str[0].split("=");
				strClass = temp[1].trim();
				break; //no need to stay in the for loop
			} else {
				if (str[i].contains("shape")) {
					String[] temp = str[i].split("shape");
					strData = strData + "shape " + temp[1].trim() + " ";
				} else if (str[i].contains("@axis")) {
					String[] temp = str[i].split("=");
					strData = strData + "axis = " + temp[1].trim() + " ";
				} else {
					strData = strData + " " + str[i].trim() + " ";
				}
			}
		}

		if (strData.length() > 100) // restrict to 100 characters
			strData = strData.substring(0, 100) + "...";
		
		return strClass + strData;
	}
}
