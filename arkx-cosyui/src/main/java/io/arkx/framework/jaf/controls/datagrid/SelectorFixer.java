//package org.ark.framework.jaf.controls.datagrid;
//
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.html.HtmlTable;
//
///**
// * @class org.ark.framework.jaf.controls.datagrid.SelectorFixer
// * @author Darkness
// * @date 2013-1-9 下午09:57:58
// * @version V1.0
// */
//public class SelectorFixer implements DataGridColumnFixer {
//
//	@Override
//	public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int columnIndex, Object... params) {
//		
//		String field = dataGridAction.dataGrid.getHeader().getTD(columnIndex).getAttribute("field");
//		String onSelect = dataGridAction.dataGrid.getHeader().getTD(columnIndex).getAttribute("onselect");
//		if (onSelect == null) {
//			onSelect = "";
//		}
//		if (dataGridAction.MultiSelect) {
//			dataGridAction.dataGrid.getHeader().getTD(columnIndex)
//					.setInnerHTML("<input type='checkbox' value='*' id='" + dataGridAction.ID + "_AllCheck' onclick=\"Ark.DataGrid.onAllCheckClick('" + dataGridAction.ID + "')\"/>");
//		}
//		String type = dataGridAction.MultiSelect ? "checkbox" : "radio";
//		
//		int headTrSize = dataGridAction.dataGrid.getHeader().getHeaderTrs().size();
//		
//		for (int j = headTrSize; j < dataGridAction.Table.Children.size(); j++) {
//			
//			String fieldValue = dataGridAction.DataSource.getString(j - headTrSize, field);
//			
//			dataGridAction.Table
//					.getTR(j)
//					.getTD(columnIndex)
//					.setInnerHTML(
//							"<input type='" + type + "' name='" + dataGridAction.ID + "_RowCheck' id='" + dataGridAction.ID + "_RowCheck" + j + "' value='"
//									+ fieldValue + "'>");
//			dataGridAction.Table.getTR(j).getTD(columnIndex).setAttribute("class", "selector");
//			dataGridAction.Table.getTR(j).getTD(columnIndex).setAttribute("onclick", "Ark.DataGrid.onSelectorClick(this,event);" + onSelect);
//			dataGridAction.Table.getTR(j).getTD(columnIndex).setAttribute("ondblclick", "stopEvent(event);" + onSelect);
//		}
//	}
//
//	@Override
//	public boolean match(String ztype) {
//		return "Selector".equalsIgnoreCase(ztype);
//	}
//
//}
