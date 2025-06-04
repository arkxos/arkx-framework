package io.arkx.framework.cosyui.control.grid;

import io.arkx.framework.commons.collection.DataColumn;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.cosyui.control.DataGridAction;
import io.arkx.framework.cosyui.html.HtmlTD;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;

/**
 * 行号
 * 
 */
public class GridRowNo extends AbstractGridFeature {
	public static final String ZTYPE = "RowNo";

	@Override
	public void beforeDataBind(DataGridAction dga, AbstractExecuteContext context, DataTable dataSource) {
		if (dataSource != null) {
			if (dataSource.getDataColumn("_RowNo") == null) {
				dataSource.insertColumn(new DataColumn("_RowNo", DataTypes.INTEGER));
			}
			for (int j = 0; j < dataSource.getRowCount(); j++) {
				int rowNo = dga.getPageIndex() * dga.getPageSize() + j + 1;
				dataSource.set(j, "_RowNo", new Integer(rowNo));
			}
		}

	}

	@Override
	public void rewriteTD(DataGridAction dga, HtmlTD th, HtmlTD td) {
		boolean rowNoFlag = ZTYPE.equalsIgnoreCase(th.getAttribute("ztype"));
		if (!rowNoFlag) {
			return;
		}
		th.addAttribute("disabledresize", "true");
		td.addAttribute("rowno", "${_RowNo}");
		td.addClassName("rowNo");
		td.setInnerHTML("${_RowNo}");
	}
}
