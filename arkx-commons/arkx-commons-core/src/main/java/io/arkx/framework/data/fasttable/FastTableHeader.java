package io.arkx.framework.data.fasttable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Darkness
 * @date 2015年12月19日 下午6:07:12
 * @version V1.0
 * @since infinity 1.0
 */
public class FastTableHeader extends BufferReader {

	/**
	 * 计算行长度
	 *
	 * @author Darkness
	 * @date 2015年12月5日 下午1:31:53
	 * @version V1.0
	 * @since infinity 1.0
	 */
	public static int caculateRowLength(List<FastColumn> columns) {
		int result = 0;
		for (FastColumn column : columns) {
			if (column.getType() == FastColumnType.String) {
				result += INT_LENGTH;
			}
			result += column.getLength();
		}
		return result;
	}

	private long rowSize;

	private String tableName;

	private Map<String, Integer> columnDataIndexMap = new HashMap<>();

	private List<FastColumn> columns = new ArrayList<>();

	public void setTableName(String tableName) {
		this.tableName = tableName;
	}

	public String getTableName() {
		return tableName;
	}

	public void setRowSize(long rowSize) {
		this.rowSize = rowSize;
	}

	public long getRowSize() {
		return rowSize;
	}

	public void addColumn(FastColumn column) {
		columns.add(column);
	}

	public void setColumns(List<FastColumn> columns) {
		this.columns.clear();

		for (FastColumn fastColumn : columns) {
			this.columns.add(fastColumn);
		}
	}

	public void setColumns(FastColumn[] columns) {
		this.columns.clear();

		for (FastColumn fastColumn : columns) {
			this.columns.add(fastColumn);
		}
	}

	public List<FastColumn> getIndexColumns() {
		List<FastColumn> result = new ArrayList<>();

		for (FastColumn fastColumn : columns) {
			if (fastColumn.getIndexType() == FastColumnIndexType.Index) {
				result.add(fastColumn);
			}
		}

		return result;
	}

	public FastColumn getColumn(String columnName) {
		for (FastColumn column : columns) {
			if (column.getName().equals(columnName)) {
				return column;
			}
		}
		return null;
	}

	public List<FastColumn> columns() {
		return columns;
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
		result += "tableName:" + tableName + ",";
		result += "rowSize:" + rowSize + ",";
		result += "columnSize:" + columns.size() + ",";
		result += "columns:";
		for (FastColumn column : columns) {
			result += "[" + column.getName() + "," + column.getType() + "," + column.getLength() + "],";
		}
		return result.substring(0, result.length() - 2);
	}

}
