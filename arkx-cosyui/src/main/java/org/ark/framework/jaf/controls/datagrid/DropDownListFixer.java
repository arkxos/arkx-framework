//package org.ark.framework.jaf.controls.datagrid;
//
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.html.HtmlTable;
//import org.ark.framework.orm.query.QueryBuilder;
//import util.io.arkx.framework.commons.StringUtil;
//
//import io.arkx.framework.framework.collection.DataTable;
//
///**   
// * @class org.ark.framework.jaf.controls.datagrid.DropDownListFixer
// * @author Darkness
// * @date 2013-1-9 下午10:10:12 
// * @version V1.0   
// */
//public class DropDownListFixer implements DataGridColumnFixer {
//
//	@Override
//	public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int i, Object... params) {
//			String field = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("field");
//			String sql =dataGridAction. dataGrid.getHeader().getTD(i).getAttribute("sql");
//			String zstyle = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("zstyle");
//			if (StringUtil.isEmpty(zstyle)) {
//				zstyle = "width:100px";
//			}
//			DataTable dt = new QueryBuilder(sql, new Object[0]).executeDataTable();
//			for (int j = 1; j < dataGridAction.Table.Children.size(); j++) {
//				StringBuilder sb = new StringBuilder();
//				sb.append("<div ztype='select' disabled='true' style='display:none;").append(zstyle).append(";' name='").append(dataGridAction.ID).append("_").append(field).append("_DropDownList")
//						.append(j).append("' id='").append(dataGridAction.ID).append("_").append(field).append("_DropDownList").append(j).append("' >");
//				for (int k = 0; k < dt.getRowCount(); k++) {
//					String value = dataGridAction.DataSource.getString(j - 1, field);
//					String selected = "";
//					if (value.equals(dt.getString(k, 0))) {
//						selected = "selected='true'";
//					}
//					sb.append("<span value='").append(dt.getString(k, 0)).append("' ").append(selected).append(">").append(dt.getString(k, 1)).append("</span>");
//				}
//				sb.append("</div>");
//				dataGridAction.Table.getTR(j).getTD(i).setInnerHTML(sb.toString());
//			}
//	}
//
//	@Override
//	public boolean match(String ztype) {
//		return "DropDownList".equalsIgnoreCase(ztype);
//	}
//
//}
