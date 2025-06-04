package com.arkxos.framework.data.jpa.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.commons.collection.tree.Treex;
import com.arkxos.framework.commons.collection.tree.TreeNode;
import com.arkxos.framework.commons.exception.ServiceException;
import com.arkxos.framework.data.jpa.entity.TreeEntity;

/**
 * @author Darkness
 * @date 2020-09-20 17:07:25
 * @version V1.0
 */
public class TreeEntityUtil {

	public static <T extends TreeEntity> Treex<String, T> buildTree(List<T> dataList) {
		Treex<String, T> tree = new Treex<>();

		Map<String, TreeNode<String, T>> treeNodeMap = new HashMap<>();
		for (T entity : dataList) {
			String parentInnerCode = entity.getParentInnerCode();
			TreeNode<String, T> treeNode = null;
			if (treeNodeMap.containsKey(parentInnerCode)) {
				TreeNode<String, T> parentNode = treeNodeMap.get(parentInnerCode);
				treeNode = parentNode.addChildByValue(entity);
			} else {
				TreeNode<String, T> rootNode = tree.getRoot();
				if (rootNode.getValue() != null) {
					throw new ServiceException("发现多个根节点数据，树根节点只能有一个");
				}
				rootNode.setValue(entity);
				
				treeNode = rootNode;
			}
			
			treeNodeMap.put(entity.getInnerCode(), treeNode);
		}

		return tree;
	}
	
	public static <T extends TreeEntity> Treex<String, T> buildTreeWithWarpRoot(List<T> dataList) {
		Treex<String, T> tree = new Treex<>();

		Map<String, TreeNode<String, T>> treeNodeMap = new HashMap<>();
		for (T entity : dataList) {
			String parentInnerCode = entity.getParentInnerCode();
			TreeNode<String, T> parentNode = tree.getRoot();
			if (treeNodeMap.containsKey(parentInnerCode)) {
				parentNode = treeNodeMap.get(parentInnerCode);
			}
			TreeNode<String, T> treeNode = parentNode.addChildByValue(entity);
			treeNodeMap.put(entity.getInnerCode(), treeNode);
		}

		return tree;
	}

}

