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

package uk.ac.diamond.scisoft.analysis.utils;

import junit.framework.Assert;

import org.dawnsci.plotting.jreality.util.JOGLChecker;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.junit.Ignore;
import org.junit.Test;

public class JOGLCheckerTest {

	@Test
	@Ignore("2011/01/24 Test ignored since not passing in Hudson GDA-3665")
	public void testJOGLChecker() {
		Display display = new Display();
		Shell shell = new Shell(display);
		Assert.assertEquals(true, JOGLChecker.canUseJOGL_OpenGL(null, shell));
	}
}
