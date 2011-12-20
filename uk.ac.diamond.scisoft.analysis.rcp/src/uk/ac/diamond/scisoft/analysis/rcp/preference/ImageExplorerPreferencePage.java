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

package uk.ac.diamond.scisoft.analysis.rcp.preference;


import java.util.List;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;
import uk.ac.diamond.scisoft.analysis.rcp.plotting.utils.GlobalColourMaps;
import uk.ac.diamond.scisoft.analysis.rcp.views.ImageExplorerView;

/**
 *
 */
public class ImageExplorerPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	private Combo cmbColourMap;
	private Spinner spnAutoThreshold;
	private Spinner spnWaitTime;
	private Spinner spnSkipImages;
	private Combo cmbDisplayViews;
	
	public ImageExplorerPreferencePage() {
	}

	public ImageExplorerPreferencePage(String title) {
		super(title);
	}

	public ImageExplorerPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}
	
	@Override
	protected Control createContents(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(3, false));
		GridData gdc = new GridData(SWT.FILL, SWT.FILL, true, true);
		comp.setLayoutData(gdc);
		Label lblColourMap = new Label(comp,SWT.LEFT);
		lblColourMap.setText("Default colour mapping");
		cmbColourMap = new Combo(comp, SWT.RIGHT);
		gdc = new GridData();
		gdc.horizontalSpan = 2;
		cmbColourMap.setLayoutData(gdc);
		for (int i = 0; i < GlobalColourMaps.colourMapNames.length; i++)
			cmbColourMap.add(GlobalColourMaps.colourMapNames[i]);		
		Label lblThreshold = new Label(comp,SWT.LEFT);
		lblThreshold.setText("Histogram autoscale threshold (in %)");
		spnAutoThreshold = new Spinner(comp,SWT.RIGHT);
		spnAutoThreshold.setMaximum(100);
		spnAutoThreshold.setMinimum(0);
		spnAutoThreshold.setIncrement(1);
		gdc = new GridData();
		gdc.horizontalSpan = 2;
		spnAutoThreshold.setLayoutData(gdc);
		Label lblWaitTime = new Label(comp,SWT.LEFT);
		lblWaitTime.setText("Time delay for next image in play mode");
		spnWaitTime = new Spinner(comp,SWT.RIGHT);
		spnWaitTime.setMinimum(150);
		spnWaitTime.setMaximum(15000);
		spnWaitTime.setIncrement(50);
		Label lblUnits = new Label(comp,SWT.LEFT);
		lblUnits.setText("in ms");
		Label lblPlayback = new Label(comp,SWT.LEFT);
		lblPlayback.setText("View to use for playback");
		cmbDisplayViews = new Combo(comp,SWT.RIGHT);
		cmbDisplayViews.setLayoutData(gdc);
		List<String> views = ImageExplorerView.getRegisteredViews();
		for (String s : views) {
			cmbDisplayViews.add(s);
		}
		Label lblSkipImages = new Label(comp,SWT.LEFT);
		lblSkipImages.setText("Playback every");
		spnSkipImages = new Spinner(comp,SWT.RIGHT);
		spnSkipImages.setMinimum(1);
		spnSkipImages.setMaximum(100);
		spnSkipImages.setIncrement(1);
		Label lblImages = new Label(comp,SWT.LEFT);
		lblImages.setText("image");
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
	
	private void initializePage() {
		cmbColourMap.select(getColourMapChoicePreference());
		spnAutoThreshold.setSelection(getHistogramScalePreference());
		spnWaitTime.setSelection(getTimeDelayPreference());
		spnSkipImages.setSelection(getPlaybackRatePreference());
		String viewName = getPlaybackViewPreference();
		for (int i = 0; i < cmbDisplayViews.getItems().length; i++)
		{
			if (cmbDisplayViews.getItems()[i].equals(viewName))
				cmbDisplayViews.select(i);
		}
	} 
	
	private void storePreferences() {
		setColourMapChoicePreference(cmbColourMap.getSelectionIndex());
		setHistogramScalePreference(spnAutoThreshold.getSelection());
		setTimeDelayPreference(spnWaitTime.getSelection());
		setPlaybackViewPreference(cmbDisplayViews.getItem(cmbDisplayViews.getSelectionIndex()));
		setPlaybackRatePreference(spnSkipImages.getSelection());
	}
	
	private void loadDefaultPreferences() {
		cmbColourMap.select(getDefaultColourMapChoicePreference());
		spnAutoThreshold.setSelection(getDefaultHistogramScalePreference());
		spnWaitTime.setSelection(getDefaultTimeDelayPreference());
		spnSkipImages.setSelection(getDefaultPlaybackRatePreference());
		String viewName = getDefaultPlaybackViewPreference();
		for (int i = 0; i < cmbDisplayViews.getItems().length; i++)
		{
			if (cmbDisplayViews.getItems()[i].equals(viewName))
				cmbDisplayViews.select(i);
		}
	} 	
	
	public int getDefaultColourMapChoicePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_COLOURMAP);
	}		
	
	public int getDefaultHistogramScalePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_HISTOGRAMAUTOSCALETHRESHOLD);
	}
	
	public int getDefaultTimeDelayPreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES);
	}
	
	public String getDefaultPlaybackViewPreference() {
		return getPreferenceStore().getDefaultString(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW);
	}
	
	public int getDefaultPlaybackRatePreference() {
		return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE);
	}
	
	public int getColourMapChoicePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.IMAGEEXPLORER_COLOURMAP)){
			return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_COLOURMAP);
		}
		return getPreferenceStore().getInt(PreferenceConstants.IMAGEEXPLORER_COLOURMAP);
	}	

	public int getHistogramScalePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.IMAGEEXPLORER_HISTOGRAMAUTOSCALETHRESHOLD)){
			return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_HISTOGRAMAUTOSCALETHRESHOLD);
		}
		return getPreferenceStore().getInt(PreferenceConstants.IMAGEEXPLORER_HISTOGRAMAUTOSCALETHRESHOLD);		
	}
	
	public int getTimeDelayPreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES)) {
			return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES);
		}
		return getPreferenceStore().getInt(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES);
	}
	
	public int getPlaybackRatePreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE)) {
			return getPreferenceStore().getDefaultInt(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE);
		}
		return getPreferenceStore().getInt(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE);		
	}
	
	public String getPlaybackViewPreference() {
		if (getPreferenceStore().isDefault(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW)) {
			return getPreferenceStore().getDefaultString(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW);
		}
		return getPreferenceStore().getString(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW);
	}
	
	public void setColourMapChoicePreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.IMAGEEXPLORER_COLOURMAP, value);
	}		
	
	public void setHistogramScalePreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.IMAGEEXPLORER_HISTOGRAMAUTOSCALETHRESHOLD, value);
	}
	
	public void setTimeDelayPreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.IMAGEEXPLORER_TIMEDELAYBETWEENIMAGES, value);
	}
	
	public void setPlaybackViewPreference(String newView) {
		getPreferenceStore().setValue(PreferenceConstants.IMAGEEXPLORER_PLAYBACKVIEW, newView);
	}
	
	public void setPlaybackRatePreference(int value) {
		getPreferenceStore().setValue(PreferenceConstants.IMAGEEXPLORER_PLAYBACKRATE, value);
	}
	
}
