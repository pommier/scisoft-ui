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

import uk.ac.diamond.scisoft.analysis.rcp.plotting.IDataSet3DCorePlot;

import java.util.LinkedList;
import java.util.ListIterator;

import de.jreality.math.Matrix;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;

/**
 * An Area Select tool for jReality for 1D Plots
 */
public class AreaSelectTool extends AbstractTool {

	private static final InputSlot pointerSlot = InputSlot.getDevice("PointerTransformation");
	private Matrix pointerTrans = new Matrix();
	private boolean geometryMatched;
	private boolean initial = false;
	private double[] pickedPointOC;
	private int primID = -1;
	private LinkedList<AreaSelectListener> listeners;

	/**
	 * Constructor of an AreaSelectTool
	 */
	
	public AreaSelectTool() {
		super(InputSlot.getDevice("RotateActivation"));
		addCurrentSlot(pointerSlot);
		listeners = new LinkedList<AreaSelectListener>();
	}
	
	@Override
	public void activate(ToolContext tc) {
		initial = true;
		perform(tc);
		ListIterator<AreaSelectListener> iter = listeners.listIterator();
		while (iter.hasNext()) {
			AreaSelectListener listener = iter.next();
			AreaSelectEvent event = new AreaSelectEvent(this,pickedPointOC,
													    (char)0,primID);
			listener.areaSelectStart(event);
		}		
		initial = false;
	}

	/**
	 * Add another AreaSelectListener to the listener list
	 * @param newListener
	 */
	public void addAreaSelectListener(AreaSelectListener newListener)
	{
		listeners.add(newListener);
	}
	
	@Override
	public void perform(ToolContext tc) {  
		tc.getTransformationMatrix(pointerSlot).toDoubleArray(pointerTrans.getArray());
		
		geometryMatched=(!(tc.getCurrentPick() == null));
		if(geometryMatched) {
			String name = tc.getCurrentPick().getPickPath().getLastComponent().getName();
			primID = -1;
			if (name.contains(IDataSet3DCorePlot.OVERLAYPREFIX)) {
				String testStr = name.substring(IDataSet3DCorePlot.OVERLAYPREFIX.length());
				try {
					primID = Integer.parseInt(testStr);
				} catch (NumberFormatException ex) { primID = -1; }
			}
			pickedPointOC=tc.getCurrentPick().getObjectCoordinates();
		}
		if (!initial)
		{
			ListIterator<AreaSelectListener> iter = listeners.listIterator();
			while (iter.hasNext()) {
				AreaSelectListener listener = iter.next();
				AreaSelectEvent event = new AreaSelectEvent(this,pickedPointOC,(char)1,primID);
				listener.areaSelectDragged(event);
			}		
		}
 	}
	
	@Override
	public void deactivate(ToolContext tc) {
		ListIterator<AreaSelectListener> iter = listeners.listIterator();
		while (iter.hasNext()) {
			AreaSelectListener listener = iter.next();
			AreaSelectEvent event = new AreaSelectEvent(this,pickedPointOC,(char)2,primID);
			listener.areaSelectEnd(event);
		}
	}	
	
}
