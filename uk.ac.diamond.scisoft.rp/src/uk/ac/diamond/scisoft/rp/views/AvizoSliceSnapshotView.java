package uk.ac.diamond.scisoft.rp.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import uk.ac.diamond.scisoft.rp.composites.AvizoSliceSnapshotComposite;

public class AvizoSliceSnapshotView extends ViewPart {

	public AvizoSliceSnapshotView() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void createPartControl(Composite parent) {
		new AvizoSliceSnapshotComposite(parent, SWT.NONE);

	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
