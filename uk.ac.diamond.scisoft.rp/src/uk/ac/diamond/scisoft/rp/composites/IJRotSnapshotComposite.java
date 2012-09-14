package uk.ac.diamond.scisoft.rp.composites;

import java.io.File;
import java.util.ArrayList;

import org.dawb.common.ui.util.EclipseUtils;
import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;
import uk.ac.diamond.scisoft.rp.IFolderRefresherThread;
import uk.ac.diamond.scisoft.rp.ImageExplorerRefresherThread;
import uk.ac.diamond.scisoft.rp.ImageMonitorRefresherThread;
import uk.ac.diamond.scisoft.rp.Render3DPreferencePage;
import uk.ac.diamond.scisoft.rp.api.AvizoImageUtils;
import uk.ac.diamond.scisoft.rp.api.FieldVerifyUtils;
import uk.ac.diamond.scisoft.rp.api.tasks.IJRotationSnapshotTask;
import uk.ac.diamond.scisoft.rp.api.tasks.RenderJob;
import uk.ac.diamond.scisoft.rp.api.tasks.Task;

public class IJRotSnapshotComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private final Label const1Label;
	private final Label const2Label;

	private final Text inputDirText;
	private final Text startAngleText;
	private final Text endAngleText;
	private final Text const1Text;
	private final Text const2Text;
	private final Text outputLocationText;
	private final Combo axisDropDown;
	private final Combo formatDropDown;
	private final Combo renderDropDown;

	private IFolder ifolder;

	public IJRotSnapshotComposite(Composite parent, int style) {
		super(parent, style);

		final Shell shell = parent.getShell();

		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		setLayout(layout);

		// GridData
		GridData dirData = new GridData(GridData.FILL_HORIZONTAL);
		GridData textData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		textData.widthHint = 100;
		GridData labelData = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
		labelData.widthHint = 150;

		// input file
		Label inputLabel = new Label(this, SWT.WRAP);
		inputLabel.setText("Input");
		inputLabel.setLayoutData(labelData);
		inputDirText = new Text(this, SWT.SINGLE | SWT.BORDER);
		inputDirText.setRedraw(false);
		inputDirText.setLayoutData(dirData);
		Button browseInputButton = new Button(this, SWT.WRAP);
		browseInputButton.setText("Browse");
		browseInputButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				FileDialog dialog = new FileDialog(shell, SWT.OPEN);
				File f = new File(inputDirText.getText());
				if (f.isFile()) {
					dialog.setFilterPath(f.getParent());
				} else {
					dialog.setFilterPath(inputDirText.getText());
				}
				String file = dialog.open();
				if (file != null) {
					inputDirText.setText(file);
				}
			}
		});

		// axis
		Label axisLabel = new Label(this, SWT.WRAP);
		axisLabel.setLayoutData(labelData);
		axisLabel.setText("Axis of rotation");
		axisDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP | SWT.READ_ONLY);
		axisDropDown.add("x");
		axisDropDown.add("y");
		axisDropDown.add("z");
		axisDropDown.select(0);
		axisDropDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (axisDropDown.getText().equals("x")) {
					const1Label.setText("Constant y rotation");
					const2Label.setText("Constant z rotation");
				} else if (axisDropDown.getText().equals("y")) {
					const1Label.setText("Constant x rotation");
					const2Label.setText("Constant z rotation");
				} else {
					const1Label.setText("Constant x rotation");
					const2Label.setText("Constant y rotation");
				}

			}
		});
		new Label(this, SWT.NULL);

		// start angle
		Label startAngleLabel = new Label(this, SWT.WRAP);
		startAngleLabel.setLayoutData(labelData);
		startAngleLabel.setText("Start angle");
		startAngleText = new Text(this, SWT.SINGLE | SWT.BORDER);
		startAngleText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// end angle
		Label endAngleLabel = new Label(this, SWT.WRAP);
		endAngleLabel.setLayoutData(labelData);
		endAngleLabel.setText("End angle");
		endAngleText = new Text(this, SWT.SINGLE | SWT.BORDER);
		endAngleText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// const1
		const1Label = new Label(this, SWT.WRAP);
		const1Label.setLayoutData(labelData);
		const1Label.setText("Constant y rotation");
		const1Text = new Text(this, SWT.SINGLE | SWT.BORDER);
		const1Text.setLayoutData(textData);
		;
		new Label(this, SWT.NULL);

		// const2
		const2Label = new Label(this, SWT.WRAP);
		const2Label.setLayoutData(labelData);
		const2Label.setText("Constant z rotation");
		const2Text = new Text(this, SWT.SINGLE | SWT.BORDER);
		const2Text.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// render
		Label renderLabel = new Label(this, SWT.WRAP);
		renderLabel.setLayoutData(labelData);
		renderLabel.setText("Rendering");
		renderDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		renderDropDown.add("Iso surface");
		renderDropDown.add("Mesh");
		renderDropDown.select(0);
		new Label(this, SWT.NULL);

		// format
		Label formatLabel = new Label(this, SWT.WRAP);
		formatLabel.setLayoutData(labelData);
		formatLabel.setText("Output format");
		formatDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		formatDropDown.add(".tif");
		formatDropDown.add(".gif");
		formatDropDown.add(".jpg");
		formatDropDown.add(".bmp");
		formatDropDown.add(".png");
		formatDropDown.add(".pgm");
		formatDropDown.select(0);
		new Label(this, SWT.NULL);

		// output folder
		Label outLabel = new Label(this, SWT.WRAP);
		outLabel.setLayoutData(labelData);
		outLabel.setText("Output location");
		outputLocationText = new Text(this, SWT.SINGLE | SWT.BORDER);
		outputLocationText.setLayoutData(dirData);
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
				refreshIFolder();
			}
		});

		// last row for Generate button
		new Label(this, SWT.NULL);
		Button generateButton = new Button(this, SWT.WRAP);
		generateButton.setText("Generate");
		generateButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {

				if (checkFields()) {
					final IPreferenceStore store = new ScopedPreferenceStore(
							InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");

					Task task = getIJRotationSnapshotTask();
					RenderJob renderJob = new RenderJob(
							"Avizo Rotation Snapshot Job", task, store, ifolder);
					renderJob.schedule();

					if (ifolder != null) {
						new IFolderRefresherThread(ifolder).start();
					}
				
					if (store.getBoolean(Render3DPreferencePage.openInIm)) {
						try {
							ImageMonitorView view = (ImageMonitorView) EclipseUtils
									.getPage().showView(ImageMonitorView.ID);
							File file = new File(outputLocationText.getText());
							view.setDirectoryPath(file.getParent());
							new ImageMonitorRefresherThread(view).start();
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}
					
					if (store.getBoolean(Render3DPreferencePage.openInIe)) {
						try {
							ImageExplorerView ieView = (ImageExplorerView) EclipseUtils
									.getPage().showView(ImageExplorerView.ID);
							if (ieView != null) {
								String folder = new File(outputLocationText
										.getText()).getParent();
								ArrayList<String> createdImages = AvizoImageUtils
										.getFilesInFolderAbsolute(folder);
								ieView.setLocationText(folder);
								ieView.setDirPath(folder);
								ieView.pushSelectedFiles(createdImages);
								ieView.update(
										ImageExplorerView.FOLDER_UPDATE_MARKER,
										createdImages);
								new ImageExplorerRefresherThread(ieView, folder).start();
							}
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
					}							
					
				}

			}
		});

	}

	private boolean checkFields() {
		boolean fieldsValid = true;

		if (FieldVerifyUtils.isFile(inputDirText.getText())) {
			inputDirText.setBackground(white);
		} else {
			inputDirText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNumeric(startAngleText.getText())) {
			startAngleText.setBackground(white);
		} else {
			startAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNumeric(endAngleText.getText())) {
			endAngleText.setBackground(white);
		} else {
			endAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNumeric(const1Text.getText())) {
			const1Text.setBackground(white);
		} else {
			const1Text.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNumeric(const2Text.getText())) {
			const2Text.setBackground(white);
		} else {
			const2Text.setBackground(red);
			fieldsValid = false;
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
		File f = new File(dir);
		String folder = f.getParent();
		outputLocationText.setText(folder);
	}

	private IJRotationSnapshotTask getIJRotationSnapshotTask() {

		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");

		String inputFile = this.inputDirText.getText();
		String axis = this.axisDropDown.getText();
		String startAngle = this.startAngleText.getText();
		String endAngle = this.endAngleText.getText();
		String const1 = this.const1Text.getText();
		String const2 = this.const2Text.getText();
		String centerX = store.getString(Render3DPreferencePage.centX);
		String centerY = store.getString(Render3DPreferencePage.centY);
		String centerZ = store.getString(Render3DPreferencePage.centZ);
		String numOfSnaps = store
				.getString(Render3DPreferencePage.snapshotNumberOfSnaps);
		String rendering = Integer.toString(this.renderDropDown
				.indexOf(formatDropDown.getText()));
		String resX = store.getString(Render3DPreferencePage.snapshotResX);
		String resY = store.getString(Render3DPreferencePage.snapshotResY);
		String extensionID = Integer.toString(this.formatDropDown
				.indexOf(formatDropDown.getText()));
		String outputDir = this.outputLocationText.getText();

		IJRotationSnapshotTask task;

		if (store.getBoolean(Render3DPreferencePage.useCenter)) {
			task = new IJRotationSnapshotTask(inputFile, axis, startAngle,
					endAngle, const1, const2, numOfSnaps, rendering, resX,
					resY, extensionID, outputDir);
		} else {
			task = new IJRotationSnapshotTask(inputFile, axis, startAngle,
					endAngle, const1, const2, centerX, centerY, centerZ,
					numOfSnaps, rendering, resX, resY, extensionID, outputDir);
		}

		return task;

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
