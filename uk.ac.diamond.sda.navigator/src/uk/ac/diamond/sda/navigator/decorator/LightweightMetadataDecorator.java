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

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.jface.viewers.LabelProvider;

import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LightweightMetadataDecorator extends LabelProvider implements ILightweightLabelDecorator {

	private static final Logger logger = LoggerFactory.getLogger(LightweightMetadataDecorator.class);
	
	public LightweightMetadataDecorator() {
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
		return false;
	}

	@Override
	public void removeListener(ILabelProviderListener listener) {
		// TODO Auto-generated method stub
	}

	@Override
	public void decorate(Object element, IDecoration decoration) {
		IResource objectResource = (IResource) element;
		if (element instanceof IFile) {
			IFile ifile = (IFile) element;
			IPath path = ifile.getLocation();
			File file = path.toFile();
			objectResource.getResourceAttributes().toString();

			String lastModified = new SimpleDateFormat("dd/MM/yy hh:mm aaa").format(new Date(file.lastModified()));
			String filePermission = getFilePermission(file);
			//file size - date of last modification - file permissions
			decoration.addSuffix("  "+readableFileSize(file.length())+"  "+lastModified+"  "+filePermission);
			//Image overlay decoration according to file permission:
			if(filePermission.equals("- - -")){
				ImageDescriptor lockOverlay = ImageDescriptor.createFromFile(this .getClass(),"/icons/decorators/unconfigured_co.gif");
				decoration.addOverlay(lockOverlay);
			}
		}
	}
	
	public static URL newURL(String urlName) {
		try {
			return new URL(urlName);
		} catch(MalformedURLException e) {
			throw new RuntimeException("Malformed url " + urlName, e);
		}
	}

	public static String readableFileSize(long size) {
		if (size <= 0)
			return "0";
		final String[] units = new String[] { "B", "KB", "MB", "GB", "TB" };
		int digitGroups = (int) (Math.log10(size) / Math.log10(1024));
		return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
	}

	public static String getFilePermission(File file) { 
		// File Permissions:
		// r = read permission
		// w = write permission
		// x = execute permission
		// - = no permission
		String read = "-", write = "-", execute = "-";
		if (file.canRead())
			read = "r";
		if (file.canWrite())
			write = "w";
		if (file.canExecute())
			execute = "x";

		return read + " " + write + " " + execute;
	}
}
