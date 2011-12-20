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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.events;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.swt.widgets.Display;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay1DConsumer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.Overlay2DConsumer;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay.OverlayProvider;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.AreaSelectEvent;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.tools.IImagePositionEvent;


/**
 * An OverlayConsumer with the ability to add listeners which are notified when the user clicks
 * in the diagram. Convenience class for implementing drawing of overlay and listening to
 * different click events.
 * 
 * How to use:
 * 1. Extend class
 * 2. Implement createDrawingParts returning the drawing codes created (currently LINE or BOX)
 * 3. Implement drawOverlay which is called before the user has made a selection and 
 *    after. If the user has not made a selection some implementations will draw nothing.
 * 4. Use addOverlaySelectionListener(...) to listen to selected vertices changing.
 * 
 */
public abstract class AbstractOverlayConsumer implements Overlay1DConsumer, Overlay2DConsumer {


	protected OverlayProvider provider;
	protected int[]           parts;
	private AreaSelectEvent   start;
	private Display           display;
	
	/**
	 * @param display
	 */
	public AbstractOverlayConsumer(final Display display) {
		this.display = display;
	}
	
	/**
	 * Implement to create the parts required for drawing.
	 */
	protected abstract int[] createDrawingParts(OverlayProvider provider);

	/**
	 * Implement this method to draw the default view. If it does nothing,
	 * nothing is drawn until the user clicks on the diagram and drawOverlay(AreaSelectEvent) is called.
	 */
	protected abstract void drawOverlay(OverlayDrawingEvent evt);
		
	
	@Override
	public void registerProvider(final OverlayProvider provider) {
		this.provider = provider;
		this.parts    = createDrawingParts(provider);
		drawOverlay(new OverlayDrawingEvent(provider, parts));
	}

	@Override
	public void unregisterProvider() {
		provider = null;
		if (selectionListeners != null) selectionListeners.clear();
		selectionListeners = null;
		parts    = null;
		start    = null;
	}

	@Override
	public void areaSelected(AreaSelectEvent event) {
		if (event.getMode() == 0) {
			start = event;
		}
		if (event.getMode() == 1 || event.getMode() == 2) {
			drawOverlay(new OverlayDrawingEvent(provider, start, event, parts));
			notifyGraphSelectionListeners(event);
		}
	}

	/**
	 * Does nothing by default
	 */
	@Override
	public void imageDragged(IImagePositionEvent event) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Does nothing by default
	 */
	@Override
	public void imageFinished(IImagePositionEvent event) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Does nothing by default
	 */
	@Override
	public void imageStart(IImagePositionEvent event) {
		// TODO Auto-generated method stub
		
	}
	
	private Collection<GraphSelectionListener> selectionListeners;
	
	/**
	 * Add listener to events being selected in the graph.
	 * @param l
	 */
	public void addGraphSelectionListener(GraphSelectionListener l) {
		if (selectionListeners == null) selectionListeners = new HashSet<GraphSelectionListener>(5);
		selectionListeners.add(l);
	}

	protected void notifyGraphSelectionListeners(final AreaSelectEvent end) {
		
		if (selectionListeners == null) return;
		
	    if (!display.isDisposed()) {
	    	display.asyncExec(new Runnable()  {
	    		@Override
	    		public void run() {
	    			final GraphSelectionEvent evt = new GraphSelectionEvent(this);
	    			evt.setStart(start);
	    			evt.setEnd(end);
	    			for (GraphSelectionListener l : selectionListeners) {
	    				l.graphSelectionPerformed(evt);
	    			}
	    		}
	    	});
	    }
	}

	@Override
	public void removePrimitives() {
		parts = null;
	}

	@Override
	public void hideOverlays() {
		if (parts != null) {
			for (int p = 0; p < parts.length; p++) {
				provider.setPrimitiveVisible(parts[p], false);
			}
		}
	}

	@Override
	public void showOverlays() {
		if (parts != null) {
			for (int p = 0; p < parts.length; p++) {
				provider.setPrimitiveVisible(parts[p], true);
			}
		}
	}
}
