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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.primitives;

import java.awt.Color;

import de.jreality.geometry.PointSetFactory;
import de.jreality.math.MatrixBuilder;
import de.jreality.scene.Appearance;
import de.jreality.scene.PointSet;
import de.jreality.scene.SceneGraphComponent;
import de.jreality.shader.CommonAttributes;
import de.jreality.shader.DefaultGeometryShader;
import de.jreality.shader.DefaultPointShader;
import de.jreality.shader.ShaderUtility;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.VectorOverlayStyles;

/**
 *
 */
public class PointPrimitive extends OverlayPrimitive {

	private double coords[][] = new double[1][3];
	private double pointSize = 1.0;
	protected PointSetFactory pFactory = null;
	private DefaultPointShader dps = null;
	
	public PointPrimitive(SceneGraphComponent comp) {
		this(comp,false);
	}
	
	public PointPrimitive(SceneGraphComponent comp, boolean fixedSize) {
		super(comp, fixedSize);
		ap = new Appearance();
		comp.setAppearance(ap);
		ap.setAttribute(CommonAttributes.POINT_SHADER+ "." + CommonAttributes.SPHERES_DRAW,false);
		ap.setAttribute(CommonAttributes.LINE_SHADER + "." + CommonAttributes.TUBES_DRAW, false);
		ap.setAttribute(CommonAttributes.LIGHTING_ENABLED, false);
		ap.setAttribute(CommonAttributes.ATTENUATE_POINT_SIZE,false);
		ap.setAttribute("useGLSL", false);

		DefaultGeometryShader dgs = 
			ShaderUtility.createDefaultGeometryShader(ap, true);
		 dps = 
			(DefaultPointShader)dgs.createPointShader("default");
		dps.setDiffuseColor(java.awt.Color.RED);
		dps.setPointSize(pointSize);
		dps.setSpheresDraw(false);
		colour = java.awt.Color.RED;
		dgs.setShowFaces(false);
		dgs.setShowLines(false);
		dgs.setShowPoints(true);	
		pFactory = new PointSetFactory();
		
	}

	protected PointSet createPointGeometry() {
		pFactory.setVertexCount(1);
		pFactory.setVertexCoordinates(coords);
		pFactory.update();
		return pFactory.getPointSet();
	}
	
	
	@Override
	public void setLineThickness(double thickness) {
		// Nothing to do
	}

	@Override
	public void setOutlineColour(Color outlineColour) {
		// Nothing to do
	}

	@Override
	public void setOutlineTransparency(double value) {
		// Nothing to do
	}

	@Override
	public void setStyle(VectorOverlayStyles style) {
		// Nothing to do

	}

	@Override
	public void setTransparency(double value) {
		if (value > 0.0f) {
			ap.setAttribute(CommonAttributes.POINT_SHADER+"." + CommonAttributes.TRANSPARENCY_ENABLED, true);
			ap.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, true);
		} else {
			ap.setAttribute(CommonAttributes.POINT_SHADER+"." + CommonAttributes.TRANSPARENCY_ENABLED, false);
			ap.setAttribute(CommonAttributes.TRANSPARENCY_ENABLED, false);
		}
		ap.setAttribute(CommonAttributes.POINT_SHADER+"." + CommonAttributes.TRANSPARENCY,value);
	}

	public void setPoint(double x, double y) {
		needToUpdateGeom = true;
		coords[0][0] = x;
		coords[0][1] = y;
		coords[0][2] = 0.005;		
	}

	public void setPhat(boolean phat) {
		needToUpdateApp = true;
		if (phat)
			pointSize = 5.0;
		else
			pointSize = 1.0;
	}
	@Override
	public void updateNode() {
		if (needToUpdateTrans) {
			MatrixBuilder.euclidean(transformMatrix).assignTo(comp);
		}
		if (needToUpdateGeom)
		{
			comp.setGeometry(createPointGeometry());
		}
		if (needToUpdateApp)
		{
			dps.setDiffuseColor(colour);
			dps.setPointSize(pointSize);
		}
		needToUpdateApp = false;
		needToUpdateGeom = false;
		needToUpdateTrans = false;
		transformMatrix = null;
	}

}
