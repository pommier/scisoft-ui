/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

import org.eclipse.swt.widgets.ScrollBar;

import de.jreality.math.Matrix;
import de.jreality.scene.SceneGraphComponent;

/**
 *
 */
public class ClickWheelZoomToolWithScrollBar extends ClickWheelZoomTool {

	private ScrollBar hBar;
	private ScrollBar vBar;
	private double[] edgePointP1;
	private double[] edgePointP2;
	private double[] middlePoint;
	private double[] edgePointP1Transformed;
	private double[] storagePointP1;
	private double[] edgePointP2Transformed;
	private double[] middlePointTransformed;
	private double hDistance;
	private double vDistance;
	
	public ClickWheelZoomToolWithScrollBar(SceneGraphComponent applyNode, 
										   SceneGraphComponent transNode,
										   ScrollBar hBar,
										   ScrollBar vBar) {
		super(applyNode, transNode);
		this.hBar = hBar;
		this.vBar = vBar;
		edgePointP1 = new double[]{-7.5,7.5,0.0};
		edgePointP2 = new double[]{7.5,-7.5,0.0};
		middlePoint = new double[]{0.0,0.0,0.0};
		edgePointP1Transformed = new double[3];
		storagePointP1 = new double[3];
		edgePointP2Transformed = new double[3];
		middlePointTransformed = new double[3];
	}

	@Override
	protected void updateEdgePoints(Matrix m) {
		System.arraycopy(edgePointP1, 0, edgePointP1Transformed, 0, 3);
		System.arraycopy(edgePointP2, 0, edgePointP2Transformed, 0, 3);
		System.arraycopy(middlePoint, 0, middlePointTransformed, 0, 3);
		m.transformVector(edgePointP1Transformed);
		m.transformVector(edgePointP2Transformed);
		m.transformVector(middlePointTransformed);
		hDistance = Math.abs(edgePointP1Transformed[0] - edgePointP1[0]) +
		            Math.abs(edgePointP2Transformed[0] - edgePointP2[0]);
		vDistance = Math.abs(edgePointP1Transformed[1] - edgePointP1[1]) +
					Math.abs(edgePointP2Transformed[1] - edgePointP2[1]);
		
		System.arraycopy(edgePointP1Transformed, 0, storagePointP1, 0, 3);		
	}
	
	public void setScrollBars(ScrollBar vBar,
							  ScrollBar hBar) {
		this.vBar = vBar;
		this.hBar = hBar;
	}
	
	
	@Override
	protected void updateEdgePointsAfterMouse(Matrix m) {
		m.transformVector(edgePointP1Transformed);
		m.transformVector(edgePointP2Transformed);
		m.transformVector(middlePointTransformed);
		final double hStartDif = hDistance * 0.5 + (storagePointP1[0] - edgePointP1Transformed[0]); 
		final double vStartDif = vDistance * 0.5 + (storagePointP1[1] - edgePointP1Transformed[1]);
	//	System.err.println(hStartDif);
		if (hBar != null && vBar != null) {
			if (hDistance > 1.0) {
				hBar.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						hBar.setVisible(true);
						hBar.setMinimum(0);
						hBar.setMaximum((int)(hDistance*10));
						hBar.setIncrement(1);
						hBar.setSelection((int)(hStartDif*10));
					}
				});
			} else  {
				hBar.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (hBar.isVisible()) {						
							hBar.setVisible(false);
							hBar.setMinimum(0);
							hBar.setMaximum(0);
							hBar.setIncrement(1);
						}
					}
				});
			}
			if (vDistance > 1.0) {
				vBar.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						vBar.setVisible(true);
						vBar.setMinimum(0);
						vBar.setMaximum((int)(vDistance*10));
						vBar.setIncrement(1);
						vBar.setSelection((int)(vStartDif*10));
					}
				});
			} else  {
				vBar.getDisplay().asyncExec(new Runnable() {
					@Override
					public void run() {
						if (vBar.isVisible()) {						
							vBar.setVisible(false);
							vBar.setMinimum(0);
							vBar.setMaximum(0);
							vBar.setIncrement(1);
						}
					}
				});
			}			
		}
	}	
}
