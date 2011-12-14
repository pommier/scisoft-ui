package uk.ac.diamond.sda.navigator.views;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;

import uk.ac.gda.util.io.SortingUtils;

public class FileContentProvider implements ILazyTreeContentProvider {

	public enum FileSortType {
		ALPHA_NUMERIC, ALPHA_NUMERIC_DIRS_FIRST;
	}
	
	private TreeViewer treeViewer;
	private FileSortType sort = FileSortType.ALPHA_NUMERIC_DIRS_FIRST;


	/**
	 * Caching seems to be needed to keep the path sorting
	 * fast. 
	 * NOTE Is there a better way of doing this.
	 */
	private Map<File, List<File>> cachedSorting;

	public FileContentProvider() {
		cachedSorting=new WeakHashMap<File, List<File>>(89);
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
		
		final List<File> fa = getFileList(node);
		if (fa!=null && index < fa.size()) {
			File element = fa.get(index);
			treeViewer.replace(parent, index, element);
			updateChildCount(element, -1);
		}
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {
		
		final File node = (File) element;
		if (node==null) return;
		
		final File[] fa = node.listFiles();
		if (fa!=null) {
			int size = fa.length;
			treeViewer.setChildCount(element, size);
		} else {
		
		    treeViewer.setChildCount(element, 0);
		}
		
	}

	private List<File> getFileList(File node) {
		
		if (!node.isDirectory()) return null;
		
		if (sort==FileSortType.ALPHA_NUMERIC) {
			final File[] fa = node.listFiles();
			if (fa==null) return null;
			return Arrays.asList(fa);
		}
		
		if (cachedSorting.containsKey(node)) {
			return cachedSorting.get(node);
		}
		
		final List<File> sorted;
		if (sort==FileSortType.ALPHA_NUMERIC) {
			sorted = SortingUtils.getSortedFileList(node, false);
		} else {
			sorted = SortingUtils.getSortedFileList(node, true);
		}
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


	public FileSortType getSort() {
		return sort;
	}


	public void setSort(FileSortType sort) {
		this.sort = sort;
	}

}
