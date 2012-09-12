package uk.ac.diamond.scisoft.rp;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.WorkbenchException;

public class Render3DPerspectiveLauch implements IWorkbenchWindowActionDelegate  {

	public Render3DPerspectiveLauch() {		
	}

	@Override
	public void run(IAction action) {
		try {
			PlatformUI.getWorkbench().showPerspective(Render3DPerspective.ID,PlatformUI.getWorkbench().getActiveWorkbenchWindow());
		} catch (WorkbenchException e) {
			e.printStackTrace();
		} 		
	}
	

	@Override
	public void selectionChanged(IAction action, ISelection selection) {		
	}

	@Override
	public void dispose() {		
	}

	@Override
	public void init(IWorkbenchWindow window) {				
	}

}
