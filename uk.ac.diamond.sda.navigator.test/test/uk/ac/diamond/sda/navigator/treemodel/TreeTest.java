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

package uk.ac.diamond.sda.navigator.treemodel;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.Tree;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeNode;
import uk.ac.diamond.scisoft.analysis.rcp.navigator.treemodel.TreeTraversalOrderEnum;

/**
 * Java POJO Tree Test Class
 * <br>
 * <br>Source can be found at following GitHub location:
 * <br><a href="https://github.com/vivin/tree">https://github.com/vivin/tree</a>
 */
public class TreeTest {

	@Test
	public void TestRootIsNullOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();
		assertNull(tree.getRoot());
	}

	@Test
	public void TestNumberOfNodesIsZeroOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();
		assertEquals(tree.getNumberOfNodes(), 0);
	}

	@Test
	public void TestIsEmptyIsTrueOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();
		assertTrue(tree.isEmpty());
	}

	@Test
	public void TestExistsIsFalseOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();
		String dataToFind = "";

		assertFalse(tree.exists(dataToFind));
	}

	@Test
	public void TestFindReturnsNullOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();
		String dataToFind = "";

		assertNull(tree.find(dataToFind));
	}

	@Test
	public void TestPreOrderBuildReturnsNullListOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertNull(tree.build(TreeTraversalOrderEnum.PRE_ORDER));
	}

	@Test
	public void TestPostOrderBuildReturnsNullListOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertNull(tree.build(TreeTraversalOrderEnum.POST_ORDER));
	}

	@Test
	public void TestPreOrderBuildWithDepthReturnsNullMapOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertNull(tree.buildWithDepth(TreeTraversalOrderEnum.PRE_ORDER));
	}

	@Test
	public void TestPostOrderBuildWithDepthReturnsNullMapOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertNull(tree.buildWithDepth(TreeTraversalOrderEnum.POST_ORDER));
	}

	@Test
	public void TestToStringReturnsEmptyStringOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertEquals(tree.toString(), "");
	}

	@Test
	public void TestToStringWithDepthReturnsEmptyStringOnNewTreeCreation() {
		Tree<String> tree = new Tree<String>();

		assertEquals(tree.toStringWithDepth(), "");
	}

	@Test
	public void TestSetRootGetRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>();
		tree.setRoot(root);

		assertNotNull(tree.getRoot());
	}

	@Test
	public void TestNumberOfNodesIsOneWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>();
		tree.setRoot(root);

		assertEquals(tree.getNumberOfNodes(), 1);
	}

	@Test
	public void TestEmptyIsFalseWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>();
		tree.setRoot(root);

		assertFalse(tree.isEmpty());
	}

	@Test
	public void TestPreOrderBuildListSizeIsOneWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>("root");
		tree.setRoot(root);

		assertEquals(tree.build(TreeTraversalOrderEnum.PRE_ORDER).size(), 1);
	}

	@Test
	public void TestPostOrderBuildListSizeIsOneWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>("root");
		tree.setRoot(root);

		assertEquals(tree.build(TreeTraversalOrderEnum.POST_ORDER).size(), 1);
	}

	@Test
	public void TestPreOrderBuildWithDepthSizeIsOneWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>("root");
		tree.setRoot(root);

		assertEquals(tree.buildWithDepth(TreeTraversalOrderEnum.PRE_ORDER)
				.size(), 1);
	}

	@Test
	public void TestPostOrderBuildWithDepthSizeIsOneWithNonNullRoot() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> root = new TreeNode<String>("root");
		tree.setRoot(root);

		assertEquals(tree.buildWithDepth(TreeTraversalOrderEnum.POST_ORDER)
				.size(), 1);
	}

	/*
	 * Tree looks like: A / \ B C \ D For the following tests
	 */
	@Test
	public void TestNumberOfNodes() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		assertEquals(tree.getNumberOfNodes(), 4);
	}

	@Test
	public void TestExistsReturnsTrue() {
		Tree<String> tree = new Tree<String>();
		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		String dataToFindD = "D";

		assertTrue(tree.exists(dataToFindD));
	}

	@Test
	public void TestFindReturnsNonNull() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		String dataToFindD = "D";

		assertNotNull(tree.find(dataToFindD));
	}

	@Test
	public void TestExistsReturnsFalse() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		String dataToFindE = "E";

		assertFalse(tree.exists(dataToFindE));
	}

	@Test
	public void TestFindReturnsNull() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		String dataToFindE = "E";

		assertNull(tree.find(dataToFindE));
	}

	// Pre-order traversal will give us A B C D
	@Test
	public void TestPreOrderBuild() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		List<TreeNode<String>> preOrderList = new ArrayList<TreeNode<String>>();
		preOrderList.add(new TreeNode<String>("A"));
		preOrderList.add(new TreeNode<String>("B"));
		preOrderList.add(new TreeNode<String>("C"));
		preOrderList.add(new TreeNode<String>("D"));

		// Instead of checking equalities on the lists themselves, we can check
		// equality on the toString's
		// they should generate the same toString's

		assertEquals(tree.build(TreeTraversalOrderEnum.PRE_ORDER).toString(),
				preOrderList.toString());
	}

	// Post-order traversal will give us B D C A
	@Test
	public void TestPostOrderBuild() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		List<TreeNode<String>> postOrderList = new ArrayList<TreeNode<String>>();
		postOrderList.add(new TreeNode<String>("B"));
		postOrderList.add(new TreeNode<String>("D"));
		postOrderList.add(new TreeNode<String>("C"));
		postOrderList.add(new TreeNode<String>("A"));

		// Instead of checking equalities on the lists themselves, we can check
		// equality on the toString's
		// they should generate the same toString's

		assertEquals(tree.build(TreeTraversalOrderEnum.POST_ORDER).toString(),
				postOrderList.toString());
	}

	// Pre-order traversal with depth will give us A:0, B:1, C:1, D:2
	@Test
	public void TestPreOrderBuildWithDepth() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		Map<TreeNode<String>, Integer> preOrderMapWithDepth = new LinkedHashMap<TreeNode<String>, Integer>();
		preOrderMapWithDepth.put(new TreeNode<String>("A"), 0);
		preOrderMapWithDepth.put(new TreeNode<String>("B"), 1);
		preOrderMapWithDepth.put(new TreeNode<String>("C"), 1);
		preOrderMapWithDepth.put(new TreeNode<String>("D"), 2);

		// Instead of checking equalities on the maps themselves, we can check
		// equality on the toString's
		// they should generate the same toString's

		assertEquals(tree.buildWithDepth(TreeTraversalOrderEnum.PRE_ORDER)
				.toString(), preOrderMapWithDepth.toString());
	}

	// Post-order traversal with depth will give us B:1, D:2, C:1, A:0
	@Test
	public void TestPostOrderBuildWithDepth() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		Map<TreeNode<String>, Integer> postOrderMapWithDepth = new LinkedHashMap<TreeNode<String>, Integer>();
		postOrderMapWithDepth.put(new TreeNode<String>("B"), 1);
		postOrderMapWithDepth.put(new TreeNode<String>("D"), 2);
		postOrderMapWithDepth.put(new TreeNode<String>("C"), 1);
		postOrderMapWithDepth.put(new TreeNode<String>("A"), 0);

		// Instead of checking equalities on the maps themselves, we can check
		// equality on the toString's
		// they should generate the same toString's

		assertEquals(tree.buildWithDepth(TreeTraversalOrderEnum.POST_ORDER)
				.toString(), postOrderMapWithDepth.toString());
	}

	// toString and toStringWithDepth both use pre-order traversal
	@Test
	public void TestToString() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		List<TreeNode<String>> preOrderList = new ArrayList<TreeNode<String>>();
		preOrderList.add(new TreeNode<String>("A"));
		preOrderList.add(new TreeNode<String>("B"));
		preOrderList.add(new TreeNode<String>("C"));
		preOrderList.add(new TreeNode<String>("D"));

		assertEquals(tree.toString(), preOrderList.toString());
	}

	@Test
	public void TestToStringWithDepth() {
		Tree<String> tree = new Tree<String>();

		TreeNode<String> rootA = new TreeNode<String>("A");
		TreeNode<String> childB = new TreeNode<String>("B");
		TreeNode<String> childC = new TreeNode<String>("C");
		TreeNode<String> childD = new TreeNode<String>("D");

		childC.addChild(childD);
		rootA.addChild(childB);
		rootA.addChild(childC);

		tree.setRoot(rootA);

		Map<TreeNode<String>, Integer> preOrderMapWithDepth = new LinkedHashMap<TreeNode<String>, Integer>();
		preOrderMapWithDepth.put(new TreeNode<String>("A"), 0);
		preOrderMapWithDepth.put(new TreeNode<String>("B"), 1);
		preOrderMapWithDepth.put(new TreeNode<String>("C"), 1);
		preOrderMapWithDepth.put(new TreeNode<String>("D"), 2);

		assertEquals(tree.toStringWithDepth(), preOrderMapWithDepth.toString());
	}

}
