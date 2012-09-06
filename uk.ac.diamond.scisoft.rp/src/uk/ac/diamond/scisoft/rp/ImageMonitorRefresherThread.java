package uk.ac.diamond.scisoft.rp;

import org.dawb.common.ui.views.ImageMonitorView;

public class ImageMonitorRefresherThread extends Thread {

	private final ImageMonitorView im;

	public ImageMonitorRefresherThread(ImageMonitorView imageMonitorView) {
		this.im = imageMonitorView;
	}

	public void run() {
		byte i = 0;
		while (i < 6) {
			try {
				Thread.sleep(5000);
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
			im.refreshAll();		
	}

}
