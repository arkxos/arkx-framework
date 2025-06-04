package com.arkxos.framework.data.lightning;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.data.fasttable.BufferReader;

/**
 *  
 * @author Darkness
 * @date 2015年12月19日 下午6:07:12
 * @version V1.0
 * @since infinity 1.0
 */
public class TableInfo extends BufferReader {
	
	/**
	 * 计算行长度
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 下午1:31:53
	 * @version V1.0
	 * @since infinity 1.0
	 */
	protected static int caculateRowLength(LightningColumn[] columns) {
		int result = 0;
		for (LightningColumn column : columns) {
			if(column.getColumnType() == LightningColumnType.STRING) {
				result += INT_LENGTH;	
			}
			result += column.length;
		}
		return result;
	}
	
	public long rowSize;
	public String tableName;
	private Map<String, Integer> columnDataIndexMap = new HashMap<>();
	private List<LightningColumn> columns = new ArrayList<>();
	public int length;
	
	public void addColumn(LightningColumn column) {
		columns.add(column);
	}
	
	public LightningColumn getPkColumn() {
		return this.columns.get(0);
	}
	
	public LightningColumn getColumn(String columnName) {
		for (LightningColumn column : columns) {
			if(column.getColumnName().equals(columnName)) {
				return column;
			}
		}
		return null;
	}

	public LightningColumn[] columns() {
		return columns.toArray(new LightningColumn[0]);
	}
	
	public int columnSize() {
		return this.columns.size();
	}

	public void setColumnDataIndex(String columnName, int columnDataIndex) {
		columnDataIndexMap.put(columnName, columnDataIndex);
	}
	
	public int getColumnDataIndex(String columnName) {
		return columnDataIndexMap.get(columnName);
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "tableName:"+tableName+",";
		result += "rowSize:"+rowSize+",";
		result += "columnSize:"+columns.size()+",";
		result += "columns:";
		for (LightningColumn	column : columns) {
			result += "[" + column.getColumnName() + "," + column.getColumnType() + "," + column.length+"],";
		}
		return result.substring(0,result.length()-2);
	}
}
