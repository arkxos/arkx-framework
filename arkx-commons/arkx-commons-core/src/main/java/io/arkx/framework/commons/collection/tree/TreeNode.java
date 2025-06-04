package io.arkx.framework.commons.collection.tree;



import io.arkx.framework.commons.collection.tree.jackson.TreeNodeSerializer;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;

/**
 * 树形结构中的一个节点
 *
 * // 创建根节点
 * TreeNode<String> root = new TreeNode<>("Root");
 *
 * // 添加子节点
 * TreeNode<String> child1 = new TreeNode<>("Child1");
 * TreeNode<String> child2 = new TreeNode<>("Child2");
 * root.addChild(child1);
 * root.addChild(child2);
 *
 * // 添加孙子节点
 * child1.addChild(new TreeNode<>("Grandchild1"));
 * child1.addChild(new TreeNode<>("Grandchild2"));
 *
 * // 打印树结构
 * root.printTree();
 *
 * 输出：
 * └── Root
 * 	│   └── Child1
 * 	│       │   └── Grandchild1
 * 	│       │   └── Grandchild2
 * 	│   └── Child2
 *
 *
 * // 前序遍历
 * System.out.println("前序遍历: " + root.preorderTraversal());
 * // [Root, Child1, Grandchild1, Grandchild2, Child2]
 *
 * // 后序遍历
 * System.out.println("后序遍历: " + root.postorderTraversal());
 * // [Grandchild1, Grandchild2, Child1, Child2, Root]
 *
 * // 深度查询
 * System.out.println("树深度: " + root.getDepth()); // 2
 *
 * @author Nobody
 * @date 2025-05-18 18:02
 * @since 1.0
 */
