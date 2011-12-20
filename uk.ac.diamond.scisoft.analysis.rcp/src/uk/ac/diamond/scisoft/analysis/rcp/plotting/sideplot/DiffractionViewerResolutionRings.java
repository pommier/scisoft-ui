/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import java.awt.Color;
import java.util.ArrayList;
import java.util.StringTokenizer;

import javax.vecmath.Vector3d;

import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.CLabel;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.DataSetPlotter;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.roi.ResolutionRingTableViewer;
import uk.ac.diamond.scisoft.analysis.rcp.preference.PreferenceConstants;
import uk.ac.diamond.scisoft.analysis.rcp.util.FloatSpinner;
import uk.ac.diamond.scisoft.analysis.roi.ResolutionRing;
import uk.ac.diamond.scisoft.analysis.roi.ResolutionRingList;

public class DiffractionViewerResolutionRings extends Composite implements SelectionListener, ICellEditorListener {

	private ResolutionRingList resolutionRingList = new ResolutionRingList();

	private ResolutionRingTableViewer tViewer;
	private DiffractionViewer diffView;

	private FloatSpinner resring;
	private Button addRing;
	private Button removeRings;
	private Button addSpacedRings;

	final private Button addIce;

	private FloatSpinner numberEvenSpacedRings;

	private Button beamCentre;

	private Button mask;

	private Button standard;

	private Button toggleRing;

	private final static double[] iceResolution = new double[] { 3.897, 3.669, 3.441, 2.671, 2.249, 2.072, 1.948,
			1.918, 1.883, 1.721 };// angstrom

	DiffractionViewerResolutionRings(Composite parent, int style, DiffractionViewer diffViews) {
		super(parent, style);
		setLayout(new FillLayout(SWT.VERTICAL));
		this.diffView = diffViews;

		Composite controls = new Composite(this, SWT.NONE);
		controls.setLayout(new FillLayout(SWT.VERTICAL));

		final Group ringsGroup = new Group(controls, SWT.NONE);
		ringsGroup.setLayout(new GridLayout(6, false));
		ringsGroup.setText("Resolution Rings");

		{
			GridData gdc = new GridData();
			toggleRing = new Button(ringsGroup, SWT.CHECK);
			toggleRing.setLayoutData(gdc);
			toggleRing.setText("Toggle Rings");
			toggleRing.setToolTipText("Adds Resolution rings to display");
			toggleRing.addSelectionListener(toggleRingListener);

			new CLabel(ringsGroup, SWT.NONE).setText("Resolution:");
			resring = new FloatSpinner(ringsGroup, SWT.BORDER, 3, 1);
			resring.setMinimum(0);
			resring.setMaximum(150);

			GridData gdb = new GridData();
			addRing = new Button(ringsGroup, SWT.PUSH);
			addRing.setLayoutData(gdb);
			addRing.setText("Add Ring");
			addRing.setEnabled(false);
			addRing.addSelectionListener(addRingListener);

			removeRings = new Button(ringsGroup, SWT.PUSH);
			removeRings.setLayoutData(gdb);
			removeRings.setText("Clear");
			removeRings.setEnabled(false);
			removeRings.addSelectionListener(clearRings);

			// OK, this is a hack
			@SuppressWarnings("unused")
			Label blank = new Label(ringsGroup, SWT.NONE);

			addIce = new Button(ringsGroup, SWT.TOGGLE);
			addIce.setEnabled(false);
			addIce.setLayoutData(gdb);
			addIce.setText("Ice rings");
			addIce.addSelectionListener(addIceListener);

			numberEvenSpacedRings = new FloatSpinner(ringsGroup, SWT.NONE);
			numberEvenSpacedRings.setFormat(2, 0);
			numberEvenSpacedRings.setMinimum(2);
			numberEvenSpacedRings.setDouble(6);
			numberEvenSpacedRings.setMaximum(10);

			addSpacedRings = new Button(ringsGroup, SWT.TOGGLE);
			addSpacedRings.setEnabled(false);
			addSpacedRings.setLayoutData(gdb);
			addSpacedRings.setText("Standard Rings");
			addSpacedRings.addSelectionListener(addEvenlySpacedRings);

			beamCentre = new Button(ringsGroup, SWT.TOGGLE);
			beamCentre.setEnabled(false);
			beamCentre.setText("Beam Centre");
			beamCentre.setLayoutData(gdb);
			beamCentre.addSelectionListener(displayBeamCentre);

			mask = new Button(ringsGroup, SWT.TOGGLE);
			mask.setEnabled(false);
			mask.setText("Mask");
			mask.setLayoutData(gdb);
			mask.addSelectionListener(addMask);

			standard = new Button(ringsGroup, SWT.TOGGLE);
			standard.setEnabled(false);
			standard.setText("Calibrant");
			standard.setLayoutData(gdb);
			standard.addSelectionListener(standardSampleListener);
		}
		Composite ringTable = new Composite(this, SWT.NONE);
		ringTable.setLayout(new FillLayout());

		tViewer = new ResolutionRingTableViewer(ringTable, this, this);
		tViewer.setInput(resolutionRingList);

	}

