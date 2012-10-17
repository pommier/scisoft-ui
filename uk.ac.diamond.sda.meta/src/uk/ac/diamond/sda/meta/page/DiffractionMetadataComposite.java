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

package uk.ac.diamond.sda.meta.page;

import java.util.HashSet;
import java.io.Serializable;
import java.lang.reflect.Method;

import javax.vecmath.Vector3d;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.AbstractDataset;
import uk.ac.diamond.scisoft.analysis.diffraction.DetectorProperties;
import uk.ac.diamond.scisoft.analysis.diffraction.DiffractionCrystalEnvironment;
import uk.ac.diamond.scisoft.analysis.io.IDiffractionMetadata;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.rcp.util.FloatSpinner;

public class DiffractionMetadataComposite implements IMetadataPage {

	private static final Logger logger = LoggerFactory.getLogger(DiffractionMetadataComposite.class);

	private FloatSpinner wavelength;
	private Text phiStart;
	private Text phiStop;
	private Text phiRange;
	private FloatSpinner distanceToDetector;
	private Text detectorSizeX;
	private Text detectorSizeY;
	private Text pixelSizeX;
	private Text pixelSizeY;
	private Text ExposureTime;
	private Spinner maxPxVal;
	private Spinner minPxVal;
	private Text meanPxVal;
	private Spinner overload;
	private Button showBeam;
	private FloatSpinner xBeam;
	private FloatSpinner yBeam;
	private Composite content;
	private double[] beam;
	private AbstractDataset dataset;
	private IMetaData metadata;
	private DetectorProperties detprop;
	private DiffractionCrystalEnvironment diffenv;
	private boolean editable;
	private double wavelengthOriginal, distanceToDetectorOriginal, xBeamOriginal, yBeamOriginal;
	private HashSet<IDiffractionMetadataCompositeListener> diffMetaCompListeners; 

	public DiffractionMetadataComposite() {

	}

	double moveXBeam(int milllimeters) {
		double mm = (beam[0] * detprop.getHPxSize()) + milllimeters;
		xBeam.setDouble(mm);
		updateBeamX(mm);
		return mm;
	}
	
	double moveYBeam(int milllimeters) {
		double mm = (beam[1] * detprop.getVPxSize()) + milllimeters;
		yBeam.setDouble(mm);
		updateBeamY(mm);
		return mm;
	}
	
	void updateBeamX(double millimeter) {
		// Calculate and set the new property value
		beam[0] = millimeter / detprop.getHPxSize();
		detprop.setBeamLocation(beam);
	}
	
	void updateBeamY(double millimeter) {
		// Calculate and set the new property value
		beam[1] = millimeter / detprop.getVPxSize();
		detprop.setBeamLocation(beam);
	}
	
	void updateWavelength(double angstrom) {
		diffenv.setWavelength(angstrom);
	}

	void updateDistanceToDetector(double mm) {
		Vector3d origin = detprop.getOrigin();
		origin.z = mm;
		detprop.setOrigin(origin);
	}
	
	void updateMaxPxVal(double value) {
	}
	void updateMinPxVal(double value) {
	}
	void updateMeanPxVal(double value) {
	}
	void updateOverload(double value) {
	}
	

	public void resetWavelengthToOriginal() {
		updateWavelength(wavelengthOriginal);
		wavelength.setDouble(wavelengthOriginal);
	}

	public void resetDistanceToDetectorToOriginal() {
		updateDistanceToDetector(distanceToDetectorOriginal);
		distanceToDetector.setDouble(distanceToDetectorOriginal);
	}
	
	public void resetXBeamToOriginal() {
		updateBeamX(xBeamOriginal);
		xBeam.setDouble(xBeamOriginal);
	}
	
	public void resetYBeamToOriginal() {
		updateBeamY(yBeamOriginal);
		yBeam.setDouble(yBeamOriginal);
	}
	
	public void resetAllToOriginal() {
		resetWavelengthToOriginal();
		resetDistanceToDetectorToOriginal();
		resetXBeamToOriginal();
		resetYBeamToOriginal();
	}
	
