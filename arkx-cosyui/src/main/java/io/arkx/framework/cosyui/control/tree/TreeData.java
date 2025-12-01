package io.arkx.framework.cosyui.control.tree;

import io.arkx.framework.Config;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.tree.TreeIterator;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.control.TreeAction;
import io.arkx.framework.thirdparty.commons.ArrayUtils;

import java.util.ArrayList;

/**
 * 树形数据重排工具类<br>
 * 注意：
 * 1、如果设置有level属性，bindData(DataTable dt)中的dt会有level+1层数据，这样做是为了让控件能够知道哪些节点有子节点，以便于进行延迟加载。
 * 2、如果设置有expand=true，则延迟加载时会一次载入相应上级节点下的所有层级的子节点
 * 3、如果未设置有expand=true，则会逐层延迟加载。这时会加载到parentLevel+2层级的数据，以便于控件知道哪些节点可以继续延迟加载。
 * 
 */
public class TreeData {
	protected static final String Blank_Image_Path = "framework/images/blank24x24.gif";

	private static final String Class_Branch = "branch";

	private static final String Class_Branch_NotLast_NotExpand = "branch-notlast-collapse";

	private static final String Class_Branch_Last_NotExpand = "branch-last-collapse";

	private static final String Class_Branch_NotLast_Expand = "branch-notlast-expand";

	private static final String Class_Branch_Last_Expand = "branch-last-expand";

	private static final String Class_Line_Vertical = "line-vertical";

	private static final String Class_Line_Null = "line-null";

	private static final String Class_Leaf_Last = "leaf-last";

	private static final String Class_Leaf_NotLast = "leaf-notlast";

	private static final String Branch_NotLast_NotExpand = "1";

	private static final String Branch_NotLast_Expand = "2";

	private static final String Branch_Last_NotExpand = "3";

	private static final String Branch_Last_Expand = "4";

	protected static final String Class_CheckBox = "tree-checkbox";

	private TreeAction ta;

	private String imagePrefix = null;

	private boolean lazyParentFlag = false;// 标明lazyload时数据源中是否含有parentID对应的元素，用于同时兼容数据源中含有与不含有父元素两种情况

	private boolean expand = true;// 表明当前节点是否己展开

	private boolean lazy = true;// 表明当前节点是否需要延迟加载子节点

	private int _NodeAttributes = 0;
	private int _NodeIcons = 0;
	private int _NodeWrapStart = 0;
	private int _NodeWrapEnd = 0;
	private int _ID = 0;
	private int _ParentID = 0;

	private StringBuilder levelSB = new StringBuilder();
	private StringBuilder attributeSB = new StringBuilder();
	private int levelStrLength = 0;
	private FastStringBuilder sb = new FastStringBuilder();

	public TreeData() {
	}

