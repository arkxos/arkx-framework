//package org.ark.framework.jaf.controls.datagrid;
//
//import org.ark.framework.jaf.html.HtmlTable;
//
//import com.arkxos.framework.cosyui.control.DataGridAction;
//
///**
// * @class org.ark.framework.jaf.controls.datagrid.CheckboxFixer
// * @author Darkness
// * @date 2013-1-9 下午10:08:01
// * @version V1.0
// */
//public class CheckboxFixer implements DataGridColumnFixer {
//
//	@Override
//	public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int i, Object... params) {
//		String field = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("field");
//		String checkedvalue = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("checkedvalue");
//		String disabled = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("disabled");
//		if (checkedvalue == null) {
//			checkedvalue = "Y";
//		}
//		if ((disabled == null) || (disabled.equalsIgnoreCase("true")))
//			disabled = "disabled";
//		else {
//			disabled = "";
//		}
//		for (int j = 1; j < dataGridAction.Table.Children.size(); j++) {
//			String checked = checkedvalue.equals(dataGridAction.DataSource.getString(j - 1, field)) ? "checked" : "";
//			dataGridAction.Table
//					.getTR(j)
//					.getTD(i)
//					.setInnerHTML(
//							"<input type='checkbox' " + disabled + " name='" + dataGridAction.ID + "_" + field + "_Checkbox' id='" + dataGridAction.ID + "_" + field + "_Checkbox" + j + "' value='"
//									+ checkedvalue + "' " + checked + ">");
//		}
//	}
//
//	@Override
//	public boolean match(String ztype) {
//		return "Checkbox".equalsIgnoreCase(ztype);
//	}
//
//}
