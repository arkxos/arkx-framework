//package org.ark.framework.jaf.tag;
//
//import java.lang.reflect.Method;
//
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import jakarta.servlet.jsp.JspException;
//import jakarta.servlet.jsp.PageContext;
//import jakarta.servlet.jsp.tagext.BodyContent;
//import jakarta.servlet.jsp.tagext.BodyTagSupport;
//
//import org.ark.framework.Constant;
//import org.ark.framework.jaf.Current;
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.html.HtmlTable;
//import org.ark.framework.security.PrivCheck;
//import util.io.arkx.framework.commons.StringUtil;
//
//import com.arkxos.framework.framework.collection.DataTable;
//
//
///**
// * @class org.ark.framework.jaf.tag.DataGridTag
// * <h2>表格标签</h2>
// * <br/>
// * <img src="images/DataGridTag_1.png"/>
// * <br/>&lt;ark:datagrid page="false" id="dg1" method="Application.bindGrid">
//<br/>&lt;table width="100%" cellpadding="2" cellspacing="0" class="z-datagrid">
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;    &lt;tr ztype="head" class="dataTableHead">
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td width="4%" <b>ztype="RowNo"</b>>序号&lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td width="3%" <b>ztype="selector" field="id"</b>> &lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td width="25%" <b>ztype="tree" level="_Treelevel"</b>>名称&lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td width="20%" <b>sortfield="code" direction=""</b>>代码&lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;    &lt;/tr>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;    &lt;tr ondblclick="Application.edit()">
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td align="center"> &lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td> &lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td>${name}&lt;/td>
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;    &lt;/tr>
//<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;    &lt;tr ztype="pagebar"></b>
//<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;        &lt;td colspan="10">${PageBar}&lt;/td></b>
//<br/><b>&nbsp;&nbsp;&nbsp;&nbsp;　　&lt;/tr></b>
//<br/>&lt;/table>
//<br/>&lt;/ark:datagrid>
//<br/>
//<br/>public void bindGrid(DataGridAction dga) {
//<br/>		
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;		DataTable dataTable = new DataTable("id", "name", "app_id");
//<br/>		
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dataTable.union(getApps());
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dataTable.union(getAppModules());
//<br/>		
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dataTable = DataTableUtil.sortTreeDataTable(dataTable, "id", "app_id");
//<br/>		
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;		dga.bindData(dataTable);
//<br/>}
//<br/>
//<br/><b>重新加载表格</b>
//<br/>function reloadMetaColumn() {
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;	DataGrid.setParam("columnGrid", Constant.PageIndex, 0);
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;	DataGrid.setParam("columnGrid", "modelId", $V("modelId"));
//<br/>&nbsp;&nbsp;&nbsp;&nbsp;	DataGrid.loadData("columnGrid");
//<br/>}
//
//	打开表格的右键菜单，在table上设置 showContextMenu="true"
// * @author Darkness
// * @date 2013-1-31 下午12:40:29 
// * @version V1.0
// */
//public class DataGridTag extends BodyTagSupport {
//
//	private static final long serialVersionUID = 1L;
//	private String method;
//	private String sql;
//	private String id;
//	private boolean page = true;
//	private int size;
//	private boolean multiSelect = true;
//
//	private boolean autoFill = true;
//
//	private boolean autoPageSize = false;
//
//	private boolean scroll = true;
//
//	private boolean lazy = false;
//	private int cacheSize;
//
//	public void setPageContext(PageContext pc) {
//		super.setPageContext(pc);
//		this.method = null;
//		this.sql = null;
//		this.id = null;
//		this.page = true;
//		this.size = 0;
//		this.multiSelect = true;
//		this.autoFill = true;
//		this.autoPageSize = false;
//		this.scroll = true;
//		this.lazy = false;
//		this.cacheSize = 0;
//	}
//
//	public int doAfterBody() throws JspException {
//		BodyContent body = getBodyContent();
//		String content = body.getString().trim();
//		try {
//			if ((StringUtil.isEmpty(this.method)) && (StringUtil.isEmpty(this.sql))) {
//				throw new RuntimeException("DataGrid's action and SQL cann't be empty at the same time");
//			}
//
//			DataGridAction dga = new DataGridAction();
//			dga.setMethod(this.method);
//			dga.setTagBody(content);
//
//			dga.setID(this.id);
//			dga.setPageFlag(this.page);
//			dga.setMultiSelect(this.multiSelect);
//			dga.setAutoFill(this.autoFill);
//			dga.setAutoPageSize(this.autoPageSize);
//			dga.setScroll(this.scroll);
//			dga.setCacheSize(this.cacheSize);
//			dga.setLazy(this.lazy);
//
//			HtmlTable table = new HtmlTable();
//			table.parseHtml(content);
//			dga.setTemplate(table);
//			dga.parse();
//
//			if (this.page) {
//				dga.setPageIndex(0);
//				if (StringUtil.isNotEmpty(dga.getParam(Constant.DataGridPageIndex))) {
//					dga.setPageIndex(Integer.parseInt(dga.getParam(Constant.DataGridPageIndex)));
//				}
//				if (dga.getPageIndex() < 0) {
//					dga.setPageIndex(0);
//				}
//				if (this.autoPageSize && size == 0) {
//					this.size = 30;
//				}
//				dga.setPageSize(this.size);
//			}
//
//			if (this.lazy) {
//				dga.bindData(new DataTable());
//			} else {
//				HttpServletRequest request = (HttpServletRequest) this.pageContext.getRequest();
//				HttpServletResponse response = (HttpServletResponse) this.pageContext.getResponse();
//
//				Method m = Current.prepareMethod(request, response, this.method, new Class[] { DataGridAction.class });
//				if (!PrivCheck.check(m, request, response)) {
//					return 5;
//				}
//
//				dga.setParams(WebCurrent.getRequest());
//				dga.Response = Current.getResponse();
//
//				if (StringUtil.isNotEmpty(this.sql)) {
//					this.method = "org.ark.framework.jaf.controls.DataGridPage.sqlBind";
//					dga.getParams().put("_ARK_DATAGRID_SQL", this.sql);
//				}
//
//				Current.invokeMethod(m, new Object[] { dga });
//			}
//			this.pageContext.setAttribute(this.id + "_ARK_ACTION", dga);
//			getPreviousOut().write(dga.getHtml());
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return 6;
//	}
//
//	public String getMethod() {
//		return this.method;
//	}
//
//	public void setMethod(String method) {
//		this.method = method;
//	}
//
//	public String getId() {
//		return this.id;
//	}
//
//	public void setId(String id) {
//		this.id = id;
//	}
//
//	public String getSql() {
//		return this.sql;
//	}
//
//	public void setSql(String sql) {
//		this.sql = sql;
//	}
//
//	public boolean isPage() {
//		return this.page;
//	}
//
//	public void setPage(boolean page) {
//		this.page = page;
//	}
//
//	public int getSize() {
//		return this.size;
//	}
//
//	public void setSize(int size) {
//		this.size = size;
//	}
//
//	public boolean isMultiSelect() {
//		return this.multiSelect;
//	}
//
//	public void setMultiSelect(boolean multiSelect) {
//		this.multiSelect = multiSelect;
//	}
//
//	public boolean isAutoFill() {
//		return this.autoFill;
//	}
//
//	public void setAutoFill(boolean autoFill) {
//		this.autoFill = autoFill;
//	}
//
//	public boolean isAutoPageSize() {
//		return this.autoPageSize;
//	}
//
//	public void setAutoPageSize(boolean autoPageSize) {
//		this.autoPageSize = autoPageSize;
//	}
//
//	public boolean isScroll() {
//		return this.scroll;
//	}
//
//	public void setScroll(boolean scroll) {
//		this.scroll = scroll;
//	}
//
//	public int getCacheSize() {
//		return this.cacheSize;
//	}
//
//	public void setCacheSize(int cacheSize) {
//		this.cacheSize = cacheSize;
//	}
//
//	public boolean isLazy() {
//		return this.lazy;
//	}
//
//	public void setLazy(boolean lazy) {
//		this.lazy = lazy;
//	}
//}