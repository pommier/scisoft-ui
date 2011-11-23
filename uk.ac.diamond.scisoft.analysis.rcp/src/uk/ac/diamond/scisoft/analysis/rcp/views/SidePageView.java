package uk.ac.diamond.scisoft.analysis.rcp.views;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.MessagePage;
import org.eclipse.ui.part.Page;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.IMetadataProvider;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;



public class SidePageView extends PageBookView {

	public static final String ID = "uk.ac.diamond.scisoft.diffraction.rcp.DiffractionView";
	private static final Logger logger = LoggerFactory.getLogger(SidePageView.class);
	
	
	@Override
	protected IPage createDefaultPage(PageBook book) {
		MessagePage messagePage = new MessagePage();
		initPage(messagePage);
		messagePage.setMessage("This is the Diffraction viewer");
		messagePage.createControl(book);
		return messagePage;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(
				"uk.ac.diamond.scisoft.analysis.rcp.diffractionpage");

		IMetaData metadata = null;
		if (part instanceof IMetadataProvider) {
			try {
				metadata = ((IMetadataProvider) part).getMetadata();
			} catch (NullPointerException npe) {
				logger.warn("Metadata could not be found");
			}
		}
		Page page = null;
		try {
			for (IConfigurationElement e : config) {
				final Object o = e.createExecutableExtension("class");
				if (o instanceof ISidePageView) {
					((ISidePageView) o).setMetadataObject(metadata);
					if (o instanceof Page)
						page = (Page) o;
					if (page != null) {
						initPage(page);
						page.createControl(getPageBook());
						return new PageRec(part, page);
					}
				}
			}
		} catch (CoreException ex) {
			logger.warn("Could not find a page");
		}
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec record) {
		record.page.dispose();
	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		IWorkbenchPage page = getSite().getPage();
		if (page != null) {
			IWorkbenchPart part = page.getActivePart();
			return isImportant(part) ? part : null;
		}
		return null;
	}

	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return part instanceof IMetadataProvider;
	}

	@Override
	public void partActivated(IWorkbenchPart part) {

		super.partActivated(part);

		final IPage page = getCurrentPage();
		final String title = page instanceof IAdaptable ? (String) ((IAdaptable) page)
				.getAdapter(String.class) : null;
		if (title != null) {
			setPartName(title);
		} else {
			setPartName("Data");
		}

	}
}
