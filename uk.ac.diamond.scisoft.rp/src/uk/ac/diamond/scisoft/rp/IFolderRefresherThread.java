package uk.ac.diamond.scisoft.rp;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;

public class IFolderRefresherThread extends Thread {

	private final IFolder ifolder;

	public IFolderRefresherThread(IFolder ifolder) {
		this.ifolder = ifolder;
	}

	public void run() {
		byte i = 0;
		while (i < 4) {
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
		try {
			ifolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

}
