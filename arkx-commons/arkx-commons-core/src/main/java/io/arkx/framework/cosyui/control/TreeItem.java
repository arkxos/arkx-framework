package io.arkx.framework.cosyui.control;

import io.arkx.framework.commons.collection.DataRow;

/**
 * 树节点数据项
 * 
 */
public class TreeItem implements Cloneable {
	private DataRow data;

	private boolean isRoot = false;

	private TreeAction ta;

	private TreeItem parent;

	public TreeItem(TreeAction ta, TreeItem parent, DataRow data, boolean isRoot) {
		this.ta = ta;
		this.data = data;
		this.isRoot = isRoot;
		this.parent = parent;
	}

	public void setIcon(String icon) {
		if (isRoot) {
			ta.setRootIcon(icon);
		} else {
			data.set(ta.getIconColumnName(), icon);
		}
	}

	public TreeItem getParent() {
		return parent;
	}

	public void setText(String text) {
		if (isRoot) {
			ta.setRootText(text);
		}
	}

	public String getID() {
		if (data == null) {
			return null;
		}
		return data.getString(ta.getParentIdentifierColumnName());
	}

	public String getParentID() {
		if (data == null) {
			return null;
		}
		return data.getString(ta.getParentIdentifierColumnName());
	}

	public boolean isRoot() {
		return isRoot;
	}

	public DataRow getData() {
		return data;
	}
}
