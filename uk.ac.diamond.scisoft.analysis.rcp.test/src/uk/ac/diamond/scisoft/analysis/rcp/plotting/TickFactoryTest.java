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

package uk.ac.diamond.scisoft.analysis.rcp.plotting;

import java.util.LinkedList;

import junit.framework.Assert;

import org.dawnsci.plotting.jreality.tick.Tick;
import org.dawnsci.plotting.jreality.tick.TickFactory;
import org.dawnsci.plotting.jreality.tick.TickFormatting;
import org.junit.Test;

public class TickFactoryTest {

	static final double LARGE_POSITIVE = 1e+8;
	static final double LARGE_NEGATIVE = -1e+8;
	static final double VSMALL_POSITIVE = 1e-8;
	static final double VSMALL_NEGATIVE = -1e-8;
	static final int LARGE_DISPAY = 10000;
	static final int SMALL_DISPAY = 100;

	@Test
	public void testGenerateTicksNoDisplay() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(0, 0., LARGE_POSITIVE, (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplay() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., LARGE_POSITIVE, (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplay() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., LARGE_POSITIVE, (short) 0, false);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixel() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., LARGE_POSITIVE, (short) 2, false);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixel() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., LARGE_POSITIVE, (short) 2, false);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayMinMaxOverAllowed() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., LARGE_POSITIVE, (short) 0, true);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayMinMaxOverAllowed() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., LARGE_POSITIVE, (short) 0, true);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelMinMaxOverAllowed() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., LARGE_POSITIVE, (short) 2, true);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelMinMaxOverAllowed() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., LARGE_POSITIVE, (short) 2, true);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	// large negative

	@Test
	public void testGenerateTicksNoDisplayN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(0, LARGE_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, LARGE_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, LARGE_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, LARGE_NEGATIVE, 0., (short) 2, false);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, LARGE_NEGATIVE, 0., (short) 2, false);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayMinMaxOverAllowedN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, LARGE_NEGATIVE, 0., (short) 0, true);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayMinMaxOverAllowedN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, LARGE_NEGATIVE, 0., (short) 0, true);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelMinMaxOverAllowedN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, LARGE_NEGATIVE, 0., (short) 2, true);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelMinMaxOverAllowedN() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, LARGE_NEGATIVE, 0., (short) 2, true);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	// small

	@Test
	public void testGenerateTicksSmallDisplayVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., VSMALL_POSITIVE, (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., VSMALL_POSITIVE, (short) 0, false);
		Assert.assertEquals(10, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., VSMALL_POSITIVE, (short) 2, false);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., VSMALL_POSITIVE, (short) 2, false);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayMinMaxOverAllowedVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., VSMALL_POSITIVE, (short) 0, true);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayMinMaxOverAllowedVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., VSMALL_POSITIVE, (short) 0, true);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelMinMaxOverAllowedVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, 0., VSMALL_POSITIVE, (short) 2, true);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelMinMaxOverAllowedVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, 0., VSMALL_POSITIVE, (short) 2, true);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	// large negative

	@Test
	public void testGenerateTicksNoDisplayNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(0, VSMALL_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, VSMALL_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, VSMALL_NEGATIVE, 0., (short) 0, false);
		Assert.assertEquals(10, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, VSMALL_NEGATIVE, 0., (short) 2, false);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, VSMALL_NEGATIVE, 0., (short) 2, false);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayMinMaxOverAllowedNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, VSMALL_NEGATIVE, 0., (short) 0, true);
		Assert.assertEquals(2, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayMinMaxOverAllowedNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, VSMALL_NEGATIVE, 0., (short) 0, true);
		Assert.assertEquals(11, ticks.size());
	}

	@Test
	public void testGenerateTicksSmallDisplayITickPerPixelMinMaxOverAllowedNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(SMALL_DISPAY, VSMALL_NEGATIVE, 0., (short) 2, true);
		Assert.assertEquals(SMALL_DISPAY + 1, ticks.size());
	}

	@Test
	public void testGenerateTicksLargeDisplayITickPerPixelMinMaxOverAllowedNVS() {
		TickFactory tickFactory = new TickFactory(TickFormatting.plainMode);
		LinkedList<Tick> ticks = tickFactory.generateTicks(LARGE_DISPAY, VSMALL_NEGATIVE, 0., (short) 2, true);
		Assert.assertEquals(LARGE_DISPAY + 1, ticks.size());
	}

}
