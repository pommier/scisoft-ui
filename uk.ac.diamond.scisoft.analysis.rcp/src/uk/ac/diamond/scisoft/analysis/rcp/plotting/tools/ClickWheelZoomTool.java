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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.tools;

import de.jreality.geometry.BoundingBoxUtility;
import de.jreality.math.Matrix;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.scene.Transformation;
import de.jreality.scene.data.DoubleArray;
import de.jreality.scene.tool.AbstractTool;
import de.jreality.scene.tool.InputSlot;
import de.jreality.scene.tool.ToolContext;
import de.jreality.util.Rectangle3D;

/**
 * Mouse wheel zoom tool that scales the underlying scene nodes uniformly
 */

public class ClickWheelZoomTool extends AbstractTool {

	protected static final InputSlot pointerSlot = InputSlot.getDevice("PointerTransformation");
	protected static InputSlot worldToNDC = InputSlot.getDevice("WorldToNDC");
	protected SceneGraphComponent sceneNode;
	protected SceneGraphComponent translationNode;
	private double speed = 1.05;
	protected Rectangle3D rect = null;
	
	/**
	 * Constructor of the ClickWheelZoomTool 
	 * @param applyNode SceneGraph node the transformation should be applied to
	 */
	public ClickWheelZoomTool(SceneGraphComponent applyNode,
							  SceneGraphComponent transNode)	{
		super(InputSlot.getDevice("PrimaryUp"),
			  InputSlot.getDevice("PrimaryDown"));

		this.sceneNode = applyNode;
		this.translationNode = transNode;
		if (sceneNode.getTransformation() == null)
			sceneNode.setTransformation(new Transformation());
	}

	protected void updateEdgePoints(@SuppressWarnings("unused") Matrix m) {
	}
	
	protected void updateEdgePointsAfterMouse(@SuppressWarnings("unused") Matrix m) {
	}
	
	@Override
	public void activate(ToolContext tc) {
		DoubleArray tm = tc.getTransformationMatrix(pointerSlot);
		DoubleArray worldNDCtm = tc.getTransformationMatrix(worldToNDC);

		Matrix tempMat = new Matrix(tm);
		Matrix worldNDC = new Matrix(worldNDCtm);
		tempMat.multiplyOnLeft(worldNDC);
		double mousePosX = tempMat.getEntry(0,3) / tempMat.getEntry(3,3);
		double mousePosY = tempMat.getEntry(1,3) / tempMat.getEntry(3,3);
		double [] matrix = null;

		if (rect == null)
			rect = 
				BoundingBoxUtility.calculateChildrenBoundingBox(sceneNode);
				
		int wheel = 0;
		if (tc.getSource()== InputSlot.getDevice("PrimaryUp")) {
			wheel = 1;
		}
		else if (tc.getSource()== InputSlot.getDevice("PrimaryDown")) {
			wheel = -1;
		}
		
		if (wheel != 0) {
			matrix = sceneNode.getTransformation().getMatrix();
			double scaling = matrix[0];
			double transX = matrix[3];
			double transY = matrix[7];
			double transZ = matrix[11];
			double newScale = (wheel < 0 ? speed : 1.0 / speed);
				
			double minX = -rect.getExtent()[0] * 0.5;
			double minY = -rect.getExtent()[1] * 0.5;
			
			double maxX = rect.getExtent()[0] * 0.5;
			double maxY = rect.getExtent()[1] * 0.5;
			scaling *= newScale;				

			if (translationNode != null)
			{
				matrix = translationNode.getTransformation().getMatrix();
				minX -= matrix[3];
				minY -= matrix[7];
				maxX -= matrix[3];
				maxY -= matrix[7];				
			}
			mousePosX = minX + (mousePosX + 1) * 0.5 * (maxX - minX);
			mousePosY = minY + (mousePosY + 1) * 0.5 * (maxY - minY);
			Matrix M = MatrixBuilder.euclidean().translate(transX,transY,transZ).scale(scaling).getMatrix();
			MatrixBuilder.euclidean().translate(transX,transY,transZ).scale(scaling).assignTo(sceneNode);
			updateEdgePoints(M);
			
			if (translationNode != null)
			{
				matrix = translationNode.getTransformation().getMatrix();

				
				double tTransX = matrix[3];
				double tTransY = matrix[7];
				double tTransZ = matrix[11];
				tTransX -= mousePosX;
				tTransY -= mousePosY;
				tTransX += (mousePosX - (mousePosX * newScale - mousePosX));
				tTransY += (mousePosY - (mousePosY * newScale - mousePosY));
				Matrix M1 = MatrixBuilder.euclidean().translate(tTransX,tTransY,tTransZ).getMatrix();
				MatrixBuilder.euclidean().translate(tTransX,tTransY,tTransZ).assignTo(translationNode);
				updateEdgePointsAfterMouse(M1);
			}			
		}
		tc.getViewer().render();
	}	
	/**
	 * Get the zoom speed
	 * @return the zoom speed (default 1.05)
	 */
	public double getSpeed() {
		return speed;
	}
	
	/**
	 * Set the zoom speed
	 * @param speed set the new zoom speed
	 */
	public void setSpeed(double speed) {
		this.speed = speed;
	}	
}
