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

package uk.ac.diamond.scisoft.analysis.rcp.preference;

import java.util.LinkedList;
import java.util.List;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.layout.GridLayoutFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * An extension to MessageDialog that adds optional text boxes in the {@link #createCustomArea(Composite)}
 */
public class ChangeInteractiveConsoleDialog extends MessageDialog {

	/**
	 * @see MessageDialog#MessageDialog(Shell, String, Image, String, int, String[], int)
	 */
	public ChangeInteractiveConsoleDialog(Shell parentShell, String dialogTitle, Image dialogTitleImage,
			String dialogMessage, int dialogImageType, String[] dialogButtonLabels, int defaultIndex) {
		super(parentShell, dialogTitle, dialogTitleImage, dialogMessage, dialogImageType, dialogButtonLabels,
				defaultIndex);
	}

	/**
	 * Exposed for unit tests only
	 */
	/*package*/ static class BoxWithTitle {
		String title, contents;

		public BoxWithTitle(String title, String contents) {
			this.title = title;
			this.contents = contents;
		}
		
		@Override
		public String toString() {
			return title + "\n" + contents;
		}
	}
	
	/**
	 * Exposed for unit tests only
	 */
	/*package*/ List<BoxWithTitle> getBoxes() {
		return boxes;
	}

	private List<BoxWithTitle> boxes = new LinkedList<BoxWithTitle>();

	/**
	 * Call as many times as desired to add a read-only text with the given title.
	 * 
	 * @param title
	 *            Title of the box
	 * @param contents
	 *            Contents, read-only, can be <code>null</code> to only have a label
	 */
	public void addTextBox(String title, String contents) {
		boxes.add(new BoxWithTitle(title, contents));
	}

	@Override
	protected Control createCustomArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayoutFactory.swtDefaults().applyTo(composite);
		
		
		for (BoxWithTitle box : boxes) {
			Label label = new Label(composite, SWT.NONE);
			label.setText(box.title);
			if (box.contents != null) {
				Text text = new Text(composite, SWT.READ_ONLY | SWT.MULTI | SWT.BORDER);
				text.setText(box.contents);
			}
		}

		return composite;
	}
}
