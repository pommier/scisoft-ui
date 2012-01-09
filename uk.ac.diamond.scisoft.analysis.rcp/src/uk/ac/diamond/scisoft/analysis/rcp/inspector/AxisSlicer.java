/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.scisoft.analysis.rcp.inspector;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.dataset.Slice;
import uk.ac.diamond.scisoft.analysis.rcp.AnalysisRCPActivator;

/**
 * A slicer comprises a labelled slider with label and value, and two spinners.
 * The slider modify the starting value and the spinners the step size and number of steps.
 */
public class AxisSlicer {

	private Composite composite;
	private Label label = null;
	private LabelledSlider slider;
	private Text value;
	private Spinner size;
	private Spinner step;
	private Button reset;
	private int length;
	private SliceProperty slice;
	private SliceProperty[] axisSlices;
	private boolean mode;
	private AbstractDataset adata;
	private AbstractDataset axisData;
	private PropertyChangeListener listener;

	public static final int COLUMNS = 6;
	private static final Image undo = AnalysisRCPActivator.getImageDescriptor("icons/arrow_undo.png").createImage();

	public AxisSlicer(Composite parent) {
		composite = parent;
		listener = new PropertyChangeListener() {
			
			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				createAxisDataset();
				init(false);
			}
		};
	}

	public Composite getParent() {
		return composite;
	}

	public void clear() {
		if (label  != null) label.dispose();
		if (slider != null) slider.dispose();
		if (value  != null) value.dispose();
		if (size   != null) size.dispose();
		if (step   != null) step.dispose();
		if (reset  != null) reset.dispose();
	}

	/**
	 * Create the GUI components for a slicer
	 * @param property slice
	 * @param axis dataset for axis values
	 * @param properties slices that dataset depends on
	 */
	public void createAxisSlicer(SliceProperty property, AbstractDataset axis, SliceProperty[] properties) {
		if (label != null) {
			setParameters(property, axis, properties, true);
			return;
		}
		label  = new Label(composite, SWT.NONE);
		slider = new LabelledSlider(composite, SWT.HORIZONTAL);
		slider.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (slice == null)
					return;
				int start = slider.getValue();
				Slice s = slice.getValue();
				if (s.setPosition(start)) {
					if (size != null)
						size.setSelection(s.getNumSteps());
				}
				slice.setStart(start);
				int max = s.getNumSteps(start, length);
				if (size != null)
					size.setMaximum(max);
				if (value != null) {
					value.setText(adata.getString(start));
				}
				reset.setEnabled(true);
			}
		});
		slider.setIncrements(1, 5);
		slider.setToolTipText("Starting position");

		value = new Text(composite, SWT.BORDER|SWT.READ_ONLY);
		value.setToolTipText("Value at starting position");

		size = new Spinner(composite, SWT.BORDER);
		size.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (slice == null)
					return;
				Slice s = slice.getValue();
				Integer st = s.getStart();
				int start = st == null ? 0 : st;
				int stop = start + size.getSelection()*s.getStep();
				slice.setStop(stop);
				if (slider != null)
					slider.setThumb(s.getNumSteps()*s.getStep());
				int max = s.getNumSteps(start, length);
				size.setMaximum(max);
				reset.setEnabled(true);
			}
		});
		size.setToolTipText("Adjust number of items in slice");

		step = new Spinner(composite, SWT.BORDER);
		step.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (slice == null)
					return;
				slice.setStep(step.getSelection());
				Slice s = slice.getValue();
				Integer st = s.getStart();
				int start = st == null ? 0 : st;
				int n = s.getNumSteps();
				size.setSelection(n);
				slider.setThumb(n*s.getStep());
				int max = s.getNumSteps(start, length);
				size.setMaximum(max);
				reset.setEnabled(true);
			}
		});
		step.setToolTipText("Adjust step size in slice");

		reset = new Button(composite, SWT.PUSH);
		reset.setImage(undo);
		reset.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				// reset slice and GUI
				init(true);
				reset.setEnabled(false);
			}
		});
		reset.setToolTipText("Reset slice");
		setParameters(property, axis, properties, true);
	}

	/**
	 * Set parameters for slicer
	 * @param property slice
	 * @param axis dataset for axis values
	 * @param properties slices that axis dataset depends on
	 * @param used true if axis is used in plot
	 */
	public void setParameters(final SliceProperty property, final AbstractDataset axis, final SliceProperty[] properties, boolean used) {
		slice = property;
		if (axisSlices != null)
			for (int i = 0; i < axisSlices.length; i++)
				axisSlices[i].removePropertyChangeListener(listener);

		axisSlices = properties;
		mode = used;
		axisData = axis;
		createAxisDataset();
		init(false);
	}

	private void createAxisDataset() {
		for (int i = 0; i < axisSlices.length; i++)
			axisSlices[i].removePropertyChangeListener(listener);

		if (axisData.getRank() > 1) {
			Slice[] s = new Slice[axisSlices.length];
			for (int i = 0; i < s.length; i++) {
				SliceProperty p = axisSlices[i];
				if (p != slice) {
					s[i] = p.getValue();
					if (s[i].getNumSteps() > 1) {
						s[i] = new Slice(0, 1);
					}
					p.addPropertyChangeListener(listener);
				}
			}
			adata = axisData.getSlice(s).squeeze();
		} else
			adata = axisData;

		assert adata.getRank() == 1 : adata.getShape();
	}

	private void init(boolean reset) {
		Slice s = slice.getValue();
		if (reset) {
			s.setStart(null);
			if (mode)
				s.setStop(null);
			else
				s.setStop(1);
			slice.setStep(1);
		} else {
			length = s != null ? s.getLength() : -1;
		}
		int maxSize = slice.getMax();
		if (length < 0) {
			length = adata.getSize();
			slice.setLength(length);
		}
		if (maxSize < 0)
			maxSize = length;

		label.setText(adata.getName());
		slider.setMinMax(0, length, adata.getString(0), adata.getString(length-1));
		slider.setIncrements(1, 5);
		String initValue;
		boolean resetable = false;
		if (s == null) {
			slider.setValue(0);
			initValue = adata.getString(0);
		} else {
			Integer start = s.getStart();
			if (start == null || start == 0) {
				slider.setValue(0);
				initValue = adata.getString(0);
			} else {
				slider.setValue(start);
				initValue = adata.getString(start);
				resetable = true;
			}
		}
		value.setText(String.format("%-20s", initValue));
		int l = s != null ? s.getNumSteps() : length;

		if (mode) {
			slider.setThumb(l);
		} else {
			int t = s == null ? maxSize : s.getStep();
			slider.setThumb(t); // thumb size is step-sized when not used
		}
		size.setValues(l, 1, maxSize, 0, 1, 5 > maxSize ? maxSize : 5);
		step.setValues(1, 1, length, 0, 1, 1);
		step.setEnabled(mode); // TODO fix steps to one for time being
		size.setEnabled(mode);
		this.reset.setEnabled(resetable);
		setVisible(true);
	}

	/**
	 * Set visibility of slicer
	 * @param isVisible
	 */
	public void setVisible(boolean isVisible) {
		label.setVisible(isVisible);
		slider.setVisible(isVisible);
		value.setVisible(isVisible);
		size.setVisible(isVisible);
		step.setVisible(isVisible);
		reset.setVisible(isVisible);
	}
}

