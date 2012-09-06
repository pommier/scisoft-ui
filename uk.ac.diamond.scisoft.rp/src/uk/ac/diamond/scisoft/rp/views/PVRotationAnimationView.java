package uk.ac.diamond.scisoft.rp.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import uk.ac.diamond.scisoft.rp.composites.PVRotAnimComposite;

public class PVRotationAnimationView extends ViewPart {

	public PVRotationAnimationView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		PVRotAnimComposite comp = new PVRotAnimComposite(parent, SWT.NONE);	
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
