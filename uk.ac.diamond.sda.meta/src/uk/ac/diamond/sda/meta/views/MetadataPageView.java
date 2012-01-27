package uk.ac.diamond.sda.meta.views;

import java.io.File;
import java.util.ArrayList;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExtension;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import org.dawb.common.services.ILoaderService;

import uk.ac.diamond.scisoft.analysis.dataset.IMetadataProvider;
import uk.ac.diamond.scisoft.analysis.io.IMetaData;
import uk.ac.diamond.sda.meta.contribution.MetadataPageContribution;
import uk.ac.diamond.sda.meta.page.HeaderTablePage;
import uk.ac.diamond.sda.meta.page.IMetadataPage;
import uk.ac.gda.common.rcp.util.EclipseUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetadataPageView extends ViewPart implements ISelectionListener, IPartListener {
	private static final Logger logger = LoggerFactory.getLogger(MetadataPageView.class);
	
	private IMetaData meta;
	private HeaderTablePage htp;
	private ArrayList<MetadataPageContribution>pagesRegister = new ArrayList<MetadataPageContribution>();

	private IToolBarManager toolBarManager;

	private static final String PAGE_EXTENTION_ID = "uk.ac.diamond.sda.meta.metadataPageRegister";

	public MetadataPageView(){
		super();
		getExtentionPoints();
	}
	
	private void getExtentionPoints() {
		IExtension[] extentionPoints = Platform.getExtensionRegistry().getExtensionPoint(PAGE_EXTENTION_ID).getExtensions();
		for (int i=0;i<extentionPoints.length;i++){
			IExtension extension = extentionPoints[i];
			IConfigurationElement[] configElements = extension.getConfigurationElements();
			for (int j = 0; j < configElements.length; j++) { 
					pagesRegister.add(new MetadataPageContribution(configElements[j]));
			}
		}
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
							metadataChanged(((IMetadataProvider) sel).getMetadata());
						} catch (Exception e) {
							logger.error("Could not capture metadata from selection",e);
						}
					}
				}
		}
	}
	
	private void metadataChanged(IMetaData meta){
		//this method should react to the different types of metadata 
		toolBarManager.removeAll();
		for(MetadataPageContribution mpc:pagesRegister){
			if(mpc.isApplicableFor(meta)){
				pageActionFactory(mpc);
			}
		}
		//htp.setMetaData(meta);
	}
	
	private void pageActionFactory(final MetadataPageContribution mpc) {
		final Action metadatapage = new Action(mpc.getExtentionPointname()) {
			@Override
			public void run() {
				
				try {
					IMetadataPage imetadataPage = mpc.getPage();
					
				} catch (CoreException e) {
					logger.warn("Could not create "+mpc.getExtentionPointname());
				}
			}
			
		};
		toolBarManager.add(metadatapage);
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

				metadataChanged(meta);

				return Status.OK_STATUS;
			}

		};

		metaJob.schedule();
	}

	@Override
	public void partActivated(IWorkbenchPart part) {

		if (part instanceof IMetadataProvider) {
			try {
				metadataChanged(((IMetadataProvider) part).getMetadata());
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
				metadataChanged(((IMetadataProvider) part).getMetadata());
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

	@Override
	public void createPartControl(Composite parent) {
		//composite
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout(1, true));
		
		getSite().getPage().getWorkbenchWindow().getSelectionService().addSelectionListener(this);
		getSite().getPage().addPartListener(this);
		
		//add some toolbar
		toolBarManager = getViewSite().getActionBars().getToolBarManager();
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

	
}