	@Override
	public Composite createComposite(Composite parent) {
		final double[] incs = {1.0, 0.1, 0.01, 0.001, 0.0001}; 

		Method resetWavelengthToOriginalMethod = null;
		Method resetDistanceToDetectorToOriginalMethod = null;
		Method resetXBeamToOriginalMethod = null, resetYBeamToOriginalMethod = null;
		try {
			resetWavelengthToOriginalMethod = this.getClass().getMethod("resetWavelengthToOriginal", (Class<?>[])null);
			resetDistanceToDetectorToOriginalMethod = this.getClass().getMethod("resetDistanceToDetectorToOriginal", (Class<?>[])null);
			resetXBeamToOriginalMethod = this.getClass().getMethod("resetXBeamToOriginal", (Class<?>[])null);
			resetYBeamToOriginalMethod = this.getClass().getMethod("resetYBeamToOriginal", (Class<?>[])null);
		} catch (SecurityException e1) {
			logger.error("Can't get reset methods (security exception)", e1);
		} catch (NoSuchMethodException e1) {
			logger.error("Can't get reset method (no such method)", e1);
		}
		
		this.content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(1, true));

		ScrolledComposite scrComp = new ScrolledComposite(content, SWT.HORIZONTAL | SWT.VERTICAL);
		scrComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite comp = new Composite(scrComp, SWT.FILL);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		comp.setLayout(new GridLayout(2, false));

		// Experimental Information
		Group experimentmetadata = new Group(comp, SWT.NONE);
		experimentmetadata.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		experimentmetadata.setLayout(new GridLayout(3, false));
		experimentmetadata.setText("Experimental Information");

		Label lblWavelength = new Label(experimentmetadata, SWT.NONE);
		lblWavelength.setText("Wavelength");
		lblWavelength.setEnabled(true);
		lblWavelength.setToolTipText("");

