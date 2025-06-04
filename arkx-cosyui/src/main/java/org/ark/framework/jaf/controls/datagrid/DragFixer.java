//package org.ark.framework.jaf.controls.datagrid;
//
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.html.HtmlTD;
//import org.ark.framework.jaf.html.HtmlTable;
//
///**   
// * @class org.ark.framework.jaf.controls.datagrid.DragFixer
// * @author Darkness
// * @date 2013-1-9 下午10:13:59 
// * @version V1.0   
// */
//public class DragFixer implements DataGridColumnFixer {
//
//	@Override
//	public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int i, Object... params) {
//		if ("true".equalsIgnoreCase(dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("drag"))) {
//			for (int j = 1; j < dataGridAction.Table.Children.size(); j++) {
//				HtmlTD td = dataGridAction.Table.getTR(j).getTD(i);
//				String style = td.getAttribute("style");
//				if (style != null) {
//					td.setAttribute("style", style);
//				}
//				td.setAttribute("class", "z-draggable");
//			}
//
//		}
//	}
//
//	@Override
//	public boolean match(String ztype) {
//		return true;
//	}
//
//}
