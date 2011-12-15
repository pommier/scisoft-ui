package uk.ac.diamond.sda.navigator.views;

import java.io.File;

import org.dawb.common.services.IFileIconService;
import org.dawb.common.services.ServiceManager;
import org.dawb.common.ui.views.ImageMonitorView;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.sda.intro.navigator.NavigatorRCPActivator;
import uk.ac.diamond.sda.navigator.views.FileContentProvider.FileSortType;
import uk.ac.gda.common.rcp.util.EclipseUtils;
import uk.ac.gda.ui.actions.CheckableActionGroup;
import uk.ac.gda.ui.content.FileContentProposalProvider;
import uk.ac.gda.util.OSUtils;

/**
 * This class navigates a file system and remembers where you last left it. 
 * 
 * It is lazy in loading the file tree.
 *
 */
public class FileView extends ViewPart {

	public static final String ID = "uk.ac.diamond.sda.navigator.views.FileView";
	
    private static final Logger logger = LoggerFactory.getLogger(FileView.class);
	
	private TreeViewer tree;

	private File savedSelection;
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		
		super.init(site, memento);
		
		if (memento==null) return;
		final String path = memento.getString("DIR");
		if (path!=null) {
			savedSelection = new File(path);
		}
	}

	@Override
	public void saveState(IMemento memento) {
		
		if (memento==null) return;
		if ( getSelectedFile() != null ) {
		    final String path = getSelectedFile().getAbsolutePath();
		    memento.putString("DIR", path);
		}
	}

	/**
	 * Get the file path selected
	 * 
	 * @return String
	 */
	public File getSelectedFile() {
		return (File)((IStructuredSelection)tree.getSelection()).getFirstElement();
	}

	
	@Override
	public void createPartControl(final Composite parent) {
		
		parent.setLayout(new GridLayout(1, false));
		
		final Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(2, false));
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		
		final Label fileLabel = new Label(top, SWT.NONE);
		fileLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		
		try {
			IFileIconService service = (IFileIconService)ServiceManager.getService(IFileIconService.class);
			final Image       icon    = service.getIconForFile(OSUtils.isWindowsOS() ? new File("C:/Windows/") : new File("/"));
			fileLabel.setImage(icon);
		} catch (Exception e) {
			logger.error("Cannot get icon for system root!", e);
		}
		
		final Text filePath = new Text(top, SWT.BORDER);
		if (savedSelection!=null) filePath.setText(savedSelection.getAbsolutePath());
		filePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		FileContentProposalProvider prov = new FileContentProposalProvider();
		ContentProposalAdapter ad = new ContentProposalAdapter(filePath, new TextContentAdapter(), prov, null, null);
		ad.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		ad.addContentProposalListener(new IContentProposalListener() {		
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				setSelectedFile(filePath.getText());
			}
		});
		
		filePath.addSelectionListener(new SelectionListener() {			
			@Override
			public void widgetSelected(SelectionEvent e) {
				setSelectedFile(filePath.getText());
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				setSelectedFile(filePath.getText());
			}
		});

		tree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER |SWT.VIRTUAL);
		tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.getTree().setHeaderVisible(true);
		tree.setUseHashlookup(true);
		
		tree.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				
				final File file = getSelectedFile();
				if (file!=null &&  file.isDirectory()) {
				    filePath.setText(file.getAbsolutePath());
				    filePath.setSelection(filePath.getText().length());
				}
			}
		});

		final String[] titles = { "Name", "Date", "Type", "Size" };
		final int[]    widths = { 250, 120, 80, 150 };
		TreeViewerColumn tVCol;
		for (int i = 0; i < titles.length; i++) {
			tVCol = new TreeViewerColumn(tree, SWT.NONE);
			TreeColumn tCol = tVCol.getColumn();
			tCol.setText(titles[i]);
			tCol.setWidth(widths[i]);
			tCol.setMoveable(true);
			try {
				tVCol.setLabelProvider(new FileLabelProvider(i));
			} catch (Exception e1) {
				logger.error("Cannot create label provider "+i, e1);
			}
		}
		getSite().setSelectionProvider(tree);
		
		createContent();
				
		// Make drag source, it can then drag into projects
		final DragSource dragSource = new DragSource(tree.getControl(), DND.DROP_MOVE| DND.DROP_DEFAULT| DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { FileTransfer.getInstance () });
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event){
				if (getSelectedFile()==null) return;
				event.data = new String[]{getSelectedFile().getAbsolutePath()};
			}
		});
		
		// Add ability to open any file double clicked on (folders open in Image Monitor View)
		tree.getTree().addListener(SWT.MouseDoubleClick, new Listener() {

             @Override
			public void handleEvent(Event event) {
                 openSelectedFile();
             }
         });
		
		tree.getTree().addKeyListener(new KeyListener() {		
			@Override
			public void keyReleased(KeyEvent e) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character == '\n') {
					openSelectedFile();
				}
			}
		});

		createRightClickMenu();
		addToolbar();

		if (savedSelection!=null) {
			if (savedSelection.exists()) {
			    tree.setSelection(new StructuredSelection(savedSelection));
			} else if (savedSelection.getParentFile().exists()) {
				// If file deleted, select parent.
				tree.setSelection(new StructuredSelection(savedSelection.getParentFile()));
			}
		}
		

	}

	protected void setSelectedFile(String path) {
		final File file = new File(path);
		if (file.exists()) {
			tree.setSelection(new StructuredSelection(file));
		}	
	}

	private void createRightClickMenu() {
		final MenuManager menuManager = new MenuManager();
		tree.getControl().setMenu(menuManager.createContextMenu(tree.getControl()));
		getSite().registerContextMenu(menuManager, tree);
	}
	
    /**
     * Never really figured out how to made toggle buttons work properly with
     * contributions. Use hard coded actions
     * 
     * TODO Move this to contributi	
     */
	private void addToolbar() {
		
        // TODO Save preference as property
		
		final IToolBarManager toolMan = getViewSite().getActionBars().getToolBarManager();
		
		final CheckableActionGroup grp = new CheckableActionGroup();
		
		final Action dirsTop = new Action("Sort alpha numeric, directories at top.", IAction.AS_CHECK_BOX) {
			public void run() {
				final File selection = getSelectedFile();
				((FileContentProvider)tree.getContentProvider()).setSort(FileSortType.ALPHA_NUMERIC_DIRS_FIRST);
				tree.refresh();
				if (selection!=null)tree.setSelection(new StructuredSelection(selection));
			}
		};
		dirsTop.setImageDescriptor(NavigatorRCPActivator.getImageDescriptor("icons/alpha_mode_folder.png"));
		dirsTop.setChecked(true);
		grp.add(dirsTop);
		toolMan.add(dirsTop);
		
		
		final Action alpha = new Action("Alpha numeric sort for everything.", IAction.AS_CHECK_BOX) {
			public void run() {
				final File selection = getSelectedFile();
				((FileContentProvider)tree.getContentProvider()).setSort(FileSortType.ALPHA_NUMERIC);
				tree.refresh();
				if (selection!=null)tree.setSelection(new StructuredSelection(selection));
			}
		};
		alpha.setImageDescriptor(NavigatorRCPActivator.getImageDescriptor("icons/alpha_mode.gif"));
		grp.add(alpha);
		toolMan.add(alpha);

	}



	protected void openSelectedFile() {
		final File file = getSelectedFile();
		if (file==null) return;
		
		if (file.isDirectory()) {
			final IWorkbenchPage page = EclipseUtils.getActivePage();
			if (page==null) return;
			
			IViewPart part=null;
			try {
				part = page.showView("org.dawb.workbench.views.imageMonitorView");
			} catch (PartInitException e) {
				// TODO Auto-generated catch block
				logger.error("TODO put description of error here", e);
				return;
			}
			if (part != null && part instanceof ImageMonitorView) {
			    ((ImageMonitorView)part).setDirectoryPath(file.getAbsolutePath());
			}
			
		} else { // Open file
			
			try {
				EclipseUtils.openExternalEditor(file.getAbsolutePath());
			} catch (PartInitException e) {
				logger.error("Cannot open file "+file, e);
			}
		}
	}

	@Override
	public void dispose() {
		super.dispose();
		
		// TODO Any other disposals?
	}

	@Override
	public void setFocus() {
		tree.getControl().setFocus();
	}

	private void createContent() {
		
		final File root = uk.ac.gda.util.OSUtils.isWindowsOS() ? new File("C:/") : new File("/");
		tree.getTree().setItemCount(root.listFiles().length);
		tree.setUseHashlookup(true);
		tree.setContentProvider(new FileContentProvider());
		tree.setInput(root);
		tree.expandToLevel(1);
	}

	

    /**
     * The adapter IContentProvider gives the value of the H5Dataset
     */
	public Object getAdapter(final Class clazz) {

		return super.getAdapter(clazz);
		
		// TODO returns an adapter part for 'IPage' which is a page summary for the file or folder?
	}
	
}
