package uk.ac.diamond.sda.meta.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.Page;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.views.HeaderTableView;

public class HeaderTablePage extends Page implements IMetadataPage{

	private Composite control;
	HeaderTableView view = null;
	@Override
	public void createControl(Composite parent) {
		
		this.control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(1,false));
		view = new HeaderTableView();
        view.createPartControl(control);
	}

	@Override
	public Control getControl() {
		return control;
	}

	@Override
	public void dispose() {
		control.dispose();
		super.dispose();
	}
	@Override
	public void setFocus() {
		control.setFocus();
	}

	/* (non-Javadoc)
	 * @see uk.ac.diamond.sda.meta.page.IMetadataPage#setMetaData(uk.ac.diamond.scisoft.analysis.io.IMetaData)
	 */
	@Override
	public void setMetaData(IMetaData metadata){
		view.setMeta(metadata);
	}
}
