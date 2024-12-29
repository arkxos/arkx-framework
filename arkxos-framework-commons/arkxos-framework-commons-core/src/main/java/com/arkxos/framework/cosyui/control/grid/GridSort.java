package com.arkxos.framework.cosyui.control.grid;

import java.util.List;
import java.util.regex.Pattern;

import com.arkxos.framework.commons.lang.FastStringBuilder;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.control.DataGridAction;
import com.arkxos.framework.cosyui.html.HtmlTD;
import com.arkxos.framework.cosyui.html.HtmlTR;
import com.rapidark.framework.Config;
import com.rapidark.framework.Constant;

/**
 * 排序
 * 
 */
public class GridSort extends AbstractGridFeature {

	public static Pattern SortPattern = Pattern.compile("[\\w\\,\\s]*", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

	@Override
	public void rewriteTR(DataGridAction dga, HtmlTR tr) {
		if (!DataGridBody.TR_HEAD.equalsIgnoreCase(tr.getAttribute("ztype"))) {
			return;
		}
		List<HtmlTD> list = tr.getTDList();
		StringBuilder sortSB = new StringBuilder();
		boolean first = true;
		for (HtmlTD th : list) {
			String sortField = th.getAttribute("sortField");
			String direction = th.getAttribute("direction");
			if (StringUtil.isNotEmpty(sortField)) {
				if (StringUtil.isNotEmpty(direction)) {// 未指定方向的先不需要排序，等在页面中点击后再排序
					if (!first) {
						sortSB.append(",");
					}
					sortSB.append(sortField);
					sortSB.append(" ");
					sortSB.append(direction);
					first = false;
				}
			}
			if (StringUtil.isNotEmpty(sortField)) {
				th.addAttribute("class", "dg-sortTh");
				th.addAttribute("onClick", "Ark.DataGrid.onSort(this);");
				StringBuilder sb = new StringBuilder();
				sb.append("<span style='float:left'>");
				sb.append(th.getInnerHTML());
				sb.append("</span>");
				sb.append("<img src='");
				sb.append(Config.getContextPath());
				sb.append("framework/images/blank.gif'");
				sb.append(" class='fr icon_sort");
				if (StringUtil.isNotEmpty(direction)) {
					sb.append(direction.toUpperCase());
				}
				sb.append("' width='12' height='12'>");
				th.setInnerHTML(sb.toString());
			}
		}
		String sort = sortSB.toString();
		dga.getParams().put(Constant.DataGridSortString, sort);
		tr.getTable().addAttribute("sortstring", sort);
	}

	@Override
	public void appendScript(DataGridAction dga, FastStringBuilder scriptSB) {
		String sort = dga.getParam(Constant.DataGridSortString);
		String id = dga.getID();
		if (ObjectUtil.notEmpty(sort)) {
			scriptSB.append("var dg = $('#").append(id).append("').getComponent('DataGrid');");
			scriptSB.append("if (dg) {");
			scriptSB.append("dg.setParam('").append(Constant.DataGridSortString).append("','").append(sort).append("');");
			scriptSB.append("}");
		}
	}

	public static boolean isSortFlag(DataGridAction dga) {
		return ObjectUtil.notEmpty(dga.getParam(Constant.DataGridSortString));
	}

	public static String getSortString(DataGridAction dga) {
		String sort = dga.getParam(Constant.DataGridSortString);
		if (ObjectUtil.notEmpty(sort) && SortPattern.matcher(sort).matches()) {
			return " order by " + sort;
		}
		return "";
	}

}