	private SelectionListener toggleRingListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			System.out.println("The astatus of the "+diffView.beamVisable);
			if (!toggleRing.getSelection()) {
				diffView.ringsVisible(false);
				diffView.drawBeamCentre(false);
				addRing.setEnabled(false);
				removeRings.setEnabled(false);
				addIce.setEnabled(false);
				addSpacedRings.setEnabled(false);
				beamCentre.setEnabled(false);
				mask.setEnabled(false);
				standard.setEnabled(false);
				diffView.hideMask();

			} else {
				diffView.beamVisable = beamCentre.getSelection();
				diffView.drawBeamCentre(diffView.beamVisable);
				addRing.setEnabled(true);
				removeRings.setEnabled(true);
				diffView.ringsVisible(true);
				addIce.setEnabled(true);
				addSpacedRings.setEnabled(true);
				beamCentre.setEnabled(true);
				mask.setEnabled(true);
				standard.setEnabled(true);
				redrawExistingRings();
			}
		}
	};

	private SelectionListener clearRings = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			diffView.clearRings();
			resolutionRingList.clear();
			tViewer.refresh();
			addSpacedRings.setSelection(false);
			addIce.setSelection(false);
			beamCentre.setSelection(false);
			diffView.drawBeamCentre(false);
			mask.setSelection(false);
			standard.setSelection(false);
			diffView.removeMask();
		}
	};

	private SelectionListener addRingListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (resring.getDouble() <= 0)
				return;
			resolutionRingList.add(new ResolutionRing(resring.getDouble()));
			tViewer.refresh();
			diffView.updateRings(resolutionRingList);
		}
	};

	private SelectionListener addMask = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (mask.getSelection()) {
				diffView.drawMask();
				((DataSetPlotter) diffView.mainPlotter).getComposite().setFocus();
			}
			if (!mask.getSelection()) {
				diffView.hideMask();
			}
		}
	};
	private SelectionListener addIceListener = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (addIce.getSelection()) {
				for (double res : iceResolution) {
					resolutionRingList.add(new ResolutionRing(res, true, Color.BLUE, true, false));
				}
				tViewer.refresh();
				diffView.updateRings(resolutionRingList);
			}
			if (!addIce.getSelection()) {
				// create new list of non-ice rings and overwrite old one
				ResolutionRingList tempList = new ResolutionRingList();
				if (resolutionRingList != null && resolutionRingList.size() > 1) {
					for (int i = 0; i < resolutionRingList.size(); i++) {
						ResolutionRing temp = resolutionRingList.get(i);
						if (!temp.isIce()) {
							tempList.add(temp);
						}
					}
					resolutionRingList.clear();
					resolutionRingList.addAll(tempList);
					tViewer.refresh();
					diffView.updateRings(resolutionRingList);
				}
			}
		}
	};

	private SelectionListener addEvenlySpacedRings = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			if (addSpacedRings.getSelection()) {
				double lambda = diffView.diffEnv.getWavelength();
				Vector3d longestVector = diffView.detConfig.getLongestVector();
				double step = longestVector.length() / numberEvenSpacedRings.getDouble();
				double d, twoThetaSpacing;
				Vector3d toDetectorVector = new Vector3d();
				Vector3d beamVector = diffView.detConfig.getBeamPosition();
				for (int i = 0; i < numberEvenSpacedRings.getDouble() - 1; i++) {
					// increase the length of the vector by step.
					longestVector.normalize();
					longestVector.scale(step + (step * i));

					toDetectorVector.add(beamVector, longestVector);
					twoThetaSpacing = beamVector.angle(toDetectorVector);
					d = lambda / Math.sin(twoThetaSpacing);
					resolutionRingList.add(new ResolutionRing(d, true, Color.YELLOW, false, true));
				}
				tViewer.refresh();
				diffView.updateRings(resolutionRingList);

			}
			if (!addSpacedRings.getSelection()) {
				ResolutionRingList tempList = new ResolutionRingList();
				if (resolutionRingList != null && resolutionRingList.size() > 1) {
					for (int i = 0; i < resolutionRingList.size(); i++) {
						ResolutionRing temp = resolutionRingList.get(i);
						if (!temp.isEvenSpaced()) {
							tempList.add(temp);
						}
					}
					resolutionRingList.clear();
					resolutionRingList.addAll(tempList);
					tViewer.refresh();
					diffView.updateRings(resolutionRingList);
				}
			}
		}
	};

	private SelectionListener displayBeamCentre = new SelectionAdapter() {
		@Override
		public void widgetSelected(SelectionEvent e) {
			diffView.drawBeamCentre(beamCentre.getSelection());
		}
	};
	private SelectionListener standardSampleListener = new SelectionAdapter() {

		@Override
		public void widgetSelected(SelectionEvent e) {
			if (standard.getSelection()) {
				drawStandardSampleRings();
			}
			if (!standard.getSelection()) {
				removeStandardSampleRings();
			}
		}
	};

	public void drawStandardSampleRings() {
		IPreferenceStore preferenceStore = AnalysisRCPActivator.getDefault().getPreferenceStore();
		@SuppressWarnings("unused")
		String standardName;
		if (preferenceStore.isDefault(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_NAME))
			standardName = preferenceStore.getDefaultString(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_NAME);
		else
			standardName = preferenceStore.getString(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_NAME);

		String standardDistances;
		if (preferenceStore.isDefault(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_DISTANCES))
			standardDistances = preferenceStore
					.getDefaultString(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_DISTANCES);
		else
			standardDistances = preferenceStore.getString(PreferenceConstants.DIFFRACTION_VIEWER_STANDARD_DISTANCES);

		ArrayList<Double> dSpacing = new ArrayList<Double>();
		StringTokenizer st = new StringTokenizer(standardDistances, ",");
		while (st.hasMoreTokens()) {
			String temp = st.nextToken();
			dSpacing.add(Double.valueOf(temp));
		}
		for (double d : dSpacing) {
			resolutionRingList.add(new ResolutionRing(d, true, Color.RED, false, false, true));
		}
		tViewer.refresh();
		diffView.updateRings(resolutionRingList);
	}

	public void removeStandardSampleRings() {
		ResolutionRingList tempList = new ResolutionRingList();
		if (resolutionRingList != null && resolutionRingList.size() > 1) {
			for (int i = 0; i < resolutionRingList.size(); i++) {
				ResolutionRing temp = resolutionRingList.get(i);
				if (!temp.isStandard()) {
					tempList.add(temp);
				}
			}
			resolutionRingList.clear();
			resolutionRingList.addAll(tempList);
			tViewer.refresh();
			diffView.updateRings(resolutionRingList);
		}
	}

	@Override
	public void widgetDefaultSelected(SelectionEvent e) {

	}

	@Override
	public void widgetSelected(SelectionEvent e) {

		if (e.getSource().equals(tViewer.deleteItem)) {
			int doomed = tViewer.getSelectionIndex();
			resolutionRingList.remove(doomed);
			diffView.updateRings(resolutionRingList);
			tViewer.refresh();
		}
	}

	@Override
	public void dispose() {
		if (!addIce.isDisposed())
			addIce.removeSelectionListener(addIceListener);
		if (!addRing.isDisposed())
			addRing.removeSelectionListener(addRingListener);
		if (!addSpacedRings.isDisposed())
			addSpacedRings.removeSelectionListener(addEvenlySpacedRings);
		if (!beamCentre.isDisposed())
			beamCentre.removeSelectionListener(displayBeamCentre);
		if (!removeRings.isDisposed())
			removeRings.removeSelectionListener(clearRings);
	}

	@Override
	public void applyEditorValue() {

	}

	@Override
	public void cancelEditor() {

	}

	@Override
	public void editorValueChanged(boolean oldValidState, boolean newValidState) {

	}

	public boolean isMaskToggled() {
		return mask.getSelection();
	}

	public boolean isBeamCentreToggled() {
		return beamCentre.getSelection();
	}

	public boolean isRingToggled() {
		return toggleRing.getSelection();
	}

	public void redrawExistingRings() {
		if (toggleRing.getSelection() && resolutionRingList != null || resolutionRingList.size() < 0)
			diffView.updateRings(resolutionRingList);
	}

	public void showBeamCentre(final boolean visible) {
		getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				diffView.beamVisable = visible;
			}
		});
	}

}
