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

package uk.ac.diamond.scisoft.analysis.rcp.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IPerspectiveDescriptor;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PerspectiveAdapter;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.DataExplorationPerspective;
import uk.ac.diamond.scisoft.analysis.rcp.inspector.DatasetInspector;

public class DatasetInspectorView extends ViewPart {
	private DatasetInspector inspector;
	private ImageExplorerView explorer = null;
	protected boolean hidden;
	private PerspectiveAdapter perspectiveListener = null;
	public static final String ID = "uk.ac.diamond.scisoft.analysis.rcp.views.DatasetInspectorView"; //$NON-NLS-1$

	private static final Logger logger = LoggerFactory.getLogger(DatasetInspectorView.class);

	@Override
	public void createPartControl(Composite parent) {
		inspector = new DatasetInspector(parent, SWT.NONE, getSite());

		if (perspectiveListener == null)
			createPerspectiveListener();

		getSite().getWorkbenchWindow().addPerspectiveListener(perspectiveListener);
	}

	private void createPerspectiveListener() {
		perspectiveListener = new PerspectiveAdapter() {
			@Override
			public void perspectiveActivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals(DataExplorationPerspective.ID)) {
					if (getImageExplorer() != null && !explorer.isDisposed())
						explorer.setLocationBarVisible(false);
				}
			}

			@Override
			public void perspectiveDeactivated(IWorkbenchPage page, IPerspectiveDescriptor perspective) {
				if (perspective.getId().equals(DataExplorationPerspective.ID)) {
					if (getImageExplorer() != null && !explorer.isDisposed())
						explorer.setLocationBarVisible(true);
				}
			}
		};
	}

	@Override
	public void setFocus() {
		inspector.setFocus();
	}

	@Override
	public void dispose() {
		if (perspectiveListener != null)
			getSite().getWorkbenchWindow().removePerspectiveListener(perspectiveListener);

		inspector.dispose();
		super.dispose();
	}

	private ImageExplorerView getImageExplorer() {
		if (explorer == null) {
			try {
				explorer = (ImageExplorerView) getSite().getPage().showView(ImageExplorerView.ID,
						null, IWorkbenchPage.VIEW_CREATE);
			} catch (PartInitException e) {
				logger.error("Cannot find image explorer view");
			}
		}
		return explorer;
	}
}
