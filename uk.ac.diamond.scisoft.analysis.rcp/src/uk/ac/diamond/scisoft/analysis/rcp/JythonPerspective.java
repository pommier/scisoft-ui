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

package uk.ac.diamond.scisoft.analysis.rcp;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.eclipse.ui.console.IConsoleConstants;
import org.python.pydev.ui.perspective.PythonPerspectiveFactory;

public class JythonPerspective implements IPerspectiveFactory {
	
	public static final String ID = "uk.ac.diamond.scisoft.jythonperspective";
	
	@Override
	public void createInitialLayout(IPageLayout layout) {
			
		// get the editor area
		String editorArea = layout.getEditorArea();
		
		IFolderLayout navigatorLayout = layout.createFolder("navigators", IPageLayout.LEFT, 0.15f, editorArea);
		navigatorLayout.addView("org.python.pydev.navigator.view");
		navigatorLayout.addView("uk.ac.diamond.sda.navigator.views.FileView");

		// add plot 1 to the left
		layout.addView("uk.ac.diamond.scisoft.analysis.rcp.plotView1", IPageLayout.RIGHT, 0.6f, editorArea);
		
		// add the console to the bottom
		layout.addView(IConsoleConstants.ID_CONSOLE_VIEW, IPageLayout.BOTTOM, 0.6f, editorArea);
		
		// finaly add the outline view to the right of the editor area
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.RIGHT, 0.75f, editorArea);
		
		// Finaly add all the Pydev actions as are required for running stuff etc.
		(new PythonPerspectiveFactory()).defineActions(layout);

	}

}
