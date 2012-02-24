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
