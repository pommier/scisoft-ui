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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;


import gda.observable.IObservable;
import gda.observable.IObserver;

import java.util.Iterator;
import java.util.LinkedList;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSet3DPlot3D;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.OverlayType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.PrimitiveType;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.enums.VectorOverlayStyles;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DConsumer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.SurfacePlotROI;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.IImagePositionEvent;
import uk.ac.diamond.scisoft.analysis.rcp.views.DataWindowView;

/**
 *
 */
public class DataWindowOverlay implements Overlay2DConsumer, IObservable {

	private Overlay2DProvider provider = null;
	private LinkedList<IObserver> observers;
	private DataWindowView view;
	private int selectPrimID = -1;
	private int selectStartX;
	private int selectStartY;
	private int selectEndX;
	private int selectEndY;
	private int oldSelectEndX = 0;
	private int oldSelectEndY = 0;
	private int xScale;
	private int yScale;
	private boolean allowUndersampling;
	private int xSamplingMode;
	private int ySamplingMode;
	private int xAspect = 0;
	private int yAspect = 0;
	
	private java.awt.Color outLineColour = java.awt.Color.green;
	private static final java.awt.Color overSampleLineColour = java.awt.Color.orange;
	private static final java.awt.Color normalColour = java.awt.Color.green;
	
	/**
	 * Constructor of the DataWindowOverlay
	 * @param xScaling scaling factor for the x dimension
	 * @param yScaling scaling factor for the y dimension
	 * @param view View this overlay is connected to
	 */
	
	public DataWindowOverlay(int xScaling, int yScaling,
							 DataWindowView view) {
		xScale = xScaling;
		yScale = yScaling;
		observers = new LinkedList<IObserver>();
		this.view = view;
	}
	
	
	/**
	 * Set the scaling factors 
	 * @param xScaling scaling factor for the x dimension
	 * @param yScaling scaling factor for the y dimension
	 */
	
	public void setScaling(int xScaling, int yScaling) {
		xScale = xScaling;
		yScale = yScaling;
		selectStartX = 0;
		selectStartY = 0;
		selectEndX = DataSet3DPlot3D.MAXDIM / xScaling;
		selectEndY = DataSet3DPlot3D.MAXDIM / yScaling;
		selectPrimID = -1;
		drawOverlay();
	}
	
	public void setAllowUndersampling(boolean newRule) {
		allowUndersampling = newRule;
	}
	
	public void setSamplingMode(int xMode, int yMode) {
		xSamplingMode = xMode;
		ySamplingMode = yMode;
	}
	
	public void setAspects(int newXAspect, int newYAspect) {
		xAspect = newXAspect;
		yAspect = newYAspect;
	}
	
	/**
	 * Set new selection position 
	 * @param startX start position in x dimension
	 * @param startY start position in y dimension
	 * @param width
	 * @param height
	 */
	public void setSelectPosition(int startX, int startY, int width, int height) {
		if (selectPrimID == -1) 
			selectPrimID = provider.registerPrimitive(PrimitiveType.BOX);
		selectStartX = startX / xScale;
		selectStartY = startY / yScale;
		selectEndX = selectStartX + (width / xScale);
		selectEndY = selectStartY + (height / yScale);
		if (!allowUndersampling)
			clampToMax();
		else
			checkIfAboveMax();

		drawOverlay();
		view.setSpinnerValues(selectStartX * xScale, selectStartY * yScale,
				  Math.abs(selectEndX - selectStartX) * xScale,
				  Math.abs(selectEndY - selectStartY) * yScale);

		notifyObservers();
	}
	
	@Override
	public void hideOverlays() {
		// Nothing to do

	}

	@Override
	public void showOverlays() {
		// Nothing to do

	}

	@Override
	public void registerProvider(OverlayProvider provider) {
		this.provider = (Overlay2DProvider)provider;
	}

	private void drawOverlay() {
		provider.begin(OverlayType.VECTOR2D);
		if (selectPrimID != -1) {
		    //provider.drawSector(primID, 100,100,40,50,45,90);
		    provider.setStyle(selectPrimID, VectorOverlayStyles.OUTLINE);
		    provider.setColour(selectPrimID, outLineColour);
		    provider.drawBox(selectPrimID, selectStartX, selectStartY, 
		    							   selectEndX, selectEndY);
		}
		provider.end(OverlayType.VECTOR2D);
	}
	
	@Override
	public void removePrimitives() {
		if (selectPrimID != -1)
			provider.unregisterPrimitive(selectPrimID);
	}

