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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.overlay;

/**
 *
 */
public interface OverlayConsumer {

	/**
	 * Register an overlay provider to the consumer
	 * @param provider the provider to be registered
	 */
	public void registerProvider(OverlayProvider provider);
	
	/**
	 * Unregisters an overlay provider to the consumer
	 */
	public void unregisterProvider();

	/**
	 * Get rid of all primitives held by consumer
	 */
	public void removePrimitives();
}
