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

package uk.ac.diamond.sda.navigator.hdf5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

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
import org.eclipse.ui.progress.UIJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import uk.ac.diamond.scisoft.analysis.dataset.DoubleDataset;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5File;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.io.DataHolder;
import uk.ac.diamond.scisoft.analysis.io.HDF5Loader;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;

public class HDF5ContentProvider implements ITreeContentProvider, IResourceChangeListener, IResourceDeltaVisitor {
	private StructuredViewer viewer;
	private String DELIMITER = "/";
	public static final String H5_EXT = "h5"; //$NON-NLS-1$
	public static final String HDF5_EXT = "hdf5"; //$NON-NLS-1$
	public static final String NXS_EXT = "nxs"; //$NON-NLS-1$
	private HDF5File hdf5File;
	private String fileName;
	private static final Object[] NO_CHILDREN = new Object[0];
	@SuppressWarnings("rawtypes")
	private final Map cachedModelMap = new HashMap();

	private DataHolder data;
	private static final Logger logger = LoggerFactory.getLogger(HDF5ContentProvider.class);

	public HDF5ContentProvider() {
		ResourcesPlugin.getWorkspace().addResourceChangeListener(this, IResourceChangeEvent.POST_CHANGE);
	}

	@SuppressWarnings("unused")
	public HDF5ContentProvider(String test) {

	}

