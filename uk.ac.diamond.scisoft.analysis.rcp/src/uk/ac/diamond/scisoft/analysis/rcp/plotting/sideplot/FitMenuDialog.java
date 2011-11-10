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

package uk.ac.diamond.scisoft.analysis.rcp.plotting.sideplot;

import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Dialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.rcp.util.FloatSpinner;
import uk.ac.diamond.scisoft.analysis.rcp.util.SDAUtils;
import uk.ac.gda.richbeans.beans.BeanUI;
import uk.ac.gda.richbeans.components.wrappers.ComboWrapper;
import uk.ac.gda.richbeans.components.wrappers.SpinnerWrapper;

/**
 * TODO Use org.eclipse.jface.dialogs.Dialog, it sorts a lot of the problems
 * out that this code is allowing for.
 * 
 * Please change this dialog to remember the last chose fit settings at some point.
 * Typically the user is working though a number of peaks and using the same settings
 * each time.
 */
public class FitMenuDialog extends Dialog {

	private static final Logger logger = LoggerFactory.getLogger(FitMenuDialog.class);
	
    // Final
	private final String[] peaks;
	private final String[] algNames;
	
	// Assignables
	private Shell shell;
	private boolean ok=false;
	
	// GUI - use the standard widgets, then can
	// save gui to xml if need to, which we probably do.
	private ComboWrapper        peakSelection;
	private SpinnerWrapper      numberOfPeaks;
	private ComboWrapper        algType;
	private FloatSpinner accuracy;
	private SpinnerWrapper      smoothing;
	private int 				selectAlg;
	private int 				selectPeak;
	
    // Data
	private FitData  fitData;
	
	public FitMenuDialog(Shell parent, String[] peakList, String [] fittingAlgNames) {
		
		// Without this it does not work on windows or some linux versions
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		this.fitData = new FitData(); // Holds the values which the user specifies.

		peaks = peakList;
		algNames = fittingAlgNames;
		selectAlg = 0;
		selectPeak = 0;
	}

	public FitMenuDialog(Shell parent, String[] peakList, String [] fittingAlgNames, int selectedPeak, int selectedFittingAlg) {
		
		// Without this it does not work on windows or some linux versions
		super(parent, SWT.APPLICATION_MODAL | SWT.DIALOG_TRIM);
		
		this.fitData = new FitData(); // Holds the values which the user specifies.

		peaks = peakList;
		algNames = fittingAlgNames;
		selectAlg = selectedFittingAlg;
		selectPeak = selectedPeak;
	}

