//package org.ark.framework.jaf.controls.datagrid;
//
//import java.io.StringReader;
//
//import org.ark.framework.Config;
//import org.ark.framework.jaf.PlaceHolderContext;
//import org.ark.framework.jaf.controls.DataGridAction;
//import org.ark.framework.jaf.expression.ExpressionParser;
//import org.ark.framework.jaf.expression.ParseException;
//import org.ark.framework.jaf.expression.Primitives;
//import org.ark.framework.jaf.html.HtmlTable;
//import org.ark.framework.utility.ObjectUtil;
//import com.arkxos.framework.commons.util.StringUtil;
//
//
///**   
// * @class org.ark.framework.jaf.controls.datagrid.TreeFixer
// * @author Darkness
// * @date 2013-1-9 下午10:11:34 
// * @version V1.0   
// */
//public class TreeFixer implements DataGridColumnFixer{
//
//	@Override
//	public void fixColumn(HtmlTable table, DataGridAction dataGridAction, int i, Object... params) {
//			String field =dataGridAction. dataGrid.getHeader().getTD(i).getAttribute("field");
//			String checkedExpr = dataGridAction.dataGrid.getHeader().getTD(i).getAttribute("checked");
//
//			ExpressionParser ep = null;
//			PlaceHolderContext context = new PlaceHolderContext();
//			if (ObjectUtil.notEmpty(checkedExpr)) {
//				ep = new ExpressionParser(new StringReader(checkedExpr));
//				ep.setContext(context);
//			}
//
//			int headTrSize = dataGridAction.dataGrid.getHeader().getHeaderTrs().size();
//			for (int j = headTrSize; j < dataGridAction.Table.Children.size(); j++) {
//				int level = dataGridAction.DataSource.getInt(j - headTrSize, "_TreeLevel");
//
//				boolean checked = false;
//				if (ep != null) {
//					ep.ReInit(new StringReader(checkedExpr));
//					context.addMap(dataGridAction.DataSource.getDataRow(j - 1).toCaseIgnoreMapx(), "List");
//					try {
//						checked = Primitives.getBoolean(ep.execute());
//					} catch (ParseException e) {
//						e.printStackTrace();
//					}
//				}
//
//				StringBuilder sb = new StringBuilder();
//				for (int k = 1; k < level; k++) {
//					sb.append("<q style='padding:0 10px'></q>");
//				}
//				int nextLevel = 0;
//				if (j != dataGridAction.Table.Children.size() - 1) {
//					nextLevel = Integer.parseInt(dataGridAction.DataSource.getString(j-headTrSize+1, "_TreeLevel"));
//				}
//				if (level < nextLevel)
//					sb.append("<img src='").append(Config.getContextPath()).append("Framework/Images/butExpand.gif' onclick='DataGrid.treeClick(this)'/>&nbsp;");
//				else {
//					sb.append("<img src='").append(Config.getContextPath()).append("Framework/Images/butNoChild.gif'/>&nbsp;");
//				}
//				if (!StringUtil.isEmpty(field)) {
//					String treeID = dataGridAction.DataSource.getString(j - headTrSize, field);
//					sb.append("<input type='checkbox'  name='").append(dataGridAction.ID)
//					.append("_TreeRowCheck' id='").append(dataGridAction.ID)
//					.append("_TreeRowCheck_").append(j).append("' value='").append(treeID)
//							.append(checked ? "' checked='true'" : "'").append(" level='").append(level).append("' onClick='treeCheckBoxClick(this);'>");
//				}
//
//				sb.append(dataGridAction.Table.getTR(j).getTD(i).getInnerHTML());
//				dataGridAction.Table.getTR(j).getTD(i).setInnerHTML(sb.toString());
//				dataGridAction.Table.getTR(j).setAttribute("level", level + "");
//			}
//
//	}
//
//	@Override
//	public boolean match(String ztype) {
//		return "Tree".equalsIgnoreCase(ztype);
//	}
//
//}
