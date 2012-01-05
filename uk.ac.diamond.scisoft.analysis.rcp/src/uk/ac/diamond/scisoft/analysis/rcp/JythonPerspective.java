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
		
		// add the console and the outline to the bottom
		IFolderLayout bottomLayout = layout.createFolder("bottom", IPageLayout.BOTTOM, 0.6f, editorArea);
		bottomLayout.addView(IConsoleConstants.ID_CONSOLE_VIEW);
		bottomLayout.addView(IPageLayout.ID_OUTLINE);
		
		// Finaly add all the Pydev actions as are required for running stuff etc.
		(new PythonPerspectiveFactory()).defineActions(layout);

	}

}
