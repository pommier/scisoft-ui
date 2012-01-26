package uk.ac.diamond.sda.meta.discriminator;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;

public class MetadataDiscriminator implements IMetadataDiscriminator{

	public MetadataDiscriminator(){
	}
	
	@Override
	public boolean isApplicableFor(IMetaData metadata) {
		if(metadata instanceof IMetaData)
			return true;
		return false;
	}

}
