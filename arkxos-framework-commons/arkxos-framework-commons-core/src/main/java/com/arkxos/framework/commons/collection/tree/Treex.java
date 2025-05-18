package com.arkxos.framework.commons.collection.tree;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.Formatter;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import lombok.Getter;

/**
 * @class org.ark.framework.collection.Treex
 * 树形数据结构
 * 
 * @author Darkness
 * @date 2012-8-6 下午10:03:39
 * @version V1.0
 */
@Getter
public class Treex<K, T> implements Iterable<TreeNode<K, T>>, Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	private boolean isWarpTreeNode = true;

	/**
	 *  获取根节点对象
	 */
	private TreeNode<K, T> root = new TreeNode<>();
	
	public Treex() {
		this(true);
	}

	public Treex(boolean isWarpTreeNode) {
		this.isWarpTreeNode = isWarpTreeNode;
	}

	/**
	 * 根据节点数据获取节点对象
	 */
	public TreeNode<K, T> getNode(T data) {// NO_UCD
		TreeIterator<K, T> ti = iterator();
		while (ti.hasNext()) {
			TreeNode<K, T> tn = ti.next();
			if (tn.getValue().equals(data)) {
				return tn;
			}
		}
		return null;
	}

	/**
	 * 遍历器，遍历整个树
	 */
	@Override
	public TreeIterator<K, T> iterator() {
		return new TreeIterator<>(root);
	}

	/**
	 * 以node为起始节点开始遍历
	 */
	public static <K, T> TreeIterator<K, T> iterator(TreeNode<K, T> node) {// NO_UCD
		return new TreeIterator<>(node);
	}

	/**
	 * 输出整个树形结构为字符串
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return toString(Formatter.DefaultFormatter);
	}

	/**
	 * 以指定的格式输出整个树形结构
	 */
	public String toString(Formatter f) {
		StringBuilder sb = new StringBuilder();
		TreeIterator<K, T> ti = this.iterator();
		while (ti.hasNext()) {
			TreeNode<K, T> tn = ti.nextNode();
			TreeNode<K, T> p = tn.getParent();
			String str = "";
			while (p != null && !p.isRoot()) {
				if (p.isLast()) {
					str = "  " + str;
				} else {
					str = "│ " + str;
				}
				p = p.getParent();
			}
			sb.append(str);
			if (!tn.isRoot()) {
				if (tn.isLast()) {
					sb.append("└─");
				} else {
					sb.append("├─");
				}
			}
			sb.append(f.format(tn.getValue()));
			sb.append("\n");
		}
		return sb.toString();
	}

	/**
	 * 获取所有节点组成的数组
	 */
	public ArrayList<TreeNode<K, T>> toArray() {// NO_UCD
		TreeIterator<K, T> ti = new TreeIterator<K, T>(root);
		ArrayList<TreeNode<K, T>> arr = new ArrayList<>();
		while (ti.hasNext()) {
			arr.add(ti.next());
		}
		return arr;
	}

	/**
	 * 根据DataTable构造一个树形结构，DataTable必须有ID和ParentID两个字段，两个字段构成父子关系
	 */
	public static Treex<String, DataRow> dataTableToTree(DataTable dt) {
		return dataTableToTree(dt, "ID", "ParentID");
	}

	/**
	 * 根据DataTable构造一个树形结构，DataTable中指定的两个字段必须构成父子关系
	 */
	public static Treex<String, DataRow> dataTableToTree(DataTable dt, String identifierColumnName, String parentIdentifierColumnName) {
		Treex<String, DataRow> tree = new Treex<>();
		HashMap<String, DataRow> rowMap = new HashMap<>(dt.getRowCount());
		HashMap<String, ArrayList<String>> parentMap = new HashMap<>();

		int _id = 0, _parentID = 0;
		for (int i = 0; i < dt.getDataColumns().length; i++) {
			DataColumn dc = dt.getDataColumns()[i];
			if (dc.getColumnName().equalsIgnoreCase(identifierColumnName)) {
				_id = i;
			} else if (dc.getColumnName().equalsIgnoreCase(parentIdentifierColumnName)) {
				_parentID = i;
			}
		}

		for (int i = 0; i < dt.getRowCount(); i++) {
			DataRow dr = dt.getDataRow(i);
			String id = dr.getString(_id);
			String parentID = dr.getString(_parentID);
			rowMap.put(id, dr);
			if (ObjectUtil.isEmpty(parentID) || parentID.equals(id)) {
				continue;
			}
			ArrayList<String> list = parentMap.get(parentID);
			if (list == null) {
				list = new ArrayList<>();
				parentMap.put(parentID, list);
			}
			list.add(id);
		}
		for (int i = 0; i < dt.getRowCount(); i++) {
			DataRow dr = dt.getDataRow(i);
			String id = dr.getString(_id);
			String parentID = dr.getString(_parentID);
			if (StringUtil.isEmpty(parentID) || parentID.equals(id) || !rowMap.containsKey(parentID)) {
				TreeNode<String, DataRow> tn = tree.root.addChildByValue(dr);
				if (parentMap.containsKey(id)) {
					dealNode(tn, rowMap, parentMap, _id);
				}
			}
		}
		return tree;
	}

	/**
	 * 递归处理DataTable中的父子关系
	 */
	//{0001=0001, 00010021=0001, 000100210002=00010021, 00010029=0001}
	//{0001=00010029, 00010021=000100210002}
	private static void dealNode(TreeNode<String, DataRow> tn, HashMap<String, DataRow> rowMap, HashMap<String, ArrayList<String>> parentMap,
			int idIndex) {
		DataRow dr = tn.getValue();
		String id = dr.getString(idIndex);
		ArrayList<String> list = parentMap.get(id);
		for (String childID : list) {
			TreeNode<String, DataRow> child = tn.addChildByValue(rowMap.get(childID));
			if (parentMap.containsKey(childID)) {
				dealNode(child, rowMap, parentMap, idIndex);
			}
		}
	}

}
