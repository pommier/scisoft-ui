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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.legend;

import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.Plot1DGraphTable;

/**
 *
 */
public class DummyLegend extends LegendComponent {

	/**
	 * @param parent
	 * @param style
	 */
	public DummyLegend(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	public void updateTable(Plot1DGraphTable table) {
		// TODO Auto-generated method stub

	}

}
