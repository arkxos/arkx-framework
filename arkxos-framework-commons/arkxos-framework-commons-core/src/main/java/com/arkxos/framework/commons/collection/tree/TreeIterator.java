package com.arkxos.framework.commons.collection.tree;

import java.util.Iterator;
import java.util.NoSuchElementException;

/**
 * 以某一节点为父节点遍历该节点用其所有子节点
 * @author Nobody
 * @date 2025-05-18 18:04
 * @since 1.0
 */
public class TreeIterator<K, T> implements Iterator<TreeNode<K, T>>, Iterable<TreeNode<K, T>> {

	private TreeNode<K, T> last;
	private TreeNode<K, T> next;
	private TreeNode<K, T> start;

	/**
	 * 以node为父节点，构造一个遍历器
	 */
	TreeIterator(TreeNode<K, T> node) {
		start = next = node;
	}

	/**
	 * 是否还有下一个节点
	 *
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		if (next == null) {
			return false;
		}
		if (next == start && start.getChildren().isEmpty()) {
			return false;
		}
		if (next != start && next.getDepth() == start.getDepth()) {
			return false;
		}
		return true;
	}

	/**
	 * 获取下一个节点
	 *
	 * @see java.util.Iterator#next()
	 */
	@Override
	public TreeNode<K, T> next() {
		if (next == null) {
			throw new NoSuchElementException();
		}
		last = next;
		if (next.hasChildren()) {
			next = next.getChildren().get(0);
		} else {
			while (next.getNextSibling() == null) {
				if (next.getParent().isRoot()) {
					next = null;
					return last;
				} else {
					next = next.getParent();
				}
			}
			next = next.getNextSibling();
		}
		return last;
	}

	public TreeNode<K, T> nextNode() {
		return next();
	}

	public TreeNode<K, T> currentNode() {// NO_UCD
		return next;
	}

	/**
	 * 删除当前节点
	 *
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove() {
		if (last == null) {
			throw new IllegalStateException();
		}
		last.getParent().getChildren().remove(last);
		last = null;
	}

	@Override
	public Iterator<TreeNode<K, T>> iterator() {
		return this;
	}
}

