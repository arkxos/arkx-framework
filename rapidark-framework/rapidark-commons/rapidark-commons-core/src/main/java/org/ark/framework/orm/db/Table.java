package org.ark.framework.orm.db;
//package org.ark.framework.orm.schema;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * 
// * @author Darkness
// * @date 2012-9-18 下午3:46:44
// * @version V1.0
// */
//public class Table {
//
//	private String tableName;
//	private String comment;
//	
//	private List<Column> columns = new ArrayList<Column>();
//
//	public String getTableName() {
//		return tableName;
//	}
//
//	public void setTableName(String tableName) {
//		this.tableName = tableName;
//	}
//
//	public List<Column> getColumns() {
//		return columns;
//	}
//
//	public void addColumn(Column column) {
//		this.columns.add(column);
//	}
//
//	/**
//	 * 获取表中列的数量
//	 * 
//	 * @author Darkness
//	 * @date 2012-9-18 下午4:35:58 
//	 * @version V1.0
//	 */
//	public int getColumnSize() {
//		return this.columns.size();
//	}
//
//	public String getComment() {
//		return comment;
//	}
//
//	public void setComment(String comment) {
//		this.comment = comment;
//	}
//
//	public Column getColumn(int i) {
//		if(i<0 || i > getColumnSize()-1) {
//			return null;
//		}
//		
//		return this.columns.get(i);
//	}
//
//}