@JsonSerialize(using = TreeNodeSerializer.class)
@Getter
@Setter
public class TreeNode<K, T> implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	/**
	 * 节点唯一标识
	 */
	private K id;

	/**
	 * 父节点ID（根节点为 null）
	 */
	private K parentId;

	/**
	 * 节点名称
	 */
	private String name;

	private String nodeType = "node";

	/**
	 * 节点路径，用于表示节点在树形结构中的路径
	 */
	private String path;

	/**
	 * 节点数据
	 */
	private T value;

	private Map<String, Object> extraDatas = new HashMap<>();

	/**
	 * 父节点对象
	 */
	private TreeNode<K, T> parentNode;

	private Boolean isParent;

	/**
	 * 子节点列表
	 */
	private ArrayList<TreeNode<K, T>> children;

	/**
	 *  节点在树形结构中的级别，根节点为0
	 */
	private int depth;

	/**
	 *  当前节点在父节点的所有子节点中的位置（第几个）
	 */
	private int position;

	// ----------------- 核心构造方法 -----------------

	public TreeNode() {
		this(null, "", null);
	}

	public TreeNode(K id, String name, T value) {
		this.id = id;
		this.setValue(value);
		this.children = new ArrayList<>();
	}

	public TreeNode(K id, String name, T value, TreeNode<K, T> parentNode) {
		this(id, name, value);
		this.parentNode = parentNode;
	}

	public void setValue(T value) {
		this.value = value;
		if (value instanceof TreeNodeData<?>) {
			TreeNodeData<K> tn = (TreeNodeData<K>) value;
			this.id = tn.getId();
			this.parentId = tn.getParentId();
			this.name = tn.getName();
			this.path = tn.getPath();
		}
	}

	public TreeNode<K, T> addChild(TreeNode<K, T> child) {
		child.setParentId(this.id);
		child.parentNode = this;

		child.depth = depth + 1;
		child.position = children.size();
		children.add(child);

		return child;
	}

	// ----------------- 基础操作方法 -----------------

	public void addExtraData(String key, Object value) {
		extraDatas.put(key, value);
	}

	/**
	 * 为本节点添加一个子节点，并为子节点指定节点数据
	 */
	public TreeNode<K, T> addChildByValue(T value) {
		TreeNode<K, T> tn = new TreeNode<>();
		tn.value = value;
		return addChild(tn);
	}

	/**
	 * 删除子节点
	 * @param child 要删除的子节点
	 * @return 是否删除成功
	 */
	public boolean removeChild(TreeNode<K, T> child) {
		boolean removed = children.remove(child);

		if (removed) {
			int pos = child.getPosition();
			for (int i = pos + 1; i < children.size(); i++) {
				TreeNode<K, T> tn = children.get(i);
				tn.positionMinusOne();
			}

			child.setParentNode(null);
		}

		return removed;
	}

	/**
	 * 根据节点数据删除一个子节点
	 */
	public void removeChildByValue(T value) {
		for (int i = 0; i < children.size(); i++) {
			TreeNode<K, T> child = children.get(i);
			if (value == null && child.getValue() == null) {
				children.remove(i);
				break;
			} else if (child.getValue().equals(value)) {
				children.remove(i);
				break;
			}
		}
	}

	/**
	 * 根据节点数据获得节点本身
	 */
	public TreeNode<K, T> findNodeByValue(T value) {// NO_UCD
		if (value == null && this.value == null) {
			return this;
		}
		if (this.value.equals(value)) {
			return this;
		}
		for (TreeNode<K, T> child : children) {
			TreeNode<K, T> found = child.findNodeByValue(value);
			if (found != null) {
				return found;
			}
		}
		return null;
	}

	/**
	 * 上一个节点，没有上一个节点则返回null
	 */
	public TreeNode<K, T> getPreviousSibling() {
		if (position == 0) {
			return null;
		}
		return parentNode.getChildren().get(position - 1);
	}

	/**
	 * 下一个节点,没有下一个节点则返回null
	 */
	public TreeNode<K, T> getNextSibling() {
		if (parentNode == null || position == parentNode.getChildren().size() - 1) {
			return null;
		}
		return parentNode.getChildren().get(position + 1);
	}

	/**
	 * 是否是根节点
	 */
	public boolean isRoot() {
		return parentNode == null;
	}

	/**
	 * 是否是最后一个节点
	 */
	public boolean isLast() {
		return parentNode == null || position == parentNode.getChildren().size() - 1;
	}

	/**
	 * 是否含有子节点
	 */
	public boolean hasChildren() {
		return !children.isEmpty();
	}

	/**
	 * 获取最后一个子节点
	 */
	public TreeNode<K, T> lastChild() {// NO_UCD
		int size = children.size();
		if (size == 0) {
			return null;
		}
		return children.get(size - 1);
	}

	/**
	 * 清空所有子节点
	 */
	public void clearChildren() {
		children.forEach(child -> child.setParentNode(null));
		children.clear();
	}

	public void positionMinusOne() {
		position--;
	}

	// ----------------- 树结构查询方法 -----------------
	/**
	 * 根据ID查找节点（深度优先搜索）
	 */
	public TreeNode<K, T> findNodeById(K targetId) {
		if (this.id.equals(targetId)) return this;
		for (TreeNode<K, T> child : children) {
			TreeNode<K, T> found = child.findNodeById(targetId);
			if (found != null) return found;
		}
		return null;
	}

	/**
	 * 判断是否为叶子节点
	 */
	public boolean isLeaf() {
		return children.isEmpty();
	}

	public boolean isParent() {
		if (this.isParent != null) {
			return this.isParent;
		}
		return !children.isEmpty();
	}

	/**
	 * 获取树的深度（从当前节点到最远叶子节点的距离）
	 */
	public int getDepth() {
		if (isLeaf()) return 0;
		int maxDepth = 0;
		for (TreeNode<K, T> child : children) {
			maxDepth = Math.max(maxDepth, child.getDepth());
		}
		return maxDepth + 1;
	}

	// ----------------- 遍历方法 -----------------
	/**
	 * 前序遍历（根 -> 子节点）
	 */
	public List<T> preorderTraversal() {
		List<T> result = new ArrayList<>();
		result.add(this.value);
		for (TreeNode<K, T> child : children) {
			result.addAll(child.preorderTraversal());
		}
		return result;
	}

	/**
	 * 后序遍历（子节点 -> 根）
	 */
	public List<T> postorderTraversal() {
		List<T> result = new ArrayList<>();
		for (TreeNode<K, T> child : children) {
			result.addAll(child.postorderTraversal());
		}
		result.add(this.value);
		return result;
	}

	/**
	 * 层序遍历（广度优先）
	 */
	public List<T> levelOrderTraversal() {
		List<T> result = new ArrayList<>();
		Queue<TreeNode<K, T>> queue = new LinkedList<>();
		queue.offer(this);
		while (!queue.isEmpty()) {
			TreeNode<K, T> node = queue.poll();
			result.add(node.getValue());
			queue.addAll(node.getChildren());
		}
		return result;
	}

	// ----------------- 辅助方法 -----------------
	@Override
	public String toString() {
		return "TreeNode{" +
				"value=" + value +
				", children=" + children.size() +
				'}';
	}

	/**
	 * 以缩进格式打印树结构
	 */
	public void printTree() {
		printTree(this, 0);
	}

	private void printTree(TreeNode<K, T> node, int indent) {
		String str = "│   ".repeat(Math.max(0, indent)) + "└── " + node.getValue();
		System.out.println(str);

		for (TreeNode<K, T> child : node.getChildren()) {
			printTree(child, indent + 1);
		}
	}
}
