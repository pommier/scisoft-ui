package uk.ac.diamond.scisoft.rp.composites;

import java.io.File;

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

import uk.ac.diamond.scisoft.rp.api.FieldVerifyUtils;
import uk.ac.diamond.scisoft.rp.api.taskHandlers.SSHTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.VPRotationAnimationTask;



public class PVRotAnimComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private final Text startXText;
	private final Text startYText;
	private final Text startZText;
	private final Text inputDirText;
	private final Text fpsText;
	private final Text magText;
	private final Text resXText;
	private final Text resYText;
	private final Text qualityText;
	private final Text numberOfFramesText;
	private final Text outputLocationText;
	private final Combo axisDropDown;
	private final Combo formatDropDown;
	private final Combo stereoTypeDropDown;
	private final Combo projectionDropDown;
	private final Button orientVisCheckButton;
	private final Button centerVisCheckButton;

	public PVRotAnimComposite(Composite parent, int style) {
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
		inputLabel.setLayoutData(labelData);
		inputLabel.setText("Input");
		inputDirText = new Text(this, SWT.SINGLE | SWT.BORDER);
		inputDirText.setRedraw(false);
		inputDirText.setLayoutData(dirData);
		inputDirText.setText("/home/vgb98675/SampleData/ParaViewTutorialData");
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
		new Label(this, SWT.NULL);

		// start x
		Label startXLabel = new Label(this, SWT.WRAP);
		startXLabel.setLayoutData(labelData);
		startXLabel.setText("Start x");
		startXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		startXText.setLayoutData(textData);
		startXText.setText("0");
		new Label(this, SWT.NULL);

		// start y
		Label startYLabel = new Label(this, SWT.WRAP);
		startYLabel.setLayoutData(labelData);
		startYLabel.setText("Start y");
		startYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		startYText.setLayoutData(textData);
		startYText.setText("0");
		new Label(this, SWT.NULL);

		// start z
		Label startZLabel = new Label(this, SWT.WRAP);
		startZLabel.setLayoutData(labelData);
		startZLabel.setText("Start z");
		startZText = new Text(this, SWT.SINGLE | SWT.BORDER);
		startZText.setLayoutData(textData);
		startZText.setText("0");
		new Label(this, SWT.NULL);

		// number of frames
		Label numberOfFramesLabel = new Label(this, SWT.WRAP);
		numberOfFramesLabel.setLayoutData(labelData);
		numberOfFramesLabel.setText("Number of frames");
		numberOfFramesText = new Text(this, SWT.SINGLE | SWT.BORDER);		
		numberOfFramesText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// fps
		Label fpsLabel = new Label(this, SWT.WRAP);
		fpsLabel.setLayoutData(labelData);
		fpsLabel.setText("Frames per second");
		fpsText = new Text(this, SWT.SINGLE | SWT.BORDER);
		fpsText.setText("power of 2 aviods error");
		fpsText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// magnification
		Label magLabel = new Label(this, SWT.WRAP);
		magLabel.setLayoutData(labelData);
		magLabel.setText("Magnification");
		magText = new Text(this, SWT.SINGLE | SWT.BORDER);
		magText.setLayoutData(textData);
		magText.setText("0");
		new Label(this, SWT.NULL);
		
		// projection
		Label projectionLabel = new Label(this, SWT.WRAP);
		projectionLabel.setLayoutData(labelData);
		projectionLabel.setText("Projection");
		projectionDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP | SWT.READ_ONLY);
		projectionDropDown.add("Parallel");
		projectionDropDown.add("Perspective");	
		projectionDropDown.select(0);
		new Label(this, SWT.NULL);

		// resolution x
		Label resXLabel = new Label(this, SWT.WRAP);
		resXLabel.setLayoutData(labelData);
		resXLabel.setText("Resolution x");
		resXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		resXText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// resolution y
		Label resYLabel = new Label(this, SWT.WRAP);
		resYLabel.setLayoutData(labelData);
		resYLabel.setText("Resolution y");
		resYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		resYText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// quality
		Label qualityLabel = new Label(this, SWT.WRAP);
		qualityLabel.setLayoutData(labelData);
		qualityLabel.setText("Magnification");
		qualityText = new Text(this, SWT.SINGLE | SWT.BORDER);
		qualityText.setLayoutData(textData);
		qualityText.setText("1");
		new Label(this, SWT.NULL);

		// orientVis
		Label orientVisLabel = new Label(this, SWT.WRAP);
		orientVisLabel.setLayoutData(labelData);
		orientVisLabel.setText("Show orientation axis");
		orientVisCheckButton = new Button(this, SWT.CHECK);
		new Label(this, SWT.NULL);

		// orientVis
		Label centerVisLabel = new Label(this, SWT.WRAP);
		centerVisLabel.setLayoutData(labelData);
		centerVisLabel.setText("Show center axis");
		centerVisCheckButton = new Button(this, SWT.CHECK);
		new Label(this, SWT.NULL);

		// video type
		Label videoTypeLabel = new Label(this, SWT.WRAP);
		videoTypeLabel.setLayoutData(labelData);
		videoTypeLabel.setText("2D/3D");
		stereoTypeDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		stereoTypeDropDown.add("Monoscopic");
		stereoTypeDropDown.add("Red-Blue");
		stereoTypeDropDown.add("Interlaced");
		stereoTypeDropDown.add("Checkerboard");
		stereoTypeDropDown.select(0);
		new Label(this, SWT.NULL);

		// format
		Label formatLabel = new Label(this, SWT.WRAP);
		formatLabel.setLayoutData(labelData);
		formatLabel.setText("Output format");
		formatDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP
				| SWT.READ_ONLY);
		formatDropDown.add(".avi");
		formatDropDown.add(".jpg");
		formatDropDown.add(".tif");
		formatDropDown.add(".png");
		formatDropDown.select(0);
		formatDropDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (formatDropDown.getText().equals(".avi")) {
					fpsText.setEnabled(true);
					if (!FieldVerifyUtils.isPossitiveNumeric(fpsText.getText())) {
						fpsText.setBackground(red);
					} else {
						fpsText.setBackground(white);
					}
				} else {
					// is an image format
					fpsText.setEnabled(false);
					fpsText.setBackground(white);
				}

			}
		});
		new Label(this, SWT.NULL);

		// output folder
		Label outLabel = new Label(this, SWT.WRAP);
		outLabel.setLayoutData(labelData);
		outLabel.setText("Output location");
		outputLocationText = new Text(this, SWT.SINGLE | SWT.BORDER);
		outputLocationText.setLayoutData(dirData);
		outputLocationText.setText("/home/vgb98675/Data_Sampling/test_output/");
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

				setFieldBackgroundsAsWhite();

				if (checkFields()) {
					//LocalTaskHandler th = new LocalTaskHandler();
					// SGETaskHandler th = new SGETaskHandler();
					 SSHTaskHandler th = new SSHTaskHandler(true, "ws049");
					VPRotationAnimationTask task = getVPRotationAnimationTask();
					th.submitTask(task);
				}

			}
		});

	}

	private boolean checkFields() {

		boolean fieldsValid = true;

		if (!FieldVerifyUtils.isFile(inputDirText.getText())) {
			inputDirText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isPositiveInteger(numberOfFramesText.getText())) {
			numberOfFramesText.setBackground(red);
			fieldsValid = false;
		}

		if (fpsText.isEnabled()) {
			if (!FieldVerifyUtils.isPossitiveNumeric(fpsText.getText())) {
				fpsText.setBackground(red);
				fieldsValid = false;
			}
		}
		
		if (!FieldVerifyUtils.isPossitiveNumeric(resXText.getText())) {
			resXText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isPossitiveNumeric(resYText.getText())) {
			resYText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNonNegNumeric(magText.getText())) {
			magText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNonNegNumeric(qualityText.getText())) {
			qualityText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(startXText.getText())) {
			startXText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(startYText.getText())) {
			startYText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(startZText.getText())) {
			startZText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isOutputValid(outputLocationText.getText())) {
			outputLocationText.setBackground(red);
			fieldsValid = false;
		}

		return fieldsValid;
	}

	private void setFieldBackgroundsAsWhite() {
		startXText.setBackground(white);
		startYText.setBackground(white);
		startZText.setBackground(white);
		inputDirText.setBackground(white);
		fpsText.setBackground(white);
		magText.setBackground(white);
		qualityText.setBackground(white);
		numberOfFramesText.setBackground(white);
		outputLocationText.setBackground(white);
		resXText.setBackground(white);
		resYText.setBackground(white);
	}

	private VPRotationAnimationTask getVPRotationAnimationTask() {

		String startX = startXText.getText();
		String startY = startYText.getText();
		String startZ = startZText.getText();
		String inputDir = inputDirText.getText();
		String fps = fpsText.getText();
		String axis = axisDropDown.getText();
		String mag = magText.getText();
		String projection = Integer.toString(projectionDropDown
				.indexOf(projectionDropDown.getText()));
		String resX = resXText.getText();
		String resY = resYText.getText();
		String quality = qualityText.getText();
		String numberOfFrames = numberOfFramesText.getText();

		String orientVis = "0";
		if (orientVisCheckButton.getSelection()) {
			orientVis = "1";
		}
		String centerVis = "0";
		if (centerVisCheckButton.getSelection()) {
			centerVis = "1";
		}

		String stereoRenderID = Integer.toString(stereoTypeDropDown
				.indexOf(stereoTypeDropDown.getText()));
		String extensionID = Integer.toString(formatDropDown
				.indexOf(formatDropDown.getText()));
		String outputDir = outputLocationText.getText();

		VPRotationAnimationTask t = new VPRotationAnimationTask(axis, inputDir,
				startX, startY, startZ, numberOfFrames, fps, mag, projection, resX, resY, quality,
				orientVis, centerVis, extensionID, stereoRenderID, outputDir);

		return t;
	}

}
