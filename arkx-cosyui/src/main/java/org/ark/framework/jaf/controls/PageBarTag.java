package org.ark.framework.jaf.controls;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.*;
import io.arkx.framework.i18n.LangMapping;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.PageContext;
import jakarta.servlet.jsp.tagext.BodyTagSupport;
import org.ark.framework.jaf.Current;
import org.ark.framework.jaf.IPageEnableAction;
import org.ark.framework.jaf.PlaceHolderContext;
import org.ark.framework.jaf.tag.ListAction;


/**
 * @class org.ark.framework.jaf.controls.PageBarTag
 * 
 * @author Darkness
 * @date 2013-1-31 下午12:43:32 
 * @version V1.0
 */
public class PageBarTag extends BodyTagSupport {
	private static final long serialVersionUID = 1L;
	private String target;
	private int type;
	private int total;
	private int pageIndex;
	private int pageSize;
	private boolean afloat;
	private boolean autoHide;
	public IPageEnableAction action;

	public void setPageContext(PageContext pc) {
		super.setPageContext(pc);
		this.pageIndex = 0;
		this.target = null;
		this.type = 0;
		this.total = 0;
		this.pageSize = 0;
		this.afloat = false;
		this.autoHide = false;
		this.action = null;
	}

	public int doStartTag() throws JspException {
		this.action = ((IPageEnableAction) this.pageContext.getAttribute(this.target + "_ARK_ACTION"));
		this.total = this.action.getTotal();
		this.pageIndex = this.action.getPageIndex();
		this.pageSize = this.action.getPageSize();
		Current.setVariable("Page.Total", Integer.valueOf(this.total));
		Current.setVariable("Page.Index", Integer.valueOf(this.pageIndex));
		Current.setVariable("Page.Size", Integer.valueOf(this.pageSize));
		return 2;
	}

