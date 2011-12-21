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
