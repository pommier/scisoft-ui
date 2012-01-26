package uk.ac.diamond.sda.meta.discriminator;

import uk.ac.diamond.scisoft.analysis.io.IDiffractionMetadata;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;

public class DiffractionMetadataDiscriminator implements IMetadataDiscriminator {

	public DiffractionMetadataDiscriminator(){
		
	}
	@Override
	public boolean isApplicableFor(IMetaData metadata) {
		if(metadata instanceof IDiffractionMetadata)
			return true;
		return false;
	}

}
