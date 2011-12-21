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

package uk.ac.diamond.sda.navigator.nexus;

import org.eclipse.core.resources.IFile;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.navigator.IDescriptionProvider;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;

/**
 * Provides a label and icon for objects of type {@link Tree}.
 */
public class NexusLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider, ILabelDecorator {

	@Override
	public Image getImage(Object element) {
		return null;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public String getText(Object element) {
		if (element instanceof IFile) {
			IFile file = (IFile) element;
			return file.getName() + "	" + file.getFullPath();
		}
		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			return ((HDF5NodeLink) data.getData()).getName();
		}
		return null;
	}

	@SuppressWarnings("rawtypes")
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
		return null;
	}

	@Override
	public String decorateText(String label, Object element) {
		return label+"";
	}
}
