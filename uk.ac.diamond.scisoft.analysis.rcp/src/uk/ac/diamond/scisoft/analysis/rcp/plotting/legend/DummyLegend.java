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
