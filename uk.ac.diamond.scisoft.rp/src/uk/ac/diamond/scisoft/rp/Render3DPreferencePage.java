package uk.ac.diamond.scisoft.rp;

import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.preference.BooleanFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.IntegerFieldEditor;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.preferences.ScopedPreferenceStore;

public class Render3DPreferencePage extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public static final String ID = "uk.ac.diamond.scisoft.rp.Preferences";

	public static final String snapshotResX = "uk.ac.diamond.scisoft.rp.Preferences.snapshotResX";
	public static final String snapshotResY = "uk.ac.diamond.scisoft.rp.Preferences.snapshotResY";
	public static final String snapshotNumberOfSnaps = "uk.ac.diamond.scisoft.rp.Preferences.snapshotNumberOfSnaps";
	public static final String movieResX = "uk.ac.diamond.scisoft.rp.Preferences.movieResX";
	public static final String movieResY = "uk.ac.diamond.scisoft.rp.Preferences.movieResY";
	public static final String movieNumberOfFrames = "uk.ac.diamond.scisoft.rp.Preferences.movieNumberOfFrames";

	public static final String useCenter = "uk.ac.diamond.scisoft.rp.Preferences.rotateAroundCenter";
	public static final String centX = "uk.ac.diamond.scisoft.rp.Preferences.centX";
	public static final String centY = "uk.ac.diamond.scisoft.rp.Preferences.centY";
	public static final String centZ = "uk.ac.diamond.scisoft.rp.Preferences.centZ";

	public static final String openInIm = "uk.ac.diamond.scisoft.rp.Preferences.openInIm";
	public static final String openInIe = "uk.ac.diamond.scisoft.rp.Preferences.openInIe";
	
	public static final String remote = "uk.ac.diamond.scisoft.rp.Preferences.remote";
	public static final String sshNode = "uk.ac.diamond.scisoft.rp.Preferences.sshNode";

	private Combo remoteDropDown;
	private StringFieldEditor sshNodeField;
	
	public Render3DPreferencePage() {
		super(GRID);		
		final IPreferenceStore store = new ScopedPreferenceStore(
				InstanceScope.INSTANCE, "uk.ac.diamond.scisoft.rp");
		setPreferenceStore(store);
		setDescription("Preferences for Render 3D.");
	}

	@Override
	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {

		// snapshot
		new Label(getFieldEditorParent(), SWT.NULL);
		new Label(getFieldEditorParent(), SWT.NULL);

		Label SnapshotLabel = new Label(getFieldEditorParent(), SWT.WRAP);
		SnapshotLabel.setText("Snapshot");
		new Label(getFieldEditorParent(), SWT.NULL);

		final IntegerFieldEditor snapshotResXField = new IntegerFieldEditor(
				snapshotResX, "Resolution x", getFieldEditorParent());
		snapshotResXField.setValidRange(1, Integer.MAX_VALUE);		
		addField(snapshotResXField);
		snapshotResXField
				.getTextControl(getFieldEditorParent())
				.setToolTipText(
						"The width in pixels which the snapshots will be produced in.");

		final IntegerFieldEditor snapshotResYField = new IntegerFieldEditor(
				snapshotResY, "Resolution y", getFieldEditorParent());
		snapshotResYField.setValidRange(1, Integer.MAX_VALUE);
		addField(snapshotResYField);
		snapshotResYField
				.getTextControl(getFieldEditorParent())
				.setToolTipText(
						"The hieght in pixels which the snapshots will be produced in.");

		final IntegerFieldEditor numberOfSnapsField = new IntegerFieldEditor(
				snapshotNumberOfSnaps, "Number of snapshots",
				getFieldEditorParent());
		numberOfSnapsField.setValidRange(0, Integer.MAX_VALUE);
		addField(numberOfSnapsField);
		numberOfSnapsField.getTextControl(getFieldEditorParent())
				.setToolTipText("The number of snapshots to be produced.");

		// movie
		new Label(getFieldEditorParent(), SWT.NULL);
		new Label(getFieldEditorParent(), SWT.NULL);

		Label animLabel = new Label(getFieldEditorParent(), SWT.WRAP);
		animLabel.setText("Animation");
		new Label(getFieldEditorParent(), SWT.NULL);

		final IntegerFieldEditor movieResXField = new IntegerFieldEditor(
				movieResX, "Resolution x", getFieldEditorParent());
		movieResXField.setValidRange(1, Integer.MAX_VALUE);
		addField(movieResXField);
		movieResXField
				.getTextControl(getFieldEditorParent())
				.setToolTipText(
						"The width in pixels which the animations will be produced in.");

		final IntegerFieldEditor movieResYField = new IntegerFieldEditor(
				movieResY, "Resolution y", getFieldEditorParent());
		movieResYField.setValidRange(1, Integer.MAX_VALUE);
		addField(movieResYField);
		movieResYField
				.getTextControl(getFieldEditorParent())
				.setToolTipText(
						"The hieght in pixels which the animations will be produced in.");

		final IntegerFieldEditor numberOfFramesField = new IntegerFieldEditor(
				movieNumberOfFrames, "Number of frames", getFieldEditorParent());
		numberOfFramesField.setValidRange(1, Integer.MAX_VALUE);
		addField(numberOfFramesField);
		numberOfFramesField.getTextControl(getFieldEditorParent())
				.setToolTipText(
						"The number of frames the animations will contain.");

		// volume rotation
		new Label(getFieldEditorParent(), SWT.NULL);
		new Label(getFieldEditorParent(), SWT.NULL);
		Label volumeRotationLabel = new Label(getFieldEditorParent(), SWT.WRAP);
		volumeRotationLabel.setText("Volume rotation");
		new Label(getFieldEditorParent(), SWT.NULL);

		final BooleanFieldEditor useCenterField = new BooleanFieldEditor(
				useCenter,
				"Rotate around object center.\nCenter x, y, z will not be used when this box is checked.",
				getFieldEditorParent());
		addField(useCenterField);

		final StringFieldEditor centXField = new StringFieldEditor(centX,
				"Center x", getFieldEditorParent());
		addField(centXField);
		centXField.getTextControl(getFieldEditorParent()).setToolTipText(
				"The x coordinate point of rotate.");

		final StringFieldEditor centYField = new StringFieldEditor(centY,
				"Center y", getFieldEditorParent());
		addField(centYField);
		centYField.getTextControl(getFieldEditorParent()).setToolTipText(
				"The y coordinate point of rotate.");

		final StringFieldEditor centZField = new StringFieldEditor(centZ,
				"Center z", getFieldEditorParent());
		addField(centZField);
		centZField.getTextControl(getFieldEditorParent()).setToolTipText(
				"The z coordinate point of rotate.");

		// open in image monitor, image explorer
		new Label(getFieldEditorParent(), SWT.NULL);
		final BooleanFieldEditor openInIMField = new BooleanFieldEditor(
				openInIm, "Open output folder in Image Monitor.",
				getFieldEditorParent());
		addField(openInIMField);		
		final BooleanFieldEditor openInIEField = new BooleanFieldEditor(
				openInIe, "Open output folder in Image Explorer.",
				getFieldEditorParent());
		addField(openInIEField);
			
		// remote processing
		new Label(getFieldEditorParent(), SWT.NULL);
		new Label(getFieldEditorParent(), SWT.NULL);

		Label axisLabel = new Label(getFieldEditorParent(), SWT.WRAP);
		axisLabel.setText("Remote processing");
		remoteDropDown = new Combo(getFieldEditorParent(), SWT.DROP_DOWN
				| SWT.WRAP | SWT.READ_ONLY);
		remoteDropDown.add("Local", 0);
		remoteDropDown.add("SSH", 1);
		remoteDropDown.add("QSub", 2);
		remoteDropDown.add("QLogin", 3);
		remoteDropDown.add("QRSH", 4);
		remoteDropDown.select(getPreferenceStore().getInt(remote));
		remoteDropDown.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (remoteDropDown.getSelectionIndex() == 1) {
					sshNodeField.setEnabled(true, getFieldEditorParent());
				} else {
					sshNodeField.setEnabled(false, getFieldEditorParent());
				}
			}
		});

		sshNodeField = new StringFieldEditor(sshNode, "SSH Node",
				getFieldEditorParent());
		if (remoteDropDown.getSelectionIndex() == 1) {
			sshNodeField.setEnabled(true, getFieldEditorParent());
		} else {
			sshNodeField.setEnabled(false, getFieldEditorParent());
		}
		addField(sshNodeField);
	}

	
	@Override
	public boolean performOk() {
		super.performOk();					
		getPreferenceStore().setValue(remote,
				remoteDropDown.getSelectionIndex());		
		return true;
	}
	

}
