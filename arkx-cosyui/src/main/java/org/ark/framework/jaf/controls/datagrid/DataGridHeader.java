// package org.ark.framework.jaf.controls.datagrid;
//
// import java.util.ArrayList;
// import java.util.List;
//
// import org.ark.framework.Config;
// import org.ark.framework.jaf.html.HtmlElement;
// import org.ark.framework.jaf.html.HtmlTD;
// import org.ark.framework.jaf.html.HtmlTR;
// import util.io.arkx.framework.commons.StringUtil;
//
// import io.arkx.framework.framework.collection.Mapx;
//
//
/// **
// * @class org.ark.framework.jaf.controls.datagrid.DataGridHeader
// * @author Darkness
// * @date 2013-1-10 上午10:31:13
// * @version V1.0
// */
// public class DataGridHeader {
//
// private List<HtmlTR> headTrs = new ArrayList<HtmlTR>();
//
// private HtmlTR head = null;
//
// public void setHead(HtmlTR head) {
// this.head = head;
// }
//
// public HtmlTR getHead() {
// return head;
// }
//
// public void initHeadSort(DataGrid dataGrid, boolean emptyFlag, Mapx<Object,
// Object> sortMap) {
// boolean firstSortFieldFlag = true;
//
// for (int i = 0; i < getHead().Children.size(); i++) {
// HtmlTD td = getHead().getTD(i);
//
// String ztype = td.getAttribute("ztype");
// if ("Tree".equalsIgnoreCase(ztype)) {
// dataGrid.setTreeFlag(true);
// try {
// dataGrid.setTreeStartLevel(Integer.parseInt(td.getAttribute("startLevel")));
// } catch (Exception localException) {
// }
// dataGrid.setTreeLazy(td.getAttribute("treeLazy"));
// }
// String sortField = td.getAttribute("sortField");
// String direction = td.getAttribute("direction");
// if (StringUtil.isNotEmpty(sortField)) {
// dataGrid.setSortFlag(true);
// if (emptyFlag) {
// if (StringUtil.isNotEmpty(direction)) {
// if (!firstSortFieldFlag) {
// dataGrid.getSortString().append(",");
// }
// dataGrid.getSortString().append(sortField);
// dataGrid.getSortString().append(" ");
// dataGrid.getSortString().append(direction);
// firstSortFieldFlag = false;
// } else {
// direction = "";
// }
// } else {
// direction = (String) sortMap.get(sortField.toLowerCase());
// if (StringUtil.isEmpty(direction)) {
// direction = "";
// }
// td.setAttribute("direction", direction);
// }
// }
// if (StringUtil.isNotEmpty(sortField)) {
// td.setAttribute("class", "dg_sortTh");
// td.setAttribute("onClick", "Ark.DataGrid.onSort(this);");
// StringBuilder sb = new StringBuilder();
// sb.append("<span style='float:left'>");
// sb.append(td.getInnerHTML());
// sb.append("</span>");
// sb.append("<img src='");
// sb.append(Config.getContextPath());
// sb.append("Framework/Images/blank.gif'");
// sb.append(" class='fr icon_sort");
// sb.append(direction.toUpperCase());
// sb.append("' width='12' height='12'>");
// td.setInnerHTML(sb.toString());
// }
// }
// }
//
// public HtmlElement getTD(int i) {
//
// return head.getTD(i);
// }
//
// public int getChildSize() {
// return head.Children.size();
// }
//
// public void addHead(HtmlTR tr) {
//
// if("head".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// head = tr;
// } else {
// headTrs.add(tr);
// }
// }
//
// public List<HtmlTR> getHeaderTrs() {
//
// if(headTrs.isEmpty()) {
// headTrs.add(head);
// }
// return headTrs;
// }
// }
