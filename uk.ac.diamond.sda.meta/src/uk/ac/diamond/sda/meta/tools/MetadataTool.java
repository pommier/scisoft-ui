package uk.ac.diamond.sda.meta.tools;

import org.dawb.common.ui.plot.tool.AbstractToolPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.page.DiffractionMetadataComposite;



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
		activate();
	}

	@Override
	public void dispose() {
		super.dispose();
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
