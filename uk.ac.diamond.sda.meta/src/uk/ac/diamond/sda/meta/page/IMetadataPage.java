package uk.ac.diamond.sda.meta.page;

import org.eclipse.swt.widgets.Composite;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;

public interface IMetadataPage {

	/**
	 * This is a setter that will allow the page to process the metadata. 
	 * @param metadata
	 */
	public void setMetaData(IMetaData metadata);
	
	/**
	 * Each IMetadata Page should be capable of returning a composite containing the GUI elements
	 * @param parent
	 * @return a composite containing a gui
	 */
	public Composite createComposite(Composite parent);
	
}