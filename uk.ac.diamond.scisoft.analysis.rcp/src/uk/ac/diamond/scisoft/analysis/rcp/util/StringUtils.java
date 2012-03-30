/*-
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StringUtils {

	/**
	 * Method that returns the list of all paths possibles out of a String[] of paths <br>
	 * Example: <br>
	 * oldFullPaths=
	 * {"/entry1/name",<br>
	 *  "/entry1/instrument/energy",<br>
	 *  "/entry1/instrument/I0",<br>
	 *  "/entry1/source/IRef"}<br>
	 * will return <br>
	 * List<HDF5NodeLink> of <fullName>=
	 * {"/",<br>
	 *  "/entry1",<br>
	 *  "/entry1/name",<br>
	 *  "/entry1/instrument",<br>
	 *  "/entry1/instrument/energy",<br>
	 *  "/entry1/instrument/I0",<br>
	 *  "/entry1/source",<br>
	 *  "/entry1/source/IRef"}<br>
	 * 
	 * @param oldFullPaths
	 *            A String[] of paths
	 * @param DELIMITER
	 * @return List<String> list The complete list of all possible HDF5NodeLink
	 */
	public static List<String> getAllPathnames(String[] oldFullPaths, String DELIMITER) {
		List<String> list = new ArrayList<String>();

		String[] newFullPaths = new String[oldFullPaths.length + 1];

		for (int i = 0; i < newFullPaths.length; i++) {
			if (i == 0)
				newFullPaths[i] = "/";
			else if (i > 0)
				newFullPaths[i] = oldFullPaths[i - 1];

			String[] tmp = newFullPaths[i].split(DELIMITER);
			String str = "";
			for (int j = 1; j < tmp.length; j++) {
				str = str.concat(DELIMITER + tmp[j]);
				if (!list.contains(str) && str != "") {
					list.add(str);
				}
			}
			if (!list.contains(str) && str != "") {
				list.add(str);
			}
		}
		//we add a root
		list.add("/");
		//we sort the list of string paths
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		
		return list;
	}
}
