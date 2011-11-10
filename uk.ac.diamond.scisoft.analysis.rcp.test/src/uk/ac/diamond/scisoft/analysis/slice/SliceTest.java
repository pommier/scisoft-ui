/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.slice;

import java.util.Arrays;

import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.DatasetUtils;
import uk.ac.diamond.scisoft.analysis.io.LoaderFactory;
import uk.ac.diamond.scisoft.analysis.io.SliceObject;
import uk.ac.diamond.scisoft.analysis.rcp.views.nexus.SliceUtils;

public class SliceTest {

	/**
	 * Tests doing a slice using LoaderFactory, the slice is every 10 using the step parameter and the test checks
	 * that this parameter results in the correct shape.
	 * @throws Exception
	 */
	@Test
	public void testSlice() throws Exception {
		
		final SliceObject currentSlice = new SliceObject();
		currentSlice.setAxes(Arrays.asList(new AbstractDataset[]{SliceUtils.createAxisDataset(61), SliceUtils.createAxisDataset(171)}));
		currentSlice.setName("NXdata.data");
        currentSlice.setPath(System.getProperty("GDALargeTestFilesLocation")+"NexusUITest/DCT_201006-good.h5");
		currentSlice.setSliceStart(new int[]{0, 0, 500});
		currentSlice.setSliceStop(new int[]{61, 171, 600});
		currentSlice.setSliceStep(new int[]{1, 1, 10});
		currentSlice.setX(0);
		currentSlice.setY(1);
		
		final AbstractDataset slice = LoaderFactory.getSlice(currentSlice, null);
		AbstractDataset       trans = DatasetUtils.transpose(slice, new int[]{0, 1, 2});
		
		if (trans.getShape()[2]!=10) {
			throw new Exception("Incorrect shape of slice returned! Expected 10 but was "+trans.getShape()[2]);
		}
		
		System.out.println("Slice test passed.");

	}

	/**
	 * Tests doing a slice using LoaderFactory, the slice is every -5using the step parameter and the test checks
	 * that this parameter results in the correct shape.
	 * @throws Exception
	 */
	@Test
	public void testSlice2() throws Exception {
		
		final SliceObject currentSlice = new SliceObject();
		currentSlice.setAxes(Arrays.asList(new AbstractDataset[]{SliceUtils.createAxisDataset(61), SliceUtils.createAxisDataset(171)}));
		currentSlice.setName("NXdata.data");
		currentSlice.setPath(System.getProperty("GDALargeTestFilesLocation")+"NexusUITest/DCT_201006-good.h5");
		currentSlice.setSliceStart(new int[]{0, 0, 600});
		currentSlice.setSliceStop(new int[]{61, 171, 500});
		currentSlice.setSliceStep(new int[]{1, 1, -5});
		currentSlice.setX(0);
		currentSlice.setY(1);

		final AbstractDataset slice = LoaderFactory.getSlice(currentSlice, null);
		AbstractDataset       trans = DatasetUtils.transpose(slice, new int[]{0, 1, 2});

		if (trans.getShape()[2]!=20) {
			throw new Exception("Incorrect shape of slice returned! Expected 20 but was "+trans.getShape()[2]);
		}

		System.out.println("Slice test passed.");

	}
	
	

	/**
	 * Tests doing a slice using LoaderFactory, the slice is every -5using the step parameter and the test checks
	 * that this parameter results in the correct shape.
	 * @throws Exception
	 */
	@Test
	public void testSlice3() throws Exception {
		
		final SliceObject currentSlice = new SliceObject();
		currentSlice.setAxes(Arrays.asList(new AbstractDataset[]{SliceUtils.createAxisDataset(225), SliceUtils.createAxisDataset(1481)}));
		currentSlice.setName("NXdata.data");
		currentSlice.setPath(System.getProperty("GDALargeTestFilesLocation")+"NexusUITest/sino.h5");
		currentSlice.setSliceStart(new int[]{4, 0, 0});
		currentSlice.setSliceStop(new int[]{5, 225, 1481});
		currentSlice.setSliceStep(new int[]{1, 1, 1});
		currentSlice.setX(1);
		currentSlice.setY(2);

		final AbstractDataset slice = LoaderFactory.getSlice(currentSlice, null);
		AbstractDataset       trans = DatasetUtils.transpose(slice, new int[]{0, 1, 2});

		// We sum the data in the dimensions that are not axes
		AbstractDataset sum    = trans;
		final int[]     dataShape = new int[]{62, 225, 1481};
		final int       len    = dataShape.length;

		for (int i = len - 1; i >= 0; i--) {
			if (!currentSlice.isAxis(i) && dataShape[i]>1)
				sum = sum.sum(i);
		}
		System.out.println(Arrays.toString(sum.getShape()));

	}

}
