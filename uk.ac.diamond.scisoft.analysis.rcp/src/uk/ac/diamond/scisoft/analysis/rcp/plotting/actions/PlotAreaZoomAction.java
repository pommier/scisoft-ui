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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.HandlerUtil;

import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.views.plot.AbstractPlotView;

/**
 *
 */
public class PlotAreaZoomAction extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String viewID = event.getParameter("uk.ac.diamond.scisoft.analysis.command.sourceView");
		if (viewID != null)
		{
			final AbstractPlotView apv = (AbstractPlotView)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().findView(viewID);
			DataSetPlotter plotter = apv.getPlotter();
			if (plotter != null) {
				ICommandService service = (ICommandService)PlatformUI.getWorkbench().getService(ICommandService.class);
				Command command = service.getCommand("uk.ac.diamond.scisoft.analysis.rcp.PlotRegionZoomAction");
				State state = command.getState("org.eclipse.ui.commands.toggleState");
				state.setValue(Boolean.FALSE);				
				command = event.getCommand();
				boolean value = HandlerUtil.toggleCommandState(command);
				plotter.setZoomEnabled(!value);
				plotter.setZoomMode(true);
			}
			return Boolean.TRUE;
			
		}
		return Boolean.FALSE;
	}

}