	@Override
	public Object[] getChildren(Object parentElement) {
		Object[] children = null;

		if (parentElement instanceof TreeNode) {
			children = ((TreeNode) parentElement).getChildren().toArray();
			if (!((TreeNode) parentElement).hasChildren())
				children = NO_CHILDREN;
		} else if (parentElement instanceof IFile) {
			IFile modelFile = (IFile) parentElement;
			if (H5_EXT.equals(modelFile.getFileExtension())||HDF5_EXT.equals(modelFile.getFileExtension())||NXS_EXT.equals(modelFile.getFileExtension())) {
				Tree hdf5Tree = null;
				updateModel(modelFile);
				hdf5Tree = (Tree) cachedModelMap.get(modelFile);
				children = hdf5Tree.getRoot().getChildren().toArray();
				if (children == null) {
					hdf5Tree = (Tree) cachedModelMap.get(modelFile);
					children = hdf5Tree.getRoot().getChildren().toArray();
				}
			}
		}
		return children != null ? children : NO_CHILDREN;
	}

	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}

	@Override
	public boolean hasChildren(Object element) {
		if (((element instanceof TreeNode) && (((TreeNode) element).hasChildren())) || (element instanceof IFile))
			return true;
		return false;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof TreeNode) {
			if (((TreeNode) element).getParent() == null) {
				TreeNode data = new TreeNode();
				logger.debug(data.getFile().toString());
				return data.getFile();
			}
			return ((TreeNode) element).getParent();
		}
		return null;
	}

	@Override
	public void dispose() {
		cachedModelMap.clear();
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(this);
	}

	@Override
	public void inputChanged(Viewer aViewer, Object oldInput, Object newInput) {
		if (oldInput != null && !oldInput.equals(newInput)) {
			cachedModelMap.clear();
		}

		viewer = (StructuredViewer) aViewer;
	}

	@Override
	public void resourceChanged(IResourceChangeEvent event) {
		IResourceDelta delta = event.getDelta();
		try {
			delta.accept(this);
		} catch (CoreException e) {
			e.printStackTrace();
		}
	}

	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
		IResource source = delta.getResource();
		switch (source.getType()) {
		case IResource.ROOT:
		case IResource.PROJECT:
		case IResource.FOLDER:
			return true;
		case IResource.FILE:
			final IFile file = (IFile) source;
			if (H5_EXT.equals(file.getFileExtension())||HDF5_EXT.equals(file.getFileExtension())||NXS_EXT.equals(file.getFileExtension())) {

				loadHDF5Data(file);
				new UIJob("Update HDF5 Model in CommonViewer") { //$NON-NLS-1$
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

	private void loadHDF5Data(IFile file) {
		fileName = file.getLocation().toString();
		try {
			setData(new HDF5Loader(fileName).loadFile());
			hdf5File = new HDF5Loader(fileName).loadTree(null);
		} catch (Exception e) {
			setData(new DataHolder());
			getData().addDataset("Failed to load File", new DoubleDataset(1)); //$NON-NLS-1$
			logger.warn("Could not load NeXus file {}", fileName);
		}
	}

	/**
	 * Load the model from the given file, if possible.
	 * 
	 * @param modelFile
	 *            The IFile which contains the persisted model
	 */
	@SuppressWarnings("unchecked")
	private synchronized DataHolder updateModel(IFile modelFile) {
		loadHDF5Data(modelFile);

		if (H5_EXT.equals(modelFile.getFileExtension())||HDF5_EXT.equals(modelFile.getFileExtension())||NXS_EXT.equals(modelFile.getFileExtension())) {
			if (modelFile.exists()) {
				List<HDF5NodeLink> nodes = getAllPathnames(data.getNames());

				cachedModelMap.put(modelFile, populate(nodes, modelFile));
				return data;
			} else {
				cachedModelMap.remove(modelFile);
			}
		}
		return null;
	}

	public DataHolder getData() {
		return data;
	}

	public void setData(DataHolder data) {
		this.data = data;
	}

	/**
	 * Method used to populate a HDF5Tree from a list of <b>sorted</b> pathnames representing a complete folder/file
	 * structure. <br>
	 * Example of input for the pathnames of a list of hdf5 node links: <br>
	 * /root <br>
	 * /root/folder1 <br>
	 * /root/folder1/file1 <br>
	 * /root/folder1/file2 <br>
	 * /root/folder2 <br>
	 * etc<br>
	 * <br>
	 * 
	 * @param list
	 *            The <b>sorted</b> list of pathnames starting with a root folder
	 * @return HDF5Tree The resulting populated tree
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public Tree populate(List<HDF5NodeLink> list, IFile file) {
		Tree hdf5Tree = new Tree();
		List<TreeNode> nodes = new ArrayList<TreeNode>();

		Stack<TreeNode> myStack = new Stack<TreeNode>();

		int heightIs = 0, heightWas = 0;
		for (int i = 0; i < list.size(); i++) {

			String[] str = ("/root"+list.get(i).getFullName()).split(DELIMITER);
			heightIs = str.length - 2;

			if (i == 0) { // if pathname is "/root"
				nodes.add(0, new TreeNode(list.get(i), file));
				myStack.push(new TreeNode(list.get(i), file));
			} else {
				heightWas = ("/root"+list.get(i - 1).getFullName()).split(DELIMITER).length - 2;

				if (heightIs == heightWas)
					myStack.pop();

				if (heightIs < heightWas) {
					int diff = heightWas - heightIs;
					for (int j = 0; j < diff + 1; j++)
						myStack.pop();
				}

				nodes.add(i, new TreeNode(list.get(i), file));
				myStack.push(nodes.get(i));

				if (myStack.size() > 1)
					myStack.get(myStack.size() - 2).addChild(myStack.get(myStack.size() - 1));
			}
		}
		hdf5Tree.setRoot(myStack.get(0));
		return hdf5Tree;
	}

	/**
	 * Method that returns the list of children of a given parent path out of a String[] of pathnames<br>
	 * Example:<br>
	 * parentpath="/entry1/instrument"<br>
	 * pathnames={"/entry1","/entry1/instrument/I0","/entry1/instrument/IRef","/entry1/instrument/name/axis"}<br>
	 * will return {"I0","IRef","name"} as a List<String><br>
	 * 
	 * @param parentpath
	 *            The path from which we want the list of children
	 * @param pathnames
	 *            The full list of pathnames
	 * @return List<String> List of children
	 */
	public List<String> getChildrenList(String parentpath, String[] pathnames) {
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < pathnames.length; i++) {
			if (pathnames[i].contains(parentpath) && !parentpath.equals(pathnames[i]) && !parentpath.equals("/")) {
				String[] tmp1 = pathnames[i].split(parentpath);
				String[] tmp2 = tmp1[1].split(DELIMITER);
				if (!list.contains(tmp2[1])) {
					list.add(tmp2[1]);
				}
			}
			if (parentpath.equals(DELIMITER)) {
				String[] tmp1 = pathnames[i].split(parentpath);
				if (!list.contains(tmp1[1])) {
					list.add(tmp1[1]);
				}
			}
		}
		return list;
	}

	/**
	 * Method which returns true if aPath is a leaf (with no children) in the List<String> allPaths
	 * 
	 * @param aPath
	 *            A chosen path among all paths
	 * @param allPaths
	 *            The list of all paths
	 * @return boolean
	 */
	public boolean isTreeLeaf(String aPath, List<String> allPaths) {
		if (allPaths.contains(aPath)) {
			for (int i = 0; i < allPaths.size(); i++) {
				if (allPaths.get(i).regionMatches(0, aPath, 0, aPath.length())) {
					if (allPaths.get(i).length() > aPath.length())
						return false;
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Method that returns the list of all paths possibles out of a String[] of paths <br>
	 * Example: <br>
	 * oldFullPaths=
	 * {"/entry1/name",<br>
	 *  "/entry1/instrument/energy",<br>
	 *  "/entry1/instrument/I0",<br>
	 *  "/entry1/source/IRef"}<br>
	 * will return <br>
	 * List<HDF5NodeLink> of <fullName>=
	 * {"/",<br>
	 *  "/entry1",<br>
	 *  "/entry1/name",<br>
	 *  "/entry1/instrument",<br>
	 *  "/entry1/instrument/energy",<br>
	 *  "/entry1/instrument/I0",<br>
	 *  "/entry1/source",<br>
	 *  "/entry1/source/IRef"}<br>
	 * 
	 * @param oldFullPaths
	 *            A String[] of paths
	 * @return List of HDF5NodeLink The complete list of all possible HDF5NodeLink
	 */
	public List<HDF5NodeLink> getAllPathnames(String[] oldFullPaths) {
		List<String> list = new ArrayList<String>();

		String[] newFullPaths = new String[oldFullPaths.length + 1];

		for (int i = 0; i < newFullPaths.length; i++) {
			if (i == 0)
				newFullPaths[i] = "/";
			else if (i > 0)
				newFullPaths[i] = oldFullPaths[i - 1];

			String[] tmp = newFullPaths[i].split(DELIMITER);
			String str = "";
			for (int j = 1; j < tmp.length; j++) {
				str = str.concat(DELIMITER + tmp[j]);
				if (!list.contains(str) && str != "") {
					list.add(str);
				}
			}
			if (!list.contains(str) && str != "") {
				list.add(str);
			}
		}
		//we add a root
		list.add("/");
		//we sort the list of string paths
		Collections.sort(list, String.CASE_INSENSITIVE_ORDER);
		
		// we create a new list of HDF5NodeLinks from the previous string list of paths
		List<HDF5NodeLink> nodeList=new ArrayList<HDF5NodeLink>();
		for (int i = 0; i < list.size(); i++)
			nodeList.add(hdf5File.findNodeLink(list.get(i)));
		
		return nodeList;
	}
}
