/*-
 * Copyright © 2009 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.doe;

import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.rcp.views.nexus.DimsData;
import uk.ac.diamond.scisoft.analysis.rcp.views.nexus.DimsDataList;
import uk.ac.gda.doe.DOEUtils;

/**
 * Unit test for DOE algorithm which is recursive and expands out
 * simulation sets using weightings defined in annotations.
 * 
 * If you have a case that does not expand correctly, add it here.
 * Probably a fix to DOEUtils.readAnnoations(...) will be what you
 * then need. 
 */
public class DOESliceObjectTest {
	
	@Test
	public void testSliceData() throws Throwable {
		
		final DimsData range = new DimsData(2);
		range.setSliceRange("100;200;1");
		
		final List<? extends Object> expanded = DOEUtils.expand(range);
		
		Assert.assertTrue(expanded.size()==101);
		
	}

	@Test
	public void testSliceDataHolder() throws Throwable {
		
		final DimsDataList holder = new DimsDataList();
		holder.add(new DimsData(0));
		holder.add(new DimsData(1));
		
		final DimsData range = new DimsData(2);
		range.setSliceRange("100;200;1");
		holder.add(range);
		
		final List<? extends Object> expanded = DOEUtils.expand(holder);
		
		Assert.assertTrue(expanded.size()==101);
		
	}

}
