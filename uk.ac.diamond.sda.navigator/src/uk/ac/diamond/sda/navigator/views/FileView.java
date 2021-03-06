/*
 * Copyright 2012 Diamond Light Source Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package uk.ac.diamond.sda.navigator.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.dawb.common.services.IFileIconService;
import org.dawb.common.services.ServiceManager;
import org.dawb.common.ui.menu.CheckableActionGroup;
import org.dawb.common.ui.util.EclipseUtils;
import org.dawb.common.ui.views.ImageMonitorView;
import org.dawb.common.util.io.IFileSelector;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.fieldassist.ContentProposalAdapter;
import org.eclipse.jface.fieldassist.IContentProposal;
import org.eclipse.jface.fieldassist.IContentProposalListener;
import org.eclipse.jface.fieldassist.TextContentAdapter;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceAdapter;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
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
import uk.ac.gda.ui.content.FileContentProposalProvider;
import uk.ac.gda.util.OSUtils;

/**
 * This class navigates a file system and remembers where you last left it. 
 * 
 * It is lazy in loading the file tree.
 *
 */
public class FileView extends ViewPart implements IFileSelector {

	public static final String ID = "uk.ac.diamond.sda.navigator.views.FileView";
	
    private static final Logger logger = LoggerFactory.getLogger(FileView.class);
	
	private TreeViewer tree;

	private File savedSelection;
	private Text filePath;
	
	public FileView() {
		super();
	}
	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		
		super.init(site, memento);
		
		String path = null;
		if (memento!=null) path = memento.getString("DIR");
		if (path==null) path = System.getProperty("uk.ac.diamond.sda.navigator.default.file.view.location");
		if (path==null) path = System.getProperty("user.home");
		
		if (path!=null){
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
		File sel = (File)((IStructuredSelection)tree.getSelection()).getFirstElement();
		if (sel==null) sel = savedSelection;
		return sel;
	}
	
	/**
	 * Get the file paths selected
	 * 
	 * @return String[]
	 */
	public String[] getSelectedFiles() {
		Object[] objects = ((IStructuredSelection)tree.getSelection()).toArray();
		if (tree.getSelection()==null || tree.getSelection().isEmpty()) 
			objects = new Object[]{savedSelection};
		
		String absolutePaths [] = new String[objects.length];
		for (int i=0; i < objects.length; i++) {
			absolutePaths[i] = ((File) (objects[i])).getAbsolutePath();
		}
		return absolutePaths;
	}
	
	private boolean updatingTextFromTreeSelections=true;
	
