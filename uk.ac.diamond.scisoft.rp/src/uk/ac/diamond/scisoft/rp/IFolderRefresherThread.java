package uk.ac.diamond.scisoft.rp;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public class IFolderRefresherThread extends Thread {

	private final IFolder ifolder;
	private boolean runCondition = true;
	private static IFolderRefresherThread CURRENT_THREAD;

	public IFolderRefresherThread(IFolder ifolder) {
		this.ifolder = ifolder;
	}

	@Override
	public void run() {
		if (IFolderRefresherThread.CURRENT_THREAD == null) {
			IFolderRefresherThread.CURRENT_THREAD = this;
		} else {
			if (IFolderRefresherThread.CURRENT_THREAD.isAlive()) {
				IFolderRefresherThread.CURRENT_THREAD.stopThread();
			}
			IFolderRefresherThread.CURRENT_THREAD = this;
		}
		byte i = 0;
		while (i < 4 && runCondition) {
			try {
				Thread.sleep(10000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			refreshIFolder();
			i++;
		}
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		refreshIFolder();
	}

	private void refreshIFolder() {
		if (runCondition) {
			try {
				ifolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

	public void stopThread() {
		runCondition = false;
	}

}
