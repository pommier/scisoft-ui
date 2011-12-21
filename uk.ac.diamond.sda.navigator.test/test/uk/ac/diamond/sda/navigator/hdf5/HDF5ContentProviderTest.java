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

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Group;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5Node;
import uk.ac.diamond.scisoft.analysis.hdf5.HDF5NodeLink;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;

public class HDF5ContentProviderTest {

	private static String[] pathnames = {
		"/entry1",
		"/entry1/instrument1/notleaf",
		"/entry1/instrument1/notleaf/leaf1",
		"/entry1/instrument2/test1",
		"/entry1/instrument2/test2",
		"/entry1/instrument3/test"};
	
//	@Test
//	void TestGetMetadata(){
//		HDF5ContentProvider cp=new HDF5ContentProvider();
//
//		assertNotNull(cp.getMetadata());
//	}
	
	@Test
	public void testGetChildrenList(){
		HDF5ContentProvider cp=new HDF5ContentProvider("test");
		List<String> expectedChildrenList=new ArrayList<String>();
		expectedChildrenList.add("instrument1");
		expectedChildrenList.add("instrument2");
		expectedChildrenList.add("instrument3");
		
		String str="/entry1";
		
		assertEquals(cp.getChildrenList(str,pathnames),expectedChildrenList);
	}

	@Test
	public void testIsTreeLeaf(){
		HDF5ContentProvider cp=new HDF5ContentProvider("test");
		String aPath="/entry1/instrument1/notleaf";
		String bPath="/entry1/instrument1/leafNotExisting";
		String cPath="/entry1/instrument1/notleaf/leaf1";
		List<String> listOfPaths=new ArrayList<String>();
		for(int i=0;i<pathnames.length;i++)
			listOfPaths.add(pathnames[i]);
		assertEquals(cp.isTreeLeaf(aPath,listOfPaths),false);
		assertEquals(cp.isTreeLeaf(bPath,listOfPaths),false);
		assertEquals(cp.isTreeLeaf(cPath,listOfPaths),true);
	}
	