	public DataTable rewrite(TreeAction ta) {
		this.ta = ta;
		imagePrefix = "<img src='" + Config.getContextPath() + Blank_Image_Path + "' class='tree-skeleton-icon ";

		DataTable dt = ta.getDataSource();

		ArrayList<String> columns = new ArrayList<String>();
		columns.add("_NodeWrapStart");
		columns.add("_NodeWrapEnd");
		columns.add("_NodeIcons");
		columns.add("_NodeAttributes");

		dt.insertColumns(columns.toArray(new String[0]));

		// 计算字段顺序以便于使用数字下标存取，以提高性能
		int i = 0;
		for (DataColumn dc : dt.getDataColumns()) {
			if (dc.getColumnName().equalsIgnoreCase("_NodeAttributes")) {
				_NodeAttributes = i;
			} else if (dc.getColumnName().equalsIgnoreCase("_NodeWrapStart")) {
				_NodeWrapStart = i;
			} else if (dc.getColumnName().equalsIgnoreCase("_NodeWrapEnd")) {
				_NodeWrapEnd = i;
			} else if (dc.getColumnName().equalsIgnoreCase("_NodeIcons")) {
				_NodeIcons = i;
			} else if (dc.getColumnName().equalsIgnoreCase(ta.getIdentifierColumnName())) {
				_ID = i;
			} else if (dc.getColumnName().equalsIgnoreCase(ta.getParentIdentifierColumnName())) {
				_ParentID = i;
			}
			i++;
		}

		Treex<String, DataRow> tree = Treex.dataTableToTree(dt, ta.getIdentifierColumnName(), ta.getParentIdentifierColumnName());
		TreeIterator<String, DataRow> ti = tree.iterator();

		String levelStr = ta.getParam("LevelStr");
		levelSB = new StringBuilder(levelStr == null ? "" : levelStr);
		levelStrLength = levelSB.length();

		Object[][] values = new Object[dt.getRowCount()][dt.getColumnCount()];
		i = 0;
		while (ti.hasNext()) {
			TreeNode<String, DataRow> node = ti.next();
			DataRow dr = node.getValue();
			if (dr != null) {
				String id = dr.getString(_ID);
				if (id.equals(ta.getParentID())) {
					lazyParentFlag = true;
				}
				if (!ta.isLazyLoad() && node.getDepth() > ta.getLevel()) {
					expand = true;
					continue;
				}
				if (ta.isLazyLoad() && !ta.isExpand() && node.getDepth() != (lazyParentFlag ? 2 : 1)) {
					continue;
				}
				attributeSB.delete(0, attributeSB.length());
				if (node.hasChildren()) {
					// 首次加载且等于最大层级
					if (!ta.isLazyLoad() && node.getDepth() == ta.getLevel()) {
						addAttribute("lazy", "1");
						expand = false;
					}
					// 延迟加载
					else if (ta.isLazyLoad() && !ta.isExpand() && node.getDepth() == (lazyParentFlag ? 2 : 1)) {
						addAttribute("lazy", "1");
						expand = false;
					}
				}
				if (node.hasChildren()) {
					addAttribute("treenodetype", "trunk");
				} else {
					addAttribute("treenodetype", "leaf");
				}
				if (node.isLast()) {
					addAttribute("islast", "true");
				}
				addAttribute("level", node.getDepth());
				addAttribute("id", ta.getID() + "_" + id);
				addAttribute("parentID", dr.getString(_ParentID));

				rewriteData(node);
				values[i++] = dr.getDataValues();
			}
		}
		dt = new DataTable(dt.getDataColumns(), (Object[][]) ArrayUtils.subarray(values, 0, i));
		return dt;
	}

	private void addAttribute(String key, Object value) {
		attributeSB.append(' ').append(key).append("=\"").append(value).append("\"");
	}

