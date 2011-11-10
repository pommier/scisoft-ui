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
package uk.ac.diamond.sda.navigator.hdf5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;

public class TestMethod {

	private static String[] pathnames = { "/entry1", "/entry1/counterTimer01", "/entry1/counterTimer01/Energy",
			"/entry1/counterTimer01/I0", "/entry1/counterTimer01/Iref", "/entry1/counterTimer01/It",
			"/entry1/counterTimer01/lnI0It", "/entry1/counterTimer01/lnItIref", "/entry1/counterTimer01/Time",
			"/entry1/entry_identifier", "/entry1/instrument", "/entry1/instrument/counterTimer01",
			"/entry1/instrument/counterTimer01/description", "/entry1/instrument/counterTimer01/I0",
			"/entry1/instrument/counterTimer01/id", "/entry1/instrument/counterTimer01/Iref",
			"/entry1/instrument/counterTimer01/It", "/entry1/instrument/counterTimer01/lnI0It",
			"/entry1/instrument/counterTimer01/lnItIref", "/entry1/instrument/counterTimer01/type",
			"/entry1/instrument/name", "/entry1/instrument/source", "/entry1/instrument/source/current",
			"/entry1/instrument/source/frequency", "/entry1/instrument/source/name", "/entry1/instrument/source/notes",
			"/entry1/instrument/source/power", "/entry1/instrument/source/probe", "/entry1/instrument/source/type",
			"/entry1/instrument/source/voltage", "/entry1/instrument/xas_scannable",
			"/entry1/instrument/xas_scannable/Energy", "/entry1/instrument/xas_scannable/Time", "/entry1/program_name",
			"/entry1/scan_command", "/entry1/scan_dimensions", "/entry1/scan_identifier", "/entry1/user01",
			"/entry1/user01/username", "/entry1/xml", "/entry1/xml/DetectorParameters", "/entry1/xml/OutputParameters",
			"/entry1/xml/SampleParameters", "/entry1/xml/ScanParameters", "/entry1/xml/VortexParameters",
			"/entry1/xml/XspressParameters" };

	private Stack stack = new Stack();
	private static String DELIMITER = "/";
	private static final Map/* <IFile, NexusTreeData[]> */cachedModelMap = new HashMap();

	/**
	 * @param args
	 */
	@SuppressWarnings({ "unchecked", "rawtypes", "unused" })
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		List<String> list = new ArrayList<String>();
		list.add("/entry1/instrument");
		list.add("/entry1");
		list.add("/entry1/instrument/leaf");

		// String str="/entry1";

		// System.out.println(isTreeLeaf(str, list));
		// System.out.println(getChildrenList(str).toString());
		// System.out.println(getNodeListByTreeLevel(2).toString());

		List<String> allPaths = getAllPathnames(pathnames);
		// sorting out of the pathnames
		Collections.sort(allPaths, String.CASE_INSENSITIVE_ORDER);
		// we start from the end of the pathnames
		// Collections.reverse(allPaths);
		System.out.println(allPaths);

		populate(allPaths);
		// while (nodeAdded<allPaths.size()) {

		// HDF5Tree<String> tree = new HDF5Tree<String>();
		// HDF5TreeNode<String> root1 = new HDF5TreeNode<String>("I am root!");
		// HDF5TreeNode<String> childA = new HDF5TreeNode<String>("A");
		// HDF5TreeNode<String> childB = new HDF5TreeNode<String>("B");
		// HDF5TreeNode<String> childC = new HDF5TreeNode<String>("C");
		// HDF5TreeNode<String> childD = new HDF5TreeNode<String>("D");
		// HDF5TreeNode<String> childE = new HDF5TreeNode<String>("E");
		//
		// childD.addChild(childE);
		//
		// childB.addChild(childC);
		// childB.addChild(childD);
		//
		// root1.addChild(childA);
		// root1.addChild(childB);
		//
		//
		// tree.setRoot(root1);

		// System.out.println(root.getChildAt(0).getData() + " has Children? " + root.getChildAt(0).hasChildren());
		// System.out.println(root.getChildren());
		// System.out.println(hdf5Tree.toStringWithDepth());
		// System.out.println(tree.toStringWithDepth());

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static void populate(List<String> allPaths) {
		Tree hdf5Tree = new Tree();
		List<TreeNode> nodes = new ArrayList<TreeNode>();

		Stack<TreeNode> myStack = new Stack<TreeNode>();

		int heightIs = 0, heightWas = 0, heightWill = 0, childCounter = 0;
		for (int i = 0; i < allPaths.size(); i++) {

			String[] str = allPaths.get(i).split(DELIMITER);
			heightIs = str.length - 2;

			if (i == 0) { // if pathname is "/root"
				nodes.add(0, new TreeNode(str[str.length - 1]));
				myStack.push(new TreeNode(str[str.length - 1]));
			} else {
				if (i > 0)
					heightWas = allPaths.get(i - 1).split(DELIMITER).length - 2;
				if (i < allPaths.size() - 1)
					heightWill = allPaths.get(i + 1).split(DELIMITER).length - 2;

				if (heightIs == heightWas) {
					myStack.pop();
					childCounter++;
				}
				if (heightIs < heightWas) {
					int diff = heightWas - heightIs;
					for (int j = 0; j < diff + 1; j++)
						myStack.pop();
					childCounter = 0;
				}

				nodes.add(i, new TreeNode(str[str.length - 1]));
				myStack.push(nodes.get(i));

				if (myStack.size() > 1)
					myStack.get(myStack.size() - 2).addChild(myStack.get(myStack.size() - 1));

			}
		}

		hdf5Tree.setRoot(myStack.get(0));
	}

	private static boolean isTreeLeaf(String aPath, List<String> allPaths) {
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

	private static List<String> getChildrenList(String parentpath) {
		List<String> list = new ArrayList<String>();
		// String[] pathnames = data.getNames();

		for (int i = 0; i < pathnames.length; i++) {
			if (pathnames[i].contains(parentpath) && !parentpath.equals(pathnames[i]) && !parentpath.equals(DELIMITER)) {
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

	private static List<String> getNodeListByTreeLevel(int treeLevel) {
		List<String> list = new ArrayList<String>();
		// String[] pathnames = data.getNames();

		for (int i = 0; i < pathnames.length; i++) {
			String[] temp = pathnames[i].split(DELIMITER);
			String str = null;
			for (int j = 0; j < treeLevel; j++) {
				if (treeLevel < temp.length) {
					str = DELIMITER + temp[j].concat(DELIMITER + temp[j + 1]);
				}
			}
			if (!list.contains(str) && str != null) {
				list.add(str);
			}

		}

		return list;
	}

	private static List<String> getAllPathnames(String[] fullPaths) {
		List<String> list = new ArrayList<String>();

		for (int i = 0; i < fullPaths.length; i++) {
			String[] tmp = fullPaths[i].split(DELIMITER);
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
		return list;
	}
}
