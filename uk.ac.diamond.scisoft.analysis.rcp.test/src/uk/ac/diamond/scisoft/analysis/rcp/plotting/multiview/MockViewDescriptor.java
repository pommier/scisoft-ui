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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.multiview;

import junit.framework.AssertionFailedError;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.views.IViewDescriptor;

class MockViewDescriptor implements IViewDescriptor {
	public static final String UK_AC_DIAMOND_TEST_VIEW = "uk.ac.diamond.test.view.";
	private final String label;

	public MockViewDescriptor(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public String getId() {
		return UK_AC_DIAMOND_TEST_VIEW + label;
	}

	@Override
	public Object getAdapter(@SuppressWarnings("rawtypes") Class adapter) {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public IViewPart createView() throws CoreException {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public String[] getCategoryPath() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public String getDescription() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public ImageDescriptor getImageDescriptor() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public float getFastViewWidthRatio() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public boolean getAllowMultiple() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

	@Override
	public boolean isRestorable() {
		throw new AssertionFailedError("Methods in MockViewDescriptor should not be called");
	}

}