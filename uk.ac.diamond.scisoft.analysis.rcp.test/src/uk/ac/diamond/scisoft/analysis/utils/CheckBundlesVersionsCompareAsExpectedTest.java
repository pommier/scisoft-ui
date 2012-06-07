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

package uk.ac.diamond.scisoft.analysis.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;
import org.osgi.framework.Version;

/**
 * This class tests that bundle versions compare as expected 
 * so that we can use qualifiers to reduce change of wrong
 * PyDev.
 */
public class CheckBundlesVersionsCompareAsExpectedTest {

	@Test
	public void testComares() {
		String pydev_2_5_release = "2.5.0.2012040618";
		String pydev_2_5_diamond_fork_rev_1 = "2.5.0.2012060623-opengda-c9f36e1";
		String pydev_2_5_diamond_fork_rev_2 = "2.5.0.2012060719-opengda-7bab4d4";
		String pydev_2_5_diamond_fork_rev_2_or_later = "2.5.0.2012060719";
		String pydev_2_5_diamond_fork_later_than_rev_2 = "2.5.0.2012060720";

		List<Version> all = new ArrayList<Version>();
		// put them all in in a random order
		all.add(new Version(pydev_2_5_diamond_fork_rev_2_or_later));
		all.add(new Version(pydev_2_5_diamond_fork_rev_1));
		all.add(new Version(pydev_2_5_release));
		all.add(new Version(pydev_2_5_diamond_fork_later_than_rev_2));
		all.add(new Version(pydev_2_5_diamond_fork_rev_2));
		Collections.sort(all);
		
		Assert.assertTrue(all.get(0).toString().equals(pydev_2_5_release));
		Assert.assertTrue(all.get(1).toString().equals(pydev_2_5_diamond_fork_rev_1));
		Assert.assertTrue(all.get(2).toString().equals(pydev_2_5_diamond_fork_rev_2_or_later));
		Assert.assertTrue(all.get(3).toString().equals(pydev_2_5_diamond_fork_rev_2));
		Assert.assertTrue(all.get(4).toString().equals(pydev_2_5_diamond_fork_later_than_rev_2));
	}
}
