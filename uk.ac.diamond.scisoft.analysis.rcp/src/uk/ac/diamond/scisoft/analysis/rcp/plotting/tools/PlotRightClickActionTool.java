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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.tools;

import java.util.LinkedList;
import java.util.ListIterator;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSet3DPlot1D;

import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;

/**
 *
 */
public class PlotRightClickActionTool extends PlotActionTool {
	private static final InputSlot click = InputSlot.getDevice("PrimarySelection");

	/**
	 * 
	 */

	public PlotRightClickActionTool() {
		super(click);
		listeners = new LinkedList<PlotActionEventListener>();
	}

	@Override
	public void perform(ToolContext tc) {
		tc.getTransformationMatrix(pointerSlot).toDoubleArray(pointerTrans.getArray());
		geometryMatched = (!(tc.getCurrentPick() == null));
		boolean foundEvent = false;
		if (geometryMatched) {
			if ((tc.getCurrentPick().getPickType() == PickResult.PICK_TYPE_POINT)
					|| (tc.getCurrentPick().getPickType() == PickResult.PICK_TYPE_LINE)) {
				String testStr = tc.getCurrentPick().getPickPath().getLastComponent().getName();
				int strIndex = testStr.indexOf(DataSet3DPlot1D.GRAPHNAMEPREFIX);
				if (strIndex != -1) {
					testStr = testStr.substring(strIndex + DataSet3DPlot1D.GRAPHNAMEPREFIX.length());
					int graphNr = -1;
					try {
						graphNr = Integer.parseInt(testStr);
					} catch (NumberFormatException ex) {
					}
					pickedPointOC = tc.getCurrentPick().getObjectCoordinates();
					foundEvent = true;
					ListIterator<PlotActionEventListener> iter = listeners.listIterator();
					while (iter.hasNext()) {
						PlotActionEventListener listener = iter.next();
						PlotActionEvent event = new PlotActionEvent(this, pickedPointOC, graphNr);
						listener.plotActionPerformed(event);
					}
				}
			}
		}

		if (!foundEvent && tc.getSource() == click) {
			ListIterator<PlotActionEventListener> iter = listeners.listIterator();
			while (iter.hasNext()) {
				PlotActionEventListener listener = iter.next();
				PlotActionEvent event = new PlotActionEvent(this, new double[] { 0, 0 }, -1);
				listener.plotActionPerformed(event);
			}
		}
	}

}