	@Override
	public void unregisterProvider() {
		provider = null;
	}

	private void checkIfAboveMax() {
		int distX = Math.abs(selectEndX-selectStartX) * xScale;
		int distY = Math.abs(selectEndY-selectStartY) * yScale;
        if (distX * distY > DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) {
        	outLineColour = overSampleLineColour;
        } else
        	outLineColour = normalColour;
	}
	
	private void clampToMax() {
		int distX = Math.abs(selectEndX-selectStartX) * xScale;
		int distY = Math.abs(selectEndY-selectStartY) * yScale;
        if (distX * distY > DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) {
        	float ratio = (float)(DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) /
        				   (float)(distX * distY);
        	
        	int deltaX = Math.abs(selectEndX - oldSelectEndX);
        	int deltaY = Math.abs(selectEndY - oldSelectEndY);
        	
        	float xChangeRatio = (float)deltaX/(float)(deltaX+deltaY);
        	
        	float xRatio = 1.0f - (1.0f - ratio) * xChangeRatio;
        	float yRatio = 1.0f - (1.0f - ratio) * (1.0f- xChangeRatio);
        	
        	if (selectEndX > selectStartX) 
        		selectEndX = selectStartX + (int)(Math.abs(selectEndX-selectStartX) * xRatio);
        	else
        		selectEndX = selectStartX - (int)(Math.abs(selectEndX-selectStartX) * xRatio);

        	if (selectEndY > selectStartY) 
        		selectEndY = selectStartY + (int)(Math.abs(selectEndY-selectStartY) * yRatio);
        	else
        		selectEndY = selectStartY - (int)(Math.abs(selectEndY-selectStartY) * yRatio);	
       	
        }

	}
	
	@Override
	public void imageDragged(IImagePositionEvent event) {
		selectEndX = event.getImagePosition()[0];
		selectEndY = event.getImagePosition()[1];
		if (!allowUndersampling)
			clampToMax();
		else
			checkIfAboveMax();
		drawOverlay();
		view.setSpinnerValues(selectStartX * xScale, selectStartY * yScale,
				  Math.abs(selectEndX - selectStartX) * xScale,
				  Math.abs(selectEndY - selectStartY) * yScale);

		oldSelectEndX = selectEndX;
		oldSelectEndY = selectEndY;
	}

	@Override
	public void imageFinished(IImagePositionEvent event) {
		selectEndX = event.getImagePosition()[0];
		selectEndY = event.getImagePosition()[1];
		if (!allowUndersampling) 
			clampToMax();
		else
			checkIfAboveMax();
		drawOverlay();
		view.setSpinnerValues(selectStartX * xScale, selectStartY * yScale,
				  Math.abs(selectEndX - selectStartX) * xScale,
				  Math.abs(selectEndY - selectStartY) * yScale);
		
		notifyObservers();
	}

	@Override
	public void imageStart(IImagePositionEvent event) {
		if (selectPrimID == -1) 
			selectPrimID = provider.registerPrimitive(PrimitiveType.BOX);
		selectStartX = event.getImagePosition()[0];
		selectStartY = event.getImagePosition()[1];
		oldSelectEndX = selectStartX;
		oldSelectEndY = selectStartY;
	}


	private void notifyObservers() {
		int distX = Math.abs(selectEndX-selectStartX) * xScale;
		int distY = Math.abs(selectEndY-selectStartY) * yScale;
        int xSampleMode = ((distX * distY > 
        					DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) ? xSamplingMode : 0);
        int ySampleMode = ((distX * distY > 
							DataSet3DPlot3D.MAXDIM * DataSet3DPlot3D.MAXDIM) ? ySamplingMode : 0);
        
		SurfacePlotROI roi = new SurfacePlotROI(selectStartX * xScale, 
												selectStartY * yScale, 
												selectEndX * xScale,
												selectEndY * yScale,
												xSampleMode,
												ySampleMode,
												xAspect,
												yAspect);
		Iterator<IObserver> iter = observers.iterator();
		while (iter.hasNext()) {
			IObserver observer = iter.next();
			observer.update(this, roi);
		}
	}
	
	@Override
	public void addIObserver(IObserver anIObserver) {
		observers.add(anIObserver);
	}


	@Override
	public void deleteIObserver(IObserver anIObserver) {
		observers.remove(anIObserver);
	}


	@Override
	public void deleteIObservers() {
		observers.clear();
	}

}
