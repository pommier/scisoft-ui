/*-
 * Copyright © 2010 Diamond Light Source Ltd.
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

package uk.ac.diamond.scisoft.analysis.rcp.preference;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.GlobalColourMaps;

public class PlotViewPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo cmbColourMap;
	private Combo cmbColourScale;
	private Combo cmbCameraPerspective;
	private Button chkExpertMode;
	private Button chkAutoHisto;
	private Button chkScrollbars;

	public PlotViewPreferencePage() {
	}

	public PlotViewPreferencePage(String title) {
		super(title);
	}

	public PlotViewPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, false));
		GridData gdc = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(gdc);
		Group plotMulti1DGroup = new Group(comp, SWT.NONE);
		plotMulti1DGroup.setText("Plot 1DStack");
		plotMulti1DGroup.setLayout(new GridLayout(2, false));
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		plotMulti1DGroup.setLayoutData(gd);
		Label lblCameraType = new Label(plotMulti1DGroup, SWT.LEFT);
		lblCameraType.setText("Camera projection: ");
		cmbCameraPerspective = new Combo(plotMulti1DGroup, SWT.RIGHT);
		cmbCameraPerspective.add("Orthographic");
		cmbCameraPerspective.add("Perspective");
		Group plot2DGroup = new Group(comp, SWT.NONE);
		plot2DGroup.setText("Plot 2D");
		plot2DGroup.setLayout(new GridLayout(2, false));
		gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		plot2DGroup.setLayoutData(gd);
		Label lblColourMap = new Label(plot2DGroup, SWT.LEFT);
		lblColourMap.setText("Default colour mapping");
		cmbColourMap = new Combo(plot2DGroup, SWT.RIGHT);
		for (int i = 0; i < GlobalColourMaps.colourMapNames.length; i++)
			cmbColourMap.add(GlobalColourMaps.colourMapNames[i]);
		Label lblExpertMode = new Label(plot2DGroup, SWT.LEFT);
		lblExpertMode.setText("Auto histogram");
		chkExpertMode = new Button(plot2DGroup, SWT.CHECK | SWT.RIGHT);
		Label lblAutoHisto = new Label(plot2DGroup, SWT.LEFT);
		lblAutoHisto.setText("Auto histogram");
		chkAutoHisto = new Button(plot2DGroup, SWT.CHECK | SWT.RIGHT);
		Label lblScaling = new Label(plot2DGroup, SWT.LEFT);
		lblScaling.setText("Colour scaling");
		cmbColourScale = new Combo(plot2DGroup, SWT.RIGHT);
		cmbColourScale.add("Linear");
		cmbColourScale.add("Logarithmic");
		Label lblScrollbars = new Label(plot2DGroup, SWT.LEFT);
		lblScrollbars.setText("Show scrollbars");
		chkScrollbars = new Button(plot2DGroup, SWT.CHECK | SWT.RIGHT);
		initializePage();

		return comp;
	}

	@Override
	public void init(IWorkbench workbench) {
		setPreferenceStore(AnalysisRCPActivator.getDefault().getPreferenceStore());
	}

	@Override
	public boolean performOk() {
		storePreferences();
		return true;
	}

	@Override
	protected void performDefaults() {
		loadDefaultPreferences();
	}

	/**
	 * Load the resolution value
	 */
	private void initializePage() {
		cmbColourMap.select(getColourMapChoicePreference());
		chkExpertMode.setSelection(getExpertModePreference());
		chkAutoHisto.setSelection(getAutohistogramPreference());
		cmbColourScale.select(getColourScaleChoicePreference());
		cmbCameraPerspective.select(getPerspectivePreference());
		chkScrollbars.setSelection(getScrollBarPreference());
	}

	/**
	 * Load the default resolution value
	 */
	private void loadDefaultPreferences() {
		cmbColourMap.select(getDefaultColourMapChoicePreference());
		chkExpertMode.setSelection(getDefaultExpertModePreference());
		chkAutoHisto.setSelection(getDefaultAutohistogramPreference());
		cmbColourScale.select(getDefautColourScaleChoicePreference());
		cmbCameraPerspective.select(getDefaultPerspectivePreference());
		chkScrollbars.setSelection(getDefaultScrollBarPreference());
	}

	/**
	 * Store the resolution value
	 */
	private void storePreferences() {
		setColourMapChoicePreference(cmbColourMap.getSelectionIndex());
		setExpertModePreference(chkExpertMode.getSelection());
		setAutohistogramPreference(chkAutoHisto.getSelection());
		setColourScaleChoicePreference(cmbColourScale.getSelectionIndex());
		setCameraPerspective(cmbCameraPerspective.getSelectionIndex());
		setScrollBarPreference(chkScrollbars.getSelection());
	}

	public int getDefaultPerspectivePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_MULTI1D_CAMERA_PROJ);
	}

	public int getDefaultColourMapChoicePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_COLOURMAP);
	}

	public int getDefautColourScaleChoicePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_SCALING);
	}

	public boolean getDefaultExpertModePreference() {
		return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_CMAP_EXPERT);
	}

	public boolean getDefaultAutohistogramPreference() {
		return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO);
	}

	public boolean getDefaultScrollBarPreference() {
		return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_SHOWSCROLLBAR);
	}

	public int getPerspectivePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_MULTI1D_CAMERA_PROJ)) {
			return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_MULTI1D_CAMERA_PROJ);
		}
		return getPreferenceStore().getInt(PreferenceConstants.PLOT_VIEWER_MULTI1D_CAMERA_PROJ);
	}

	public int getColourMapChoicePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_COLOURMAP)) {
			return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_COLOURMAP);
		}
		return getPreferenceStore().getInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_COLOURMAP);
	}

	public boolean getExpertModePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_CMAP_EXPERT)) {
			return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_CMAP_EXPERT);
		}
		return getPreferenceStore().getBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_CMAP_EXPERT);
	}

	public boolean getAutohistogramPreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO)) {
			return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO);
		}
		return getPreferenceStore().getBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO);
	}

	public int getColourScaleChoicePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_SCALING)) {
			return getPreferenceStore().getDefaultInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_SCALING);
		}
		return getPreferenceStore().getInt(PreferenceConstants.PLOT_VIEWER_PLOT2D_SCALING);
	}

	public boolean getScrollBarPreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.PLOT_VIEWER_PLOT2D_SHOWSCROLLBAR)) {
			return getPreferenceStore().getDefaultBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_SHOWSCROLLBAR);
		}
		return getPreferenceStore().getBoolean(PreferenceConstants.PLOT_VIEWER_PLOT2D_SHOWSCROLLBAR);

	}

	public void setColourMapChoicePreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_PLOT2D_COLOURMAP, value);
	}

	public void setColourScaleChoicePreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_PLOT2D_SCALING, value);
	}

	public void setExpertModePreference(boolean value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_PLOT2D_CMAP_EXPERT, value);
	}

	public void setAutohistogramPreference(boolean value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_PLOT2D_AUTOHISTO, value);
	}

	public void setCameraPerspective(int value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_MULTI1D_CAMERA_PROJ, value);
	}

	public void setScrollBarPreference(boolean value) {
		getPreferenceStore().setValue(PreferenceConstants.PLOT_VIEWER_PLOT2D_SHOWSCROLLBAR, value);
	}
}
