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
import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.PVRotationSnapshotTask;

public class PVRotSnapshotComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private final Label const1Label;
	private final Label const2Label;

	private final Text centXText;
	private final Text centYText;
	private final Text centZText;
	private final Text inputDirText;
	private final Text startAngleText;
	private final Text endAngleText;
	private final Text magText;
	private final Text const1Text;
	private final Text const2Text;
	private final Text numberOfSnapshotsText;
	private final Text transXText;
	private final Text transYText;
	private final Text transZText;
	private final Text resXText;
	private final Text resYText;
	private final Text outputLocationText;
	private final Combo axisDropDown;
	private final Combo projectionDropDown;
	private final Combo formatDropDown;
	private final Combo stereoTypeDropDown;
	private final Button orientVisCheckButton;
	private final Button centerVisCheckButton;

	public PVRotSnapshotComposite(Composite parent, int style) {
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

		// center of rotation x
		Label centXLabel = new Label(this, SWT.WRAP);
		centXLabel.setLayoutData(labelData);
		centXLabel.setText("Center x");
		centXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		centXText.setLayoutData(textData);
		centXText.setText("0");
		new Label(this, SWT.NULL);

		// center of rotation y
		Label centYLabel = new Label(this, SWT.WRAP);
		centYLabel.setLayoutData(labelData);
		centYLabel.setText("Center y");
		centYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		centYText.setLayoutData(textData);
		centYText.setText("0");
		new Label(this, SWT.NULL);

		// center of rotation z
		Label centZLabel = new Label(this, SWT.WRAP);
		centZLabel.setLayoutData(labelData);
		centZLabel.setText("Center z");
		centZText = new Text(this, SWT.SINGLE | SWT.BORDER);
		centZText.setLayoutData(textData);
		centZText.setText("0");
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

		// const1
		const1Label = new Label(this, SWT.WRAP);
		const1Label.setLayoutData(labelData);
		const1Label.setText("Constant y rotation");
		const1Text = new Text(this, SWT.SINGLE | SWT.BORDER);
		const1Text.setLayoutData(textData);
		const1Text.setText("0");
		new Label(this, SWT.NULL);

		// const2
		const2Label = new Label(this, SWT.WRAP);
		const2Label.setLayoutData(labelData);
		const2Label.setText("Constant z rotation");
		const2Text = new Text(this, SWT.SINGLE | SWT.BORDER);
		const2Text.setLayoutData(textData);
		const2Text.setText("0");
		new Label(this, SWT.NULL);

		// trans x
		Label transXLabel = new Label(this, SWT.WRAP);
		transXLabel.setLayoutData(labelData);
		transXLabel.setText("Translation x");
		transXText = new Text(this, SWT.SINGLE | SWT.BORDER);
		transXText.setLayoutData(textData);
		transXText.setText("0");
		new Label(this, SWT.NULL);

		// trans y
		Label transYLabel = new Label(this, SWT.WRAP);
		transYLabel.setLayoutData(labelData);
		transYLabel.setText("Translation y");
		transYText = new Text(this, SWT.SINGLE | SWT.BORDER);
		transYText.setLayoutData(textData);
		transYText.setText("0");
		new Label(this, SWT.NULL);

		// trans z
		Label transZLabel = new Label(this, SWT.WRAP);
		transZLabel.setLayoutData(labelData);
		transZLabel.setText("Translation y");
		transZText = new Text(this, SWT.SINGLE | SWT.BORDER);
		transZText.setLayoutData(textData);
		transZText.setText("0");
		new Label(this, SWT.NULL);

		// number snapshots
		Label numberOfFramesLabel = new Label(this, SWT.WRAP);
		numberOfFramesLabel.setLayoutData(labelData);
		numberOfFramesLabel.setText("Number of snapshots");
		numberOfSnapshotsText = new Text(this, SWT.SINGLE | SWT.BORDER);
		numberOfSnapshotsText.setLayoutData(textData);
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
		formatDropDown.add(".png");
		formatDropDown.add(".bmp");
		formatDropDown.add(".tif");
		formatDropDown.add(".ppm");
		formatDropDown.add(".jpg");
		formatDropDown.select(0);
		new Label(this, SWT.NULL);

		// output folder
		Label outLabel = new Label(this, SWT.WRAP);
		outLabel.setLayoutData(labelData);
		outLabel.setText("Output location");
		outputLocationText = new Text(this, SWT.SINGLE | SWT.BORDER);
		outputLocationText.setText("/home/vgb98675/Data_Sampling/test_output/");
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
					LocalTaskHandler th = new LocalTaskHandler();
					// SGETaskHandler th = new SGETaskHandler();
					// SSHTaskHandler th = new SSHTaskHandler(true, "ws049");
					PVRotationSnapshotTask task = getPVRotationSnapshotTask();
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

		if (!FieldVerifyUtils.isNumeric(startAngleText.getText())) {
			startAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(endAngleText.getText())) {
			endAngleText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(centXText.getText())) {
			centXText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(centYText.getText())) {
			centYText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(centZText.getText())) {
			centZText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(const1Text.getText())) {
			const1Text.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(const2Text.getText())) {
			const2Text.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(transXText.getText())) {
			transXText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(transYText.getText())) {
			transYText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNumeric(transZText.getText())) {
			transZText.setBackground(red);
			fieldsValid = false;
		}
		
		if (!FieldVerifyUtils.isPossitiveNumeric(resXText.getText())) {
			resXText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isPossitiveNumeric(resYText.getText())) {
			resYText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils
				.isPositiveInteger(numberOfSnapshotsText.getText())) {
			numberOfSnapshotsText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isNonNegNumeric(magText.getText())) {
			magText.setBackground(red);
			fieldsValid = false;
		}

		if (!FieldVerifyUtils.isOutputValid(outputLocationText.getText())) {
			outputLocationText.setBackground(red);
			fieldsValid = false;
		}

		return fieldsValid;
	}

	private void setFieldBackgroundsAsWhite() {
		inputDirText.setBackground(white);
		startAngleText.setBackground(white);
		endAngleText.setBackground(white);
		centXText.setBackground(white);
		centYText.setBackground(white);
		centZText.setBackground(white);
		const1Text.setBackground(white);
		const2Text.setBackground(white);
		transXText.setBackground(white);
		transYText.setBackground(white);
		transZText.setBackground(white);
		numberOfSnapshotsText.setBackground(white);
		magText.setBackground(white);
		outputLocationText.setBackground(white);
		resXText.setBackground(white);
		resYText.setBackground(white);
	}

	private PVRotationSnapshotTask getPVRotationSnapshotTask() {

		String inputFile = inputDirText.getText();
		String startAngle = startAngleText.getText();
		String endAngle = endAngleText.getText();
		String originX = centXText.getText();
		String originY = centYText.getText();
		String originZ = centZText.getText();
		String const1 = const1Text.getText();
		String const2 = const2Text.getText();
		String axis = axisDropDown.getText();
		String transX = transXText.getText();
		String transY = transYText.getText();
		String transZ = transZText.getText();
		String numOfSnaps = numberOfSnapshotsText.getText();
		String orientVis = "0";
		if (orientVisCheckButton.getSelection()) {
			orientVis = "1";
		}
		String centerVis = "0";
		if (centerVisCheckButton.getSelection()) {
			centerVis = "1";
		}
		String mag = magText.getText();
		String projection = Integer.toString(projectionDropDown
				.indexOf(projectionDropDown.getText()));
		String resX = resXText.getText();
		String resY = resYText.getText();
		String stereoRenderID = Integer.toString(stereoTypeDropDown
				.indexOf(stereoTypeDropDown.getText()));
		String extensionID = Integer.toString(formatDropDown
				.indexOf(formatDropDown.getText()));
		String outputDir = outputLocationText.getText();

		PVRotationSnapshotTask t = new PVRotationSnapshotTask(inputFile,
				startAngle, endAngle, originX, originY, originZ, const1,
				const2, axis, transX, transY, transZ, numOfSnaps, orientVis,
				centerVis, mag, projection, resX, resY, stereoRenderID, extensionID, outputDir);
		return t;
	}

}
