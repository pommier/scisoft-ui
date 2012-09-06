package uk.ac.diamond.scisoft.rp.views;


import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import uk.ac.diamond.scisoft.rp.composites.PVRotSnapshotComposite;

public class PVRotSnapshotView extends ViewPart {
	
	public static final String ID = "uk.ac.diamond.scisoft.RPSliceAnimation";

	public PVRotSnapshotView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new PVRotSnapshotComposite(parent, SWT.NONE);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
