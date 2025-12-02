// package org.ark.framework.jaf.controls.datagrid;
//
// import java.util.List;
//
// import org.ark.framework.Constant;
// import org.ark.framework.jaf.controls.DataGridAction;
// import org.ark.framework.jaf.html.HtmlTR;
// import org.ark.framework.jaf.html.HtmlTable;
//
//
/// **
// * @class org.ark.framework.jaf.controls.datagrid.DataGridParser
// * @author Darkness
// * @date 2013-1-9 下午02:05:33
// * @version V1.0
// */
// public class DataGridParser {
//
// DataGrid dataGrid = null;
//
// private void initDataGrid(HtmlTable template) {
// for (int i = 0; i < template.Children.size(); i++) {
//
// HtmlTR tr = (HtmlTR) template.Children.get(i);
//
// if ("customHead".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// dataGrid.setHead(tr);
// }if ("Head".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// dataGrid.setHead(tr);
// } else if ("PageBar".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// dataGrid.setPageBar(tr);
// } else if ("SimplePageBar".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// dataGrid.setSimplePageBar(tr);
// } else if ("Edit".equalsIgnoreCase(tr.getAttribute("ztype"))) {
// dataGrid.setEditTemplate(tr);
// } else {
// dataGrid.setTemplate(tr);
// }
// }
// }
//
// public DataGrid parse(DataGridAction dataGridAction) {
//
// dataGrid = new DataGrid();
//
// dataGridAction.Table = new HtmlTable();
// dataGridAction.Table.setAttributes(dataGridAction.Template.getAttributes());
//
// initDataGrid(dataGridAction.Template);
//
// DataGridHeader header = dataGrid.getHeader();
// List<HtmlTR> htmlTRs = header.getHeaderTrs();
// for (HtmlTR htmlTR : htmlTRs) {
// dataGridAction.Table.addTR(htmlTR);
// }
//
// dataGrid.initSortString(dataGridAction.Params.getString(Constant.DataGridSortString));
//
// dataGridAction.Table.setAttribute("SortString",
// dataGrid.getSortString().toString());
//
// dataGridAction.Table.setAttribute("id", dataGridAction.ID);
// dataGridAction.Table.setAttribute("page", dataGridAction.PageFlag);
// dataGridAction.Table.setAttribute("size", dataGridAction.PageSize);
// dataGridAction.Table.setAttribute("method", dataGridAction.Method);
// dataGridAction.Table.setAttribute("multiselect", dataGridAction.MultiSelect);
// dataGridAction.Table.setAttribute("autofill", dataGridAction.autoFill);
// dataGridAction.Table.setAttribute("autopagesize",
// dataGridAction.autoPageSize);
// dataGridAction.Table.setAttribute("scroll", dataGridAction.Scroll + "");
// dataGridAction.Table.setAttribute("lazy", dataGridAction.Lazy + "");
// dataGridAction.Table.setAttribute("cachesize", dataGridAction.cacheSize);
//
// dataGrid.initFields();
//
// return dataGrid;
// }
//
// }
