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

import javax.vecmath.Vector3d;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.progress.UIJob;

import uk.ac.diamond.scisoft.analysis.diffraction.DetectorProperties;
import uk.ac.diamond.scisoft.analysis.diffraction.DiffractionCrystalEnvironment;
import uk.ac.diamond.scisoft.analysis.io.IDiffractionMetadata;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.scisoft.analysis.rcp.util.FloatSpinner;
import uk.ac.diamond.sda.meta.Activator;

public class DiffractionMetadataComposite implements IMetadataPage {

	private Text wavelength;
	private Text phiStart;
	private Text phiStop;
	private Text phiRange;
	private FloatSpinner distanceToDetector;
	private Text detectorSizeX;
	private Text detectorSizeY;
	private Text PixelSizeX;
	private Text PixelSizeY;
	private Text ExposureTime;
	private Text maxPxVal;
	private Text minPxVal;
	private Text meanPxVal;
	private Text overload;
	private Button showBeam;
	private Text xBeam;
	private Text yBeam;
	private Composite content;
	private double[] beam;
	private DetectorProperties detprop;
	private DiffractionCrystalEnvironment diffenv;
	private boolean editable;
	private HashSet<IDiffractionMetadataCompositeListener> diffMetaCompListeners; 

	public DiffractionMetadataComposite() {

	}

	double moveXBeam(int milllimeters) {
		double mm = (beam[0] * detprop.getHPxSize()) + milllimeters;
		xBeam.setText(String.valueOf(mm));
		updateBeamX(mm);
		return mm;
	}
	
	double moveYBeam(int milllimeters) {
		double mm = (beam[1] * detprop.getVPxSize()) + milllimeters;
		yBeam.setText(String.valueOf(mm));
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
	
	@Override
	public Composite createComposite(Composite parent) {

		this.content = new Composite(parent, SWT.NONE);
		content.setLayout(new GridLayout(1, true));

		ScrolledComposite scrComp = new ScrolledComposite(content, SWT.HORIZONTAL | SWT.VERTICAL);
		scrComp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Composite comp = new Composite(scrComp, SWT.FILL);
		comp.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, true));
		comp.setLayout(new GridLayout(2, false));

