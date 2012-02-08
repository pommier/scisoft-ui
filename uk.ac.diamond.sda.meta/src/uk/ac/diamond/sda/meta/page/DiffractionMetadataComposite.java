package uk.ac.diamond.sda.meta.page;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
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
import uk.ac.diamond.sda.meta.Activator;

public class DiffractionMetadataComposite implements IMetadataPage{

	
	private Text wavelength;
	private Text phiStart;
	private Text phiStop;
	private Text phiRange;
	private Text distanceToDetector;
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
	private Composite               content;
	
	public DiffractionMetadataComposite(){
		
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
			distanceToDetector = new Text(detectorMetadata, SWT.READ_ONLY);
			distanceToDetector.setBackground(detectorMetadata.getBackground());

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
		new Label(detectorMetadata, SWT.NONE);
		{
			Label lblMinPxVal = new Label(detectorMetadata, SWT.NONE);
			lblMinPxVal.setText("Minimum Value");
			lblMinPxVal.setToolTipText("Minimum pixel value of the dataset being plotted.");
		}
		{
			minPxVal = new Text(detectorMetadata, SWT.READ_ONLY);
			minPxVal.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE);
		{
			Label lblMeanPxVal = new Label(detectorMetadata, SWT.NONE);
			lblMeanPxVal.setText("Mean Value");
			lblMeanPxVal.setToolTipText("Mean pixel value of the dataset being plotted.");
		}
		{
			meanPxVal = new Text(detectorMetadata, SWT.READ_ONLY);
			meanPxVal.setBackground(detectorMetadata.getBackground());
		}
		new Label(detectorMetadata, SWT.NONE);
		{
			Label lblThreashold = new Label(detectorMetadata, SWT.NONE);
			lblThreashold.setText("Overload Value");
			lblThreashold.setToolTipText("Displays the maximum possible pixel value");
		}
		{
			overload = new Text(detectorMetadata, SWT.READ_ONLY);
			overload.setBackground(detectorMetadata.getBackground());
		}
		
		new Label(detectorMetadata, SWT.NONE);
		{
			Group grpBeamCentreControls = new Group(comp, SWT.NONE);
			grpBeamCentreControls.setLayout(new GridLayout(2, false));
			grpBeamCentreControls.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
			grpBeamCentreControls.setText("Beam Centre");
			{
				Composite beamCentreControls = new Composite(grpBeamCentreControls, SWT.NONE);
				beamCentreControls.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true, 1, 1));
				beamCentreControls.setLayout(new GridLayout(3, false));
				new Label(beamCentreControls, SWT.NONE);
				{
					Button upBeam = new Button(beamCentreControls, SWT.NONE);
					upBeam.setImage(Activator.getImageDescriptor("/icons/arrow_up.png").createImage());
					upBeam.setEnabled(false);
				}
				new Label(beamCentreControls, SWT.NONE);
				{
					Button leftBeam = new Button(beamCentreControls, SWT.NONE);

					leftBeam.setImage(Activator.getImageDescriptor("/icons/arrow_left.png").createImage());
					leftBeam.setEnabled(false);
				}
				{
					showBeam = new Button(beamCentreControls, SWT.TOGGLE);
					showBeam.setToolTipText("Show beam centre");
					showBeam.setImage(Activator.getImageDescriptor("icons/asterisk_yellow.png")
							.createImage());
					showBeam.setEnabled(false);
				}
				{
					Button rightBeam = new Button(beamCentreControls, SWT.NONE);
					rightBeam.setImage(Activator.getImageDescriptor("/icons/arrow_right.png").createImage());
					rightBeam.setEnabled(false);
				}
				new Label(beamCentreControls, SWT.NONE);
				{
					Button downBeam = new Button(beamCentreControls, SWT.NONE);
					downBeam.setImage(Activator.getImageDescriptor("/icons/arrow_down.png").createImage());
					downBeam.setEnabled(false);
				}
				new Label(beamCentreControls, SWT.NONE);

				Composite beamSpinners = new Composite(grpBeamCentreControls, SWT.FILL);
				beamSpinners.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false));
				beamSpinners.setLayout(new GridLayout(2, false));

				Label labXBeam = new Label(beamSpinners, SWT.NONE);
				labXBeam.setText("Beam X");

				xBeam = new Text(beamSpinners, SWT.READ_ONLY);
				xBeam.setBackground(grpBeamCentreControls.getBackground());

				Label labYBeam = new Label(beamSpinners, SWT.NONE);
				labYBeam.setText("Beam Y");

				yBeam = new Text(beamSpinners, SWT.READ_ONLY);
				yBeam.setBackground(grpBeamCentreControls.getBackground());
			}
		}

		scrComp.setContent(comp);
		final Point controlsSize = comp.computeSize(SWT.DEFAULT, SWT.DEFAULT);
		comp.setSize(controlsSize);
		
		return content;
	}


	private void updateGUI(final IDiffractionMetadata meta) {
		UIJob update = new UIJob("Updating metadata GUI") {
		
			@Override
			public IStatus runInUIThread(IProgressMonitor monitor) {
				
				DetectorProperties detprop = meta.getDetector2DProperties();
				DiffractionCrystalEnvironment diffenv = meta.getDiffractionCrystalEnvironment();
				wavelength.setText(String.valueOf(diffenv.getWavelength()));
				phiStart.setText(String.valueOf(diffenv.getPhiStart()));
				phiStop.setText(String.valueOf(diffenv.getPhiStart()+diffenv.getPhiRange()));
				phiRange.setText(String.valueOf(diffenv.getPhiRange()));;
				distanceToDetector.setText(String.valueOf(detprop.getOrigin().z));
				detectorSizeX.setText(String.valueOf(detprop.getDetectorSizeH()));
				detectorSizeY.setText(String.valueOf(detprop.getDetectorSizeV()));
				PixelSizeX.setText(String.valueOf(detprop.getHPxSize()));
				PixelSizeY.setText(String.valueOf(detprop.getVPxSize()));
				ExposureTime.setText(String.valueOf(diffenv.getExposureTime()));
				maxPxVal.setText("N/A");
				minPxVal.setText(String.valueOf("N/A"));
				meanPxVal.setText(String.valueOf("N/A"));
				overload.setText(String.valueOf("N/A"));
				int[] beam = detprop.pixelCoords(detprop.getBeamPosition());
				xBeam.setText(String.valueOf(beam[0]*detprop.getHPxSize()));
				yBeam.setText(String.valueOf(beam[1]*detprop.getVPxSize()));
				return Status.OK_STATUS;
			}
		};
		update.schedule();
	}

//	@Override
//	public void update(Object source, Object arg) {
//		if(source instanceof IMetadataProvider){
//			IMetaData localMetadata;
//			try {
//				localMetadata = ((IMetadataProvider)source).getMetadata();
//				if(localMetadata instanceof IDiffractionMetadata){
//					updateGUI((IDiffractionMetadata)localMetadata);
//				}else{
//					clearGUI();
//				}
//			} catch (Exception e) {
//				logger.error("Could not get metadata",e);
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
				phiRange.setText("");;
				distanceToDetector.setText("");
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
		if(metadata instanceof IDiffractionMetadata){
			updateGUI((IDiffractionMetadata)metadata);
		}if(metadata ==null){
			clearGUI();
		}
	}


}
