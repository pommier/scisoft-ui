package uk.ac.diamond.sda.meta.page;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.views.HeaderTableView;

public class HeaderTablePage  implements IMetadataPage {

	private Composite control;
	HeaderTableView view = null;

	public HeaderTablePage() {

	}

	@Override
	public Composite createComposite(Composite parent) {

		this.control = new Composite(parent, SWT.NONE);
		control.setLayout(new GridLayout(1, false));
		view = new HeaderTableView();
		view.createPartControl(control);
		return control;
	}


	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * uk.ac.diamond.sda.meta.page.IMetadataPage#setMetaData(uk.ac.diamond.scisoft
	 * .analysis.io.IMetaData)
	 */
	@Override
	public void setMetaData(IMetaData metadata) {
		view.setMeta(metadata);
	}

}
