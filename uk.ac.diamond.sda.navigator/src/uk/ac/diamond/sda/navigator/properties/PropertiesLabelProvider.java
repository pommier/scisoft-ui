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

package uk.ac.diamond.sda.navigator.properties;

import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.IDescriptionProvider;

/**
 * Provides a label and icon for objects of type {@link PropertiesTreeData}.
 * 
 */
public class PropertiesLabelProvider extends LabelProvider implements ILabelProvider, IDescriptionProvider {

	@Override
	public Image getImage(Object element) {
		if (element instanceof PropertiesTreeData)
			return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJS_INFO_TSK);
		return null;
	}

	@Override
	public String getText(Object element) {
		if (element instanceof PropertiesTreeData) {
			PropertiesTreeData data = (PropertiesTreeData) element;
			return data.getName() + "= " + data.getValue(); //$NON-NLS-1$
		}
		return null;
	}

	@Override
	public String getDescription(Object anElement) {
		if (anElement instanceof PropertiesTreeData) {
			PropertiesTreeData data = (PropertiesTreeData) anElement;
			return "Property: " + data.getName(); //$NON-NLS-1$
		}
		return null;
	}

}
