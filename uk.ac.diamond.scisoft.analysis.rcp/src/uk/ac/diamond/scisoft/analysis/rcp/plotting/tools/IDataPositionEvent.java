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

public interface IDataPositionEvent {

	/**
	 * Describes the various states a data position event can be in:
	 * <li/> START - position event has been initiated
	 * <li/> DRAG - position is changing
	 * <li/> END - position event is complete
	 */
	public enum Mode {START, DRAG, END}
	
	/**
	 * Get the current mode
	 * @return current mode
	 */
	public abstract Mode getMode();

	/**
	 * Get the position in texture coordinates
	 * @return texture coordinates
	 */
	public abstract double[] getPosition();

}