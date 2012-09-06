package uk.ac.diamond.scisoft.rp.actions;

import org.dawb.common.ui.util.EclipseUtils;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import uk.ac.diamond.scisoft.rp.views.RenderImagesMainView;

public class RenderImageOpenAction extends AbstractHandler implements
		IObjectActionDelegate {

	@Override
	public void run(IAction action) {
		doAction();
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {		
	}

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		return doAction();
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart targetPart) {
	}

	private Object doAction() {
	
		final IWorkbenchPage page = PlatformUI.getWorkbench()
				.getActiveWorkbenchWindow().getActivePage();
		final IStructuredSelection sel = (IStructuredSelection) page
				.getSelection();

		Runnable job = new Runnable() {

			@Override
			public void run() {

				if (sel != null) {
					Object[] selObjects = sel.toArray();
					if (selObjects[0] instanceof IFolder) {
						try {
							//open the Render 3D view
							final RenderImagesMainView view = (RenderImagesMainView) EclipseUtils
									.getPage()
									.showView(RenderImagesMainView.ID);

							//refresh the Ifolder 
							IFolder ifolder = (IFolder) selObjects[0];
							try {
								ifolder.refreshLocal(IFolder.DEPTH_INFINITE,
										null);
							} catch (CoreException e) {
								e.printStackTrace();
							}

							String folderDir = ((IFolder) selObjects[0])
									.getLocation().toOSString();

							if (folderDir != null) {
								view.setIFolder(ifolder);
								view.setFolderDir(folderDir);								
							} else {
								System.out.println("folderDir is null");
							}

						} catch (PartInitException e) {
							System.out.println(e.getMessage());
						}

					}
				}
			}
		};
		PlatformUI.getWorkbench().getDisplay().asyncExec(job);

		return Boolean.TRUE;
	}

}
