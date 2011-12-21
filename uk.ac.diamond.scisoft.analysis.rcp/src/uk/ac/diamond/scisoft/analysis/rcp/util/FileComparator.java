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
