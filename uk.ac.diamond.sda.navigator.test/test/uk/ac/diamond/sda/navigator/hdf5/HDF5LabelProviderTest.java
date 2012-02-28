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

package uk.ac.diamond.sda.navigator.hdf5;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5File;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.io.HDF5Loader;

public class HDF5LabelProviderTest {

	private static final Logger logger = LoggerFactory.getLogger(HDF5LabelProviderTest.class);
	private HDF5File hdf5File = null;
	private String fileName = "testFiles/2.nxs";

	private void loadHDF5Data() {
		try {
			hdf5File = new HDF5Loader(fileName).loadTree();
		} catch (Exception e) {
			logger.error("Could not load NeXus file {}", fileName);
		}
	}

	@Test
	public void testGetNodeLinkData() {
		loadHDF5Data();
		HDF5NodeLink hdf5nodelink = hdf5File.findNodeLink("/entry1/scan_command");
		HDF5Node node = hdf5nodelink.getDestination();
		logger.debug(node.toString());
		assertEquals(HDF5LabelProvider.getNodeLinkData(node).trim(),
				"scan DCMFPitch -0.12 0.12 0.0040 counter 1.0 BPM1IN");

	}
}
