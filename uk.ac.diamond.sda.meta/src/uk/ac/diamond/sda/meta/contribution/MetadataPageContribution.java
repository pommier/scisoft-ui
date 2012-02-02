package uk.ac.diamond.sda.meta.contribution;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.jface.resource.ImageDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.Activator;
import uk.ac.diamond.sda.meta.discriminator.IMetadataDiscriminator;
import uk.ac.diamond.sda.meta.page.IMetadataPage;

public class MetadataPageContribution {

	private static final Logger logger = LoggerFactory.getLogger(MetadataPageContribution.class);
	
	public static final String CLASS_NAME = "class";
	public static final String NAME = "name";
	public static final String SUPPORTED_METADATA = "supportedMetadata";
	public static final String ICON = "icon";
	
	private String extentionPointname;
	private ImageDescriptor icon;
	final private IConfigurationElement configurationElement;
	private IMetadataDiscriminator discriminator;
	
	public MetadataPageContribution(IConfigurationElement iConfigurationElement) {
		this.configurationElement = iConfigurationElement;
		icon = Activator.getImageDescriptor(iConfigurationElement.getAttribute(ICON));
		extentionPointname = iConfigurationElement.getAttribute(NAME);
		try {
			discriminator = (IMetadataDiscriminator)iConfigurationElement.createExecutableExtension(SUPPORTED_METADATA);
		} catch (CoreException e) {
			logger.warn("Could not load the discriminator for different metadata types");
		}
	}

	public IMetadataPage getPage() throws CoreException {
		return (IMetadataPage)configurationElement.createExecutableExtension(CLASS_NAME);
	}

	public String getExtentionPointname() {
		return extentionPointname;
	}

	public ImageDescriptor getIcon() {
		return icon;
	}

	
	public boolean isApplicableFor(IMetaData meta){
		if(discriminator == null)
			return false;
		return discriminator.isApplicableFor(meta);
	}
	
}
