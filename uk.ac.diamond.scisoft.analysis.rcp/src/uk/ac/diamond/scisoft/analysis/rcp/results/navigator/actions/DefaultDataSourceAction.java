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

package uk.ac.diamond.scisoft.analysis.rcp.results.navigator.actions;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import uk.ac.diamond.scisoft.analysis.rcp.results.navigator.DataNavigator;
import uk.ac.gda.common.rcp.util.PathUtils;

/**
 * This class currently asks the user for a different root file to 
 * set as the file for the nexus tree.
 */
public class DefaultDataSourceAction extends AbstractHandler {


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {

		final DataNavigator view = (DataNavigator)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(DataNavigator.ID);
		if (view == null) return Boolean.FALSE;
		view.setSelectedPath(PathUtils.createFromDefaultProperty());
		return Boolean.TRUE;
	}

}
