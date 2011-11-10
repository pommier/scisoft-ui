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

import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.IDataSet3DCorePlot;

import de.jreality.scene.pick.PickResult;
import de.jreality.scene.tool.ToolContext;

/**
 *
 */
public class PlotActionTool2D extends PlotActionTool {
	   
		@Override
		public void perform(ToolContext tc){  
			tc.getTransformationMatrix(pointerSlot).toDoubleArray(pointerTrans.getArray());		
			geometryMatched=(!(tc.getCurrentPick() == null));
	    	if(geometryMatched) {
	    		if((tc.getCurrentPick().getPickType() == PickResult.PICK_TYPE_FACE ||
	    			tc.getCurrentPick().getPickType() == PickResult.PICK_TYPE_LINE ||
	    			tc.getCurrentPick().getPickType() == PickResult.PICK_TYPE_POINT)) 
	           	{
					List<PickResult> results = tc.getCurrentPicks();
					Iterator<PickResult> iter = results.iterator();
					while (iter.hasNext()) {
						PickResult result = iter.next();
						String name = result.getPickPath().getLastComponent().getName();
		        		if (name.contains(IDataSet3DCorePlot.GRAPHNODENAME)) {
		        			pickedPointOC=result.getObjectCoordinates();
		        			ListIterator<PlotActionEventListener> actionIter = listeners.listIterator();
		        			while (actionIter.hasNext()) {
		        				PlotActionEventListener listener = actionIter.next();
		        				PlotActionEvent event = new PlotActionEvent(this,pickedPointOC);
		        				listener.plotActionPerformed(event);
		        			}
		        			break;
		        		}
	        		}
	           	}
	    	}
	    }    
}
