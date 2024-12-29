package com.arkxit.data.jpa.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arkxit.data.jpa.entity.TreeEntity;
import com.arkxos.framework.commons.collection.Treex;
import com.arkxos.framework.commons.collection.Treex.TreeNode;
import com.arkxos.framework.commons.exception.ServiceException;

/**
 * @author Darkness
 * @date 2020-09-20 17:07:25
 * @version V1.0
 */
public class TreeEntityUtil {

	public static <T extends TreeEntity> Treex<T> buildTree(List<T> dataList) {
		Treex<T> tree = new Treex<>();

		Map<String, Treex.TreeNode<T>> treeNodeMap = new HashMap<>();
		for (T entity : dataList) {
			String parentInnerCode = entity.getParentInnerCode();
			Treex.TreeNode<T> treeNode = null;
			if (treeNodeMap.containsKey(parentInnerCode)) {
				Treex.TreeNode<T> parentNode = treeNodeMap.get(parentInnerCode);
				treeNode = parentNode.addChild(entity);
			} else {
				TreeNode<T> rootNode = (TreeNode<T>) tree.getRoot();
				if (rootNode.getData() != null) {
					throw new ServiceException("发现多个根节点数据，树根节点只能有一个");
				}
				rootNode.setData(entity);
				
				treeNode = rootNode;
			}
			
			treeNodeMap.put(entity.getInnerCode(), treeNode);
		}

		return tree;
	}
	
	public static <T extends TreeEntity> Treex<T> buildTreeWithWarpRoot(List<T> dataList) {
		Treex<T> tree = new Treex<>();

		Map<String, Treex.TreeNode<T>> treeNodeMap = new HashMap<>();
		for (T entity : dataList) {
			String parentInnerCode = entity.getParentInnerCode();
			Treex.TreeNode<T> parentNode = (TreeNode<T>) tree.getRoot();
			if (treeNodeMap.containsKey(parentInnerCode)) {
				parentNode = treeNodeMap.get(parentInnerCode);
			}
			Treex.TreeNode<T> treeNode = parentNode.addChild(entity);
			treeNodeMap.put(entity.getInnerCode(), treeNode);
		}

		return tree;
	}

}

