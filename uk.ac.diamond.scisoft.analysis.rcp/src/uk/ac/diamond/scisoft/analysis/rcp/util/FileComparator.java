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

package uk.ac.diamond.scisoft.analysis.rcp.util;

import java.io.File;
import java.util.Comparator;

/**
 *
 */
public class FileComparator implements Comparator<File> {

	private FileCompareMode compareMode;
	
	public FileComparator(FileCompareMode compareMode) {
		this.compareMode = compareMode;
	}
	
	@Override
	public int compare(File o1, File o2) {
		switch(compareMode) {
		case datetime:
			if (o1.lastModified() == o2.lastModified())
					return 0;
			if (o1.lastModified() < o2.lastModified())
				return -1;
			return 1;
		case name:
			return o1.compareTo(o2);		
		}
		return 0;
	}

}
