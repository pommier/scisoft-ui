package uk.ac.diamond.sda.navigator.views;

import java.io.File;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

		tree = new TreeViewer(parent, SWT.SINGLE | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER |SWT.VIRTUAL);
		tree.getTree().setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		tree.getTree().setHeaderVisible(true);
		tree.setUseHashlookup(true);

		final String[] titles = { "Name", "Date", "Type", "Size" };
		final int[]    widths = { 250, 120, 80, 150 };
		TreeViewerColumn tVCol;
		for (int i = 0; i < titles.length; i++) {
			tVCol = new TreeViewerColumn(tree, SWT.NONE);
			TreeColumn tCol = tVCol.getColumn();
			tCol.setText(titles[i]);
			tCol.setWidth(widths[i]);
			tCol.setMoveable(true);
			tVCol.setLabelProvider(new FileLabelProvider(i));
		}
		getSite().setSelectionProvider(tree);
		
		refresh();
		
		if (savedSelection!=null) {
			if (savedSelection.exists()) {
			    tree.setSelection(new StructuredSelection(savedSelection));
			} else if (savedSelection.getParentFile().exists()) {
				// If file deleted, select parent.
				tree.setSelection(new StructuredSelection(savedSelection.getParentFile()));
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

	private void refresh() {
		
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