	private static String[] pathnames2 = {
		"/entry1/counterTimer01/I0",
		"/entry1/instrument1/notleaf/leaf0",
		"/entry1/instrument1/notleaf/leaf1",
		"/entry1/instrument2/test1/leaf210",
		"/entry1/instrument2/test1/leaf211",
		"/entry1/instrument3/test1/leaf310"};
	
//	@Test
//	public void testGetAllPathnames(){
//		HDF5ContentProvider cp=new HDF5ContentProvider("test");
//		List<String> goodResult=new ArrayList<String>();
//		goodResult.add("/entry1");
//		goodResult.add("/entry1/counterTimer01");
//		goodResult.add("/entry1/counterTimer01/I0");
//		goodResult.add("/entry1/instrument1");
//		goodResult.add("/entry1/instrument1/notleaf");
//		goodResult.add("/entry1/instrument1/notleaf/leaf0");
//		goodResult.add("/entry1/instrument1/notleaf/leaf1");
//		goodResult.add("/entry1/instrument2");
//		goodResult.add("/entry1/instrument2/test1");
//		goodResult.add("/entry1/instrument2/test1/leaf210");
//		goodResult.add("/entry1/instrument2/test1/leaf211");
//		goodResult.add("/entry1/instrument3");
//		goodResult.add("/entry1/instrument3/test1");
//		goodResult.add("/entry1/instrument3/test1/leaf310");
//
//		assertEquals(goodResult,cp.getAllPathnames(pathnames2));
//	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Test
	public void testPopulate(){
		HDF5ContentProvider cp=new HDF5ContentProvider("test");

		/*
		 * Expected tree:
		 *              root
		 *            / \     \
		 *           /   \     \
		 *      node1  node4   node7
		 *      / \      /  \
		 *     /   \    /    \
		 * node2 node3 node5 node6
		 * */
		Tree expectedTree=new Tree();
		TreeNode root=new TreeNode("root\n");
		TreeNode node1=new TreeNode("root/node1\n");
		TreeNode node2=new TreeNode("root/node1/node2\n");
		TreeNode node3=new TreeNode("root/node1/node3\n");
		TreeNode node4=new TreeNode("root/node4\n");
		TreeNode node5=new TreeNode("root/node4/node5\n");
		TreeNode node6=new TreeNode("root/node4/node6\n");
		TreeNode node7=new TreeNode("root/node7\n");
		root.addChild(node1);
		root.addChild(node4);
		root.addChild(node7);
		node1.addChild(node2);
		node1.addChild(node3);
		node4.addChild(node5);
		node4.addChild(node6);
		//node4.addChild(node7);
		expectedTree.setRoot(root);
		
		Tree populatedTree=new Tree();
		List<HDF5NodeLink> allPaths=new ArrayList<HDF5NodeLink>();
		HDF5Node hdfroot=  new HDF5Node(0);
		HDF5Node hdfnode1= new HDF5Node(1);
		HDF5Node hdfnode2= new HDF5Node(2);
		HDF5Node hdfnode3= new HDF5Node(3);
		HDF5Node hdfnode4= new HDF5Node(4);
		HDF5Node hdfnode5= new HDF5Node(5);
		HDF5Node hdfnode6= new HDF5Node(6);
		HDF5Node hdfnode7= new HDF5Node(7);
		allPaths.add(new HDF5NodeLink(null, "root", null, hdfroot));
		allPaths.add(new HDF5NodeLink(null, "root/node1", hdfroot, hdfnode1));
		allPaths.add(new HDF5NodeLink(null, "root/node1/node2", hdfnode1, hdfnode2));
		allPaths.add(new HDF5NodeLink(null, "root/node1/node3", hdfnode1, hdfnode3));
		allPaths.add(new HDF5NodeLink(null, "root/node4", hdfroot, hdfnode4));
		allPaths.add(new HDF5NodeLink(null, "root/node4/node5", hdfnode4, hdfnode5));
		allPaths.add(new HDF5NodeLink(null, "root/node4/node6", hdfnode4, hdfnode6));
		allPaths.add(new HDF5NodeLink(null, "root/node7", hdfroot, hdfnode7));
		//Collections.sort(allPaths, String.CASE_INSENSITIVE_ORDER);
		IFile file = null;
		populatedTree=cp.populate(allPaths,file);
		
		//test rootChildren
		List rootChildren=new ArrayList();
		rootChildren.add("root/node1\n");
		rootChildren.add("root/node4\n");
		rootChildren.add("root/node7\n");
		String[] rootChildrenArray=new String[rootChildren.size()];
		for(int i=0;i<rootChildren.size();i++)
			rootChildrenArray[i]=rootChildren.get(i).toString();
		
		List populatedRootChildren=populatedTree.getRoot().getChildren();
		String[] populatedRootChildrenArray=new String[populatedRootChildren.size()];
		for(int i=0;i<populatedRootChildren.size();i++)
			populatedRootChildrenArray[i]=populatedRootChildren.get(i).toString();
		
		//assertEquals(rootChildrenArray,populatedRootChildrenArray);

		//test tree structure
		assertEquals(expectedTree.toStringWithDepth(),populatedTree.toStringWithDepth());
		
		//test nodes children
		
		//FIXME: Fix HDF5NodeLink and TreeNode object mismatch
		/*
		assertEquals(node1.getChildren(),populatedTree.find("node1").getChildren());
		assertEquals(node2.getChildren(),populatedTree.find("node2").getChildren());
		assertEquals(node3.getChildren(),populatedTree.find("node3").getChildren());
		assertEquals(node4.getChildren(),populatedTree.find("node4").getChildren());
		assertEquals(node5.getChildren(),populatedTree.find("node5").getChildren());
		assertEquals(node6.getChildren(),populatedTree.find("node6").getChildren());
		assertEquals(node7.getChildren(),populatedTree.find("node7").getChildren());
		*/

	}
}
