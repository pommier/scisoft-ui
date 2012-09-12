package uk.ac.diamond.scisoft.rp;

import org.dawb.common.ui.views.ImageMonitorView;

public class ImageMonitorRefresherThread extends Thread {

	private final ImageMonitorView im;
	private boolean runCondition = true;
	private static ImageMonitorRefresherThread CURRENT_THREAD;

	public ImageMonitorRefresherThread(ImageMonitorView imageMonitorView) {
		this.im = imageMonitorView;
	}

	@Override
	public void run() {
		if (ImageMonitorRefresherThread.CURRENT_THREAD == null) {
			ImageMonitorRefresherThread.CURRENT_THREAD = this;
		} else {
			if (ImageMonitorRefresherThread.CURRENT_THREAD.isAlive()) {
				ImageMonitorRefresherThread.CURRENT_THREAD.stopThread();
			}
			ImageMonitorRefresherThread.CURRENT_THREAD = this;
		}
		byte i = 0;
		while (i < 6) {
			try {
				Thread.sleep(8000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			refreshIM();
			i++;
		}
		try {
			Thread.sleep(25000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		refreshIM();
	}

	private void refreshIM() {
		if (runCondition) {
			im.refreshAll();
		}
	}

	public void stopThread() {
		runCondition = false;
	}

}
