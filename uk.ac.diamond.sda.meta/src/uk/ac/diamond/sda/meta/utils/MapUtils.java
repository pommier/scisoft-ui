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

package uk.ac.diamond.sda.meta.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MapUtils {

	/**
	 * @param value
	 * @return v
	 */
	public static String getString(final Map<String, String> value) {
		if (value == null)
			return null;
		if (value.isEmpty())
			return null;
		final String line = value.toString();
		return line.substring(1, line.length() - 1); // key=value, key1=value1, ...
	}

	/**
	 * @param value
	 * @return v
	 */
	public static Map<String, String> getMap(final String value) {

		if (value == null)
			return null;
		if ("".equals(value.trim()))
			return null;
		final List<String> lines = getList(value);
		if (lines == null)
			return null;
		if (lines.isEmpty())
			return Collections.emptyMap();

		final Map<String, String> ret = new LinkedHashMap<String, String>(lines.size());
		for (String line : lines) {
			final String[] kv = line.split("=");
			if (kv == null || kv.length != 2)
				continue;
			ret.put(kv[0].trim(), kv[1].trim());
		}
		return ret;
	}

	/**
	 * copied from uk.ac.gda.util.list and made private
	 * 
	 * @param value
	 * @return v
	 */
	private static List<String> getList(final String value) {
		if (value == null)
			return null;
		if ("".equals(value.trim()))
			return null;
		final String[] vals = value.split(",");
		final List<String> ret = new ArrayList<String>(vals.length);
		for (int i = 0; i < vals.length; i++)
			ret.add(vals[i].trim());
		return ret;
	}
}
