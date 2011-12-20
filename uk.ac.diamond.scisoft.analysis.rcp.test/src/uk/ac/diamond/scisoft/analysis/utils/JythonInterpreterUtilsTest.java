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

import org.junit.Test;
import org.python.core.PyFloat;
import org.python.core.PyObjectDerived;
import org.python.util.PythonInterpreter;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.rcp.util.JythonInterpreterUtils;

public class JythonInterpreterUtilsTest {

	
	/**
	 * This test is used to ensure that a Jython interpreter
	 * importing the scisoft python files can be created.
	 * 
	 * It checks that the .py files created can be instantiated
	 * without errors. The method JythonInterpreterUtils.getInterpreter()
	 * is used within the workflow tool to run jython nodes directly
	 * in the same VM.
	 * 
	 */
	@Test
	public void test() throws Exception {
		
		PythonInterpreter interpreter = JythonInterpreterUtils.getInterpreter();
		if (interpreter == null) throw new Exception("Cannot be sure that python scripts for scisoft are loadable by the provided Jython version!");
		
		
		interpreter.set("fred", 10d);
		interpreter.exec("fred = dnp.Sciwrap(fred)");
        
		final Object fred = interpreter.get("fred");
		if (fred==null) throw new Exception("Cannot read object 'fred'!");
		if (!(fred instanceof PyFloat)) throw new Exception("Fred should be a float!");
		
		final AbstractDataset set = AbstractDataset.arange(0, 100, 1, AbstractDataset.FLOAT32);
		interpreter.set("x", set);
		interpreter.exec("x = dnp.Sciwrap(x)");
		final Object x = interpreter.get("x");
		if (x==null) throw new Exception("Cannot read object 'x'!");
		if (!(x instanceof PyObjectDerived)) throw new Exception("x should be a PyObjectDerived!");
		
		interpreter.exec("sum = x.sum()");
		final Object sumX = interpreter.get("sum");
		if (sumX==null) throw new Exception("Cannot read object 'sumX'!");
		if (!(sumX instanceof PyFloat)) throw new Exception("sumX should be a float!");
        if (((PyFloat)sumX).getValue()!=4950.0d) throw new Exception("sumX should be 4950.0!");
	}
}
