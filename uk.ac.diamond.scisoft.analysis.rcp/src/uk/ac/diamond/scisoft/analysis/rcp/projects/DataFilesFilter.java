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

package uk.ac.diamond.scisoft.analysis.rcp.projects;

import java.io.File;
import java.io.FilenameFilter;

/**
 *
 */
public class DataFilesFilter implements FilenameFilter {


	private static final String LISTOFSUFFIX[] = {"png","jpg","tif{1,2}","mar","cbf","dat",
        "img","raw","mccd","cif","imgcif","nxs"};
	
	@Override
	public boolean accept(File dir, String name) {
		if (dir.isDirectory()) return true;
		for (int i = 0; i < LISTOFSUFFIX.length; i++)
		if (name.endsWith(LISTOFSUFFIX[i]))
			return true;
		return false;
	}

}
