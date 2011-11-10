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
package uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel;

import java.util.*;
/**
 * Java POJO Tree Class 
 * <br>Used to model a tree-like data structure
 * <br>
 * <br>Source can be found at following GitHub location: 
 * <br><a href="https://github.com/vivin/tree">https://github.com/vivin/tree</a>
 */
public class Tree<T> {

	private TreeNode<T> root;
	
	public Tree() {
		super();
	}

	public TreeNode<T> getRoot() {
		return this.root;
	}

	public void setRoot(TreeNode<T> root) {
		this.root = root;
	}

	public int getNumberOfNodes() {
		int numberOfNodes = 0;

		if (root != null) {
			numberOfNodes = auxiliaryGetNumberOfNodes(root) + 1; // 1 for the root!
		}

		return numberOfNodes;
	}

	private int auxiliaryGetNumberOfNodes(TreeNode<T> node) {
		int numberOfNodes = node.getNumberOfChildren();

		for (TreeNode<T> child : node.getChildren()) {
			numberOfNodes += auxiliaryGetNumberOfNodes(child);
		}

		return numberOfNodes;
	}

	public boolean exists(T dataToFind) {
		return (find(dataToFind) != null);
	}

	public TreeNode<T> find(T dataToFind) {
		TreeNode<T> returnNode = null;

		if (root != null) {
			returnNode = auxiliaryFind(root, dataToFind);
		}

		return returnNode;
	}

	private TreeNode<T> auxiliaryFind(TreeNode<T> currentNode, T dataToFind) {
		TreeNode<T> returnNode = null;
		int i = 0;

		if (currentNode.getData().equals(dataToFind)) {
			returnNode = currentNode;
		}

		else if (currentNode.hasChildren()) {
			i = 0;
			while (returnNode == null && i < currentNode.getNumberOfChildren()) {
				returnNode = auxiliaryFind(currentNode.getChildAt(i), dataToFind);
				i++;
			}
		}

		return returnNode;
	}

	public boolean isEmpty() {
		return (root == null);
	}

	public List<TreeNode<T>> build(TreeTraversalOrderEnum traversalOrder) {
		List<TreeNode<T>> returnList = null;

		if (root != null) {
			returnList = build(root, traversalOrder);
		}

		return returnList;
	}

	public List<TreeNode<T>> build(TreeNode<T> node, TreeTraversalOrderEnum traversalOrder) {
		List<TreeNode<T>> traversalResult = new ArrayList<TreeNode<T>>();

		if (traversalOrder == TreeTraversalOrderEnum.PRE_ORDER) {
			buildPreOrder(node, traversalResult);
		}

		else if (traversalOrder == TreeTraversalOrderEnum.POST_ORDER) {
			buildPostOrder(node, traversalResult);
		}

		return traversalResult;
	}

	private void buildPreOrder(TreeNode<T> node, List<TreeNode<T>> traversalResult) {
		traversalResult.add(node);

		for (TreeNode<T> child : node.getChildren()) {
			buildPreOrder(child, traversalResult);
		}
	}

	private void buildPostOrder(TreeNode<T> node, List<TreeNode<T>> traversalResult) {
		for (TreeNode<T> child : node.getChildren()) {
			buildPostOrder(child, traversalResult);
		}

		traversalResult.add(node);
	}

	public Map<TreeNode<T>, Integer> buildWithDepth(TreeTraversalOrderEnum traversalOrder) {
		Map<TreeNode<T>, Integer> returnMap = null;

		if (root != null) {
			returnMap = buildWithDepth(root, traversalOrder);
		}

		return returnMap;
	}

	public Map<TreeNode<T>, Integer> buildWithDepth(TreeNode<T> node, TreeTraversalOrderEnum traversalOrder) {
		Map<TreeNode<T>, Integer> traversalResult = new LinkedHashMap<TreeNode<T>, Integer>();

		if (traversalOrder == TreeTraversalOrderEnum.PRE_ORDER) {
			buildPreOrderWithDepth(node, traversalResult, 0);
		}

		else if (traversalOrder == TreeTraversalOrderEnum.POST_ORDER) {
			buildPostOrderWithDepth(node, traversalResult, 0);
		}

		return traversalResult;
	}

	private void buildPreOrderWithDepth(TreeNode<T> node, Map<TreeNode<T>, Integer> traversalResult, int depth) {
		traversalResult.put(node, depth);

		for (TreeNode<T> child : node.getChildren()) {
			buildPreOrderWithDepth(child, traversalResult, depth + 1);
		}
	}

	private void buildPostOrderWithDepth(TreeNode<T> node, Map<TreeNode<T>, Integer> traversalResult, int depth) {
		for (TreeNode<T> child : node.getChildren()) {
			buildPostOrderWithDepth(child, traversalResult, depth + 1);
		}

		traversalResult.put(node, depth);
	}

	@Override
	public java.lang.String toString() {
		/*
		 * We're going to assume a pre-order traversal by default
		 */

		java.lang.String stringRepresentation = "";

		if (root != null) {
			stringRepresentation = build(TreeTraversalOrderEnum.PRE_ORDER).toString();

		}

		return stringRepresentation;
	}

	public java.lang.String toStringWithDepth() {
		/*
		 * We're going to assume a pre-order traversal by default
		 */

		java.lang.String stringRepresentation = "";

		if (root != null) {
			stringRepresentation = buildWithDepth(TreeTraversalOrderEnum.PRE_ORDER).toString();
		}

		return stringRepresentation;
	}
}
