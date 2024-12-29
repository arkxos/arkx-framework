//package org.ark.framework.jaf.controls.datagrid;
//
//import org.ark.framework.collection.DataTableUtil;
//import org.ark.framework.jaf.Current;
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.html.HtmlTD;
//import org.ark.framework.jaf.html.HtmlTR;
//import com.arkxos.framework.commons.util.StringUtil;
//
//import com.arkxos.framework.framework.collection.DataColumn;
//import com.arkxos.framework.framework.collection.DataRow;
//import com.arkxos.framework.framework.collection.DataTypes;
//
///**   
// * @class org.ark.framework.jaf.controls.datagrid.DataGridDataBinder
// * @author Darkness
// * @date 2013-1-9 下午02:02:14 
// * @version V1.0   
// */
//public class DataGridDataBinder {
//
//	DataGridAction dataGridAction;
//	
//	private void fixTreeDataSource() {
//		if ((dataGridAction.dataGrid.isTreeFlag()) 
//				&& (dataGridAction.DataSource.getDataColumn("ParentID") != null) 
//				&& (dataGridAction.DataSource.getDataColumn("ID") != null)) {
//			dataGridAction.DataSource = DataTableUtil.sortTreeDataTable(dataGridAction.DataSource, "ID", "ParentID");
//		}
//	}
//	public void bindData(DataGridAction dataGridAction) throws Exception {
//		
//		this.dataGridAction = dataGridAction;
//		
//		if (!dataGridAction.PageFlag) {
//			dataGridAction.Total = dataGridAction.DataSource.getRowCount();
//		}
//
//		fixTreeDataSource();
//
//		if (dataGridAction.DataSource == null) {
//			throw new RuntimeException("DataSource must set in bindData()");
//		}
//
//		fixDataSourceRowNo();
//		
//		buildTableTrByDataSource();
//		
//		DataGridColumnFixer[] dataGridColumnFixers = new DataGridColumnFixer[]{
//			new RowNoFixer(), new SelectorFixer(),
//			new CheckboxFixer(), new DropDownListFixer(),
//			new TreeFixer(), new DragFixer()
//		};
//		for (int i = 0; i < dataGridAction.dataGrid.getHeader().getChildSize(); i++) {
//			
//			String ztype =  dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("ztype");
//			
//			for (DataGridColumnFixer dataGridColumnFixer : dataGridColumnFixers) {
//				if(dataGridColumnFixer.match(ztype)) {
//					dataGridColumnFixer.fixColumn(dataGridAction.Table, dataGridAction, i, dataGridAction.PageIndex, dataGridAction.PageSize);
//				}
//			}
//
//		}
//
//		for (int i = 0; i < dataGridAction.Table.getChildren().size(); i++) {
//			HtmlTR tr = dataGridAction.Table.getTR(i);
//			for (int j = 0; j < tr.getChildren().size(); j++) {
//				HtmlTD td = tr.getTD(j);
//				if (StringUtil.isEmpty(td.getInnerHTML()))
//					td.setInnerHTML("&nbsp;");
//			}
//		}
//	}
//	
//	private void buildTableTrByDataSource() throws Exception {
//		
//		for (int i = 0; i < dataGridAction.DataSource.getRowCount(); i++) {
//			DataRow dr = dataGridAction.DataSource.getDataRow(i);
//
//			String trHtml = applyOneTrByTemplate(dr);
//			
//			HtmlTR tr = new HtmlTR(dataGridAction.Table);
//			tr.parseHtml(trHtml);
//			if (i % 2 == 1) {
//				if (dataGridAction.dataGrid.getStyle1() != null) {
//					tr.setAttribute("style", dataGridAction.dataGrid.getStyle1());
//				}
//				if (dataGridAction.dataGrid.getClass1() != null)
//					tr.setAttribute("class", dataGridAction.dataGrid.getClass1());
//			} else {
//				if (dataGridAction.dataGrid.getStyle2() != null) {
//					tr.setAttribute("style", dataGridAction.dataGrid.getStyle2());
//				}
//				if (dataGridAction.dataGrid.getClass2() != null) {
//					tr.setAttribute("class", dataGridAction.dataGrid.getClass2());
//				}
//			}
//			String clickEvent = tr.getAttribute("onclick");
//			if (StringUtil.isEmpty(clickEvent)) {
//				clickEvent = "";
//			}
//			tr.setAttribute("onclick", "Ark.DataGrid.onRowClick(this,event);" + clickEvent);
//			String dblEvent = tr.getAttribute("ondblclick");
//			if (StringUtil.isNotEmpty(dblEvent))
//				tr.setAttribute("ondblclick", dblEvent);
//			else if (dataGridAction.dataGrid.getEditTemplate() != null) {
//				tr.setAttribute("ondblclick", "Ark.DataGrid.editRow(this)");
//			}
//			
//			String showContextMenu = tr.getAttribute("showContextMenu");
//			if(StringUtil.isNotEmpty(showContextMenu) && "true".equals(showContextMenu)) {
//				tr.setAttribute("oncontextmenu", "Ark.DataGrid._onContextMenu(this,event)");
//			}
//
//			dataGridAction.Table.addTR(tr);
//		}
//	}
//	
//	private String applyOneTrByTemplate(DataRow dr) {
//		StringBuilder sb = new StringBuilder();
//		for (int j = 0; j < dataGridAction.dataGrid.getA1().size(); j++) {
//			sb.append(dataGridAction.dataGrid.getA1().get(j));
//			if (j < dataGridAction.dataGrid.getA2().size()) {
//				String key = dataGridAction.dataGrid.getA2().get(j);
//				if (dr.getDataColumn(key) != null)
//					sb.append(dr.getString(key));
//				else if ((Current.getRequest() != null) && (Current.getRequest().containsKey(key)))
//					sb.append(Current.getRequest().getString(key));
//				else if ((Current.getResponse() != null) && (Current.getResponse().containsKey(key)))
//					sb.append(Current.getResponse().getString(key));
//				else if (Current.containsVariable(key))
//					sb.append(Current.getVariable(key).toString());
//				else if (!dataGridAction.DataSource.containsColumn(key)) {
//					sb.append("${" + key + "}");
//				}
//			}
//		}
//		return sb.toString();
//	}
//	private void fixDataSourceRowNo() {
//		if (dataGridAction.DataSource.getDataColumn("_RowNo") == null) {
//			dataGridAction.DataSource.insertColumn(new DataColumn("_RowNo", DataTypes.INTEGER));
//			for (int j = 0; j < dataGridAction.DataSource.getRowCount(); j++) {
//				int rowNo = dataGridAction.PageIndex * dataGridAction.PageSize + j + 1;
//				dataGridAction.DataSource.set(j, "_RowNo", rowNo);
//			}
//		}
//	}
//}
