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

package uk.ac.diamond.scisoft.analysis.utils;

import junit.framework.Assert;

import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Ignore;
import org.junit.Test;

import uk.ac.diamond.scisoft.system.info.JOGLChecker;

import de.jreality.util.Secure;
import de.jreality.util.SystemProperties;

public class JOGLCheckerTest {

	@Test
	@Ignore("2011/01/24 Test ignored since not passing in Hudson GDA-3665")
	public void testJOGLChecker() {
		Display display = new Display();
		Shell shell = new Shell(display);
		String viewer = Secure.getProperty(SystemProperties.VIEWER,
										   SystemProperties.VIEWER_DEFAULT_JOGL);
		Assert.assertEquals(true, JOGLChecker.canUseJOGL_OpenGL(viewer, shell));
	}
}
