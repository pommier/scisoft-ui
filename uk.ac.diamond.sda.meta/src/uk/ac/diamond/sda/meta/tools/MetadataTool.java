package uk.ac.diamond.sda.meta.tools;

import org.dawb.common.ui.plot.tool.AbstractToolPage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;

import uk.ac.diamond.sda.meta.page.DiffractionMetadataComposite;

public class MetadataTool extends AbstractToolPage {

	protected DiffractionMetadataComposite diffMetadataComp;
	
	public MetadataTool() {
		// TODO Auto-generated constructor stub
		
	}

	@Override
	public void createControl(Composite parent) {
		// TODO Auto-generated method stub
		diffMetadataComp = new DiffractionMetadataComposite();

	}

	@Override
	public void dispose() {
		super.dispose();
	}

	@Override
	public Control getControl() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ToolPageRole getToolPageRole() {
		return ToolPageRole.ROLE_2D;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
