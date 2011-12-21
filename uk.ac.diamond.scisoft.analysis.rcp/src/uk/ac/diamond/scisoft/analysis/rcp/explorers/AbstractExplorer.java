/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
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
