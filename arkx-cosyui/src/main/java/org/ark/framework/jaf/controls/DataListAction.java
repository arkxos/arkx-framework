package org.ark.framework.jaf.controls;

import java.util.regex.Matcher;

import org.ark.framework.jaf.IPageEnableAction;
import org.ark.framework.jaf.html.HtmlScript;
import org.ark.framework.orm.SchemaSet;
import org.ark.framework.orm.sql.DBUtil;

import io.arkx.framework.Account;
import io.arkx.framework.Constant;
import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.Html2Util;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.i18n.LangUtil;


/**
 * @class org.ark.framework.jaf.controls.DataListAction
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:42:28 
 * @version V1.0
 */
public class DataListAction implements IPageEnableAction {
	private DataTable DataSource;
	private String ID;
	private String TagBody;
	private boolean page;
	protected Mapx<String, Object> Params = new Mapx<String, Object>();
	private String method;
	private int total;
	private int pageIndex;
	private int pageSize;
	private boolean autoFill;
	private boolean autoPageSize;
	private String dragHandle;
	private String listNodes;
	private String sortEnd;
	boolean TotalFlag = false;

	public String getMethod() {
		return this.method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	public Mapx<String, Object> getParams() {
		return this.Params;
	}

	public void setParams(Mapx<String, Object> params) {
		this.Params = params;
	}

	public String getParam(String key) {
		return this.Params.getString(key);
	}

	public void bindData(DataTable dt) {
		this.DataSource = dt;
		try {
			bindData();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void bindData(SchemaSet<?> set) {
		bindData(set.toDataTable());
	}

	private void bindData() throws Exception {
		if (this.DataSource.getDataColumn("_RowNo") == null) {
			this.DataSource.insertColumn(new DataColumn("_RowNo", 8));
		}
		for (int j = 0; j < this.DataSource.getRowCount(); j++) {
			int rowNo = this.pageIndex * this.pageSize + j + 1;
			this.DataSource.set(j, "_RowNo", Integer.valueOf(rowNo));
		}
		LangUtil.decodeDataTable(this.DataSource, Account.getLanguage());
	}

	public void bindData(Query qb) {
		bindData(qb, this.page);
	}

	public void bindData(Query qb, boolean pageFlag) {
		if (pageFlag) {
			if (!this.TotalFlag) {
				setTotal(DBUtil.getCount(qb));
			}
			
			DataTable dataTable = (DataTable)qb.executePagedDataTable(this.pageSize, this.pageIndex).getData();
			bindData(dataTable);
		} else {
			bindData(qb.executeDataTable());
		}
	}

	public String getHtml() {
		StringBuilder sb = new StringBuilder();
		sb.append("<!--_ARK_DATALIST_START_").append(this.ID).append("-->");
		sb.append("<input type=\"hidden\" id=\"").append(this.ID).append("\" method=\"").append(this.method).append("\"");
		if (this.page) {
			sb.append(" page=\"true\"");
		}
		if (this.pageSize > 0) {
			sb.append(" size=\"").append(this.pageSize).append("\"");
		}
		if (this.autoFill) {
			sb.append(" autofill=\"true\"");
		}
		if (this.autoPageSize) {
			sb.append(" autopagesize=\"true\"");
		}
		if (this.dragHandle != null) {
			sb.append(" draghandle=\"").append(this.dragHandle).append("\"");
		}
		if (this.listNodes != null) {
			sb.append(" listnodes=\"").append(this.listNodes).append("\"");
		}
		if (this.sortEnd != null) {
			sb.append(" sortend=\"").append(this.sortEnd).append("\"");
		}
		sb.append("/>");
		sb.append(Html2Util.replaceWithDataTable(this.DataSource, this.TagBody, false));
		HtmlScript script = new HtmlScript();
		script.setAttribute("ztype", "DataList");
		script.setInnerHTML(getScript());
		sb.append(script.getOuterHtml());
		sb.append("<!--_ARK_DATALIST_END_").append(this.ID).append("-->");
		return sb.toString();
	}

	public String getScript() {
		StringBuilder sb = new StringBuilder();

		sb.append("Ark.getDom('").append(this.ID).append("').TagBody = \"").append(StringUtil.htmlEncode(getTagBody().replaceAll("\\s+", " "))).append("\";");
		for (String k : this.Params.keySet()) {
			Object v = this.Params.get(k);
			if ((k.equals("_ARK_TAGBODY")) || (v == null))
				continue;
			sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append(k).append("',\"").append(StringUtil.javaEncode(v.toString())).append("\");");
		}

		if (this.page) {
			PageBarTag tag = new PageBarTag();
			tag.action = this;
		}

		sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_PAGEINDEX").append("',").append(this.pageIndex).append(");");
		sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_PAGETOTAL").append("',").append(this.total).append(");");
		sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_PAGE").append("',").append(this.page).append(");");
		sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_SIZE").append("',").append(this.pageSize).append(");");
		if (StringUtil.isNotEmpty(this.dragHandle)) {
			sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_DRAGHANDLE").append("','").append(this.dragHandle).append("');");
		}
		if (StringUtil.isNotEmpty(this.sortEnd)) {
			sb.append("Ark.DataList.setParam('").append(this.ID).append("','").append("_ARK_SORTEND").append("','").append(this.sortEnd).append("');");
		}
		sb.append("");
		sb.append("Ark.Page.onReady(function(){Ark.DataList.init('").append(this.ID).append("');});");
		String content = sb.toString();
		Matcher matcher = Constant.PatternField.matcher(content);
		sb = new StringBuilder();
		int lastEndIndex = 0;
		while (matcher.find(lastEndIndex)) {
			sb.append(content.substring(lastEndIndex, matcher.start()));
			sb.append("$\\{");
			sb.append(matcher.group(1));
			sb.append("}");
			lastEndIndex = matcher.end();
		}
		sb.append(content.substring(lastEndIndex));

		return sb.toString();
	}

	public String getTagBody() {
		return this.TagBody;
	}

	public void setTagBody(String tagBody) {
		this.TagBody = tagBody;
	}

	public DataTable getDataSource() {
		return this.DataSource;
	}

	public String getID() {
		return this.ID;
	}

	public void setID(String id) {
		this.ID = id;
	}

	public boolean isPage() {
		return this.page;
	}

	public void setPage(boolean page) {
		this.page = page;
	}

	public int getTotal() {
		return this.total;
	}

	public void setTotal(int total) {
		this.total = total;
		if (this.pageIndex > Math.ceil(total * 1.0D / this.pageSize)) {
			this.pageIndex = new Double(Math.floor(total * 1.0D / this.pageSize)).intValue();
		}
		this.TotalFlag = true;
	}

	public void setTotal(Query qb) {
		if (this.pageIndex == 0)
			setTotal(DBUtil.getCount(qb));
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

	public void setPageIndex(int pageIndex) {
		this.pageIndex = pageIndex;
	}

	public int getPageSize() {
		return this.pageSize;
	}

	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
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

	public String getDragHandle() {
		return this.dragHandle;
	}

	public void setDragHandle(String dragHandle) {
		this.dragHandle = dragHandle;
	}

	public String getListNodes() {
		return this.listNodes;
	}

	public void setListNodes(String listNodes) {
		this.listNodes = listNodes;
	}

	public String getSortEnd() {
		return this.sortEnd;
	}

	public void setSortEnd(String sortEnd) {
		this.sortEnd = sortEnd;
	}

	public void setPageEnabled(boolean pageEnable) {
		this.page = pageEnable;
	}

	public boolean isPageEnabled() {
		return this.page;
	}
}