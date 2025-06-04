package io.arkx.framework.cosyui.control.grid;

import java.util.List;

import io.arkx.framework.Constant;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.lang.FastStringBuilder;
import io.arkx.framework.commons.util.StringFormat;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;
import io.arkx.framework.cosyui.html.HtmlTR;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;
import io.arkx.framework.i18n.LangMapping;

/**
 * 行号
 * 
 */
public class GridPageBar extends AbstractGridFeature {
	private static final String Var = "DataGrid_PageBarHTML";
	private static final String PlaceHolder = "${(" + Var + ")}";

	@Override
	public void rewriteTR(DataGridAction dga, HtmlTR tr) {
		String ztype = tr.attributeValue("ztype");
		if (!DataGridBody.TR_SIMPLEPAGEBAR.equalsIgnoreCase(ztype) && !DataGridBody.TR_PAGEBAR.equalsIgnoreCase(ztype)) {
			return;
		}
		// 重置colspan
		List<HtmlTD> tds = tr.getTDList();
		tds.get(0).setColSpan("" + dga.getTagBody().getHeadTR().getTDList().size());
		tds.get(0).setInnerHTML(PlaceHolder);
		for (int i = tds.size() - 1; i > 0; i--) {
			tr.removeTD(i);
		}
		tds.get(0).setID("_PageBar_" + dga.getID());
		tds.get(0).addAttribute("pagebartype", tr.getAttribute("pagebartype"));
	}

	@Override
	public void beforeDataBind(DataGridAction dga, AbstractExecuteContext context, DataTable dataSource) {
		HtmlTR tr = dga.getTagBody().getPageBarTR();
		if (tr == null) {
			return;
		}
		String type = tr.getAttribute("pagebartype");
		if (StringUtil.isEmpty(type)) {
			type = "0";
		}
		boolean simple = DataGridBody.TR_SIMPLEPAGEBAR.equalsIgnoreCase(tr.getAttribute("ztype"));
		String pagebarHtml = getPageBarHtml(dga.getID(), dga.getParams(), dga.getTotal(), dga.getPageIndex(), dga.getPageSize(), simple,
				Integer.parseInt(type));
		context.addRootVariable(Var, pagebarHtml);
	}

	public String getPageBarHtml(String id, Mapx<String, Object> params, int total, int pageIndex, int pageSize, boolean simpleFlag,
			int type) {
		FastStringBuilder sb = new FastStringBuilder();
		int totalPages = new Double(Math.ceil(total * 1.0 / pageSize)).intValue();

		params.put(Constant.DataGridPageTotal, "" + total);
		params.remove(Constant.DataGridPageIndex);

		String first = LangMapping.get("Framework.DataGrid.FirstPage");
		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
		String next = LangMapping.get("Framework.DataGrid.NextPage");
		String last = LangMapping.get("Framework.DataGrid.LastPage");
		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
		String records = LangMapping.get("Framework.DataGrid.Records");

		sb.append("<div class='tfoot-fr' style='float:right;'><div class='pagebar'>&nbsp;");

		sb.append("<span class='first'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.firstPage('").append(id)
				.append("');\">").append(first).append("</a>").append("</span>");
		sb.append("<span class='previous'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.previousPage('").append(id)
				.append("');\">").append(prev).append("</a>").append("</span>");

		sb.append("<span class='next'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.nextPage('").append(id).append("');\">")
				.append(next).append("</a>").append("</span>");
		sb.append("<span class='last'>").append("<a href='javascript:void(0);' onclick=\"DataGrid.lastPage('").append(id).append("');\">")
				.append(last).append("</a>").append("</span>");

		if (!simpleFlag) {
			sb.append("&nbsp;&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(id)
					.append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
			sb.append(
					" onKeyUp=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
					.append(id).append("').onclick();\">");
			sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
			sb.append(gotoEnd).append("&nbsp;");
			sb.append("<input type='button' id='_PageBar_JumpBtn_").append(id)
					.append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(id)
					.append("').value,t=Number(Ark.DataGrid.getParam('").append(id)
					.append("', Constant.PageTotal)),s=Number(Ark.DataGrid.getParam('").append(id)
					.append("', Constant.Size)),l=Math.floor(t/s)+(t%s===0?0:1);if(!/^\\d+$/.test(v)").append("||v<1||v>l){alert('")
					.append(error).append("');document.getElementById('_PageBar_Index_").append(id)
					.append("').focus();}else{var pageIndex = ($V('_PageBar_Index_").append(id).append("')-1)>0?$V('_PageBar_Index_")
					.append(id).append("')-1:0;DataGrid.gotoPage('").append(id).append("',pageIndex);}\" value='' title='")
					.append(gotoButton).append("'>");
		}
		sb.append("</div></div>");
		sb.append("<div style=\"float:left;\" class=\"tfoot-fl\">");
		sb.append("<span class=\"pageInfo_trigger\" style=\"display:none;\">");
		if (type == 2) {
			sb.append("&gl;&gl;");
		} else {
			sb.append(new StringFormat(records, "<span class=\"js_total\">" + total + "</span>"));
		}
		sb.append("</span>");
		sb.append("<span class=\"pageInfo\">");
		sb.append(new StringFormat(pagebar, "<span class=\"js_total\">" + total + "</span>", "<span class=\"inline-block pageSizeGroup\">"
				+ "<ul class=\"sizeList\" style=\"position:absolute;\">" + "<li onclick=\"Ark.DataGrid.changePageSize('" + id
				+ "',10)\">10</li>" + "<li onclick=\"Ark.DataGrid.changePageSize('" + id + "',15)\">15</li>"
				+ "<li onclick=\"Ark.DataGrid.changePageSize('" + id + "',20)\">20</li>"
				+ "<li onclick=\"Ark.DataGrid.changePageSize('" + id + "',30)\">30</li>"
				+ "<li onclick=\"Ark.DataGrid.changePageSize('" + id + "',50)\">50</li>" + "</ul>" + "<span class=\"js_pageSize\">"
				+ pageSize + "</span>" + "</span>", "<span class=\"js_pageIndex\">" + (totalPages == 0 ? 0 : pageIndex + 1) + "</span>",
				"<span class=\"js_totalPages\">" + totalPages + "</span>"));
		sb.append("</span>");
		sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div>");

		return sb.toStringAndClose();
	}

}