	public boolean open() {
		
		createUI();
		shell.open();
		
		Display display = getParent().getDisplay();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch())
				display.sleep();
		}
		
		return ok;
	}

	private void createUI() {
		
		Shell parent = getParent();
		shell = new Shell(parent, SWT.RESIZE | SWT.APPLICATION_MODAL);
		shell.setText("Peak Fitting");
		shell.setLayout(new GridLayout(1, false));
		shell.setSize(350, 290);
		
		Group top = new Group(shell, SWT.NONE);
		top.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 1, 1));
		top.setLayout(new GridLayout(2, false));

		Label numPeakLab = new Label(top, SWT.None);
		numPeakLab.setText("Number of peaks");

		numberOfPeaks = new SpinnerWrapper(top, SWT.BORDER);
		numberOfPeaks.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		numberOfPeaks.setMinimum(1);
		numberOfPeaks.setIncrement(1);
		numberOfPeaks.setValue(1);
		
		ExpandableComposite advancedExpandableComposite = new ExpandableComposite(shell, SWT.NONE);
		advancedExpandableComposite.setText("Advanced");
		advancedExpandableComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		
		Group advanced = new Group(advancedExpandableComposite, SWT.NONE);
		advanced.setLayout(new GridLayout(2, false));

		Label peakLab = new Label(advanced, SWT.NONE);
		peakLab.setText("Peak type");

		peakSelection = new ComboWrapper(advanced, SWT.READ_ONLY);
		peakSelection.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		peakSelection.setItems(ComboWrapper.getItemMap(peaks));
		peakSelection.setValue(selectPeak);

		Label algLabel = new Label(advanced, SWT.NONE);
		algLabel.setText("Fitting algorithm");

		algType = new ComboWrapper(advanced, SWT.READ_ONLY);
		algType.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		algType.setItems(ComboWrapper.getItemMap(algNames));
		algType.setValue(selectAlg);

		Label accuractlab = new Label(advanced, SWT.NONE);
		accuractlab.setText("Accuracy");
		accuractlab.setToolTipText("This sets the accuracy of the optomisation. The lower the number to more accurate the calculation");

		accuracy = new FloatSpinner(advanced, SWT.NONE,6,5);
		accuracy.setMinimum(0.00001);
		accuracy.setDouble(0.01);

		Label smoothingLab = new Label(advanced, SWT.NONE);
		smoothingLab.setText("Smoothing");
		smoothingLab.setToolTipText("Defines the smoothing which will be applied to the peak searching algorithm");

		smoothing = new SpinnerWrapper(advanced, SWT.NONE);
		smoothing.setDigits(0);
		smoothing.setValue(1);
		smoothing.setMinimum(0);
		smoothing.setMaximum(10000);

		advancedExpandableComposite.setClient(advanced);
		ExpansionAdapter expansionListener = new ExpansionAdapter() {
			@Override
			public void expansionStateChanged(ExpansionEvent e) {
				shell.layout();
			}
		};
		advancedExpandableComposite.addExpansionListener(expansionListener);

		Composite buttons = new Composite(shell, SWT.NONE);
		buttons.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		buttons.setLayout(new GridLayout(2, false));
		
		Button fit = new Button(buttons, SWT.PUSH);
		// Changed to 'fit' otherwise UI has too much 'fitting' words
		// The & gives a keyboard short cut when Alt is pressed.
		fit.setText("  &Fit  ");
		fit.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ok = true;
				saveValues();
				shell.dispose();
			}
		});

		Button cancel = new Button(buttons, SWT.PUSH);
		cancel.setText("&Cancel");
		cancel.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ok = false;
				shell.dispose();
			}
		});
		
		try {
			BeanUI.switchState(this, true);
			loadValues();
		} catch (Exception e1) {
			logger.error("Cannot turn on UI", e1);
		}
	}
	
	/**
	 * Save values for later
	 */
	private void saveValues() {
		XMLEncoder encoder = null;
		try {
			// Get the UI values.
			BeanUI.uiToBean(FitMenuDialog.this, fitData);
			
			// Save the last selected values, so that the next form shown can use them
			final File  peakFitPath  = new File(SDAUtils.getSdaHome()+FitMenuDialog.class.getName()+".xml");
			if (!peakFitPath.getParentFile().exists()) peakFitPath.getParentFile().mkdirs();
			
			encoder = new XMLEncoder(new BufferedOutputStream(new FileOutputStream(peakFitPath)));
			encoder.writeObject(fitData);
			
		} catch (Exception e1) {
			logger.error("Cannot save fit menu data!", e1);
		} finally {
			if (encoder!=null) encoder.close();
		}
	}
	
	/**
	 * Load values from last saved
	 */
	private void loadValues() {
		
		XMLDecoder decoder = null;
		try {
			
			final File  peakFitPath  = new File(SDAUtils.getSdaHome()+FitMenuDialog.class.getName()+".xml");
			if (!peakFitPath.exists()) return;
			
			decoder = new XMLDecoder(new BufferedInputStream(new FileInputStream(peakFitPath)));

			this.fitData = (FitData)decoder.readObject();
			BeanUI.beanToUI(fitData, this);
			
		} catch (Exception e1) {
			logger.error("Cannot open fit menu data!", e1);
		} finally {
			if (decoder!=null) decoder.close();
		}
	}

	/**
	 * Returns the fit data set by the user.
	 */
	public FitData getFitData() {
		return this.fitData;
	}
	
	/**
	 * Use getFitData() to access the data, this method
	 * is used internally.
	 * @return unknown
	 */
	public ComboWrapper getPeakSelection() {
		return peakSelection;
	}

	/**
	 * Use getFitData() to access the data, this method
	 * is used internally.
	 * @return unknown
	 */
	public SpinnerWrapper getNumberOfPeaks() {
		return numberOfPeaks;
	}

	/**
	 * Use getFitData() to access the data, this method
	 * is used internally.
	 * @return unknown
	 */
	public ComboWrapper getAlgType() {
		return algType;
	}

	/**
	 * Use getFitData() to access the data, this method
	 * is used internally.
	 * @return unknown
	 */
	public FloatSpinner getAccuracy() {
		return accuracy;
	}

	/**
	 * Use getFitData() to access the data, this method
	 * is used internally.
	 * @return unknown
	 */
	public SpinnerWrapper getSmoothing() {
		return smoothing;
	}
}