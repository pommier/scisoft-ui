/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.sda.polling.views;

import java.util.HashMap;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.progress.UIJob;

public class URLPollView extends ViewPart {

	private static HashMap<String, Browser> browsers = new HashMap<String, Browser>();
	private Browser browser = null;
	
	public URLPollView() {
	}

	@Override
	public void createPartControl(Composite parent) {
		this.getTitle();
		browser = new Browser(parent, SWT.NONE);
		browsers.put(this.getTitle(), browser);
	}

	@Override
	public void setFocus() {
		browser.setFocus();
	}

	public static void setURL(final String URL, final String browserName) {
		UIJob uiJob = new UIJob("Updating URL") {
			
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				browsers.get(browserName).setUrl(URL);
				return Status.OK_STATUS;
			}
		};
		
		uiJob.schedule();
		
	}
	
	
}
