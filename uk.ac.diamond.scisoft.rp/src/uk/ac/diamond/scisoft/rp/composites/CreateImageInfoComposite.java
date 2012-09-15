package uk.ac.diamond.scisoft.rp.composites;

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import uk.ac.diamond.scisoft.rp.api.AvizoImageUtils;
import uk.ac.diamond.scisoft.rp.api.FieldVerifyUtils;

public class CreateImageInfoComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private Text baseNameText;
	private final Text pixelSizeXText;
	private final Text pixelSizeYText;
	private final Text zPositionIncText;
	private final Text stackSizeText;
	private final Text imageExtensionText;
	private final Text outputLocationText;
	private final Button searchFolderButton;
	private final Text folderToSearchText;
	private final Button browseFolderToSearchButton;

	private IFolder ifolder;

	public CreateImageInfoComposite(Composite parent, int style) {
		super(parent, style);

		final Shell shell = parent.getShell();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		setLayout(layout);

		// GridData
		GridData dirData = new GridData(GridData.FILL_HORIZONTAL);
		GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		textData.widthHint = 90;
		GridData labelData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelData.widthHint = 145;

		// prefix
		Label baseNameLabel = new Label(this, SWT.WRAP);
		baseNameLabel.setLayoutData(labelData);
		baseNameLabel.setText("Prefix");
		baseNameText = new Text(this, SWT.SINGLE | SWT.BORDER);
		baseNameText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// image extension
		Label imageExtensionLabel = new Label(this, SWT.WRAP);
		imageExtensionLabel.setLayoutData(labelData);
		imageExtensionLabel.setText("Image extension");
		imageExtensionText = new Text(this, SWT.SINGLE | SWT.BORDER);
		imageExtensionText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// pixel size x
		Label pixelSizeXLabel = new Label(this, SWT.WRAP);
		pixelSizeXLabel.setLayoutData(labelData);
		pixelSizeXLabel.setText("Pixel size x");
		pixelSizeXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		pixelSizeXText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// pixel size y
		Label pixelSizeYLabel = new Label(this, SWT.WRAP);
		pixelSizeYLabel.setLayoutData(labelData);
		pixelSizeYLabel.setText("Pixel size y");
		pixelSizeYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		pixelSizeYText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// pixel size z
		Label zPositionIncLabel = new Label(this, SWT.WRAP);
		zPositionIncLabel.setLayoutData(labelData);
		zPositionIncLabel.setText("Pixel size z");
		zPositionIncText = new Text(this, SWT.SINGLE | SWT.BORDER);
		zPositionIncText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// search folder for images with prefix and extension
		searchFolderButton = new Button(this, SWT.CHECK);
		searchFolderButton.setText("Search folder");
		searchFolderButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent theEvent) {
				if (((Button) (theEvent.widget)).getSelection()) {
					stackSizeText.setBackground(white);
					stackSizeText.setEnabled(false);
					folderToSearchText.setEnabled(true);
					browseFolderToSearchButton.setEnabled(true);
				} else {
					folderToSearchText.setEnabled(false);
					browseFolderToSearchButton.setEnabled(false);
					stackSizeText.setEnabled(true);
					if (FieldVerifyUtils.isPositiveInteger(stackSizeText.getText())) {
						stackSizeText.setBackground(white);
					} else {
						stackSizeText.setBackground(red);
					}
				}
			}
		});
		new Label(this, SWT.NULL);
		new Label(this, SWT.NULL);

		// folder to search
		Label folderToSearchLabel = new Label(this, SWT.WRAP);
		folderToSearchLabel.setLayoutData(labelData);
		folderToSearchLabel.setText("Folder to search");
		folderToSearchText = new Text(this, SWT.SINGLE | SWT.BORDER);
		folderToSearchText.setLayoutData(dirData);
		folderToSearchText.setEnabled(false);
		browseFolderToSearchButton = new Button(this, SWT.WRAP);
		browseFolderToSearchButton.setText("Browse");
		browseFolderToSearchButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
				if (FieldVerifyUtils.isFolder(folderToSearchText.getText())) {
					dialog.setFilterPath(folderToSearchText.getText());
				} else {
					File file = new File(folderToSearchText.getText());
					dialog.setFilterPath(file.getParent());
				}
				String file = dialog.open();
				if (file != null) {
					folderToSearchText.setText(file);
				}
			}
		});
		browseFolderToSearchButton.setEnabled(false);

		// stack size
		Label stackSizeLabel = new Label(this, SWT.WRAP);
		stackSizeLabel.setLayoutData(labelData);
		stackSizeLabel.setText("Stack size");
		stackSizeText = new Text(this, SWT.SINGLE | SWT.BORDER);
		stackSizeText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// output folder
		Label outLabel = new Label(this, SWT.WRAP);
		outLabel.setLayoutData(labelData);
		outLabel.setText("Output location");
		outputLocationText = new Text(this, SWT.SINGLE | SWT.BORDER);
		outputLocationText.setLayoutData(dirData);
		outputLocationText.setText("/home/vgb98675/test_output/");
		Button browseOutputButton = new Button(this, SWT.WRAP);
		browseOutputButton.setText("Browse");
		browseOutputButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.SAVE);

				if (FieldVerifyUtils.isFolder(outputLocationText.getText())) {
					dialog.setFilterPath(outputLocationText.getText());
				} else {
					File file = new File(outputLocationText.getText());
					dialog.setFilterPath(file.getParent());
				}

				String file = dialog.open();
				if (file != null) {
					outputLocationText.setText(file);
				}
			}
		});

		// last row for Generate button
		new Label(this, SWT.NULL);
		Button generateButton = new Button(this, SWT.WRAP);
		generateButton.setText("Generate");
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (checkFields()) {

					Job renderJob = new Job("Creating image .info file") {

						String baseName = baseNameText.getText();
						int pixelSizeX = Integer.parseInt(pixelSizeXText.getText());
						int pixelSizeY = Integer.parseInt(pixelSizeYText.getText());
						int zPositionInc = Integer.parseInt(zPositionIncText.getText());
						String imageExtension = imageExtensionText.getText();
						String location = outputLocationText.getText();
						boolean searchFolder = searchFolderButton.getSelection();
						File folder = new File(folderToSearchText.getText());

						int stackSize = getIntegerFromString(stackSizeText.getText());

						@Override
						protected IStatus run(IProgressMonitor monitor) {
							if (searchFolder) {
								AvizoImageUtils.writeStackedImageInfoFile(folder, baseName, imageExtension, pixelSizeX,
										pixelSizeY, zPositionInc, location);
							} else {
								AvizoImageUtils.writeStackedImageInfoFile(baseName, pixelSizeX, pixelSizeY,
										zPositionInc, stackSize, imageExtension, location);
							}
							refreshIFolder();
							return Status.OK_STATUS;
						}
					};
					renderJob.schedule();
				}

			}
		});
	}

	private boolean checkFields() {

		boolean fieldsValid = true;

		if (FieldVerifyUtils.isNonNegNumeric(pixelSizeXText.getText())) {
			pixelSizeXText.setBackground(white);
		} else {
			pixelSizeXText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(pixelSizeYText.getText())) {
			pixelSizeYText.setBackground(white);
		} else {
			pixelSizeYText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(zPositionIncText.getText())) {
			zPositionIncText.setBackground(white);
		} else {
			zPositionIncText.setBackground(red);
			fieldsValid = false;
		}

		if (!searchFolderButton.getSelection()) {
			if (FieldVerifyUtils.isPositiveInteger(stackSizeText.getText())) {
				stackSizeText.setBackground(white);
			} else {
				stackSizeText.setBackground(red);
				fieldsValid = false;
			}
		}

		if (searchFolderButton.getSelection()) {
			if (FieldVerifyUtils.isFolder(folderToSearchText.getText())) {
				folderToSearchText.setBackground(white);
			} else {
				folderToSearchText.setBackground(red);
				fieldsValid = false;
			}
		}

		if (outputLocationText.getText().equals("")) {
			outputLocationText.setBackground(red);
			fieldsValid = false;
		} else {
			if (FieldVerifyUtils.isOutputValid(outputLocationText.getText())) {
				outputLocationText.setBackground(white);
			} else {
				outputLocationText.setBackground(red);
				fieldsValid = false;
			}
		}
		return fieldsValid;
	}

	public void setDirectory(String dir) {
		folderToSearchText.setText(dir);
		outputLocationText.setText(dir + "/");
		searchFolderButton.setSelection(true);
		stackSizeText.setEnabled(false);
		folderToSearchText.setEnabled(true);
	}

	private static int getIntegerFromString(String s) {
		if (s.equals("")) {
			return 0;
		}
		int result = 0;
		try {
			result = Integer.parseInt(s);
		} catch (Exception e) {
			return result;
		}
		return result;
	}

	public void setIFolder(IFolder ifolder) {
		this.ifolder = ifolder;
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
