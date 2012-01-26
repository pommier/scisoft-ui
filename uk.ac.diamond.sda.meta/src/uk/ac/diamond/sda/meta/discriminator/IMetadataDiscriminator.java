package uk.ac.diamond.sda.meta.discriminator;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;

public interface IMetadataDiscriminator {

	
	/**
	 * This method returns true if the metadata being presented can be processed by the the page
	 * @param metadata
	 * @return is the page can process the metadata
	 */
	public boolean isApplicableFor(IMetaData metadata);
}
