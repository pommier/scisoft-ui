package uk.ac.diamond.sda.meta.tools;

import org.dawb.common.ui.plot.tool.AbstractToolPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPart;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.page.DiffractionMetadataComposite;
import uk.ac.diamond.sda.meta.page.IDiffractionMetadataCompositeListener;

public class MetadataTool extends AbstractToolPage {

	protected DiffractionMetadataComposite diffMetadataComp;
	protected Composite composite;
	
	public MetadataTool() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void createControl(Composite parent) {
		diffMetadataComp = new DiffractionMetadataComposite();
		diffMetadataComp.setEditable(true);
		composite = diffMetadataComp.createComposite(parent);
		IMetaData meta = getImageTrace().getData().getMetadata();
		diffMetadataComp.setMetaData(meta);
		
		// Add the MXPlotImageEditor as a listener
		IWorkbenchPart viewPart = getPart();
		if (viewPart instanceof IDiffractionMetadataCompositeListener) {
			IDiffractionMetadataCompositeListener diffMetaCompListener = (IDiffractionMetadataCompositeListener)viewPart;
			diffMetadataComp.addDiffractionMetadataCompositeListener(diffMetaCompListener);
		}
		
		activate();
	}

	@Override
	public void dispose() {
		super.dispose();
		
		// Remove the MXPlotImageEditor as a listener
		IWorkbenchPart viewPart = getPart();
		if (viewPart instanceof IDiffractionMetadataCompositeListener) {
			IDiffractionMetadataCompositeListener diffMetaCompListener = (IDiffractionMetadataCompositeListener)viewPart;
			diffMetadataComp.removeDiffractionMetadataCompositeListener(diffMetaCompListener);
		}
	}

	@Override
	public Control getControl() {
		return composite;
	}

	@Override
	public ToolPageRole getToolPageRole() {
		return ToolPageRole.ROLE_2D;
	}

	@Override
	public void setFocus() {
		composite.setFocus();
	}
}
