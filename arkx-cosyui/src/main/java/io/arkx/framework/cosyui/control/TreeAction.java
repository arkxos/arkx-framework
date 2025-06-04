package io.arkx.framework.cosyui.control;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.cosyui.UIException;
import io.arkx.framework.cosyui.control.tree.TreeBody;
import io.arkx.framework.cosyui.control.tree.TreeData;
import io.arkx.framework.cosyui.tag.ListTag;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.cosyui.zhtml.ZhtmlExecuteContext;
import io.arkx.framework.cosyui.zhtml.ZhtmlManagerContext;
import io.arkx.framework.i18n.LangUtil;

/**
 * 树数据绑定行为
 * 
 */
public class TreeAction {

	/**
	 * 唯一标识列名
	 */
	private String IdentifierColumnName = "ID";

	/**
	 * 父级唯一标识列名
	 */
	private String ParentIdentifierColumnName = "ParentID";

	/**
	 * 图标列名
	 */
	private String IconColumnName = "Icon";

	private String rootIcon = "icons/extra/icon_tree10.gif";

	private String leafIcon = "icons/extra/icon_tree09.gif";

	private String branchIcon = "icons/extra/icon_tree09.gif";

	private DataTable dataSource;

	private String rootText;

	private String ID;

	private int level;

	private int parentLevel;

	private String parentID;

	private boolean lazy;

	private boolean customscrollbar;

	private boolean cascade = true;

	private String checkbox;

	private String radio;

	private boolean lazyLoad;// 标志是否是延迟加载的进入

	private boolean expand; // 是否在延迟加载是全部展开

	private String style;

	private TreeBody tagBody;

	private Mapx<String, Object> Params = new Mapx<>();

	private String method;
	private String rest;

	private boolean isAjaxRequest;

	private String result = "";

