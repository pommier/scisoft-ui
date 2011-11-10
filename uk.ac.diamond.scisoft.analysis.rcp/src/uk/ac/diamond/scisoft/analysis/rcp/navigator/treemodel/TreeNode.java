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

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.core.resources.IFile;

/**
 * Java POJO Tree Node Class is used with the <i>HDF5Tree</i> class to create nodes 
 * <br>
 * <br>Source can be found at following GitHub location: 
 * <br><a href="https://github.com/vivin/tree">https://github.com/vivin/tree</a>
 */
public class TreeNode<T> {

	private T data;
	private int id;
	private int level;
	private List<TreeNode<T>> children;
	private TreeNode<T> parent;
	private IFile container;

	public TreeNode() {
		super();
		children = new ArrayList<TreeNode<T>>();
	}

	public TreeNode(T data) {
		this();
		setData(data);
	}
	
	public TreeNode(T data, IFile file) {
		this();
		setData(data);
		setFile(file);
	}

	public TreeNode(T data, int level) {
		this();
		// setID(id);
		setLevel(level);
		setData(data);
	}

	public TreeNode<T> getParent() {
		return this.parent;
	}

	public List<TreeNode<T>> getChildren() {
		return this.children;
	}

	public int getNumberOfChildren() {
		return getChildren().size();
	}

	public boolean hasChildren() {
		return (getNumberOfChildren() > 0);
	}

	public void setChildren(List<TreeNode<T>> children) {
		for (TreeNode<T> child : children) {
			child.parent = this;
		}

		this.children = children;
	}

	public void addChild(TreeNode<T> child) {
		child.parent = this;
		children.add(child);
	}

	public void addChildAt(int index, TreeNode<T> child) throws IndexOutOfBoundsException {
		child.parent = this;
		children.add(index, child);
	}

	public void removeChildren() {
		this.children = new ArrayList<TreeNode<T>>();
	}

	public void removeChildAt(int index) throws IndexOutOfBoundsException {
		children.remove(index);
	}

	public TreeNode<T> getChildAt(int index) throws IndexOutOfBoundsException {
		return children.get(index);
	}

	public T getData() {
		return this.data;
	}

	public void setData(T data) {
		this.data = data;
	}

	public IFile getFile() {
		return container;
	}

	public void setFile(IFile container) {
		this.container = container;
	}

	public int getID() {
		return this.id;
	}

	public void setID(int id) {
		this.id = id;
	}

	public int getLevel() {
		return this.level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public java.lang.String toString() {
		return getData().toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		TreeNode<?> other = (TreeNode<?>) obj;
		if (data == null) {
			if (other.data != null) {
				return false;
			}
		} else if (!data.equals(other.data)) {
			return false;
		}
		return true;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.T#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((data == null) ? 0 : data.hashCode());
		return result;
	}

	public java.lang.String toStringVerbose() {
		java.lang.String stringRepresentation = (getData().toString() + ":[");

		for (TreeNode<T> node : getChildren()) {
			stringRepresentation += node.getData().toString() + ", ";
		}

		// Pattern.DOTALL causes ^ and $ to match. Otherwise it won't. It's retarded.
		Pattern pattern = Pattern.compile(", $", Pattern.DOTALL);
		Matcher matcher = pattern.matcher(stringRepresentation);

		stringRepresentation = matcher.replaceFirst("");
		stringRepresentation += "]";

		return stringRepresentation;
	}
}
