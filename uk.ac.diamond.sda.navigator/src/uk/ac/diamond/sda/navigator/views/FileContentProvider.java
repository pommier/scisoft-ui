package uk.ac.diamond.sda.navigator.views;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import uk.ac.gda.util.io.SortingUtils;

public class FileContentProvider implements ILazyTreeContentProvider {

	private TreeViewer treeViewer;

	/**
	 * Caching seems to be needed to keep the path sorting
	 * fast. 
	 * NOTE Is there a better way of doing this.
	 */
	private Map<File, List<File>> cachedSorting;

	public FileContentProvider() {
		cachedSorting=new HashMap<File, List<File>>(89);
	}

	
	@Override
	public void dispose() {
		cachedSorting.clear();
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		treeViewer = (TreeViewer) viewer;
		treeViewer.refresh();
	}

	@Override
	public void updateElement(Object parent, int index) {
		File node = (File) parent;
		
		if (node.isDirectory()) {
			final List<File> fa = getSortedFileList(node);
			if (index < fa.size()) {
				File element = fa.get(index);
				treeViewer.replace(parent, index, element);
				updateChildCount(element, -1);
			}
		}
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {
		File node = (File) element;
		if (node==null) return;
		if (node.isDirectory()) {
			final List<File> fa = getSortedFileList(node);
			if (fa!=null) {
				int size = fa.size();
				treeViewer.setChildCount(element, size);
				return;
			} 
		}
		
		treeViewer.setChildCount(element, 0);
		
	}

	private List<File> getSortedFileList(File node) {
		
		if (cachedSorting.containsKey(node)) {
			return cachedSorting.get(node);
		}
		final List<File> sorted = SortingUtils.getSortedFileList(node);
		cachedSorting.put(node, sorted);
		return sorted;
	}


	@Override
	public Object getParent(Object element) {
		if (element==null || !(element instanceof File)) {
			return null;
		}
		final File node = ((File) element);
		return node.getParentFile();
	}


}
