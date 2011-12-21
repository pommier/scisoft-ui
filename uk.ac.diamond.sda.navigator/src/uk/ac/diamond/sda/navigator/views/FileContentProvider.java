/*
 * Copyright © 2011 Diamond Light Source Ltd.
 * Contact :  ScientificSoftware@diamond.ac.uk
 * 
 * This is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 * 
 * This software is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General
 * Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along
 * with this software. If not, see <http://www.gnu.org/licenses/>.
 */

package uk.ac.diamond.sda.navigator.views;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.ILazyTreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Cursor;
import org.eclipse.ui.PlatformUI;

import uk.ac.gda.util.io.SortingUtils;

public class FileContentProvider implements ILazyTreeContentProvider {

	public enum FileSortType {
		ALPHA_NUMERIC, ALPHA_NUMERIC_DIRS_FIRST;
	}
	
	private TreeViewer treeViewer;
	private FileSortType sort = FileSortType.ALPHA_NUMERIC_DIRS_FIRST;
    private BlockingQueue<UpdateRequest> queue;

	/**
	 * Caching seems to be needed to keep the path sorting
	 * fast. 
	 * NOTE Is there a better way of doing this.
	 */
	private Map<File, List<File>> cachedSorting;

	public FileContentProvider() {
		this.cachedSorting = new WeakHashMap<File, List<File>>(89);
		this.queue         = new LinkedBlockingDeque<UpdateRequest>(Integer.MAX_VALUE);
	}

	
	@Override
	public void dispose() {
		cachedSorting.clear();
		cachedSorting = null;
		queue.add(new UpdateRequest()); // break the queue
		queue = null;
	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		treeViewer = (TreeViewer) viewer;
		treeViewer.refresh();
	}

	@Override
	public void updateElement(Object parent, int index) {

		if (queue==null) return;
		if (PlatformUI.isWorkbenchRunning()) {
			createUpdateElementJobIfNotAlreadyGoing();
			queue.add(new UpdateRequest(parent, index));
		} else {
			final File node = (File) parent;
			final List<File> fa = getFileList(node);
			updateElementInternal(node, index, fa);
		}
	}
	
	private Thread updateElementJob;
	
	/**
	 * Somewhat long method name, but you get the idea.
	 */
	private void createUpdateElementJobIfNotAlreadyGoing() {
		
		if (updateElementJob==null) {
			updateElementJob = new Thread("Update directory contents") {
				
				private Cursor  busy   = treeViewer.getControl().getDisplay().getSystemCursor(SWT.CURSOR_WAIT);
				private boolean isBusy = false;
				
				@Override
				public void run() {
					
					while(!treeViewer.getControl().isDisposed() && queue!=null) {
                        try {

                    		final UpdateRequest req = queue.take();
                    		
                    		// Blank object added to break the queue
                    		if (req.getElement()==null && req.getIndex()==-1) return;
                    		
                    		if (!isBusy) {
                    			isBusy = true;
                    			if (treeViewer.getControl().isDisposed()) break;
	                    		treeViewer.getControl().getDisplay().syncExec(new Runnable() {
									@Override
									public void run() {
										if (treeViewer.getControl().isDisposed()) return;
										treeViewer.getControl().setCursor(busy);
									}
	                			});
                    		}

                			final File node = (File) req.getElement();
                			final List<File> fa = getFileList(node);
                			
                   			if (treeViewer.getControl().isDisposed()) break;
               			    treeViewer.getControl().getDisplay().asyncExec(new Runnable() {
								@Override
								public void run() {
									if (treeViewer.getControl().isDisposed()) return;

								    updateElementInternal(node, req.getIndex(), fa);
								}
                			});

                        } catch (InterruptedException ne) {
                        	break;

                        } catch (org.eclipse.swt.SWTException swtE) {
                        	if (queue==null) break;
                         	queue.clear();
                        	break;
                        	
                        } catch (Exception ne) {
                        	if (queue==null) break;
                        	queue.clear();
                        	continue;
                        } finally {
                        	
                        	if (queue==null) break;
                        	if (queue.isEmpty()) {
                        		isBusy = false;
                       			if (treeViewer.getControl().isDisposed()) break;
                                treeViewer.getControl().getDisplay().syncExec(new Runnable() {
									@Override
									public void run() {
										treeViewer.getControl().setCursor(null);
									}
	                			});
                        	}
                        }
					}
				}	
			};
			updateElementJob.setPriority(9);
			updateElementJob.setDaemon(true);
			updateElementJob.start();

		}
	}


	public void updateElementInternal(Object parent, int index, List<File> fa) {
		
		
		if (fa!=null && index < fa.size()) {
			File element = fa.get(index);
			treeViewer.replace(parent, index, element);
			
			// We correct when they expand, listFiles() could be slow.
			if (element.isDirectory()) {
				treeViewer.setChildCount(element, 1);
			} else {
				treeViewer.setChildCount(element, 0);
			}
		}
	}

	@Override
	public void updateChildCount(Object element, int currentChildCount) {
		
		if (currentChildCount>-1) {
			treeViewer.setChildCount(element, currentChildCount);
		}
		
		final File node = (File) element;
		if (node==null) return;
		
		if (node.isDirectory()) {
			final File[] fa = node.listFiles(); // Can be slow to return
			if (fa!=null) {
				int size = fa.length;
				treeViewer.setChildCount(element, size);
			} else {
			
			    treeViewer.setChildCount(element, 0);
			}
		} else {
			treeViewer.setChildCount(element, 0);
		}
		
	}

	private List<File> getFileList(File node) {
		
		if (!node.isDirectory()) return null;
		if (cachedSorting==null) return null;
		
		if (sort==FileSortType.ALPHA_NUMERIC) {
			final File[] fa = node.listFiles();
			if (fa==null) return null;
			return Arrays.asList(fa);
		}
		
		if (cachedSorting.containsKey(node)) {
			return cachedSorting.get(node);
		}
		
		// Simulate Lustreless
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
		
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
	
	private class UpdateRequest {
		
		private Object element;
		private int index;

		UpdateRequest() {
			element=null;
			index  =-1;
		}

		UpdateRequest(final Object element, final int index) {
			this.element = element;
			this.index   = index;
		}

		public Object getElement() {
			return element;
		}

		public int getIndex() {
			return index;
		}

	}

}
