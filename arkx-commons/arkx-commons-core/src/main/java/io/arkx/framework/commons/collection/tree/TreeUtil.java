package io.arkx.framework.commons.collection.tree;

import org.checkerframework.checker.units.qual.K;

import java.util.*;

/**
 * @author Nobody
 * @date 2025-05-18 23:07
 * @since 1.0
 */
public class TreeUtil {

	/**
	 * 从平面列表构建树结构
	 * @param nodes 所有节点列表（包含 id/parentId）
	 * @return 构建完成的根节点列表
	 */
	public static <K, T> List<? extends TreeNode<K, T>> buildTree(Collection<? extends TreeNode<K, T>> nodes) {
		Map<K, TreeNode<K, T>> nodeMap = new HashMap<>();
		List<TreeNode<K, T>> roots = new ArrayList<>();

		// 第一遍遍历：创建所有节点并记录父ID
		for (TreeNode<K, T> node : nodes) {
			nodeMap.put(node.getId(), node);
		}

		// 第二遍遍历：建立父子关系
		for (TreeNode<K, T> node : nodes) {
			K parentId = node.getParentId();
			if (parentId == null) {
				roots.add(node);
			} else {
				TreeNode<K, T> parent = nodeMap.get(parentId);
				if (parent != null) {
					parent.addChild(node);
				} else {
					// 可选：记录孤儿节点或抛出异常
					System.err.println("Orphan node: " + node.getId());
				}
			}
		}
		return roots;
	}

	public static <K, R extends TreeNodeData<K>> Treex<K, R> buildTreexFromData(Collection<R> datas) {
		Treex<K, R> treex = new Treex<>();

		List<TreeNode<K, R>> nodes = buildTreeFromData(datas);
		treex.getRoot().addChildren(nodes);
		return treex;
	}

	public static <K, R extends TreeNodeData<K>> List<TreeNode<K, R>> buildTreeFromData(Collection<R> nodes) {
		Map<K, TreeNode<K, R>> nodeMap = new HashMap<>();
		List<TreeNode<K, R>> roots = new ArrayList<>();

		// 第一遍遍历：创建所有节点并记录父ID
		for (R node : nodes) {
			TreeNode<K, R> treeNode = new TreeNode<>();
			treeNode.setValue(node);
			nodeMap.put(node.getId(), treeNode);
		}

		// 第二遍遍历：建立父子关系
		for (TreeNode<K, R> node : nodeMap.values()) {
			K parentId = node.getParentId();
			if (parentId == null) {
				roots.add(node);
			} else {
				TreeNode<K, R> parent = nodeMap.get(parentId);
				if (parent != null) {
					parent.addChild(node);
				} else {
					roots.add(node);
					// 可选：记录孤儿节点或抛出异常
					System.err.println("Orphan node: " + node.getId());
				}
			}
		}
		return roots;
	}

}