	public int doEndTag() throws JspException {
		try {
			String html = "";
			if ((this.total != 0) || (!this.autoHide)) {
				html = getBodyContent() == null ? null : getBodyContent().getString().trim();
				if (ObjectUtil.empty(html)) {
					html = getPageBarHtml(this.target, this.type, this.total, this.pageSize, this.pageIndex);
					if ((this.action instanceof DataListAction)) {
						html = html + "<script>Ark.DataGrid.setParam('" + this.target + "','PageBarType'," + this.type + ");";
						if (this.afloat) {
							html = html + "new Ark.Afloat(Ark.getDom('_PageBar_" + this.target + "').parentNode);";
						}
						html = html + "</script>";
					}
				} else {
					PlaceHolderContext context = PlaceHolderContext.getInstance(null, this.pageContext);
					html = Html2Util.replacePlaceHolder(html, context, true, false);
				}
			}
			this.pageContext.getOut().print(html);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return 6;
	}

	public String getPageBarHtml(String target, int type, int total, int pageSize, int pageIndex) {
		if (type == 2)
			return getType2(target, total, pageSize, pageIndex);
		if (type == 1) {
			return getType1(target, total, pageSize, pageIndex);
		}
		return getDefault(this, target, total, pageSize, pageIndex);
	}
	
	public static String getDefault(PageBarTag pageBarTag, String target, int total, int pageSize, int pageIndex) {
		String first = LangMapping.get("Framework.DataGrid.FirstPage");
		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
		String next = LangMapping.get("Framework.DataGrid.NextPage");
		String last = LangMapping.get("Framework.DataGrid.LastPage");
		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");

		StringBuilder sb = new StringBuilder();
		int totalPages = Double.valueOf(Math.ceil(total * 1.0D / pageSize)).intValue();
		sb.append("<div id='_PageBar_").append(target).append("' class=\"pagebar_wrap\" pagebartype=0>");
		sb.append("<div style=\"float:right;\" class=\"tfoot-fr\"><div class=\"pagebar\">&nbsp;");
		String queryString = null;
		if ((pageBarTag.action instanceof ListAction)) {
			queryString = ((ListAction) pageBarTag.action).getQueryString();
			if (StringUtil.isEmpty(queryString)) {
				queryString = "?PageIndex=";
			} else {
				Mapx map = StringUtil.splitToMapx(queryString, "&", "=");
				map.remove("PageIndex");
				if (map.size() == 0)
					queryString = "?PageIndex=";
				else {
					queryString = ServletUtil.getQueryStringFromMap(map) + "&PageIndex=";
				}
			}
		}
		if (pageIndex > 0) {
			if ((pageBarTag.action instanceof DataListAction)) {
				sb.append("<span class=\"first\"><a href='javascript:void(0);' onclick=\"Ark.DataList.firstPage('").append(target).append("');\">").append(first).append("</a></span>&nbsp;|&nbsp;");
				sb.append("<span class=\"previous\"><a href='javascript:void(0);' onclick=\"Ark.DataList.previousPage('").append(target).append("');\">").append(prev).append("</a></span>&nbsp;|&nbsp;");
			} else {
				sb.append("<a href='").append(queryString).append("1'>").append(first).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(pageIndex).append("'>").append(prev).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"first\">").append(first).append("</span>&nbsp;|&nbsp;");
			sb.append("<span class=\"previous\">").append(prev).append("</span>&nbsp;|&nbsp;");
		}
		if ((totalPages != 0) && (pageIndex + 1 != totalPages)) {
			if ((pageBarTag.action instanceof DataListAction)) {
				sb.append("<span class=\"next\"><a href='javascript:void(0);' onclick=\"Ark.DataList.nextPage('").append(target).append("');\">").append(next).append("</a></span>&nbsp;|&nbsp;");
				sb.append("<span class=\"last\"><a href='javascript:void(0);' onclick=\"Ark.DataList.lastPage('").append(target).append("');\">").append(last).append("</a></span>");
			} else {
				sb.append("<a href='").append(queryString).append(pageIndex + 2).append("'>").append(next).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(totalPages).append("'>").append(last).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"next\">").append(next).append("</span>&nbsp;|&nbsp;");
			sb.append("<span class=\"last\">").append(last).append("</span>");
		}

		sb.append("&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(target).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
		sb.append(
				"onkeyup=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
				.append(target).append("').onclick();\">");
		sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
		sb.append(gotoEnd).append("&nbsp;");
		sb.append("<input type='button' id='_PageBar_JumpBtn_").append(target).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(target)
				.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataList.getParam('").append(target).append("', Constant.PageTotal))/Number(Ark.DataList.getParam('")
				.append(target).append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(target).append("').focus();}")
				.append("else{var pageIndex = v>0?v-1:0;");
		if ((pageBarTag.action instanceof DataListAction))
			sb.append("Ark.DataList.setParam('").append(target).append("','").append("_ARK_PAGEINDEX").append("',pageIndex);Ark.DataList.loadData('").append(target).append("');");
		else {
			sb.append("window.location='").append(queryString).append("'+(pageIndex+1);");
		}
		sb.append("}\" value='").append(gotoButton).append("'>");

		sb.append("</div></div>");
		sb.append("<div style=\"float:left;\" class=\"tfoot-fl\">");
		sb.append(new StringFormat(pagebar, new Object[] { "<span class=\"js_total\">" + total + "</span>", "<span class=\"js_pageSize\">" + pageSize + "</span>",
				"<span class=\"js_pageIndex\">" + (totalPages == 0 ? 0 : pageIndex + 1) + "</span>", "<span class=\"js_totalPages\">" + totalPages + "</span>" }));
		sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div></div>");
		return sb.toString();
	}

	private String getType1(String target, int total, int pageSize, int pageIndex) {
		String first = LangMapping.get("Framework.DataGrid.FirstPage");
		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
		String next = LangMapping.get("Framework.DataGrid.NextPage");
		String last = LangMapping.get("Framework.DataGrid.LastPage");
		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");
		String pagebar1 = LangMapping.get("Framework.DataGrid.PageBar1");

		StringBuilder sb = new StringBuilder();
		int totalPages = Double.valueOf(Math.ceil(total * 1.0D / pageSize)).intValue();
		sb.append("<div id='_PageBar_").append(target).append("' class=\"pagebar_wrap\" pagebartype=1>");
		sb.append("<div style=\"float:right;\" class=\"tfoot-fr\"><div class=\"pagebar\">&nbsp;");
		String queryString = null;
		if ((this.action instanceof ListAction)) {
			queryString = ((ListAction) this.action).getQueryString();
			if (StringUtil.isEmpty(queryString)) {
				queryString = "?PageIndex=";
			} else {
				Mapx map = StringUtil.splitToMapx(queryString, "&", "=");
				map.remove("PageIndex");
				if (map.size() == 0)
					queryString = "?PageIndex=";
				else {
					queryString = ServletUtil.getQueryStringFromMap(map) + "&PageIndex=";
				}
			}
		}
		if (pageIndex > 0) {
			if ((this.action instanceof DataListAction)) {
				sb.append("<span class=\"first\"><a href='javascript:void(0);' onclick=\"Ark.DataList.firstPage('").append(target).append("');\">").append(first).append("</a></span>");
				sb.append("<span class=\"previous\"><a href='javascript:void(0);' onclick=\"Ark.DataList.previousPage('").append(target).append("');\">").append(prev).append("</a></span>");
			} else {
				sb.append("<a href='").append(queryString).append("1'>").append(first).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(pageIndex).append("'>").append(prev).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"first\">").append(first).append("</span>");
			sb.append("<span class=\"previous\">").append(prev).append("</span>");
		}
		if ((totalPages != 0) && (pageIndex + 1 != totalPages)) {
			if ((this.action instanceof DataListAction)) {
				sb.append("<span class=\"next\"><a href='javascript:void(0);' onclick=\"Ark.DataList.nextPage('").append(target).append("');\">").append(next).append("</a></span>");
				sb.append("<span class=\"last\"><a href='javascript:void(0);' onclick=\"Ark.DataList.lastPage('").append(target).append("');\">").append(last).append("</a></span>");
			} else {
				sb.append("<a href='").append(queryString).append(pageIndex + 2).append("'>").append(next).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(totalPages).append("'>").append(last).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"next\">").append(next).append("</span>");
			sb.append("<span class=\"last\">").append(last).append("</span>");
		}

		sb.append("&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(target).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
		sb.append(
				"onkeyup=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
				.append(target).append("').onclick();\">");
		sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
		sb.append(gotoEnd).append("&nbsp;");
		sb.append("<input type='button' id='_PageBar_JumpBtn_").append(target).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(target)
				.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataList.getParam('").append(target).append("', Constant.PageTotal))/Number(Ark.DataList.getParam('")
				.append(target).append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(target).append("').focus();}")
				.append("else{var pageIndex = v>0?v-1:0;");
		if ((this.action instanceof DataListAction))
			sb.append("Ark.DataList.setParam('").append(target).append("','").append("_ARK_PAGEINDEX").append("',pageIndex);Ark.DataList.loadData('").append(target).append("');");
		else {
			sb.append("window.location='").append(queryString).append("'+(pageIndex+1);");
		}
		sb.append("}\" value='").append(gotoButton).append("'>");

		sb.append("</div></div>");
		sb.append("<div style=\"float:left;\" class=\"tfoot-fl\" title=\"");
		sb.append(new StringFormat(pagebar, new Object[] { Integer.valueOf(total), Integer.valueOf(pageSize), Integer.valueOf(totalPages == 0 ? 0 : pageIndex + 1), Integer.valueOf(totalPages) }));
		sb.append("\">");
		sb.append(new StringFormat(pagebar1, new Object[] { "<span class=\"js_total\">" + total + "</span>" }));
		sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div></div>");
		return sb.toString();
	}

	private String getType2(String target, int total, int pageSize, int pageIndex) {
		String first = LangMapping.get("Framework.DataGrid.FirstPage");
		String prev = LangMapping.get("Framework.DataGrid.PreviousPage");
		String next = LangMapping.get("Framework.DataGrid.NextPage");
		String last = LangMapping.get("Framework.DataGrid.LastPage");
		String gotoPage = LangMapping.get("Framework.DataGrid.GotoPage");
		String gotoEnd = LangMapping.get("Framework.DataGrid.GotoPageEnd");
		String error = LangMapping.get("Framework.DataGrid.ErrorPage");
		String gotoButton = LangMapping.get("Framework.DataGrid.Goto");
		String pagebar = LangMapping.get("Framework.DataGrid.PageBar");

		StringBuilder sb = new StringBuilder();
		int totalPages = Double.valueOf(Math.ceil(total * 1.0D / pageSize)).intValue();
		sb.append("<div id='_PageBar_").append(target).append("' class=\"pagebar_wrap\" pagebartype=2>");
		sb.append("<div style=\"float:right;\" class=\"tfoot-fr\"><div class=\"pagebar\">&nbsp;");
		String queryString = null;
		if ((this.action instanceof ListAction)) {
			queryString = ((ListAction) this.action).getQueryString();
			if (StringUtil.isEmpty(queryString)) {
				queryString = "?PageIndex=";
			} else {
				Mapx map = StringUtil.splitToMapx(queryString, "&", "=");
				map.remove("PageIndex");
				if (map.size() == 0)
					queryString = "?PageIndex=";
				else {
					queryString = ServletUtil.getQueryStringFromMap(map) + "&PageIndex=";
				}
			}
		}
		if (pageIndex > 0) {
			if ((this.action instanceof DataListAction)) {
				sb.append("<span class=\"first\"><a href='javascript:void(0);' onclick=\"Ark.DataList.firstPage('").append(target).append("');\">").append(first).append("</a></span>");
				sb.append("<span class=\"previous\"><a href='javascript:void(0);' onclick=\"Ark.DataList.previousPage('").append(target).append("');\">").append(prev).append("</a></span>");
			} else {
				sb.append("<a href='").append(queryString).append("1'>").append(first).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(pageIndex).append("'>").append(prev).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"first\">").append(first).append("</span>");
			sb.append("<span class=\"previous\">").append(prev).append("</span>");
		}
		if ((totalPages != 0) && (pageIndex + 1 != totalPages)) {
			if ((this.action instanceof DataListAction)) {
				sb.append("<span class=\"next\"><a href='javascript:void(0);' onclick=\"Ark.DataList.nextPage('").append(target).append("');\">").append(next).append("</a></span>");
				sb.append("<span class=\"last\"><a href='javascript:void(0);' onclick=\"Ark.DataList.lastPage('").append(target).append("');\">").append(last).append("</a></span>");
			} else {
				sb.append("<a href='").append(queryString).append(pageIndex + 2).append("'>").append(next).append("</a>&nbsp;|&nbsp;");
				sb.append("<a href='").append(queryString).append(totalPages).append("'>").append(last).append("</a>&nbsp;|&nbsp;");
			}
		} else {
			sb.append("<span class=\"next\">").append(next).append("</span>");
			sb.append("<span class=\"last\">").append(last).append("</span>");
		}

		sb.append("&nbsp;").append(gotoPage).append("&nbsp;<input id='_PageBar_Index_").append(target).append("' type='text' class='inputText gotopage' value=\"").append(pageIndex + 1).append("\"");
		sb.append(
				"onkeyup=\"value=value.replace(/\\D/g,'');style.width=Math.min(7*(value.length||1)+1,36)+'px';\" onkeydown=\"if(Ark.getEvent().keyCode==13)document.getElementById('_PageBar_JumpBtn_")
				.append(target).append("').onclick();\">");
		sb.append("/<span class=\"js_totalPages\">").append(totalPages).append("</span>");
		sb.append(gotoEnd).append("&nbsp;");
		sb.append("<input type='button' id='_PageBar_JumpBtn_").append(target).append("' class='pageJumpBtn' onclick=\"var v=document.getElementById('_PageBar_Index_").append(target)
				.append("').value;if(!/^\\d+$/.test(v)").append("||v<1||v>Number(Ark.DataList.getParam('").append(target).append("', Constant.PageTotal))/Number(Ark.DataList.getParam('")
				.append(target).append("', Constant.Size))+1){alert('").append(error).append("');document.getElementById('_PageBar_Index_").append(target).append("').focus();}")
				.append("else{var pageIndex = v>0?v-1:0;");
		if ((this.action instanceof DataListAction))
			sb.append("Ark.DataList.setParam('").append(target).append("','").append("_ARK_PAGEINDEX").append("',pageIndex);Ark.DataList.loadData('").append(target).append("');");
		else {
			sb.append("window.location='").append(queryString).append("'+(pageIndex+1);");
		}
		sb.append("}\" value='").append(gotoButton).append("'>");

		sb.append("</div></div>");
		sb.append("<div style=\"float:left;\" class=\"tfoot-fl\" title=\"");
		sb.append(new StringFormat(pagebar, new Object[] { Integer.valueOf(total), Integer.valueOf(pageSize), Integer.valueOf(totalPages == 0 ? 0 : pageIndex + 1), Integer.valueOf(totalPages) }));
		sb.append("\">");
		sb.append("&nbsp;");
		sb.append("</div><div style=\"clear:both;height:0;overflow:hidden;\"></div></div>");
		return sb.toString();
	}

	public String getTarget() {
		return this.target;
	}

	public void setTarget(String target) {
		this.target = target;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public boolean isAfloat() {
		return this.afloat;
	}

	public void setAfloat(boolean afloat) {
		this.afloat = afloat;
	}

	public boolean isAutoHide() {
		return this.autoHide;
	}

	public void setAutoHide(boolean autoHide) {
		this.autoHide = autoHide;
	}
}