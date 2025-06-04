package io.arkx.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.Config;
import io.arkx.framework.Constant;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.UIException;
import io.arkx.framework.cosyui.control.datalist.DataListBody;
import io.arkx.framework.cosyui.control.datalist.DataListBodyManager;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.tag.RestUtil;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.TemplateExecutor;
import io.arkx.framework.cosyui.template.command.TagCommand;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.cosyui.web.RequestData;
import io.arkx.framework.security.PrivCheck;

/**
 * DataList标签
 * 
 */
public class DataListTag extends ArkTag {
	private String method;
	private String rest;
	private String id;

	private int size;

	private boolean page = true;

	private boolean autoFill = true;

	private boolean autoPageSize = false;

	private String dragClass;

	private String listNodes;

	private String sortEnd;

	private String bodyUID;

	@Override
	public String getTagName() {
		return "datalist";
	}

	DataListAction prepareAction() {
		DataListAction dla = new DataListAction();
		dla.setPageEnabled(page);
		dla.setAutoFill(autoFill);
		dla.setAutoPageSize(autoPageSize);
		dla.setMethod(method);
		dla.setRest(rest);
		dla.setID(id);
		dla.setDragClass(dragClass);
		dla.setListNodes(listNodes);
		dla.setSortEnd(sortEnd);
		dla.setPageSize(size);

		if (page) {
			dla.setPageIndex(0);
			if (dla.getParams() != null && StringUtil.isNotEmpty(dla.getParam(Constant.DataGridPageIndex))) {
				dla.setPageIndex(Integer.parseInt(dla.getParam(Constant.DataGridPageIndex)));
			}
			if (dla.getPageIndex() < 0) {
				dla.setPageIndex(0);
			}
			dla.setPageSize(size);
		}
		dla.setTagBody(DataListBodyManager.get(dla, bodyUID, getTagSource()));
		return dla;
	}

	@Override
	public int doStartTag() throws TemplateRuntimeException {
		try {
			if (StringUtil.isEmpty(method) && StringUtil.isEmpty(rest)) {
				throw new UIException("DataList's method or rest is not set!");
			}

			DataListAction dla = prepareAction();
			
			if(!StringUtil.isEmpty(method)) {
				IMethodLocator m = MethodLocatorUtil.find(method);
				PrivCheck.check(m);
	
				if (WebCurrent.getRequest() != null) {
					dla.setParams(WebCurrent.getRequest());
				}
				m.execute(dla);
			} else {
				if(this.page) {
					RequestData requestData = WebCurrent.getRequest();
					requestData.put("pageIndex", dla.getPageIndex());
					requestData.put("pageSize", dla.getPageSize());
					JsonResult jsonResult = RestUtil.post(rest, requestData, PagedData.class);
					if(!jsonResult.isSuccess()) {
						throw new TemplateRuntimeException(jsonResult.getMessage());
					}
					PagedData pagedData = (PagedData)jsonResult.getData();
					dla.setTotal(pagedData.getTotal());
					dla.bindData(pagedData.getDataTable());
				} else {
					RequestData requestData = WebCurrent.getRequest();
					JsonResult jsonResult = RestUtil.post(rest, requestData, DataTable.class);
					if(!jsonResult.isSuccess()) {
						throw new TemplateRuntimeException(jsonResult.getMessage());
					}
					DataTable dataTable = (DataTable)jsonResult.getData();
					dla.bindData(dataTable);
				}
			}
			
			pageContext.setAttribute(id + Constant.ActionInPageContext, dla);
			dla.addVariables(context);
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return EVAL_BODY_INCLUDE;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean isPage() {
		return page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public boolean isAutoFill() {
		return autoFill;
	}

	public void setAutoFill(boolean autoFill) {
		this.autoFill = autoFill;
	}

	public boolean isAutoPageSize() {
		return autoPageSize;
	}

	public void setAutoPageSize(boolean autoPageSize) {
		this.autoPageSize = autoPageSize;
	}

	public String getDragClass() {
		return dragClass;
	}

	public void setDragClass(String dragClass) {
		this.dragClass = dragClass;
	}

	public String getListNodes() {
		return listNodes;
	}

	public void setListNodes(String listNodes) {
		this.listNodes = listNodes;
	}

	public String getSortEnd() {
		return sortEnd;
	}

	public void setSortEnd(String sortEnd) {
		this.sortEnd = sortEnd;
	}

	@Override
	public List<TagAttr> getTagAttrs() {
		List<TagAttr> list = new ArrayList<TagAttr>();
		list.add(new TagAttr("id", true));
		list.add(new TagAttr("autoFill", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("autoPageSize", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("page", TagAttr.BOOL_OPTIONS));
		list.add(new TagAttr("size", DataTypes.INTEGER.code()));
		list.add(new TagAttr("sortEnd"));
		list.add(new TagAttr("method"));
		list.add(new TagAttr("listNodes"));
		list.add(new TagAttr("dragClass"));
		return list;
	}

	@Override
	public String getExtendItemName() {
		return "@{Framework.UIControl.DataListTagName}";
	}

	@Override
	public String getDescription() {
		return "";
	}

	@Override
	public String getPluginID() {
		return FrameworkPlugin.ID;
	}

	@Override
	public boolean isKeepTagSource() {
		return true;// 本标签要求编译后依然持有源代码
	}

	@Override
	public void afterCompile(TagCommand tc, TemplateExecutor te) {
		String fileName = te.getFileName();
		if (fileName != null && fileName.startsWith(Config.getContextRealPath())) {
			fileName = fileName.substring(Config.getContextRealPath().length());
		}
		bodyUID = fileName + "#" + StringUtil.md5Hex(getTagSource());
		DataListAction dla = prepareAction();
		dla.setParams(new Mapx<String, Object>());
		DataListBody body = DataListBodyManager.get(dla, bodyUID, getTagSource());
		if (!tc.isHasBody()) {
			tc.setHasBody(true);
		}
		tc.setCommands(body.getExecutor().getCommands());
	}

	public String getRest() {
		return rest;
	}

	public void setRest(String rest) {
		this.rest = rest;
	}

}
