package uk.ac.diamond.sda.meta.views;

import java.io.File;
import java.util.HashMap;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.IPage;
import org.eclipse.ui.part.PageBook;
import org.eclipse.ui.part.PageBookView;

import org.dawb.common.services.ILoaderService;

import uk.ac.diamond.scisoft.analysis.dataset.IMetadataProvider;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.page.HeaderTablePage;
import uk.ac.diamond.sda.meta.page.IMetadataPage;
import uk.ac.gda.common.rcp.util.EclipseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataPageBookView extends PageBookView implements ISelectionListener, IPartListener {
	private static final Logger logger = LoggerFactory.getLogger(MetadataPageBookView.class);
	private IMetaData meta;
	private HeaderTablePage htp;
	private HashMap<String, IMetadataPage> pagesRegister;

	private static final String PAGE_EXTENTION_ID = "uk.ac.diamond.sda.meta.metadataPageRegister";

	public MetadataPageBookView(){
		super();
		getExtentionPoints();
	}
	
	private void getExtentionPoints() {
		pagesRegister = new HashMap<String, IMetadataPage>();
		IExtension[] extentionPoints = Platform.getExtensionRegistry().getExtensionPoint(PAGE_EXTENTION_ID).getExtensions();
		for (int i=0;i<extentionPoints.length;i++){
			IExtension extension = extentionPoints[i];
			IConfigurationElement[] configElements = extension.getConfigurationElements();
		}
	}

	@Override
	protected IPage createDefaultPage(PageBook book) {
		// Instead of sample controller we use the workbench
		// selection. If this is an image editor part, then we know that to do.
		getSite().getPage().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		getSite().getPage().addPartListener(this);

		// create a list of pages that extend this extention point
		
		
		htp = new HeaderTablePage();
		initPage(htp);
		htp.createControl(book);
		return htp;
	}

	@Override
	protected PageRec doCreatePage(IWorkbenchPart part) {
		return null;
	}

	@Override
	protected void doDestroyPage(IWorkbenchPart part, PageRec pageRecord) {
		// TODO Auto-generated method stub

	}

	@Override
	protected IWorkbenchPart getBootstrapPart() {
		// TODO Auto-generated method stub
		return null;
	}

	//this is not necessary as the metadata view is looking at 
	//many parts and selections and the pagebook view is not 
	//associated with a particular view or flavour of view
	@Override
	protected boolean isImportant(IWorkbenchPart part) {
		return true;
	}

	@Override
	public void selectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part instanceof IMetadataProvider)
			try {
				htp.setMetaData(((IMetadataProvider) part).getMetadata());
			} catch (Exception e) {
				logger.error(
						"There was a error reading the metadata from the selection",
						e);
			}
		else {
			if (selection == null)
				if (selection instanceof StructuredSelection) {
					// this.lastSelection = (StructuredSelection) selection;
					final Object sel = ((StructuredSelection) selection)
							.getFirstElement();

					if (sel instanceof IFile) {
						final String filePath = ((IFile) sel).getLocation()
								.toOSString();
						updatePath(filePath);
					} else if (sel instanceof File) {
						final String filePath = ((File) sel).getAbsolutePath();
						updatePath(filePath);
					} else if (sel instanceof IMetadataProvider) {
						try {
							htp.setMetaData(((IMetadataProvider) sel).getMetadata());
						} catch (Exception e) {
							logger.error("Could not capture metadata from selection",e);
						}
					}
				}
		}
	}
	
	
	private void updatePath(final String filePath) {
		final Job metaJob = new Job("Extra Meta Data " + filePath) {

			@Override
			protected IStatus run(IProgressMonitor monitor) {

				final ILoaderService service = (ILoaderService) PlatformUI
						.getWorkbench().getService(ILoaderService.class);
				try {
					meta = service.getMetaData(filePath, monitor);
				} catch (Exception e1) {
					logger.error("Cannot get meta data for " + filePath, e1);
					return Status.CANCEL_STATUS;
				}

				htp.setMetaData(meta);

				return Status.OK_STATUS;
			}

		};

		metaJob.schedule();
	}

	@Override
	public void partActivated(IWorkbenchPart part) {

		if (part instanceof IMetadataProvider) {
			try {
				htp.setMetaData(((IMetadataProvider) part).getMetadata());
			} catch (Exception e) {
				logger.warn("Could not get metadata from currently active window");
			}
		}
		if (part instanceof IEditorPart) {
			final IEditorPart ed = (IEditorPart) part;
			final IEditorInput in = ed.getEditorInput();
			final String path = EclipseUtils.getFilePath(in);
			updatePath(path);
		}

	}

	@Override
	public void partBroughtToTop(IWorkbenchPart part) {
		if (part instanceof IMetadataProvider) {
			try {
				htp.setMetaData(((IMetadataProvider) part).getMetadata());
			} catch (Exception e) {
				logger.warn("Could not get metadata from currently active window");
			}
		}
	}

	@Override
	public void partClosed(IWorkbenchPart part) {

	}

	@Override
	public void partDeactivated(IWorkbenchPart part) {

	}

	@Override
	public void partOpened(IWorkbenchPart part) {

	}
}