	@Override
	public void createPartControl(final Composite parent) {
		
		parent.setLayout(new GridLayout(1, false));
		
		final Composite top = new Composite(parent, SWT.NONE);
		top.setLayout(new GridLayout(2, false));
		top.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		
		final Label fileLabel = new Label(top, SWT.NONE);
		fileLabel.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, false, false));
		try {
			IFileIconService service = (IFileIconService)ServiceManager.getService(IFileIconService.class);
			final Image       icon    = service.getIconForFile(OSUtils.isWindowsOS() ? new File("C:/Windows/") : new File("/"));
			fileLabel.setImage(icon);
		} catch (Exception e) {
			logger.error("Cannot get icon for system root!", e);
		}
		
		this.filePath = new Text(top, SWT.BORDER);
		if (savedSelection!=null) filePath.setText(savedSelection.getAbsolutePath());
		filePath.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		final FileContentProposalProvider   prov = new FileContentProposalProvider();
		final TextContentAdapter         adapter = new TextContentAdapter();
		final ContentProposalAdapter ad = new ContentProposalAdapter(filePath, adapter, prov, null, null);
		ad.setProposalAcceptanceStyle(ContentProposalAdapter.PROPOSAL_REPLACE);
		ad.addContentProposalListener(new IContentProposalListener() {		
			@Override
			public void proposalAccepted(IContentProposal proposal) {
				final String path = proposal.getContent();
				try {
					updatingTextFromTreeSelections=false;
					setSelectedFile(path);
				} finally {
					updatingTextFromTreeSelections=true;
				}
			}
		});
	
		filePath.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if (e.character=='\t') {
					if (ad.isProposalPopupOpen()) {
						if (prov.getFirstPath()!=null) {
							final String path = prov.getFirstPath();
							
							filePath.getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									try {
										updatingTextFromTreeSelections=false;
										filePath.setFocus();
										filePath.setText(path);
										setSelectedFile(path);
										filePath.setFocus();
										filePath.setSelection(path.length(), path.length());
									} finally {
										updatingTextFromTreeSelections=true;
									}
								}
							});
						}
					}
				} else if (e.character=='\t' || e.character=='\n'|| e.character=='\r') {
					final String path = filePath.getText();
					try {
						updatingTextFromTreeSelections=false;
						setSelectedFile(path);
					} finally {
						updatingTextFromTreeSelections=true;
					}
				}
			}
		});
		
		tree = new TreeViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER |SWT.VIRTUAL);
		tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.getTree().setHeaderVisible(true);
		tree.setUseHashlookup(true);
		
		tree.addSelectionChangedListener(new ISelectionChangedListener() {			
			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (!updatingTextFromTreeSelections) return;
				final File file = getSelectedFile();
				if (file!=null &&  file.isDirectory()) {
					try {
						ad.setEnabled(false);
					    filePath.setText(file.getAbsolutePath());
					    filePath.setSelection(filePath.getText().length());
					} finally {
						ad.setEnabled(true);
					}
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
		
		createContent(true);
				
		// Make drag source, it can then drag into projects
		final DragSource dragSource = new DragSource(tree.getControl(), DND.DROP_MOVE| DND.DROP_DEFAULT| DND.DROP_COPY);
		dragSource.setTransfer(new Transfer[] { FileTransfer.getInstance () });
		dragSource.addDragListener(new DragSourceAdapter() {
			@Override
			public void dragSetData(DragSourceEvent event){
				if (getSelectedFiles()==null) return;
				event.data = getSelectedFiles();
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
				if (e.character == '\n' || e.character == '\r') {
					openSelectedFile();
				}
			}
		});

		createRightClickMenu();
		addToolbar();

		if (savedSelection!=null) {
			if (savedSelection.exists()) {
			    tree.setSelection(new StructuredSelection(savedSelection));
			} else if (savedSelection.getParentFile()!=null && savedSelection.getParentFile().exists()) {
				// If file deleted, select parent.
				tree.setSelection(new StructuredSelection(savedSelection.getParentFile()));
			}
		}
		
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				final TreePath path = new TreePath(new Object[]{File.listRoots()[0]});
				tree.setExpandedState(path, true);
			}
		});

	}
	
	public void collapseAll() {
		this.tree.collapseAll();
	}
	
	public void refresh() {
		final File     file     = getSelectedFile();
		refresh(file);
	}
		
	protected void refresh(File file) {

		final Object[] elements = file==null?this.tree.getExpandedElements():null;
		final FileContentProvider fileCont = (FileContentProvider)tree.getContentProvider();
		fileCont.clearAndStop();

		tree.refresh(file!=null?file.getParentFile():tree.getInput());
		
		if (elements!=null) this.tree.setExpandedElements(elements);
		if (file!=null)     {
			setSelectedFile(file);
		}
	}

	private void createContent(boolean setItemCount) {
		
		if (setItemCount) tree.getTree().setItemCount(File.listRoots().length);
		tree.setContentProvider(new FileContentProvider(getViewSite().getActionBars().getStatusLineManager()));
		if (File.listRoots().length==1) {
			tree.setInput(File.listRoots()[0]);
		} else {
		    tree.setInput("Root");
		}
		tree.expandToLevel(1);
	}

	public void setSelectedFile(String path) {
		final File file = new File(path);
		setSelectedFile(file);
	}
	
	public void setSelectedFile(final File file) {
		if (file.exists()) {			
			final List<File> segs = getSegments(file);
			
			final Job expandJob = new Job("Update tree expanded state") {
				// Job needed because lazy tree - do not copy for all trees!	
				// Required this funny way because tree is lazy
				@Override
				protected IStatus run(IProgressMonitor monitor) {
					for (final File f : segs) {						
						if (monitor.isCanceled()) break;
						
						int count = 0;
						while (!getExpandedState(f) && count<1000) {
							expand(f);
							try {
								if (monitor.isCanceled()) break;
								Thread.sleep(20);
								count++;
							} catch (InterruptedException e) {
								break;
							}
						}
					}
					
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
					        tree.setSelection(new StructuredSelection(file));
						}
					});
					
					return Status.OK_STATUS;
				}
				
				private void expand(final File f) {
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							tree.setExpandedState(f, true);
						}
					});
				}
				private boolean getExpandedState(final File f) {
					final List<Boolean> res = new ArrayList<Boolean>(1);
					Display.getDefault().syncExec(new Runnable() {
						@Override
						public void run() {
							res.add(tree.getExpandedState(f));
						}
					});
					return res.get(0);
				}

			};
			expandJob.setUser(false);
			expandJob.setSystem(true);
			expandJob.setPriority(Job.INTERACTIVE);
			expandJob.schedule();
		}	
	}

	private List<File> getSegments(File file) {
		final List<File> segs = new ArrayList<File>();
		segs.add(file);
		File par = file.getParentFile();
		while(par!=null) {
			segs.add(0, par);
			par = par.getParentFile();
		}
		return segs;
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
     * TODO Move this to contributions
     */
	private void addToolbar() {
		
        // TODO Save preference as property
		
		final IToolBarManager toolMan = getViewSite().getActionBars().getToolBarManager();
		
		final CheckableActionGroup grp = new CheckableActionGroup();
		
		final Action dirsTop = new Action("Sort alpha numeric, directories at top.", IAction.AS_CHECK_BOX) {
			@Override
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
			@Override
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

		toolMan.add(new Separator("uk.ac.diamond.sda.navigator.views.monitorSep"));
		
        // NO MONITORING! There are some issues with monitoring, the Images Monitor part should
		// be used for this.

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

	

    /**
     * The adapter IContentProvider gives the value of the H5Dataset
     */
	@SuppressWarnings("rawtypes")
	@Override
	public Object getAdapter(final Class clazz) {

		return super.getAdapter(clazz);
		
		// TODO returns an adapter part for 'IPage' which is a page summary for the file or folder?
	}


}
