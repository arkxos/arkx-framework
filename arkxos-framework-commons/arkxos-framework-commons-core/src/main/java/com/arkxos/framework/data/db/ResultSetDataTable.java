package com.arkxos.framework.data.db;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;

import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.DataTypes;
import com.arkxos.framework.data.db.connection.Connection;
import com.arkxos.framework.data.db.dbtype.DBTypeService;
import com.arkxos.framework.data.db.dbtype.IDBType;

/**
 *  数据表格，主要用来封装ResultSet。
 * @author Darkness
 * @date 2017年1月3日 下午5:58:02
 * @version V1.0
 */
public class ResultSetDataTable extends DataTable {
	
	private static final long serialVersionUID = 1L;

	/**
	 * 构造器
	 * 
	 * @param rs ResultSet
	 */
	public ResultSetDataTable(Connection conn, ResultSet rs) {
		this(conn, rs, Integer.MAX_VALUE, 0, false);
	}

	/**
	 * 构造器
	 * 
	 * @param rs ResultSet
	 * @param latin1Flag 是否为latin1字符集，在oracle下使用此字符集需要特殊处理
	 */
	public ResultSetDataTable(Connection conn, ResultSet rs, boolean latin1Flag) {
		this(conn, rs, Integer.MAX_VALUE, 0, latin1Flag);
	}

	/**
	 * 构造器
	 * 
	 * @param rs ResultSet
	 * @param pageSize 分页大小
	 * @param pageIndex 第几页，0为第一页
	 */
	public ResultSetDataTable(Connection conn, ResultSet rs, int pageSize, int pageIndex) {// NO_UCD
		this(conn, rs, pageSize, pageIndex, false);
	}

	/**
	 * 构造器
	 * 
	 * @param rs ResultSet
	 * @param pageSize 分页大小
	 * @param pageIndex 第几页，0为第一页
	 * @param latin1Flag 是否为latin1字符集，在oracle下使用此字符集需要特殊处理
	 */
	public ResultSetDataTable(Connection conn, ResultSet rs, int pageSize, int pageIndex, boolean latin1Flag) {
		ResultSetMetaData rsmd;
		try {
			// 以下准备DataColumn[]
			rsmd = rs.getMetaData();
			int columnCount = rsmd.getColumnCount();
			DataColumn[] types = new DataColumn[columnCount];
			for (int i = 1; i <= columnCount; i++) {
				String name = rsmd.getColumnLabel(i);
				boolean b = rsmd.isNullable(i) == ResultSetMetaData.columnNullable;
				DataColumn dc = new DataColumn();
				dc.setAllowNull(b);
				dc.setColumnName(name);

				// 以下设置数据类型
				int dataType = rsmd.getColumnType(i);
				if (dataType == Types.CHAR || dataType == Types.VARCHAR) {
					dc.setColumnType(DataTypes.STRING);
				} else if (dataType == Types.TIMESTAMP || dataType == Types.DATE) {
					dc.setColumnType(DataTypes.DATETIME);
				} else if (dataType == Types.DECIMAL) {
					dc.setColumnType(DataTypes.DECIMAL);
					int dataScale = rsmd.getScale(i);
					int dataPrecision = rsmd.getPrecision(i);
					if (dataScale == 0 && dataPrecision != 0) {
						dc.setColumnType(DataTypes.LONG);
					} else if (dataScale > 0 && dataScale + dataPrecision > 17) {// 双精度有效十进制位数为17
						dc.setColumnType(DataTypes.BIGDECIMAL);
					} else {
						dc.setColumnType(DataTypes.DECIMAL);
					}
				} else if (dataType == Types.DOUBLE || dataType == Types.REAL) {
					dc.setColumnType(DataTypes.DOUBLE);
				} else if (dataType == Types.FLOAT) {
					dc.setColumnType(DataTypes.FLOAT);
				} else if (dataType == Types.INTEGER) {
					dc.setColumnType(DataTypes.INTEGER);
				} else if (dataType == Types.SMALLINT || dataType == Types.TINYINT) {
					dc.setColumnType(DataTypes.SMALLINT);
				} else if (dataType == Types.BIT) {
					dc.setColumnType(DataTypes.BIT);
				} else if (dataType == Types.BIGINT) {
					dc.setColumnType(DataTypes.LONG);
				} else if (dataType == Types.BLOB || dataType == Types.LONGVARBINARY) {
					dc.setColumnType(DataTypes.BLOB);
				} else if (dataType == Types.CLOB || dataType == Types.LONGVARCHAR) {
					dc.setColumnType(DataTypes.CLOB);
				} else if (dataType == Types.NUMERIC) {
					int dataScale = rsmd.getScale(i);
					int dataPrecision = rsmd.getPrecision(i);
					if (dataScale == 0 && dataPrecision != 0) {
						dc.setColumnType(DataTypes.LONG);
					} else if (dataScale > 0 && dataScale + dataPrecision > 17) {// 双精度有效十进制位数为17
						dc.setColumnType(DataTypes.BIGDECIMAL);
					} else {
						dc.setColumnType(DataTypes.DOUBLE);
					}
				} else {
					dc.setColumnType(DataTypes.STRING);
				}
				types[i - 1] = dc;
			}

			columns = types;
			renameAmbiguousColumns();

			IDBType db = DBTypeService.getInstance().get(conn.getDBConfig().DBType);

			// 以下准备ColumnValues[]
			int index = 0;
			int begin = pageIndex * pageSize;
			int end = (pageIndex + 1) * pageSize;
			while (rs.next()) {
				if (index >= end) {
					break;
				}
				if (index >= begin) {
					Object[] rowValue = new Object[columnCount];
					for (int j = 1; j <= columnCount; j++) {
						DataTypes columnType = columns[j - 1].getColumnType();
						rowValue[j - 1] = db.getValueFromResultSet(rs, j, columnType, latin1Flag);
					}
					rows.add(new DataRow(this, rowValue));
				}
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