		wavelength = new FloatSpinner(experimentmetadata, SWT.SINGLE | SWT.BORDER | SWT.RIGHT, 7, 4);
		wavelength.createMenu(incs, this, resetWavelengthToOriginalMethod);
		wavelength.setBackground(experimentmetadata.getBackground());
		wavelength.setEnabled(editable);
		wavelength.setIncrement(0.01);
		wavelength.setToolTipText("Right-click to reset and set increment.");
		wavelength.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateWavelength(wavelength.getDouble());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});

		new Label(experimentmetadata, SWT.NONE).setText("\u00c5");

		Label lblStart = new Label(experimentmetadata, SWT.NONE);
		lblStart.setText("Start");
		phiStart = new Text(experimentmetadata, SWT.READ_ONLY);
		phiStart.setBackground(experimentmetadata.getBackground());
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");

		Label lblStop = new Label(experimentmetadata, SWT.NONE);
		lblStop.setText("Stop");
		phiStop = new Text(experimentmetadata, SWT.READ_ONLY);
		phiStop.setBackground(experimentmetadata.getBackground());
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");

		Label lblOscillationRange = new Label(experimentmetadata, SWT.NONE);
		lblOscillationRange.setText("Osc. Range");
		phiRange = new Text(experimentmetadata, SWT.READ_ONLY);
		phiRange.setBackground(experimentmetadata.getBackground());
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");

		// Detector Metadata
		Group detectorMetadata = new Group(comp, SWT.NONE);
		detectorMetadata.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, true, true, 1, 1));
		detectorMetadata.setLayout(new GridLayout(3, false));
		detectorMetadata.setText("Detector Metadata");

		Label lblDistance = new Label(detectorMetadata, SWT.NONE);
		lblDistance.setText("Distance");
		
		distanceToDetector = new FloatSpinner(detectorMetadata, SWT.SINGLE | SWT.BORDER | SWT.RIGHT, 8, 4);
		distanceToDetector.createMenu(incs, this, resetDistanceToDetectorToOriginalMethod);
		distanceToDetector.setBackground(detectorMetadata.getBackground());
		distanceToDetector.setEnabled(editable);
		distanceToDetector.setIncrement(1.0);
		distanceToDetector.setToolTipText("Right-click to reset and set increment.");
		distanceToDetector.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateDistanceToDetector(distanceToDetector.getDouble());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});		

		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblSizex = new Label(detectorMetadata, SWT.NONE);
			lblSizex.setText("Size (x)");
		}
		{
			detectorSizeX = new Text(detectorMetadata, SWT.READ_ONLY);
			detectorSizeX.setBackground(detectorMetadata.getBackground());

		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblSizey = new Label(detectorMetadata, SWT.NONE);
			lblSizey.setText("Size (y)");
		}
		{
			detectorSizeY = new Text(detectorMetadata, SWT.READ_ONLY);
			detectorSizeY.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblPixelSizex = new Label(detectorMetadata, SWT.NONE);
			lblPixelSizex.setText("Pixel Size (x)");
		}
		{
			pixelSizeX = new Text(detectorMetadata, SWT.READ_ONLY);
			pixelSizeX.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblPixelSizey = new Label(detectorMetadata, SWT.NONE);
			lblPixelSizey.setText("Pixel Size (y)");
		}
		{
			pixelSizeY = new Text(detectorMetadata, SWT.READ_ONLY);
			pixelSizeY.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblTime = new Label(detectorMetadata, SWT.NONE);
			lblTime.setText("Exp. Time");
		}
		{
			ExposureTime = new Text(detectorMetadata, SWT.READ_ONLY);
			ExposureTime.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("s");
		
		
		// Pixel Values
		if (dataset != null) { 
			Group grpPixelValueControls = new Group(comp, SWT.NONE);
			grpPixelValueControls.setLayout(new GridLayout(2, false));
			grpPixelValueControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			grpPixelValueControls.setText("Pixel Values");
	
			Label lblMaxPxVal = new Label(grpPixelValueControls, SWT.NONE);
			lblMaxPxVal.setText("Max.");
			lblMaxPxVal.setToolTipText("Maximum pixel value of the dataset being plotted.");
	
			maxPxVal = new Spinner(grpPixelValueControls, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
			maxPxVal.setEnabled(false /*editable*/);
			maxPxVal.setMaximum(Integer.MAX_VALUE);
			maxPxVal.setMinimum(Integer.MIN_VALUE);
			maxPxVal.setIncrement(1);
			maxPxVal.setSize(50, 15);
			maxPxVal.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateMaxPxVal(maxPxVal.getSelection()); 
				}
	
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
				}
			});		
	
			Label lblMinPxVal = new Label(grpPixelValueControls, SWT.NONE);
			lblMinPxVal.setText("Min.");
			lblMinPxVal.setToolTipText("Minimum pixel value of the dataset being plotted.");
	
			minPxVal = new Spinner(grpPixelValueControls, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
			//minPxVal.createMenu(incs, this, resetDistanceToDetectorToOriginalMethod);
			minPxVal.setEnabled(false /*editable*/);
			minPxVal.setMaximum(Integer.MAX_VALUE);
			minPxVal.setMinimum(Integer.MIN_VALUE);
			minPxVal.setIncrement(1);
			minPxVal.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateMinPxVal(minPxVal.getSelection());
				}
	
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
				}
			});		
	
			Label lblMeanPxVal = new Label(grpPixelValueControls, SWT.NONE);
			lblMeanPxVal.setText("Mean");
			lblMeanPxVal.setToolTipText("Mean pixel value of the dataset being plotted.");
	
			meanPxVal = new Text(grpPixelValueControls, SWT.READ_ONLY);
			meanPxVal.setBackground(grpPixelValueControls.getBackground());
			meanPxVal.setEditable(false /*editable*/);
			
			Label lblThreashold = new Label(grpPixelValueControls, SWT.NONE);
			lblThreashold.setText("Overload");
			lblThreashold.setToolTipText("Displays the maximum possible pixel value");
	
			overload = new Spinner(grpPixelValueControls, SWT.SINGLE | SWT.BORDER | SWT.RIGHT);
			//overload.createMenu(incs, this, resetDistanceToDetectorToOriginalMethod);
			overload.setEnabled(false /*editable*/);
			overload.setIncrement(1);
			overload.setMaximum(Integer.MAX_VALUE);
			overload.setMinimum(0);
			overload.addSelectionListener(new SelectionListener() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					updateOverload(overload.getSelection());
				}
	
				@Override
				public void widgetDefaultSelected(SelectionEvent e) {
					// TODO Auto-generated method stub
				}
			});		
		}
		// Beam centre
		Group grpBeamCentreControls = new Group(comp, SWT.NONE);
		grpBeamCentreControls.setLayout(new GridLayout(2, false));
		grpBeamCentreControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpBeamCentreControls.setText("Beam Centre");

		// 3 x 3 grid for buttons to move beam centre   
		
		Composite beamCentreControls = new Composite(grpBeamCentreControls, SWT.NONE);
		beamCentreControls.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		beamCentreControls.setLayout(new GridLayout(3, false));

		/*
		if (editable) {
				
			// Top left tile in grid is empty 
			new Label(beamCentreControls, SWT.NONE).setText("");
	
			// Top centre tile contains arrow up
			Button upBeam = new Button(beamCentreControls, SWT.NONE);
			upBeam.setImage(Activator.getImageDescriptor("/icons/arrow_up.png").createImage());
			upBeam.setEnabled(editable);
			upBeam.setVisible(editable);
			
			upBeam.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveYBeam(-1);
				}
			});
	
			// Top right tile is empty
			new Label(beamCentreControls, SWT.NONE).setText("");
	
			// Middle left tile contains the left button
			Button leftBeam = new Button(beamCentreControls, SWT.NONE);
	
			leftBeam.setImage(Activator.getImageDescriptor("/icons/arrow_left.png").createImage());
			leftBeam.setEnabled(editable);
			leftBeam.setVisible(editable);
			leftBeam.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveXBeam(-1);
				}
			});
	
			// Middle centre tile contains the button to show the beam centre
			showBeam = new Button(beamCentreControls, SWT.TOGGLE);
			showBeam.setToolTipText("Show beam centre");
			showBeam.setImage(Activator.getImageDescriptor("icons/asterisk_yellow.png").createImage());
			showBeam.setEnabled(editable);
			showBeam.setVisible(editable);
			showBeam.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					fireDiffractionMetadataCompositeListeners(new DiffractionMetadataCompositeEvent(this, "Beam Center"));
				}
			});
	
			
			
			// Middle right tile contains the right button
			Button rightBeam = new Button(beamCentreControls, SWT.NONE);
			rightBeam.setImage(Activator.getImageDescriptor("/icons/arrow_right.png").createImage());
			rightBeam.setEnabled(editable);
			rightBeam.setVisible(editable);
			rightBeam.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveXBeam(1);
				}
			});
	
			// Lower left tile is empty
			new Label(beamCentreControls, SWT.NONE).setText("");
	
			// Lower centre tile contains the down arrow
			Button downBeam = new Button(beamCentreControls, SWT.NONE);
			downBeam.setImage(Activator.getImageDescriptor("/icons/arrow_down.png").createImage());
			downBeam.setEnabled(editable);
			downBeam.setVisible(editable);
			downBeam.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					moveYBeam(1);
				}
			});
	
			// Lower right button is empty
			new Label(beamCentreControls, SWT.NONE).setText("");
		}
		 */
		
		// 2 x 2 grid for labels showing the beam position
		Composite beamSpinners = new Composite(grpBeamCentreControls, SWT.FILL);
		beamSpinners.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		beamSpinners.setLayout(new GridLayout(2, false));

		// Beam X label
		Label labXBeam = new Label(beamSpinners, SWT.NONE);
		labXBeam.setText("X");

		// Beam X value
		xBeam = new FloatSpinner(beamSpinners, SWT.SINGLE | SWT.BORDER | SWT.RIGHT, 8, 4);
		xBeam.createMenu(incs, this, resetXBeamToOriginalMethod);
		xBeam.setBackground(grpBeamCentreControls.getBackground());
		xBeam.setEnabled(editable);
		xBeam.setIncrement(1.0);
		xBeam.setToolTipText("Right-click to reset and set increment.");
		xBeam.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateBeamX(xBeam.getDouble());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});		

		
		
		// Beam Y label
		Label labYBeam = new Label(beamSpinners, SWT.NONE);
		labYBeam.setText("Y");

		// Beam Y value
		yBeam = new FloatSpinner(beamSpinners, SWT.SINGLE | SWT.BORDER | SWT.RIGHT, 8, 4);
		yBeam.createMenu(incs, this, resetYBeamToOriginalMethod);
		yBeam.setBackground(grpBeamCentreControls.getBackground());
		yBeam.setEnabled(editable);
		yBeam.setIncrement(1.0);
		yBeam.setToolTipText("Right-click to reset and set increment.");
		yBeam.addSelectionListener(new SelectionListener() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				updateBeamY(yBeam.getDouble());
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
			}
		});		


		scrComp.setContent(comp);
		final Point controlsSize = comp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		comp.setSize(controlsSize);
		return content;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}

	private void updateGUI() {
		UIJob update = new UIJob("Updating metadata GUI") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				wavelength.setDouble(diffenv.getWavelength());
				wavelengthOriginal = diffenv.getWavelength();
				phiStart.setText(String.valueOf(diffenv.getPhiStart()));
				phiStop.setText(String.valueOf(diffenv.getPhiStart() + diffenv.getPhiRange()));
				phiRange.setText(String.valueOf(diffenv.getPhiRange()));
				distanceToDetector.setDouble(detprop.getOrigin().z);
				distanceToDetectorOriginal = detprop.getOrigin().z;
				detectorSizeX.setText(String.valueOf(detprop.getDetectorSizeH()));
				detectorSizeY.setText(String.valueOf(detprop.getDetectorSizeV()));
				pixelSizeX.setText(String.valueOf(detprop.getHPxSize()));
				pixelSizeY.setText(String.valueOf(detprop.getVPxSize()));
				ExposureTime.setText(String.valueOf(diffenv.getExposureTime()));
				
				AbstractDataset dataset = getData();
				if (dataset != null) {
					if (maxPxVal != null) {
						Number max = dataset.max(true);
						maxPxVal.setSelection(max.intValue());
					}
					if (minPxVal != null) {
						Number min = dataset.min(true);
						minPxVal.setSelection(min.intValue());
					}
					if (meanPxVal != null) {
						Object mean = dataset.mean(true);
						if (mean instanceof Double)
							meanPxVal.setText(String.valueOf(mean));
					}
				}

				// Extract overload value from metadata
				if (metadata != null) {
					Serializable overloadVal = null;
					try {
						overloadVal = metadata.getMetaValue("Count_cutoff");
						if (overloadVal == null) {
							overloadVal = metadata.getMetaValue("CCD_IMAGE_SATURATION");
						}
					} catch (Exception e) {
						logger.error("Could not get overload value from image header", e);
					}

					if (overload != null && overloadVal != null) {
						String overloadStr = overloadVal.toString();
						overloadStr = overloadStr.replaceAll("[^\\d.]", "");
						Double overloadD = Double.valueOf(overloadStr);
						overload.setSelection(overloadD.intValue());
					}
				}

				
				

				beam = detprop.getBeamLocation();
				xBeam.setDouble(beam[0] * detprop.getHPxSize());
				xBeamOriginal = xBeam.getDouble();
				yBeam.setDouble(beam[1] * detprop.getVPxSize());
				yBeamOriginal = yBeam.getDouble();

				return Status.OK_STATUS;
			}
		};
		update.schedule();
	}

	
