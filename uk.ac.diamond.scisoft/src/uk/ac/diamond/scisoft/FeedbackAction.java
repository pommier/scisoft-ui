/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;

import uk.ac.diamond.scisoft.views.FeedbackView;

public class FeedbackAction implements IWorkbenchWindowActionDelegate {

	@Override
	public void run(IAction action) {
		try {
			PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().showView(FeedbackView.ID);
		} catch (PartInitException e) {
			// Not much to do here as its not that important.
			e.printStackTrace();
		}

	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// Not implemented

	}

	@Override
	public void dispose() {
		// Not implemented

	}

	@Override
	public void init(IWorkbenchWindow window) {
		// Not implemented

	}

}
