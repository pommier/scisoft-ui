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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.enums;

/**
 * Different image compositing operator
 */
public enum CompositeOp {

	/**
	 * ADD Operator
	 */	
	ADD,
	/**
	 * SUBTRACT Operator
	 */	
	SUBTRACT,
	/**
	 * MULTIPLY Operator
	 */	
	MULTIPLY,
	/**
	 * DIVIDE Operator
	 */	
	DIVIDE,
	/**
	 * MAX Operator
	 */	
	MAX,
	/**
	 * MIN Operator
	 */	
	MIN
}
