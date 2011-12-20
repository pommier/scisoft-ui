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

import de.jreality.geometry.PointSetFactory;
import de.jreality.scene.PointSet;
import de.jreality.scene.SceneGraphComponent;

/**
 *
 */
public class PointListPrimitive extends PointPrimitive {

	private double[] pointCoords;
	private int numVertices;
	
	public PointListPrimitive(SceneGraphComponent comp) {
		super(comp);
	}

	public PointListPrimitive(SceneGraphComponent comp, 
							  boolean fixedSize) {
		super(comp,fixedSize);
	}

	@Override
	public void setPoint(double x, double y) {
		
	}
	
	public void setPoints(double[] x, double[] y) {
		if (x.length == y.length) {
			pointCoords = new double[x.length*3];
			numVertices = x.length;
			for (int i = 0; i < x.length; i++) {
				pointCoords[i*3] = x[i];
				pointCoords[i*3+1] = y[i];
				pointCoords[i*3+2] = 0.005;
			}
			needToUpdateGeom = true;
		}
	}
	
	@Override
	protected PointSet createPointGeometry() {
		pFactory = new PointSetFactory();
		pFactory.setVertexCount(numVertices);
		pFactory.setVertexCoordinates(pointCoords);
		pFactory.update();
		return pFactory.getPointSet();
	}	
}