	public String getMethod() {
		if(method == null) {
			method = "";
		}
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Mapx<String, Object> getParams() {
		return Params;
	}

	public void setParams(Mapx<String, Object> params) {
		Params = params;
	}

	/**
	 * 获取指定参数名的值
	 */
	public String getParam(String key) {
		return Params.getString(key);
	}

	public void bindData(DataTable dt) {
		dataSource = dt;
		TreeData td = new TreeData();
		td.setLazy(lazy);
		dataSource = td.rewrite(this);
		items = null;
	}

	void addVariables(AbstractExecuteContext context) {
		context.addDataVariable(ListTag.ZListDataNameKey, dataSource);
		context.addDataVariable(ListTag.ZListItemNameKey, "DataRow");
		context.addDataVariable("_TreeAction", this);
		LangUtil.decodeDataTable(dataSource, context.getLanguage()); // 检查国际化字符串
	}

	protected void bindData() throws Exception {
		if (dataSource == null) {
			throw new UIException("DataSource can't be empty");
		}

		if (isAjaxRequest) {
			ZhtmlExecuteContext context = new ZhtmlExecuteContext(ZhtmlManagerContext.getInstance(), null, null);
			addVariables(context);
			if (!ObjectUtil.isEmpty(tagBody)) {
				tagBody.getExecutor().execute(context);
			}
			result = context.getOut().getResult();
		}
	}

	public String getRootText() {
		return rootText;
	}

	/**
	 * 设置树形菜单根节点文本内容
	 */
	public void setRootText(String rootText) {
		this.rootText = rootText;
	}

	/**
	 * 设置根节点的图标
	 */
	public void setRootIcon(String iconFileName) {
		rootIcon = iconFileName;
	}

	/**
	 * 设置叶子节点图标
	 */
	public void setLeafIcon(String iconFileName) {
		leafIcon = iconFileName;
	}

	/**
	 * 设置分支节点图标
	 */
	public void setBranchIcon(String iconFileName) {
		branchIcon = iconFileName;
	}

	/**
	 * 获取分支节点图标相对路径
	 */
	public String getBranchIcon() {
		return branchIcon;
	}

	public String getLeafIcon() {
		return leafIcon;
	}

	public String getRootIcon() {
		return rootIcon;
	}

	public String getIdentifierColumnName() {
		return IdentifierColumnName;
	}

	/**
	 * 设置主键列名
	 */
	public void setIdentifierColumnName(String identifierColumnName) {
		IdentifierColumnName = identifierColumnName;
	}

	public String getParentIdentifierColumnName() {
		return ParentIdentifierColumnName;
	}

	/**
	 * 设置父节点主键列名
	 */
	public void setParentIdentifierColumnName(String parentIdentifierColumnName) {
		ParentIdentifierColumnName = parentIdentifierColumnName;
	}

	public boolean isLazy() {
		return lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public boolean isCustomscrollbar() {
		return customscrollbar;
	}

	public void setCustomscrollbar(boolean customscrollbar) {
		this.customscrollbar = customscrollbar;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getID() {
		return ID;
	}

	public void setID(String id) {
		ID = id;
	}

	public String getStyle() {
		return style;
	}

	public void setStyle(String style) {
		this.style = style;
	}

	public boolean isLazyLoad() {
		return lazyLoad;
	}

	public void setLazyLoad(boolean lazyLoad) {
		this.lazyLoad = lazyLoad;
	}

	public int getParentLevel() {
		return parentLevel;
	}

	public void setParentLevel(int parentLevel) {
		this.parentLevel = parentLevel;
	}

	public boolean isExpand() {
		return expand;
	}

	public void setExpand(boolean expand) {
		this.expand = expand;
	}

	public DataTable getDataSource() {
		return dataSource;
	}

	public String getCheckbox() {
		return checkbox;
	}

	public void setCheckbox(String checkbox) {
		this.checkbox = checkbox;
	}

	public String getRadio() {
		return radio;
	}

	public void setRadio(String radio) {
		this.radio = radio;
	}

	public boolean isCascade() {
		return cascade;
	}

	public void setCascade(boolean cascade) {
		this.cascade = cascade;
	}

	public TreeBody getTagBody() {
		return tagBody;
	}

	public void setTagBody(TreeBody tagBody) {
		this.tagBody = tagBody;
	}

	public boolean isAjaxRequest() {
		return isAjaxRequest;
	}

	public void setAjaxRequest(boolean isAjaxRequest) {
		this.isAjaxRequest = isAjaxRequest;
	}

	public String getResult() {
		return result;
	}

	public String getParentID() {
		return parentID;
	}

	public void setParentID(String parentID) {
		this.parentID = parentID;
	}

	public String getIconColumnName() {
		return IconColumnName;
	}

	public void setIconColumnName(String iconColumnName) {
		IconColumnName = iconColumnName;
	}

	/**
	 * 己废弃，没有实际用途
	 */
	@Deprecated
	public void setTotal(int total) {

	}

	/**
	 * 己废弃，返回Integer.MAX_VALUE
	 */
	@Deprecated
	public int getPageSize() {
		return Integer.MAX_VALUE;
	}

	/**
	 * 己废弃，返回0
	 */
	@Deprecated
	public int getPageIndex() {
		return 0;
	}

	@Deprecated
	public int getItemSize() {
		return dataSource.getRowCount() + 1;
	}

	private List<TreeItem> items = null;

	@Deprecated
	public List<TreeItem> getItemList() {
		if (items == null) {
			ArrayList<TreeItem> list = new ArrayList<>();
			HashMap<String, TreeItem> map = new HashMap<>();
			TreeItem root = new TreeItem(this, null, null, true);
			list.add(root);
			for (DataRow dr : dataSource) {
				String parentID = dr.getString(ParentIdentifierColumnName);
				TreeItem parent = map.get(parentID);
				if (parent == null) {
					parent = root;
				}
				TreeItem ti = new TreeItem(this, parent, dr, false);
				map.put(dr.getString(IdentifierColumnName), ti);
				list.add(ti);
			}
			items = list;
		}
		return items;
	}

	@Deprecated
	public TreeItem getItem(int index) { // NO_UCD
		getItemList();
		return items.get(index);
	}

	public String getRest() {
		if(rest == null) {
			rest = "";
		}
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}
}