		Group experimentmetadata = new Group(comp, SWT.NONE);
		experimentmetadata.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 1));
		experimentmetadata.setLayout(new GridLayout(3, false));
		experimentmetadata.setText("Experimental Information");

		{
			Label lblWavelength = new Label(experimentmetadata, SWT.NONE);
			lblWavelength.setText("Wavelength");
		}
		{
			wavelength = new Text(experimentmetadata, SWT.READ_ONLY);
			wavelength.setBackground(experimentmetadata.getBackground());
			wavelength.setEditable(editable);
			
			wavelength.addModifyListener(new ModifyListener() {
				@Override
				public void modifyText(ModifyEvent e) {
					String t = wavelength.getText();
					updateWavelength(Double.valueOf(t));
				}
			});

		}
		new Label(experimentmetadata, SWT.NONE).setText("\u00c5");
		{
			Label lblStart = new Label(experimentmetadata, SWT.NONE);
			lblStart.setText("Start");
		}
		{
			phiStart = new Text(experimentmetadata, SWT.READ_ONLY);
			phiStart.setBackground(experimentmetadata.getBackground());
		}
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");
		{
			Label lblStop = new Label(experimentmetadata, SWT.NONE);
			lblStop.setText("Stop");
		}
		{
			phiStop = new Text(experimentmetadata, SWT.READ_ONLY);
			phiStop.setBackground(experimentmetadata.getBackground());
		}
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");
		{
			Label lblOscillationRange = new Label(experimentmetadata, SWT.NONE);
			lblOscillationRange.setText("Oscillation Range");
		}
		{
			phiRange = new Text(experimentmetadata, SWT.READ_ONLY);
			phiRange.setBackground(experimentmetadata.getBackground());
		}
		new Label(experimentmetadata, SWT.NONE).setText("\u00B0");

		Group detectorMetadata = new Group(comp, SWT.NONE);
		detectorMetadata.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false, 1, 2));
		detectorMetadata.setLayout(new GridLayout(3, false));
		detectorMetadata.setText("Detector Metadata");
		{
			Label lblDistance = new Label(detectorMetadata, SWT.NONE);
			lblDistance.setText("Distance");
		}
		{
			//distanceToDetector = new Text(detectorMetadata, SWT.READ_ONLY);
			
			distanceToDetector = new FloatSpinner(detectorMetadata, SWT.SINGLE | SWT.BORDER | SWT.RIGHT, 6, 2);
			distanceToDetector.setBackground(detectorMetadata.getBackground());
			distanceToDetector.setEnabled(editable);
			
			distanceToDetector.addKeyListener(new KeyListener() {
				@Override
				public void keyReleased(KeyEvent e) {
				}

				@Override
				public void keyPressed(KeyEvent e) {
					if (e.keyCode == SWT.CR) {
						updateDistanceToDetector(distanceToDetector.getDouble());
					}
				}
			});		
		}
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
			PixelSizeX = new Text(detectorMetadata, SWT.READ_ONLY);
			PixelSizeX.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblPixelSizey = new Label(detectorMetadata, SWT.NONE);
			lblPixelSizey.setText("Pixel Size (y)");
		}
		{
			PixelSizeY = new Text(detectorMetadata, SWT.READ_ONLY);
			PixelSizeY.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("mm");
		{
			Label lblTime = new Label(detectorMetadata, SWT.NONE);
			lblTime.setText("Exposure Time");
		}
		{
			ExposureTime = new Text(detectorMetadata, SWT.READ_ONLY);
			ExposureTime.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("s");
		{
			Label lblMaxPxVal = new Label(detectorMetadata, SWT.NONE);
			lblMaxPxVal.setText("Maximum Value");
			lblMaxPxVal.setToolTipText("Maximum pixel value of the dataset being plotted.");
		}
		{
			maxPxVal = new Text(detectorMetadata, SWT.READ_ONLY);
			maxPxVal.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("");
		{
			Label lblMinPxVal = new Label(detectorMetadata, SWT.NONE);
			lblMinPxVal.setText("Minimum Value");
			lblMinPxVal.setToolTipText("Minimum pixel value of the dataset being plotted.");
		}
		{
			minPxVal = new Text(detectorMetadata, SWT.READ_ONLY);
			minPxVal.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("");
		{
			Label lblMeanPxVal = new Label(detectorMetadata, SWT.NONE);
			lblMeanPxVal.setText("Mean Value");
			lblMeanPxVal.setToolTipText("Mean pixel value of the dataset being plotted.");
		}
		{
			meanPxVal = new Text(detectorMetadata, SWT.READ_ONLY);
			meanPxVal.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE).setText("");
		{
			Label lblThreashold = new Label(detectorMetadata, SWT.NONE);
			lblThreashold.setText("Overload Value");
			lblThreashold.setToolTipText("Displays the maximum possible pixel value");
		}
		{
			overload = new Text(detectorMetadata, SWT.READ_ONLY);
			overload.setBackground(detectorMetadata.getBackground());
		}

		new Label(detectorMetadata, SWT.NONE).setText("");
		Group grpBeamCentreControls = new Group(comp, SWT.NONE);
		grpBeamCentreControls.setLayout(new GridLayout(2, false));
		grpBeamCentreControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		grpBeamCentreControls.setText("Beam Centre");

		// 3 x 3 grid for buttons to move beam centre   
		
		Composite beamCentreControls = new Composite(grpBeamCentreControls, SWT.NONE);
		beamCentreControls.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
		beamCentreControls.setLayout(new GridLayout(3, false));

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

		// 2 x 2 grid for labels showing the beam position
		Composite beamSpinners = new Composite(grpBeamCentreControls, SWT.FILL);
		beamSpinners.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
		beamSpinners.setLayout(new GridLayout(2, false));

		// Beam X label
		Label labXBeam = new Label(beamSpinners, SWT.NONE);
		labXBeam.setText("X");

		// Beam X value
		xBeam = new Text(beamSpinners, SWT.BORDER);
		xBeam.setLayoutData(beamSpinners.getLayoutData());
		xBeam.setBackground(grpBeamCentreControls.getBackground());
		xBeam.setEditable(editable);
		xBeam.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String t = xBeam.getText();
				updateBeamX(Double.valueOf(t));
			}
		});
		
		// Beam Y label
		Label labYBeam = new Label(beamSpinners, SWT.NONE);
		labYBeam.setText("Y");

		// Beam Y value
		yBeam = new Text(beamSpinners, SWT.BORDER);
		//yBeam.setSize(width, height);
		yBeam.setLayoutData(beamSpinners.getLayoutData());
		yBeam.setBackground(grpBeamCentreControls.getBackground());
		yBeam.setEditable(editable);
		yBeam.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				String t = yBeam.getText();
				updateBeamY(Double.valueOf(t));
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

	private void updateGUI(final IDiffractionMetadata meta) {
		UIJob update = new UIJob("Updating metadata GUI") {

			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				wavelength.setText(String.valueOf(diffenv.getWavelength()));
				phiStart.setText(String.valueOf(diffenv.getPhiStart()));
				phiStop.setText(String.valueOf(diffenv.getPhiStart() + diffenv.getPhiRange()));
				phiRange.setText(String.valueOf(diffenv.getPhiRange()));

				distanceToDetector.setDouble(detprop.getOrigin().z);
				detectorSizeX.setText(String.valueOf(detprop.getDetectorSizeH()));
				detectorSizeY.setText(String.valueOf(detprop.getDetectorSizeV()));
				PixelSizeX.setText(String.valueOf(detprop.getHPxSize()));
				PixelSizeY.setText(String.valueOf(detprop.getVPxSize()));
				ExposureTime.setText(String.valueOf(diffenv.getExposureTime()));
				maxPxVal.setText("N/A");
				minPxVal.setText(String.valueOf("N/A"));
				meanPxVal.setText(String.valueOf("N/A"));
				overload.setText(String.valueOf("N/A"));
				beam = detprop.getBeamLocation();
				xBeam.setText(String.valueOf(beam[0] * detprop.getHPxSize()));
				yBeam.setText(String.valueOf(beam[1] * detprop.getVPxSize()));

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

				wavelength.setText("");
				phiStart.setText("");
				phiStop.setText("");
				phiRange.setText("");

				distanceToDetector.setDouble(0.0);
				detectorSizeX.setText("");
				detectorSizeY.setText("");
				PixelSizeX.setText("");
				PixelSizeY.setText("");
				ExposureTime.setText("");
				maxPxVal.setText("");
				minPxVal.setText(String.valueOf(""));
				meanPxVal.setText(String.valueOf(""));
				overload.setText(String.valueOf(""));
				xBeam.setText(String.valueOf(""));
				yBeam.setText(String.valueOf(""));
				return Status.OK_STATUS;
			}
		};
		update.schedule();
	}

	@Override
	public void setMetaData(IMetaData metadata) {
		if (metadata instanceof IDiffractionMetadata) {
			this.detprop = ((IDiffractionMetadata)metadata).getDetector2DProperties(); 
			this.diffenv = ((IDiffractionMetadata)metadata).getDiffractionCrystalEnvironment();
			updateGUI((IDiffractionMetadata)metadata);
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
