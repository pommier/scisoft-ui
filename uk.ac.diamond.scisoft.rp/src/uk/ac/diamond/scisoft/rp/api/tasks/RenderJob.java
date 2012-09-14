package uk.ac.diamond.scisoft.rp.api.tasks;

import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.ui.PlatformUI;
import uk.ac.diamond.scisoft.rp.Render3DPreferencePage;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.QLoginTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.QSubTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.QrshTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.SSHTaskHandler;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.TaskHandler;

public class RenderJob extends Job {

	private Task task;
	private IPreferenceStore store;
	private IFolder ifolder;

	public RenderJob(String name) {
		super(name);
	}

	public RenderJob(String name, Task task, IPreferenceStore store, IFolder ifolder) {
		super(name);
		this.task = task;
		this.store = store;
		this.ifolder = ifolder;
	}

	@Override
	protected IStatus run(IProgressMonitor monitor) {
		TaskHandler th;

		switch (store.getInt(Render3DPreferencePage.remote)) {
		case 0:
			th = new LocalTaskHandler();
			break;
		case 1:
			th = new SSHTaskHandler(true, store.getString(Render3DPreferencePage.sshNode));
			break;
		case 2:
			th = new QSubTaskHandler();
			break;
		case 3:
			th = new QLoginTaskHandler();
			break;
		case 4:
			th = new QrshTaskHandler();
			break;
		default:
			th = new LocalTaskHandler();
			break;
		}

		th.submitTask(task);
		refreshIFolder();
		refreshIM();
		return Status.OK_STATUS;
	}

	private void refreshIM() {
		ImageMonitorView imageMonitorView = null;
		try {
			imageMonitorView = (ImageMonitorView) PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage()
					.findView(ImageMonitorView.ID);
		} catch (NullPointerException e) {
		}
		if (imageMonitorView != null) {
			imageMonitorView.refreshAll();
		}
	}

	private void refreshIFolder() {
		if (ifolder != null) {
			try {
				ifolder.refreshLocal(IResource.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
