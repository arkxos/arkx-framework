package com.arkxos.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import com.arkxos.framework.Config;
import com.arkxos.framework.Constant;
import com.arkxos.framework.Current;
import com.arkxos.framework.FrameworkPlugin;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.core.method.IMethodLocator;
import com.arkxos.framework.core.method.MethodLocatorUtil;
import com.arkxos.framework.cosyui.control.grid.DataGridBody;
import com.arkxos.framework.cosyui.control.grid.DataGridBodyManager;
import com.arkxos.framework.cosyui.tag.ArkTag;
import com.arkxos.framework.cosyui.template.TagAttr;
import com.arkxos.framework.cosyui.template.TemplateExecutor;
import com.arkxos.framework.cosyui.template.command.TagCommand;
import com.arkxos.framework.cosyui.template.exception.TemplateRuntimeException;
import com.arkxos.framework.security.PrivCheck;
import com.arkxos.framework.security.exception.PrivException;

/**
 * DataGrid标签
 * 
 */
public class DataGridTag extends ArkTag {
	private String method;
	Object data;
	private String id;
	private boolean page = true;
	private int size;
	private boolean multiSelect = true;
	private boolean autoFill = true;
	private boolean autoPageSize = false;
	private boolean scroll = true;
	private boolean lazy = false;// 默认是否加载数据
	private int cacheSize;
	private String bodyUID;

	public String getTagName() {
		return "datagrid";
	}

	DataGridAction prepareAction() {
		DataGridAction dga = new DataGridAction();
		dga.setMethod(this.method);
		dga.setID(this.id);
		dga.setAjaxRequest(false);
		dga.setPageEnabled(this.page);
		dga.setMultiSelect(this.multiSelect);
		dga.setAutoFill(this.autoFill);
		dga.setAutoPageSize(this.autoPageSize);
		dga.setScroll(this.scroll);
		dga.setCacheSize(this.cacheSize);
		dga.setLazy(this.lazy);
		if (this.page) {
			dga.setPageIndex(0);
			if ((dga.getParams() != null) && (StringUtil.isNotEmpty(dga.getParam(Constant.DataGridPageIndex)))) {
				dga.setPageIndex(Integer.parseInt(dga.getParam(Constant.DataGridPageIndex)));
			}
			if (dga.getPageIndex() < 0) {
				dga.setPageIndex(0);
			}
			if (this.autoPageSize) {
				this.size = 30;
			}
			dga.setPageSize(this.size);
		}
		dga.setTagBody(DataGridBodyManager.get(dga, this.bodyUID, getTagSource()));
		return dga;
	}

	public int doStartTag() throws TemplateRuntimeException {
		try {
			DataGridAction dga = prepareAction();
			if (this.lazy) {
				dga.bindData(new DataTable());// 默认不加载
			} else if (ObjectUtil.notEmpty(this.method)) {
				IMethodLocator m = MethodLocatorUtil.find(this.method);
				PrivCheck.check(m);
				dga.setParams(Current.getRequest());
				m.execute(new Object[] { dga });
			} else if (this.data != null) {
				if ((this.data instanceof DataTable)) {
					dga.bindData((DataTable) this.data);
				} else if ("".equals(this.data)) {
					dga.bindData(new DataTable());
				} else {
					throw new TemplateRuntimeException("Neither method attribute nor data attribute has been set!",
							this);
				}
			}
			this.pageContext.setAttribute(this.id + Constant.ActionInPageContext, dga);
			dga.addVariables(this.context);
		} catch (PrivException e) {
			e.printStackTrace();
			throw e;
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setId(String id) {
		this.id = id;
	}

	public boolean isPage() {
		return this.page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public int getSize() {
		return this.size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isMultiSelect() {
		return this.multiSelect;
	}

	public void setMultiSelect(boolean multiSelect) {
		this.multiSelect = multiSelect;
	}

	public boolean isAutoFill() {
		return this.autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isAutoPageSize() {
		return this.autoPageSize;
	}

	public void setAutoPageSize(boolean autoPageSize) {
		this.autoPageSize = autoPageSize;
	}

	public boolean isScroll() {
		return this.scroll;
	}

	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}

	public int getCacheSize() {
		return this.cacheSize;
	}

	public void setCacheSize(int cacheSize) {
		this.cacheSize = cacheSize;
	}

	public boolean isLazy() {
		return this.lazy;
	}

	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("autoFill", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("autoPageSize", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("cacheSize", DataTypes.INTEGER.code()));
		list.add(new TagAttr("lazy", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("method"));
		list.add(new TagAttr("data"));
		list.add(new TagAttr("multiSelect", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("page", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("scroll", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("size", DataTypes.INTEGER.code()));
		return list;
	}

	public String getExtendItemName() {
		return "@{Framework.UIControl.DataGridTagName}";
	}

	public String getDescription() {
		return "";
	}

	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	public boolean isKeepTagSource() {
		return true;// 本标签要求编译后依然持有源代码
	}

	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		String fileName = te.getFileName();
		if ((fileName != null) && (fileName.startsWith(Config.getContextRealPath()))) {
			fileName = fileName.substring(Config.getContextRealPath().length());
		}
		this.bodyUID = (fileName + "#" + StringUtil.md5Hex(getTagSource()));
		DataGridAction dga = prepareAction();
		dga.setParams(new Mapx<>());
		DataGridBody body = DataGridBodyManager.get(dga, this.bodyUID, getTagSource());
		if (!tc.isHasBody()) {
			tc.setHasBody(true);
		}
		tc.setCommands(body.getExecutor().getCommands());
	}

	public Object getData() {
		return this.data;
	}

	public void setData(Object data) {
		this.data = data;
	}
}
