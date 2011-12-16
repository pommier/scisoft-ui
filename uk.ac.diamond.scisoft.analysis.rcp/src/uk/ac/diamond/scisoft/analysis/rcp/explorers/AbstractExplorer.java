/*-
 * Copyright © 2011 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.explorers;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPartSite;

import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.gda.monitor.IMonitor;

/**
 * This is a template for data file explorers to fill out. A concrete subclass of this can be used in any (read-only) editor part,
 * view part or the compare files editor.
 * 
 * <p>Warning: do not invoke {@link IWorkbenchPartSite#setSelectionProvider(ISelectionProvider)} in constructor.
 * Leave this to the method that instantiates the subclass.
 */
abstract public class AbstractExplorer extends Composite implements ISelectionProvider {
	protected IWorkbenchPartSite site;
	protected ISelectionChangedListener metaValueListener;

	/**
	 * @param parent
	 * @param partSite
	 * @param valueSelect listener to be called when a value is selected (in context menu)
	 */
	public AbstractExplorer(Composite parent, IWorkbenchPartSite partSite, ISelectionChangedListener valueSelect) {
		super(parent, SWT.NONE);

		site = partSite;
		metaValueListener = valueSelect;
	}

	/**
	 * Load file
	 * @param fileName
	 * @param mon
	 * @return data holder
	 * @throws Exception
	 */
	abstract public DataHolder loadFile(String fileName, IMonitor mon) throws Exception;

	/**
	 * Load file and display in explorer
	 * @param fileName
	 * @param mon
	 * @throws Exception
	 */
	abstract public void loadFileAndDisplay(String fileName, IMonitor mon) throws Exception;
}
