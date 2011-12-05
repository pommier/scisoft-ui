/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.sda.navigator.decorator;

import java.io.File;
import java.util.Collection;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jface.viewers.IColorDecorator;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;

public class SRSFileDecorator extends LabelProvider implements ILabelDecorator, IColorDecorator {

	private static final Object SRS_EXT = "dat"; //$NON-NLS-1$
	private DataHolder data;
	private IMetaData metaData;
	private String fileName;
	private static final Logger logger = LoggerFactory.getLogger(SRSFileDecorator.class);

	public SRSFileDecorator() {
		super();
	}

	@Override
	public Image decorateImage(Image image, Object element) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String decorateText(String label, Object element) {
		IResource objectResource = (IResource) element;
		String string = "";
		String scanCommand = "";
		if (element instanceof IFile) {
			IFile modelFile = (IFile) element;
			if (SRS_EXT.equals(modelFile.getFileExtension())) {
				IFile ifile = (IFile) element;
				IPath path = ifile.getLocation();
				File file = path.toFile();
				srsFileLoader(ifile);

				Collection<String> list;
				try {
					list = metaData.getMetaNames();
					scanCommand = metaData.getMetaValue("cmd");
					if (scanCommand == null)
						scanCommand = "* No Scan Command";
					else
						scanCommand="* "+scanCommand;
				} catch (Exception e) {
					// TODO Auto-generated catch block
					logger.error("Could not read metadata: ", e);
				}

			}
		}

		return label + "  " + scanCommand;
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

	private void srsFileLoader(IFile file) {

		fileName = file.getLocation().toString();
		try {
			metaData = LoaderFactory.getMetaData(fileName, null);
		} catch (Exception ne) {
			logger.error("Cannot open dat file", ne);
		}
	}

	@Override
	public Color decorateForeground(Object element) {
		return PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
	}

	@Override
	public Color decorateBackground(Object element) {
		return PlatformUI.getWorkbench().getDisplay().getSystemColor(SWT.COLOR_BLUE);
	}

}
