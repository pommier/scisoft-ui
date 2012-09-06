package uk.ac.diamond.scisoft.rp.composites;

import java.io.File;
import java.util.ArrayList;

import org.dawb.common.ui.util.EclipseUtils;
import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;
import uk.ac.diamond.scisoft.rp.IFolderRefresherThread;
import uk.ac.diamond.scisoft.rp.ImageMonitorRefresherThread;
import uk.ac.diamond.scisoft.rp.Render3DPreferencePage;
import uk.ac.diamond.scisoft.rp.api.AvizoImageUtils;
import uk.ac.diamond.scisoft.rp.api.FieldVerifyUtils;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationAnimationTask;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationSnapshotTask;
import uk.ac.diamond.scisoft.rp.api.tasks.RenderJob;
import uk.ac.diamond.scisoft.rp.api.tasks.Task;

public class AvizoRotSnapshotComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private final Button rotateCameraButton;

	private final Text inputDirText;
	private final Text zoomAmountText;
	private final Text cameraXText;
	private final Text cameraYText;
	private final Text cameraZText;
	private final Text startAngleText;
	private final Text endAngleText;
	private final Text alphaScaleText;
	private final Text outputLocationText;
	private final Combo axisDropDown;
	private final Combo renderDetailDropDown;
	private final Combo lightingDropDown;
	private final Combo projectionDropDown;
	private final Button edgeEnhancementCheckButton;
	private final Button useXvfbCheckButton;
	private final Combo formatDropDown;

	private IFolder ifolder;

	public AvizoRotSnapshotComposite(Composite parent, int style) {
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

		// use camera rotate
		new Label(this, SWT.NULL);
		rotateCameraButton = new Button(this, SWT.CHECK);
		rotateCameraButton.setText("Rotate camera (instead of object)");
		rotateCameraButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent theEvent) {
				setAngleText();
			}
		});
		new Label(this, SWT.NULL);

		// input file
		Label inputLabel = new Label(this, SWT.WRAP);
		inputLabel.setLayoutData(labelData);
		inputLabel.setText("Input");
		inputDirText = new Text(this, SWT.SINGLE | SWT.BORDER);
		inputDirText.setLayoutData(dirData);
		inputDirText.setRedraw(false);
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

		// camera x
		Label cameraXLabel = new Label(this, SWT.WRAP);
		cameraXLabel.setLayoutData(labelData);
		cameraXLabel.setText("Camera rotation x");
		cameraXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		cameraXText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// camera y
		Label cameraYLabel = new Label(this, SWT.WRAP);
		cameraYLabel.setLayoutData(labelData);
		cameraYLabel.setText("Camera rotation y");
		cameraYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		cameraYText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// camera z
		Label cameraZLabel = new Label(this, SWT.WRAP);
		cameraZLabel.setLayoutData(labelData);
		cameraZLabel.setText("Camera rotation z");
		cameraZText = new Text(this, SWT.SINGLE | SWT.BORDER);
		cameraZText.setLayoutData(textData);
		new Label(this, SWT.NULL);

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
				setAngleText();
			}
		});
		new Label(this, SWT.NULL);

		// zoom amount
		Label zoomLabel = new Label(this, SWT.WRAP);
		zoomLabel.setLayoutData(labelData);
		zoomLabel.setText("Zoom amount");
		zoomAmountText = new Text(this, SWT.SINGLE | SWT.BORDER);
		zoomAmountText.setLayoutData(textData);
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

		// renderDetail
		Label renderDetailLabel = new Label(this, SWT.WRAP);
		renderDetailLabel.setLayoutData(labelData);
		renderDetailLabel.setText("Render Detail");
		renderDetailDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		renderDetailDropDown.add("low");
		renderDetailDropDown.add("normal");
		renderDetailDropDown.add("high");
		renderDetailDropDown.select(1);
		new Label(this, SWT.NULL);

		// lighting
		Label lightingLabel = new Label(this, SWT.WRAP);
		lightingLabel.setLayoutData(labelData);
		lightingLabel.setText("Lighting affect");
		lightingDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		lightingDropDown.add("none");
		lightingDropDown.add("diffuse");
		lightingDropDown.add("diffuse with specular");
		lightingDropDown.select(1);
		new Label(this, SWT.NULL);

		// edge enhancement
		Label edgeEnhancementLabel = new Label(this, SWT.WRAP);
		edgeEnhancementLabel.setLayoutData(labelData);
		edgeEnhancementLabel.setText("Edge enhancement");
		edgeEnhancementCheckButton = new Button(this, SWT.CHECK);
		new Label(this, SWT.NULL);

		// use Xvfb
		Label useXvfbLabel = new Label(this, SWT.WRAP);
		useXvfbLabel.setLayoutData(labelData);
		useXvfbLabel.setText("Use Xvfb");
		useXvfbCheckButton = new Button(this, SWT.CHECK);
		new Label(this, SWT.NULL);

		// alpha scale
		Label alphaScaleLabel = new Label(this, SWT.WRAP);
		alphaScaleLabel.setLayoutData(labelData);
		alphaScaleLabel.setText("Alpha scale value");
		alphaScaleText = new Text(this, SWT.SINGLE | SWT.BORDER);
		alphaScaleText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// projection
		Label projectionLabel = new Label(this, SWT.WRAP);
		projectionLabel.setLayoutData(labelData);
		projectionLabel.setText("Projection");
		projectionDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		projectionDropDown.add("Parallel");
		projectionDropDown.add("Perspective");
		projectionDropDown.select(0);
		new Label(this, SWT.NULL);

		// format
		Label formatLabel = new Label(this, SWT.WRAP);
		formatLabel.setLayoutData(labelData);
		formatLabel.setText("Output format");
		formatDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		formatDropDown.add(".jpg");
		formatDropDown.add(".tif");
		formatDropDown.add(".png");
		formatDropDown.add(".bmp");
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

					Task task = getAvizoRotationSnapshotTask();
					RenderJob renderJob = new RenderJob(
							"Avizo Rotation Snapshot Job", task, store, ifolder);
					renderJob.schedule();

					if (ifolder != null) {
						new IFolderRefresherThread(ifolder).start();
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
							}
						} catch (PartInitException e1) {
							e1.printStackTrace();
						}
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

		if (FieldVerifyUtils.isNumeric(zoomAmountText.getText())) {
			zoomAmountText.setBackground(white);
		} else {
			zoomAmountText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(startAngleText.getText())) {
			startAngleText.setBackground(white);
		} else {
			startAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(endAngleText.getText())) {
			endAngleText.setBackground(white);
		} else {
			endAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(alphaScaleText.getText())) {
			alphaScaleText.setBackground(white);
		} else {
			alphaScaleText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNonNegNumeric(endAngleText.getText())
				&& FieldVerifyUtils.isNonNegNumeric(startAngleText.getText())) {
			int start = Integer.parseInt(startAngleText.getText());
			int end = Integer.parseInt(startAngleText.getText());
			if (start > end) {
				startAngleText.setBackground(red);
				startAngleText.setBackground(red);
				fieldsValid = false;
			}
		}

		if (cameraXText.isEnabled()) {
			if (FieldVerifyUtils.isNumeric(cameraXText.getText())) {
				cameraXText.setBackground(white);
			} else {
				cameraXText.setBackground(red);
				fieldsValid = false;
			}
		}

		if (cameraYText.isEnabled()) {
			if (FieldVerifyUtils.isNumeric(cameraYText.getText())) {
				cameraYText.setBackground(white);
			} else {
				cameraYText.setBackground(red);
				fieldsValid = false;
			}
		}

		if (cameraZText.isEnabled()) {
			if (FieldVerifyUtils.isNumeric(cameraZText.getText())) {
				cameraZText.setBackground(white);
			} else {
				cameraZText.setBackground(red);
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

	private void setAngleText() {
		this.cameraZText.setEnabled(true);
		this.cameraXText.setEnabled(true);
		this.cameraYText.setEnabled(true);
		if (this.rotateCameraButton.getSelection()) {
			if (this.axisDropDown.getText().equals("x")) {
				this.cameraXText.setEnabled(false);
			}
			if (this.axisDropDown.getText().equals("y")) {
				this.cameraYText.setEnabled(false);
			}
			if (this.axisDropDown.getText().equals("z")) {
				this.cameraZText.setEnabled(false);
			}
		} else {
			this.cameraXText.setEnabled(true);
			this.cameraYText.setEnabled(true);
			this.cameraZText.setEnabled(true);
		}
	}

	public void setDirectory(String dir) {
		inputDirText.setText(dir);
		File f = new File(dir);
		String folder = f.getParent();
		outputLocationText.setText(folder);
	}

	private AvizoRotationSnapshotTask getAvizoRotationSnapshotTask() {

		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");

		boolean useXvfb = this.useXvfbCheckButton.getSelection();

		String dataFileDir = this.inputDirText.getText();
		String zoomAmount = this.zoomAmountText.getText();
		String cameraX = cameraXText.getText();
		String cameraY = cameraYText.getText();
		String cameraZ = cameraZText.getText();
		String startAngle = this.startAngleText.getText();
		String endAngle = this.endAngleText.getText();
		String axis = this.axisDropDown.getText();
		String const1 = null;
		String const2 = null;
		if (this.rotateCameraButton.getSelection()) {
			if (axis.equals("x")) {
				const1 = cameraY;
				const2 = cameraZ;
			}
			if (axis.equals("y")) {
				const1 = cameraZ;
				const2 = cameraX;
			}
			if (axis.equals("z")) {
				const1 = cameraX;
				const2 = cameraY;
			}
		}
		String centerX = store.getString(Render3DPreferencePage.centX);
		String centerY = store.getString(Render3DPreferencePage.centY);
		String centerZ = store.getString(Render3DPreferencePage.centZ);
		String renderDetail = Integer.toString(renderDetailDropDown
				.indexOf(renderDetailDropDown.getText()));
		String lighting = Integer.toString(lightingDropDown
				.indexOf(lightingDropDown.getText()));
		String edgeAdhance = "0";
		if (edgeEnhancementCheckButton.getSelection()) {
			edgeAdhance = "1";
		}
		String alphaScale = this.alphaScaleText.getText();
		String numberOfSnapshots = store
				.getString(Render3DPreferencePage.snapshotNumberOfSnaps);
		String resX = store.getString(Render3DPreferencePage.snapshotResX);
		String resY = store.getString(Render3DPreferencePage.snapshotResY);
		String outputFile = outputLocationText.getText();
		String projection = Integer.toString(projectionDropDown
				.indexOf(projectionDropDown.getText()));
		String format = this.formatDropDown.getText();

		AvizoRotationSnapshotTask task;
		if (this.rotateCameraButton.getSelection()) {
			task = new AvizoRotationSnapshotTask(useXvfb, dataFileDir,
					zoomAmount, const1, const2, startAngle, endAngle, axis,
					renderDetail, lighting, edgeAdhance, alphaScale,
					numberOfSnapshots, outputFile, format, resX, resY,
					projection);
		} else {
			if (store.getBoolean(Render3DPreferencePage.useCenter)) {
				task = new AvizoRotationSnapshotTask(useXvfb, dataFileDir,
						zoomAmount, cameraX, cameraY, cameraZ, startAngle,
						endAngle, axis, renderDetail, lighting, edgeAdhance,
						alphaScale, numberOfSnapshots, outputFile, format,
						resX, resY, projection);
			} else {
				task = new AvizoRotationSnapshotTask(useXvfb, dataFileDir,
						zoomAmount, cameraX, cameraY, cameraZ, startAngle,
						endAngle, axis, centerX, centerY, centerZ,
						renderDetail, lighting, edgeAdhance, alphaScale,
						numberOfSnapshots, outputFile, format, resX, resY,
						projection);
			}
		}
		return task;
	}

	public void setIFolder(IFolder ifolder) {
		this.ifolder = ifolder;
	}

	private void refreshIFolder() {
		if (ifolder != null) {
			try {
				ifolder.refreshLocal(IFolder.DEPTH_INFINITE, null);
			} catch (CoreException e) {
				e.printStackTrace();
			}
		}
	}

}