//	@Override
//	public void update(Object source, Object arg) {
//		if (source instanceof IMetadataProvider) {
//			IMetaData localMetadata;
//			try {
//				localMetadata = ((IMetadataProvider) source).getMetadata();
//				if (localMetadata instanceof IDiffractionMetadata) {
//					updateGUI((IDiffractionMetadata) localMetadata);
//				} else {
//					clearGUI();
//				}
//			} catch (Exception e) {
//				logger.error("Could not get metadata", e);
//			}
//
//		}
//	}

	private void clearGUI() {
		UIJob update = new UIJob("Updating metadata GUI") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {

				wavelength.setDouble(0.0);
				phiStart.setText("");
				phiStop.setText("");
				phiRange.setText("");

				distanceToDetector.setDouble(0.0);
				detectorSizeX.setText("");
				detectorSizeY.setText("");
				pixelSizeX.setText("");
				pixelSizeY.setText("");
				ExposureTime.setText("");
				maxPxVal.setSelection(0); //.setDouble(Double.NaN);
				minPxVal.setSelection(0); //.setDouble(Double.NaN);
				meanPxVal.setText(""); //.setSelection(0); //.setDouble(Double.NaN);
				overload.setSelection(0); //.setDouble(Double.NaN);
				xBeam.setDouble(Double.NaN);
				yBeam.setDouble(Double.NaN);
				return Status.OK_STATUS;
			}
		};
		update.schedule();
	}

	public void setData(AbstractDataset dataset) {
		this.dataset = dataset;
	}
	
	public AbstractDataset getData() {
		return this.dataset;
	}
	
	
	@Override
	public void setMetaData(IMetaData metadata) {
		this.metadata = metadata;
		if (metadata instanceof IDiffractionMetadata) {
			this.detprop = ((IDiffractionMetadata)metadata).getDetector2DProperties(); 
			this.diffenv = ((IDiffractionMetadata)metadata).getDiffractionCrystalEnvironment();
			updateGUI();
		}
//		if (metadata == null) {
//			clearGUI();
//		}
	}
	
	public void addDiffractionMetadataCompositeListener(IDiffractionMetadataCompositeListener l) {
		if (diffMetaCompListeners==null) 
			diffMetaCompListeners = new HashSet<IDiffractionMetadataCompositeListener>(5);
		diffMetaCompListeners.add(l);
	}
	/**
	 * Call from dispose of part listening to listen to detector properties changing
	 * @param l
	 */
	public void removeDiffractionMetadataCompositeListener(IDiffractionMetadataCompositeListener l) {
		if (diffMetaCompListeners==null) 
			return;
		diffMetaCompListeners.remove(l);
	}
	
	protected void fireDiffractionMetadataCompositeListeners(DiffractionMetadataCompositeEvent evt) {
		if (diffMetaCompListeners==null) 
			return;
		for (IDiffractionMetadataCompositeListener l : diffMetaCompListeners) {
			l.diffractionMetadataCompositeChanged(evt);
		}
	}


}