	/**
	 * 重写数据，增加一些辅助字段. 主要完成以下工作：<br>
	 * 1、计算层级图标
	 * 2、计算节点图标
	 * 3、处理单选多选
	 * 4、将节点HTML包裹上合适的dd/dl
	 */
	private void rewriteData(TreeNode<String, DataRow> node) {
		DataRow dr = node.getValue();
		TreeNode<String, DataRow> parent = node.getParentNode();

		// 准备层级关系标识串
		levelSB.delete(levelStrLength, levelSB.length());

		char[] arr = new char[parent.getDepth()];
		int i = 0;
		while (parent != null && !parent.isRoot()) {
			if (ta.isLazyLoad() && lazyParentFlag && parent.getDepth() == 1) {// 延迟加载时父节点本身也会加载进来，单独作为第1层级
				break;
			}
			i = parent.getDepth() - 1;
			if (parent.isLast()) {
				arr[i] = '0';
			} else {
				arr[i] = '1';
			}
			parent = parent.getParentNode();
		}
		levelSB.append(arr, i, arr.length - i);

		// 层级图标
		sb.clear();
		for (int j = 0; j < levelSB.length(); j++) {
			sb.append(imagePrefix);
			if (levelSB.charAt(j) == '0') {
				sb.append(Class_Line_Null);
			} else if (levelSB.charAt(j) == '1') {
				sb.append(Class_Line_Vertical);
			}
			sb.append("'>");
		}

		// 节点类型图标
		sb.append(imagePrefix);
		if (node.hasChildren() && node.isLast() && expand) {
			levelSB.append('0');
			addAttribute("expand", Branch_Last_Expand);
			sb.append(Class_Branch).append(" ").append(Class_Branch_Last_Expand);
		} else if (node.hasChildren() && node.isLast() && !expand) {
			levelSB.append('0');
			addAttribute("expand", Branch_Last_NotExpand);
			sb.append(Class_Branch).append(" ").append(Class_Branch_Last_NotExpand);
		} else if (node.hasChildren() && !node.isLast() && !expand) {
			levelSB.append('1');
			addAttribute("expand", Branch_NotLast_NotExpand);
			sb.append(Class_Branch).append(" ").append(Class_Branch_NotLast_NotExpand);
		} else if (node.hasChildren() && !node.isLast() && expand) {
			levelSB.append('1');
			addAttribute("expand", Branch_NotLast_Expand);
			sb.append(Class_Branch).append(" ").append(Class_Branch_NotLast_Expand);
		} else if (!node.hasChildren() && node.isLast()) {
			sb.append(Class_Leaf_Last);
		} else if (!node.hasChildren() && !node.isLast()) {
			sb.append(Class_Leaf_NotLast);
		}
		sb.append("'>");

		addAttribute("levelStr", levelSB.toString());

		// 准备节点本身的图标
		String iconName = ta.getIconColumnName();
		String icon = null;
		if (iconName != null && dr.getDataColumn(iconName) != null) {
			icon = dr.getString(iconName);
		}
		if (ObjectUtil.empty(icon)) {
			if (node.isRoot()) {
				icon = ta.getRootIcon();
			} else if (!node.hasChildren()) {
				icon = ta.getLeafIcon();
			} else if (node.hasChildren()) {
				icon = ta.getBranchIcon();
			}
		}

		// 图标转换为样式
		if (icon.startsWith("icons/")) {
			String className = icon.substring(6, icon.lastIndexOf('.'));
			if (className.indexOf('/') > 0) {
				sb.append(imagePrefix.substring(0, imagePrefix.length() - 1));// 去掉一个空格
				sb.append("' style='background:url(").append(Config.getContextPath()).append(icon).append(") no-repeat center center;'>");
			} else {
				sb.append(imagePrefix);
				sb.append(className).append("' style='margin:2px;'>");
			}
		} else {
			sb.append(imagePrefix.substring(0, imagePrefix.length() - 1));// 去掉一个空格
			sb.append("' style='background:url(").append(Config.getContextPath()).append(icon).append(") no-repeat center center;'>");
		}

		// 单选多选
		String checkbox = ta.getCheckbox();
		String radio = ta.getRadio();
		if (StringUtil.isNotEmpty(checkbox)) {
			if (checkbox.equals("all") || checkbox.equals("true") || checkbox.equals("branch") && node.hasChildren()
					|| checkbox.equals("leaf") && !node.hasChildren()) {
				sb.append(imagePrefix);
				sb.append(Class_CheckBox).append("'>");
			}
		} else if (StringUtil.isNotEmpty(radio)) {
			if (radio.equals("all") || radio.equals("true") || radio.equals("branch") && node.hasChildren() || radio.equals("leaf")
					&& !node.hasChildren()) {
				sb.append("<input type='radio' value='").append(dr.getString(_ID)).append("'").append(" name='").append(ta.getID())
						.append("'>");
			}
		}
		dr.set(_NodeIcons, sb.toStringAndClose());
		// 包裹上dl/dd
		boolean leafFlag = false;
		if (!ta.isLazyLoad() && node.getDepth() == ta.getLevel()) {
			// 首次加载且等于最大层级
			leafFlag = true;
		} else if (ta.isLazyLoad() && !ta.isExpand() && node.getDepth() == (lazyParentFlag ? 2 : 1)) {
			// 延迟加载
			leafFlag = true;
		}
		dr.set(_NodeWrapStart, "<dl>");
		sb.clear();
		if (!node.hasChildren() || leafFlag) {
			sb.append("<dd style='display:none'>");
			if (node.isLast()) {
				parent = node.getParentNode();
				while (!parent.isRoot() && parent.isLast()) {
					parent = parent.getParentNode();
				}
				int level = parent.isRoot() ? 1 : parent.getDepth();
				for (int j = 0; j < node.getDepth() - level; j++) {
					sb.append("</dd></dl>");
				}
			}
			sb.append("</dd></dl>");
			dr.set(_NodeWrapEnd, sb.toStringAndClose());
		} else {
			dr.set(_NodeWrapEnd, "<dd>");
		}
		dr.set(_NodeAttributes, attributeSB.toString());
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}
}
