/*-
 * Copyright © 2011 Diamond Light Source Ltd.
 *
 * This file is part of GDA.
 *
 * GDA is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License version 3 as published by the Free
 * Software Foundation.
 *
 * GDA is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along
 * with GDA. If not, see <http://www.gnu.org/licenses/>.
 */
package uk.ac.diamond.sda.navigator.srs;

import gda.analysis.io.ScanFileHolderException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.eclipse.ui.progress.UIJob;

import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.ExtendedSRSLoader;
import uk.ac.diamond.scisoft.analysis.io.SRSLoader;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.srs.SRSTreeData;

/**
 * Provides the properties contained in a *.dat file as children of that file in a Common Navigator.
 */
public class SRSContentProvider extends EditorPart implements ITreeContentProvider, IResourceChangeListener,
		IResourceDeltaVisitor {

	private static final Object[] NO_CHILDREN = new Object[0];
	private static final Object SRS_EXT = "dat"; //$NON-NLS-1$
	private final Map/* <IFile, SRSTreeData[]> */cachedModelMap = new HashMap();
	private static StructuredViewer viewer;
	protected static String fileName;
	private DataHolder data;

	/**
	 * Create the SRSContentProvider instance. Adds the content provider as a resource change listener to track changes
	 * on disk.
	 */
	public SRSContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	/**
	 * Return the model elements for a *.dat IFile or NO_CHILDREN for otherwise.
	 */
	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = null;
		if (parentElement instanceof SRSTreeData) {
			children = NO_CHILDREN;
		} else if (parentElement instanceof IFile) {
			/* possible model file */
			IFile modelFile = (IFile) parentElement;
			if (SRS_EXT.equals(modelFile.getFileExtension())) {
				children = (SRSTreeData[]) cachedModelMap.get(modelFile);
				if (children == null && updateModel(modelFile) != null) {
					children = (SRSTreeData[]) cachedModelMap.get(modelFile);
				}
			}
		}
		return children != null ? children : NO_CHILDREN;
	}

	/**
	 * Method that calls the SRSLoader class to load a .dat file
	 * 
	 * @param file
	 *            The .dat file to open
	 */
	public void srsFileLoader(IFile file) {

		fileName = file.getLocation().toString();
		try {
			SRSLoader dataLoader = new ExtendedSRSLoader(fileName);
			data = dataLoader.loadFile();
		} catch (ScanFileHolderException e) {
			data = new DataHolder();
			data.addDataset("Failed to load File", new DoubleDataset(1));
		}

	}

	/**
	 * Load the model from the given file, if possible.
	 * 
	 * @param modelFile
	 *            The IFile which contains the persisted model
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private synchronized DataHolder updateModel(IFile modelFile) {
		srsFileLoader(modelFile);

		if (SRS_EXT.equals(modelFile.getFileExtension())) {
			if (modelFile.exists()) {
				List properties = new ArrayList();
				String[] names = getData().getNames();
				for (int i = 0; i < getData().size(); i++) {
					properties.add(new SRSTreeData(names[i], getData().getDataset(i).min().toString(), getData().getDataset(i)
							.max().toString(), getClass(getData().getDataset(i).elementClass().toString()), modelFile));
				}
				SRSTreeData[] srsTreeData = (SRSTreeData[]) properties.toArray(new SRSTreeData[properties.size()]);

				cachedModelMap.put(modelFile, srsTreeData);
				return getData();
			} else {
				cachedModelMap.remove(modelFile);
			}
		}
		return null;
	}

	private String getClass(String str) {
		String[] parts = str.split("\\."); //$NON-NLS-1$
		return parts[parts.length - 1];
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof SRSTreeData) {
			SRSTreeData data = (SRSTreeData) element;
			return data.getFile();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof SRSTreeData) {
			return false;
		} else if (element instanceof IFile) {
			return SRS_EXT.equals(((IFile) element).getFileExtension());
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public void dispose() {
		cachedModelMap.clear();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		if (oldInput != null && !oldInput.equals(newInput))
			cachedModelMap.clear();
		viewer = (StructuredViewer) aViewer;
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceChangeListener#resourceChanged(org
	 * .eclipse.core.resources.IResourceChangeEvent)
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {

		IResourceDelta delta = event.getDelta();
		try {
			delta.accept(this);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.core.resources.IResourceDeltaVisitor#visit(org.eclipse.core .resources.IResourceDelta)
	 */
	@Override
	public boolean visit(IResourceDelta delta) {

		IResource source = delta.getResource();
		switch (source.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
		case IResource.FOLDER:
			return true;
		case IResource.FILE:
			final IFile file = (IFile) source;
			if (SRS_EXT.equals(file.getFileExtension())) {
				updateModel(file);
				new UIJob("Update SRS Model in CommonViewer") { //$NON-NLS-1$
					@Override
					public IStatus runInUIThread(IProgressMonitor monitor) {
						if (viewer != null && !viewer.getControl().isDisposed())
							viewer.refresh(file);
						return Status.OK_STATUS;
					}
				}.schedule();
			}
			return false;
		}
		return false;
	}

	@Override
	public boolean isDirty() {
		// TODO Auto-generated method stub
		return false;
	}

	public DataHolder getData() {
		return data;
	}

	public void setData(DataHolder data) {
		this.data= data;
	}
	
	public static StructuredViewer getViewer() {
		return viewer;
	}

	public void setViewer(StructuredViewer viewer) {
		this.viewer = viewer;
	}

	@Override
	public void doSave(IProgressMonitor arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void doSaveAs() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void init(IEditorSite arg0, IEditorInput arg1) throws PartInitException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub
		
	}

}
