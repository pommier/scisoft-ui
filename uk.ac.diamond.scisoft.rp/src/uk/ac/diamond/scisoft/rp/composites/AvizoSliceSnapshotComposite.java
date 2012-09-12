package uk.ac.diamond.scisoft.rp.composites;

import java.io.File;
import org.dawb.common.ui.util.EclipseUtils;
import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.core.resources.IFolder;
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
import uk.ac.diamond.scisoft.rp.api.taskHandlers.LocalTaskHandler;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationAnimationTask;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoRotationSnapshotTask;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoSliceAnimationTask;
import uk.ac.diamond.scisoft.rp.api.tasks.AvizoSliceSnapshotTask;
import uk.ac.diamond.scisoft.rp.api.tasks.RenderJob;
import uk.ac.diamond.scisoft.rp.api.tasks.Task;

public class AvizoSliceSnapshotComposite extends Composite {

	private final Device device = Display.getCurrent();
	private final Color red = new Color(device, 255, 120, 120);
	private final Color white = new Color(device, 255, 255, 255);

	private final Text inputDirText;
	private final Combo axisDropDown;
	private Text zoomAmountText;
	private final Text startSliceText;
	private final Text endSliceText;
	private final Text outputLocationText;
	private final Combo formatDropDown;
	private final Button useXvfbCheckButton;
	private final Combo projectionDropDown;
	private final Button useMaxSliceButton;

	private IFolder ifolder;

	public AvizoSliceSnapshotComposite(Composite parent, int style) {
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

		// axis
		Label axisLabel = new Label(this, SWT.WRAP);
		axisLabel.setLayoutData(labelData);
		axisLabel.setText("Slice axis");
		axisDropDown = new Combo(this, SWT.DROP_DOWN | SWT.WRAP | SWT.READ_ONLY);
		axisDropDown.add("xy");
		axisDropDown.add("xz");
		axisDropDown.add("yz");
		axisDropDown.select(0);
		new Label(this, SWT.NULL);

		// zoom amount
		Label zoomLabel = new Label(this, SWT.WRAP);
		zoomLabel.setLayoutData(labelData);
		zoomLabel.setText("Zoom amount");
		zoomAmountText = new Text(this, SWT.SINGLE | SWT.BORDER);
		zoomAmountText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// start slice
		Label startSliceLabel = new Label(this, SWT.WRAP);
		startSliceLabel.setLayoutData(labelData);
		startSliceLabel.setText("Start slice");
		startSliceText = new Text(this, SWT.SINGLE | SWT.BORDER);
		startSliceText.setLayoutData(textData);
		new Label(this, SWT.NULL);

		// end slice
		Label endSliceLabel = new Label(this, SWT.WRAP);
		endSliceLabel.setLayoutData(labelData);
		endSliceLabel.setText("End slice");
		endSliceText = new Text(this, SWT.SINGLE | SWT.BORDER);
		endSliceText.setLayoutData(textData);
		endSliceText.setEnabled(false);

		// max slice button
		useMaxSliceButton = new Button(this, SWT.CHECK);
		useMaxSliceButton.setText("Use max slice");
		useMaxSliceButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent theEvent) {
				if (((Button) (theEvent.widget)).getSelection()) {
					endSliceText.setBackground(white);
					endSliceText.setEnabled(false);
				} else {
					endSliceText.setEnabled(true);
					if (!FieldVerifyUtils.isPositiveInteger(endSliceText
							.getText())) {
						endSliceText.setBackground(red);
					}
				}
			}
		});
		useMaxSliceButton.setSelection(true);

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

		// use Xvfb
		Label useXvfbLabel = new Label(this, SWT.WRAP);
		useXvfbLabel.setLayoutData(labelData);
		useXvfbLabel.setText("Use Xvfb");
		useXvfbCheckButton = new Button(this, SWT.CHECK);
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

					Task task = getAvizoSliceSnapshotTask();
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
								new ImageExplorerRefresherThread(ieView, folder)
										.start();
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

		if (FieldVerifyUtils.isNumeric(zoomAmountText.getText())) {
			zoomAmountText.setBackground(white);
		} else {
			zoomAmountText.setBackground(red);
			fieldsValid = false;
		}

		if (FieldVerifyUtils.isNumeric(startSliceText.getText())) {
			startSliceText.setBackground(white);
		} else {
			startSliceText.setBackground(red);
			fieldsValid = false;
		}

		if (!this.useMaxSliceButton.getSelection()) {
			if (FieldVerifyUtils.isPositiveInteger(endSliceText.getText())) {
				endSliceText.setBackground(white);
			} else {
				endSliceText.setBackground(red);
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
		inputDirText.setText(dir);
		File f = new File(dir);
		String folder = f.getParent();
		outputLocationText.setText(folder);
	}

	private AvizoSliceSnapshotTask getAvizoSliceSnapshotTask() {

		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");

		boolean useXvfb = this.useXvfbCheckButton.getSelection();

		String inputDir = this.inputDirText.getText();
		String axis = Integer.toString(this.axisDropDown
				.indexOf(this.axisDropDown.getText()));
		String zoomAmount = this.zoomAmountText.getText();
		String startSlice = this.startSliceText.getText();
		String endSlice = this.endSliceText.getText();
		String numberOfSnaps = store
				.getString(Render3DPreferencePage.snapshotNumberOfSnaps);
		String outputLocation = this.outputLocationText.getText();
		String format = this.formatDropDown.getText();
		String resX = store.getString(Render3DPreferencePage.snapshotResX);
		String resY = store.getString(Render3DPreferencePage.snapshotResY);
		String projection = Integer.toString(this.projectionDropDown
				.indexOf(this.projectionDropDown.getText()));

		AvizoSliceSnapshotTask task;
		if (this.useMaxSliceButton.getSelection()) {
			task = new AvizoSliceSnapshotTask(useXvfb, inputDir, axis,
					zoomAmount, startSlice, numberOfSnaps, outputLocation,
					format, resX, resY, projection);
		} else {
			task = new AvizoSliceSnapshotTask(useXvfb, inputDir, axis,
					zoomAmount, startSlice, endSlice, numberOfSnaps,
					outputLocation, format, resX, resY, projection);
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
