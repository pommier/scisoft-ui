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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.actions;

import java.util.ArrayList;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;

import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;


/**
 *
 */
public class DemoGridViewSelectionAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String viewID = event.getParameter("uk.ac.diamond.scisoft.analysis.command.sourceView");
		if (viewID != null)
		{
			ImageExplorerView view = (ImageExplorerView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewID);
			ArrayList<String> selectionList = view.getSelection();
//			Iterator<String> iter = selectionList.iterator();
//			while (iter.hasNext()) {
//				System.out.println(iter.next());
//			}
			view.pushSelectedFiles(selectionList);
			return Boolean.TRUE;
		}
		return Boolean.FALSE;
	}

}
