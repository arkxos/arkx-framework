// package org.ark.framework.jaf.controls.datagrid;
//
// import org.ark.framework.jaf.controls.DataGridAction;
// import org.ark.framework.jaf.html.HtmlTable;
//
/// **
// * @class org.ark.framework.jaf.controls.datagrid.RowNoFixer
// * @author Darkness
// * @date 2013-1-9 下午09:32:27
// * @version V1.0
// */
// public class RowNoFixer implements DataGridColumnFixer {
//
// @Override
// public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int
// columnIndex, Object... params) {
//
// int PageIndex = (Integer) params[0];
// int PageSize = (Integer) params[1];
// int headTrSize = dataGridAction.dataGrid.getHeader().getHeaderTrs().size();
// for (int j = headTrSize; j < table.Children.size(); j++) {
// int rowNo = PageIndex * PageSize + j-headTrSize+1;
// table.getTR(j).getTD(columnIndex).setInnerHTML(rowNo + "");
// table.getTR(j).getTD(columnIndex).setAttribute("rowno", rowNo + "");
// table.getTR(j).getTD(columnIndex).setAttribute("class", "rowNo");
//
// }
// }
//
// @Override
// public boolean match(String ztype) {
//
// return "RowNo".equalsIgnoreCase(ztype);
// }
//
// }
