package uk.ac.diamond.scisoft.rp;

import java.util.ArrayList;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.progress.UIJob;
import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;
import uk.ac.diamond.scisoft.rp.api.AvizoImageUtils;

public class ImageExplorerRefresherThread extends Thread {

	private final ImageExplorerView ieView;
	private final String folder;

	private final UIJob j = new UIJob("Updating Image Explorer") {
		@Override
		public IStatus runInUIThread(IProgressMonitor monitor) {
			ArrayList<String> createdImages = AvizoImageUtils
					.getFilesInFolderAbsolute(folder);
			ieView.setLocationText(folder);
			ieView.setDirPath(folder);
			ieView.pushSelectedFiles(createdImages);
			ieView.update(ImageExplorerView.FOLDER_UPDATE_MARKER, createdImages);
			return Status.OK_STATUS;
		}
	};

	public ImageExplorerRefresherThread(ImageExplorerView ieView, String folder) {
		this.ieView = ieView;
		this.folder = folder;
	}

	@Override
	public void run() {
		byte i = 0;
		while (i < 6) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			refreshIE();
			i++;
		}
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		refreshIE();
	}

	private void refreshIE() {
		j.schedule();
	}

}
