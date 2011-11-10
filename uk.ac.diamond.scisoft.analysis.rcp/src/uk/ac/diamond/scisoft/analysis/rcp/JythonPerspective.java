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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;

public class JythonPerspective implements IPerspectiveFactory {

	private static final String PLOT1_VIEW_ID = "uk.ac.diamond.scisoft.analysis.rcp.plotView1";
	
	public static final String ID = "uk.ac.diamond.scisoft.jythonperspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
		
		// get the editor area
		String editorArea = layout.getEditorArea();
		
		// add plot 1 to the left
		layout.addView(PLOT1_VIEW_ID, IPageLayout.RIGHT, 0.5f, editorArea);
		
		// add the console to the top
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.TOP, 0.5f, editorArea);
		
		// finaly add the outline view to the right of the editor area
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, editorArea);

	}

}
