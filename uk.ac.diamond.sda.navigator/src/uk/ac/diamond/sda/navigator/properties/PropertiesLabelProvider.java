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
