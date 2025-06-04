//package org.ark.framework.jaf.controls;
//
//import java.io.OutputStream;
//import java.util.Arrays;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.ark.framework.Constant;
//import org.ark.framework.collection.DataTableUtil;
//import org.ark.framework.collection.IPageData;
//import org.ark.framework.i18n.LangMapping;
//import org.ark.framework.jaf.IPageEnableAction;
//import org.ark.framework.jaf.controls.datagrid.DataGrid;
//import org.ark.framework.jaf.controls.datagrid.DataGridDataBinder;
//import org.ark.framework.jaf.controls.datagrid.DataGridParser;
//import org.ark.framework.jaf.html.HtmlScript;
//import org.ark.framework.jaf.html.HtmlTD;
//import org.ark.framework.jaf.html.HtmlTR;
//import org.ark.framework.jaf.html.HtmlTable;
//import org.ark.framework.orm.SchemaSet;
//import org.ark.framework.orm.query.QueryBuilder;
//import org.ark.framework.orm.sql.DBUtil;
//import org.ark.framework.utility.StringFormat;
//import util.io.arkx.framework.commons.StringUtil;
//
//import com.arkxos.framework.framework.ResponseData;
//import com.arkxos.framework.framework.collection.DataTable;
//import com.arkxos.framework.framework.collection.Mapx;
//import com.arkxos.framework.framework.data.DataCollection;
//
//
///**
// * @class org.ark.framework.jaf.controls.DataGridAction
// * 
//
// */
//public class DataGridAction implements IPageEnableAction {
//	
//	public static final String TreeLevelField = "_TreeLevel";
//	public String ID;
//	public boolean MultiSelect = true;
//
//	public boolean autoFill = true;
//
//	public boolean autoPageSize = false;
//
//	public boolean Scroll = true;
//
//	public boolean Lazy = false;
//	public int cacheSize;
//	public HtmlTable Template;
//	public int PageSize;
//	public int PageIndex;
//	public int Total;
//	public boolean PageFlag;
//	
//	public boolean WebMode = true;
//
//	public String Method;
//	public String TagBody;
//	public DataTable DataSource;
//	public HtmlTable Table;
//	public Mapx<String, Object> Params = new Mapx<String, Object>();
//	public ResponseData Response;
//	
//	public boolean TotalFlag = false;
//
//	public static Pattern sortPattern = Pattern.compile("[\\w\\,\\s]*", Pattern.CASE_INSENSITIVE|Pattern.DOTALL);
//
//	public DataGrid dataGrid = null;
//	
//	public void parse() throws Exception {
//		dataGrid = new DataGridParser().parse(this);
//	}
//
//	private void bindData() throws Exception {
//		
//		new DataGridDataBinder().bindData(this);
//	}
//
//	public String getHtml() {
//		HtmlScript script = new HtmlScript();
//		script.setAttribute("ztype", "DataGrid");
//		script.setInnerHTML(getScript());
//		this.Table.addChild(script);
//		String html = null;
//		if (this.Scroll) {
//			html = scrollWrap();
//		} else {
//			if ((!dataGrid.isTreeFlag()) && (this.PageFlag) && (dataGrid.getPageBar() != null)) {
//				dealPageBar();
//				this.Table.addTR(dataGrid.getPageBar());
//			}
//			html = this.Table.getOuterHtml();
//		}
//		return html;
//	}
//
//	public String getBodyHtml() {
//		HtmlScript script = new HtmlScript();
//		script.setAttribute("ztype", "DataGrid");
//		script.setInnerHTML(getScript());
//		this.Table.addChild(script);
//		String html = null;
//
//		html = this.Table.getOuterHtml();
//
//		return html;
//	}
//
//	public String scrollWrap() {
//		for (int i = 0; i < this.Table.getTR(0).getChildren().size(); i++) {
//			this.Table.getTR(0).getTD(i).setHead(true);
//		}
//		this.Table.removeAttribute("class");
//		this.Table.getTR(0).removeAttribute("class");
//
//		StringBuilder sb = new StringBuilder();
//		String fw = this.Table.getAttribute("fixedWidth");
//		String fh = this.Table.getAttribute("fixedHeight");
//
//		sb.append("<div id='").append(this.ID).append("_Wrap' class='z-datagrid dg_scrollable dg_nobr' ztype='_DataGridWrapper'");
//		if (StringUtil.isNotEmpty(fw)) {
//			sb.append(" style='width:").append(fw).append(";'");
//		}
//		sb.append(">");
//		sb.append("<div id='").append(this.ID).append("_Wrap_head' class='dg_head'>");
//		HtmlTable tmpTable = (HtmlTable) this.Table.clone();
//		for (int i = tmpTable.Children.size() - 1; i > 0; i--) {
//			tmpTable.removeTR(i);
//		}
//		tmpTable.setID(null);
//		tmpTable.setClassName("dg_headTable");
//		tmpTable.getTR(0).setClassName("dg_headTr");
//		for (int i = 0; i < tmpTable.getTR(0).getChildren().size(); i++) {
//			HtmlTD td = tmpTable.getTR(0).getTD(i);
//			td.InnerHTML = ("<div id='dataTable0_th" + i + "' class='dg_th'>" + td.InnerHTML + "</div>");
//		}
//		sb.append(tmpTable.getOuterHtml());
//		sb.append("</div>");
//
//		sb.append("<div id='").append(this.ID).append("_Wrap_body' class='dg_body' style='");
//		if ((StringUtil.isNotEmpty(fw)) && (fw.indexOf("%") < 0)) {
//			sb.append("width:").append(fw).append(";");
//		}
//		if (StringUtil.isNotEmpty(fh)) {
//			sb.append("height:").append(fh).append(";");
//		}
//		sb.append("'>");
//		sb.append(this.Table.getOuterHtml());
//		sb.append("</div>");
//
//		if ((!dataGrid.isTreeFlag()) && (this.PageFlag) && (dataGrid.getPageBar() != null)) {
//			dealPageBar();
//
//			HtmlTable footTable = new HtmlTable();
//			footTable.setAttribute("width", "100%");
//			footTable.addTR(dataGrid.getPageBar());
//
//			sb.append("<div class='dg_foot'>");
//			sb.append(footTable.getOuterHtml());
//			sb.append("</div>");
//		}
//		sb.append("</div>");
//		return sb.toString();
//	}
//
//	private void dealPageBar() {
//		try {
//			for (int i = dataGrid.getPageBar().Children.size() - 1; i > 0; i--) {
//				dataGrid.getPageBar().removeTD(i);
//			}
//			String html = getPageBarHtmlByType();
//			dataGrid.getPageBar().getTD(0).setInnerHTML(html);
//			dataGrid.getPageBar().getTD(0).setColSpan(dataGrid.getHeader().getChildSize() + "");
//			dataGrid.getPageBar().getTD(0).setID("_PageBar_" + this.ID);
//			dataGrid.getPageBar().getTD(0).setAttribute("pagebartype", dataGrid.getPageBar().getAttribute("pagebartype"));
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		dataGrid.getPageBar().setAttribute("dragOver", "Ark.DataGrid.dragOver");
//		dataGrid.getPageBar().setAttribute("dragOut", "Ark.DataGrid.dragOut");
//		dataGrid.getPageBar().setAttribute("dragEnd", "Ark.DataGrid.dragEnd");
//	}
//
//	public String getScript() {
//		StringBuilder sb = new StringBuilder();
//		sb.append("\nArk.Page.onLoad(DataGrid_").append(this.ID).append("_Init,9);");
//		sb.append("\nfunction DataGrid_").append(this.ID).append("_Init(){");
//		if (this.DataSource != null) {
//			sb.append(DataCollection.dataTableToJS(this.DataSource));
//			sb.append("\nArk.getDom('").append(this.ID).append("').DataSource = new Ark.DataTable();");
//			sb.append("\nArk.getDom('").append(this.ID).append("').DataSource.init(_Ark_Cols,_Ark_Values);");
//			sb.append("\nwindow.attachEvent('onunload', function(){Ark.DataGrid.destroy('").append(this.ID).append("');});");
//		}
//		sb.append("\nvar _Ark_Arr = [];");
//		HtmlTR tr = new HtmlTR(this.Table);
//		try {
//			tr.parseHtml(dataGrid.getTemplateHtml());
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		for (int i = 0; i < tr.getChildren().size(); i++) {
//			sb.append("\n_Ark_Arr.push(\"").append(StringUtil.javaEncode(tr.getTD(i).getInnerHTML())).append("\");");
//		}
//		sb.append("\nArk.getDom('").append(this.ID).append("').TemplateArray = _Ark_Arr;");
//		if (dataGrid.getEditTemplate() != null) {
//			sb.append("\n_Ark_Arr = [];\n");
//			for (int i = 0; i < dataGrid.getEditTemplate().getChildren().size(); i++) {
//				sb.append("\n_Ark_Arr.push(\"").append(StringUtil.javaEncode(dataGrid.getEditTemplate().getTD(i).getInnerHTML())).append("\");");
//			}
//			sb.append("\nArk.getDom('").append(this.ID).append("').EditArray = _Ark_Arr;");
//		}
//
//		sb.append("\nArk.getDom('").append(this.ID).append("').TagBody = \"").append(StringUtil.htmlEncode(getTagBody().replaceAll("\\s+", " "))).append("\";");
//		for (String key : this.Params.keySet()) {
//			Object v = this.Params.get(key);
//			if ((!key.equals("_ARK_TAGBODY")) && (v != null)) {
//				if (((v instanceof DataTable)) || ((v instanceof SchemaSet))) {
//					DataTable dt = null;
//					if ((v instanceof SchemaSet))
//						dt = ((SchemaSet) v).toDataTable();
//					else {
//						dt = (DataTable) v;
//					}
//					sb.append(DataCollection.dataTableToJS(dt));
//					sb.append("\nvar _TmpDt = new DataTable();");
//					sb.append("\n_TmpDt.init(_Ark_Cols,_Ark_Values);");
//					sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append(key).append("',_TmpDt);");
//				} else {
//					sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append(key).append("',\"").append(StringUtil.javaEncode(v.toString())).append("\");");
//				}
//			}
//		}
//		if (this.PageFlag) {
//			sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_PAGEINDEX").append("',").append(this.PageIndex).append(");");
//			sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_PAGETOTAL").append("'," + this.Total).append(");");
//			sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_SIZE").append("',").append(this.PageSize).append(");");
//		}
//		if (dataGrid.isSortFlag()) {
//			sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_SORTSTRING").append("','").append(dataGrid.getSortString()).append("');");
//		}
//		sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_MULTISELECT").append("','" + this.MultiSelect).append("');");
//		sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_AUTOFILL").append("','").append(this.autoFill).append("');");
//		sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_SCROLL").append("','").append(this.Scroll).append("');");
//		sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_LAZY").append("','").append(this.Lazy).append("');");
//		if (this.cacheSize > 0) {
//			sb.append("\nArk.DataGrid.setParam('").append(this.ID).append("','").append("_ARK_CACHESIZE").append("','").append(this.cacheSize).append("');");
//		}
//
//		sb.append("\nArk.DataGrid.init('").append(this.ID).append("');");
//		sb.append("\n}");
//
//		String content = sb.toString();
//		Matcher matcher = Constant.PatternField.matcher(content);
//		sb = new StringBuilder();
//		int lastEndIndex = 0;
//		while (matcher.find(lastEndIndex)) {
//			sb.append(content.substring(lastEndIndex, matcher.start()));
//			sb.append("$\\{");
//			sb.append(matcher.group(1));
//			sb.append("}");
//			lastEndIndex = matcher.end();
//		}
//		sb.append(content.substring(lastEndIndex));
//
//		content = sb.toString();
//		matcher = Constant.PatternSpeicalField.matcher(content);
//		sb = new StringBuilder();
//		lastEndIndex = 0;
//		while (matcher.find(lastEndIndex)) {
//			sb.append(content.substring(lastEndIndex, matcher.start()));
//			sb.append("${#");
//			sb.append(matcher.group(1));
//			sb.append("}");
//			lastEndIndex = matcher.end();
//		}
//		sb.append(content.substring(lastEndIndex));
//		return sb.toString();
//	}
//
//	public HtmlTable getTemplate() {
//		return this.Template;
//	}
//
//	public void setTemplate(HtmlTable table) {
//		this.Template = table;
//	}
//
//	public DataTable getDataSource() {
//		return this.DataSource;
//	}
//
//	@Deprecated
//	public void bindData(QueryBuilder qb) {
//		bindData(qb, this.PageFlag);
//	}
//
//	@Deprecated
//	public void bindData(QueryBuilder qb, boolean pageFlag) {
//		if (pageFlag) {
//			if (!this.TotalFlag) {
//				setTotal(DBUtil.getCount(qb));
//			}
//			bindData(qb.executePagedDataTable(this.PageSize, this.PageIndex));
//		} else {
//			bindData(qb.executeDataTable());
//		}
//	}
//
//	public void bindData(IPageData pageData) {
//		if (PageFlag) {
//			if (!this.TotalFlag) {
//				setTotal(pageData.getTotal());
//			}
//			bindData((DataTable) pageData.getData());
//		} else {
//			bindData((DataTable) pageData.getData());
//		}
//	}
//
//	public void bindData(DataTable dt) {
//		if ("1".equals(this.Params.get("_ExcelFlag"))) {
//			String[] columnNames = (String[]) this.Params.get("_ColumnNames");
//			String[] widths = (String[]) this.Params.get("_Widths");
//			String[] columnIndexes = (String[]) this.Params.get("_ColumnIndexes");
//			int[] indexes = new int[columnIndexes.length];
//			int i = 0;
//			for (String str : columnIndexes) {
//				indexes[(i++)] = Integer.parseInt(str);
//			}
//			Arrays.sort(indexes);
//			for (i = indexes.length - 1; i >= 0; i++) {
//				dt.deleteColumn(indexes[i]);
//			}
//			DataTableUtil.dataTableToExcel(dt, (OutputStream) this.Params.get("_OutputStream"), columnNames, widths);
//		} else {
//			this.DataSource = dt;
//			try {
//				bindData();
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public void bindData(SchemaSet<?> set) {
//		bindData(set.toDataTable());
//	}
//
//	public String getPageBarHtmlByType() {
//		if ("1".equals(dataGrid.getPageBar().getAttribute("pagebartype")))
//			return getPageBarHtml1(this.ID, this.Params, this.Total, 
//					this.PageIndex, this.PageSize, dataGrid.isSimplePageBar());
//		if ("2".equals(dataGrid.getPageBar().getAttribute("pagebartype"))) {
//			return getPageBarHtml2(this.ID, this.Params, 
//					this.Total, this.PageIndex, this.PageSize, dataGrid.isSimplePageBar());
//		}
//		return getPageBarHtml(this.ID, this.Params, this.Total, 
//				this.PageIndex, this.PageSize, dataGrid.isSimplePageBar());
//	}
//
//	public String getPageBarHtml(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize) {
//		return getPageBarHtml(id, params, total, pageIndex, pageSize, false);
//	}
//
//	public String getPageBarHtml(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize, boolean simpleFlag) {
//		StringBuilder sb = new StringBuilder();
//		int totalPages = new Double(Math.ceil(total * 1.0D / pageSize)).intValue();
//
//		params.put("_ARK_PAGETOTAL", total);
//		params.remove("_ARK_PAGEINDEX");
//
//		String first = LangMapping.get("Framework.DataGrid.FirstPage");
//		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
//		String next = LangMapping.get("Framework.DataGrid.NextPage");
//		String last = LangMapping.get("Framework.DataGrid.LastPage");
//		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
//		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
//		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
//		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
//		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
//
//		sb.append("<div class='tfoot-fr' style='float:right;'><div class='pagebar'>&nbsp;");
//
//		sb.append("<span class='first'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.firstPage('").append(id).append("');\">").append(first).append("</a>").append("</span>");
//		sb.append("<span class='previous'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.previousPage('").append(id).append("');\">").append(prev).append("</a>").append("</span>");
//
//		sb.append("<span class='next'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.nextPage('").append(id).append("');\">").append(next).append("</a>").append("</span>");
//		sb.append("<span class='last'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.lastPage('").append(id).append("');\">").append(last).append("</a>").append("</span>");
//
//		if (!simpleFlag) {
//			sb.append("&nbsp;&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(id).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1)
//					.append("\"");
//			sb.append(
//					" onKeyUp=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
//					.append(id).append("').onclick();\">");
//			sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
//			sb.append(gotoEnd).append("&nbsp;");
//			sb.append("<input type='button' id='_PageBar_JumpBtn_").append(id).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(id)
//					.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataGrid.getParam('").append(id).append("', Constant.PageTotal))/Number(Ark.DataGrid.getParam('").append(id)
//					.append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(id)
//					.append("').focus();}else{var pageIndex = ($V('_PageBar_Index_").append(id).append("')-1)>0?$V('_PageBar_Index_").append(id).append("')-1:0;DataGrid.setParam('").append(id)
//					.append("','").append("_ARK_PAGEINDEX").append("',pageIndex);DataGrid.loadData('").append(id).append("');}\" value='' title='").append(gotoButton).append("'>");
//		}
//		sb.append("</div></div>");
//		sb.append("<div class='tfoot-fl' style='float:left;'>");
//		String node_total = "<span class='js_total'>" + String.valueOf(total) + "</span>";
//		String node_pageSize = "<span class='js_pageSize'>" + String.valueOf(pageSize) + "</span>";
//		String node_pageIndex = "<span class='js_pageIndex'>" + String.valueOf(totalPages == 0 ? 0 : pageIndex + 1) + "</span>";
//		String node_totalPages = "<span class='js_totalPages'>" + String.valueOf(totalPages) + "</span>";
//
//		sb.append(new StringFormat(pagebar, new Object[] { node_total, node_pageSize, node_pageIndex, node_totalPages }));
//
//		return sb.toString();
//	}
//
//	public static String getPageBarHtml1(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize, boolean simpleFlag) {
//		StringBuilder sb = new StringBuilder();
//		int totalPages = new Double(Math.ceil(total * 1.0D / pageSize)).intValue();
//
//		params.put("_ARK_PAGETOTAL", total);
//		params.remove("_ARK_PAGEINDEX");
//
//		String first = LangMapping.get("Framework.DataGrid.FirstPage");
//		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
//		String next = LangMapping.get("Framework.DataGrid.NextPage");
//		String last = LangMapping.get("Framework.DataGrid.LastPage");
//		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
//		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
//		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
//		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
//		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
//		String pagebar1 = LangMapping.get("Framework.DataGrid.PageBar1");
//
//		sb.append("<div class='tfoot-fr' style='float:right;'><div class='pagebar'>&nbsp;");
//
//		sb.append("<span class='first'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.firstPage('").append(id).append("');\">").append(first).append("</a>").append("</span>");
//		sb.append("<span class='previous'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.previousPage('").append(id).append("');\">").append(prev).append("</a>").append("</span>");
//
//		sb.append("<span class='next'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.nextPage('").append(id).append("');\">").append(next).append("</a>").append("</span>");
//		sb.append("<span class='last'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.lastPage('").append(id).append("');\">").append(last).append("</a>").append("</span>");
//
//		if (!simpleFlag) {
//			sb.append("&nbsp;&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(id).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1)
//					.append("\"");
//			sb.append(
//					"onKeyUp=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
//					.append(id).append("').onclick();\">");
//			sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
//			sb.append(gotoEnd).append("&nbsp;");
//			sb.append("<input type='button' id='_PageBar_JumpBtn_").append(id).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(id)
//					.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataGrid.getParam('").append(id).append("', Constant.PageTotal))/Number(Ark.DataGrid.getParam('").append(id)
//					.append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(id)
//					.append("').focus();}else{var pageIndex = ($V('_PageBar_Index_").append(id).append("')-1)>0?$V('_PageBar_Index_").append(id).append("')-1:0;DataGrid.setParam('").append(id)
//					.append("','").append("_ARK_PAGEINDEX").append("',pageIndex);DataGrid.loadData('").append(id).append("');}\" value='' title='").append(gotoButton).append("'>");
//		}
//		sb.append("</div></div>");
//		sb.append("<div style=\"float:left;\" class=\"tfoot-fl\" title=\"");
//		sb.append(new StringFormat(pagebar, new Object[] { Integer.valueOf(total), Integer.valueOf(pageSize), Integer.valueOf(totalPages == 0 ? 0 : pageIndex + 1), Integer.valueOf(totalPages) }));
//
//		sb.append("\">");
//		String node_total = "<span class='js_total'>" + String.valueOf(total) + "</span>";
//		String node_pageSize = "<span class='js_pageSize'>" + String.valueOf(pageSize) + "</span>";
//		String node_pageIndex = "<span class='js_pageIndex'>" + String.valueOf(totalPages == 0 ? 0 : pageIndex + 1) + "</span>";
//		String node_totalPages = "<span class='js_totalPages'>" + String.valueOf(totalPages) + "</span>";
//
//		sb.append(new StringFormat(pagebar1, new Object[] { node_total, node_pageSize, node_pageIndex, node_totalPages }));
//
//		return sb.toString();
//	}
//
//	public static String getPageBarHtml2(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize, boolean simpleFlag) {
//		StringBuilder sb = new StringBuilder();
//		int totalPages = new Double(Math.ceil(total * 1.0D / pageSize)).intValue();
//
//		params.put("_ARK_PAGETOTAL", total);
//		params.remove("_ARK_PAGEINDEX");
//
//		String first = LangMapping.get("Framework.DataGrid.FirstPage");
//		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
//		String next = LangMapping.get("Framework.DataGrid.NextPage");
//		String last = LangMapping.get("Framework.DataGrid.LastPage");
//		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
//		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
//		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
//		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
//
//		sb.append("<div class='tfoot-fr' style='float:right;'><div class='pagebar'>&nbsp;");
//
//		sb.append("<span class='first'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.firstPage('").append(id).append("');\">").append(first).append("</a>").append("</span>");
//		sb.append("<span class='previous'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.previousPage('").append(id).append("');\">").append(prev).append("</a>").append("</span>");
//
//		sb.append("<span class='next'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.nextPage('").append(id).append("');\">").append(next).append("</a>").append("</span>");
//		sb.append("<span class='last'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.lastPage('").append(id).append("');\">").append(last).append("</a>").append("</span>");
//
//		if (!simpleFlag) {
//			sb.append("&nbsp;&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(id).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1)
//					.append("\"");
//			sb.append(
//					"onKeyUp=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
//					.append(id).append("').onclick();\">");
//			sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
//			sb.append(gotoEnd).append("&nbsp;");
//			sb.append("<input type='button' id='_PageBar_JumpBtn_").append(id).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(id)
//					.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataGrid.getParam('").append(id).append("', Constant.PageTotal))/Number(Ark.DataGrid.getParam('").append(id)
//					.append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(id)
//					.append("').focus();}else{var pageIndex = ($V('_PageBar_Index_").append(id).append("')-1)>0?$V('_PageBar_Index_").append(id).append("')-1:0;DataGrid.setParam('").append(id)
//					.append("','").append("_ARK_PAGEINDEX").append("',pageIndex);DataGrid.loadData('").append(id).append("');}\" value='' title='").append(gotoButton).append("'>");
//		}
//		sb.append("</div></div>");
//		return sb.toString();
//	}
//
//	public int getPageIndex() {
//		return this.PageIndex;
//	}
//
//	public void setPageIndex(int pageIndex) {
//		this.PageIndex = pageIndex;
//	}
//
//	public int getPageSize() {
//		return this.PageSize;
//	}
//
//	public void setPageSize(int pageSize) {
//		this.PageSize = pageSize;
//	}
//
//	public String getParam(String key) {
//		return this.Params.getString(key);
//	}
//
//	public Mapx<String, Object> getParams() {
//		return this.Params;
//	}
//
//	public void setParams(Mapx<String, Object> params) {
//		this.Params = params;
//	}
//
//	public boolean isPageFlag() {
//		return this.PageFlag;
//	}
//
//	public void setPageFlag(boolean pageFlag) {
//		this.PageFlag = pageFlag;
//	}
//
//	public HtmlTable getTable() {
//		return this.Table;
//	}
//
//	public String getID() {
//		return this.ID;
//	}
//
//	public void setID(String id) {
//		this.ID = id;
//	}
//
//	public String getMethod() {
//		return this.Method;
//	}
//
//	public void setMethod(String method) {
//		this.Method = method;
//	}
//
//	public int getTotal() {
//		return this.Total;
//	}
//
//	public void setTotal(int total) {
//		this.Total = total;
//		if (this.PageIndex > Math.ceil(this.Total * 1.0D / this.PageSize)) {
//			this.PageIndex = new Double(Math.floor(this.Total * 1.0D / this.PageSize)).intValue();
//		}
//		this.TotalFlag = true;
//	}
//
//	public void setTotal(QueryBuilder qb) {
//		if (this.PageIndex == 0)
//			setTotal(DBUtil.getCount(qb));
//	}
//
//	public boolean isWebMode() {
//		return this.WebMode;
//	}
//
//	public void setWebMode(boolean webMode) {
//		this.WebMode = webMode;
//	}
//
//	public String getSortString() {
//		if (dataGrid.getSortString().length() == 0) {
//			return "";
//		}
//		String str = dataGrid.getSortString().toString();
//		if (sortPattern.matcher(str).matches()) {
//			return " order by " + dataGrid.getSortString().toString();
//		}
//		return "";
//	}
//
//	public String getTagBody() {
//		return this.TagBody;
//	}
//
//	public void setTagBody(String tagBody) {
//		this.TagBody = tagBody;
//	}
//
//	public boolean isMultiSelect() {
//		return this.MultiSelect;
//	}
//
//	public void setMultiSelect(boolean multiSelect) {
//		this.MultiSelect = multiSelect;
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
//		return this.Scroll;
//	}
//
//	public void setScroll(boolean scroll) {
//		this.Scroll = scroll;
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
//		return this.Lazy;
//	}
//
//	public void setLazy(boolean lazy) {
//		this.Lazy = lazy;
//	}
//
//	public void setPageEnable(boolean pageEnable) {
//		this.PageFlag = pageEnable;
//	}
//
//	public boolean isPageEnable() {
//		return this.PageFlag;
//	}
//}