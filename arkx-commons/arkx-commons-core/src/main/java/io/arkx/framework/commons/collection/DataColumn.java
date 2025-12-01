package io.arkx.framework.commons.collection;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;

/**
 * @class org.ark.framework.collection.DataColumn
 * 数据字段，表示DataTable中的一个列
 * @author Darkness
 * @date 2013-2-19 上午11:13:21 
 * @version V1.0
 */
public class DataColumn implements Serializable, Cloneable, JSONAware {

	private static final long serialVersionUID = 1L;
	
	public static DataColumn stringColumn(String columnName, int length) {
		return new DataColumn(columnName, DataTypes.STRING, length);
	}
	
	public static DataColumn stringIndexColumn(String columnName, int length) {
		return new DataColumn(columnName, DataTypes.STRING, length, false, true);
	}
	
	public static DataColumn fixedStringColumn(String columnName, int length) {
		return new DataColumn(columnName, DataTypes.FIXED_STRING, length);
	}
	
	public static DataColumn fixedIndexStringColumn(String columnName, int length) {
		return new DataColumn(columnName, DataTypes.FIXED_STRING, length, false, true);
	}
	
	public static DataColumn intColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.INTEGER);
	}
	
	public static DataColumn longColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.LONG);
	}
	
	public static DataColumn floatColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.FLOAT);
	}
	
	public static DataColumn doubleColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.DOUBLE);
	}
	
	public static DataColumn dateColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.DATE);
	}
	
	public static DataColumn dateIndexColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.DATE, false, true);
	}
	
	public static DataColumn dateTimeColumn(String columnName) {
		return new DataColumn(columnName, DataTypes.DATETIME);
	}
	
	public static DataColumn createNstringColumn(String columnName, int length) {
		return new DataColumn(columnName, DataTypes.STRING, length);
	}
	
	private boolean isPk;
	private boolean isIndex;
	protected String columnName;
	protected DataTypes ColumnType;
	public int length;
	
	protected boolean isAllowNull = true;

	protected String dateFormat = null;
	protected int hash;
	
	/**
	 * 空构造器
	 */
	public DataColumn() {
	}
	
	/**
	 * 克隆
	 */
	@Override
	public Object clone() {
		return new DataColumn(this.columnName, this.ColumnType, this.length);
	}
	/**
	 * 将java类型转换成DataColumn 的类型
	 * 
	 * @author Darkness
	 * @date 2012-11-26 下午03:08:13 
	 * @version V1.0
	 */
	public static DataTypes convertType(Class<?> type) {
		if (String.class == type) {
			return DataTypes.STRING;
		}
		if (Date.class == type) {
			return DataTypes.DATETIME;
		}
		if (float.class == type || Float.class == type) {
			return DataTypes.FLOAT;
		}
		if (double.class == type || Double.class == type) {
			return DataTypes.DOUBLE;
		}
		if (long.class == type || Long.class == type) {
			return DataTypes.LONG;
		}

		return DataTypes.STRING;
	}

//	public DataColumn(String columnName, DataColumn otherColumn) {
//		this(otherColumn.ColumnName, otherColumn.ColumnType, otherColumn.length);
//	}
	
	public int length() {
		return this.length;
	}

	/**
	 * 构造器
	 * 
	 * @param columnName 字段名
	 * @param columnType 数据类型
	 * @see DataTypes
	 */
	public DataColumn(String columnName, DataTypes columnType) {
		this(columnName, columnType, columnType.length());
	}
	
	public DataColumn(String columnName, int columnType) {
		this(columnName, DataTypes.valueOf(columnType));
	}
	
	/**
	 * 构造器
	 * 
	 * @param columnName 字段名
	 * @param columnType 数据类型
	 * @param allowNull 是否允许为空
	 * @see DataTypes
	 */
	public DataColumn(String columnName, DataTypes columnType, boolean allowNull) {// NO_UCD
		this.columnName = columnName;
		this.ColumnType = columnType;
		isAllowNull = allowNull;
		hash = CaseIgnoreMapx.caseIgnoreHash(columnName);
	}
	
	private DataColumn(String columnName, DataTypes columnType, boolean isPk, boolean isIndex) {
		this(columnName, columnType, columnType.length(), isPk, isIndex);
	}
	
	public DataColumn(String columnName, DataTypes columnType, int length) {
		this(columnName, columnType, length, false, false);
	}
	
	public DataColumn(String columnName, DataTypes columnType, int length, boolean isPk, boolean isIndex) {
		this.columnName = columnName;
		this.ColumnType = columnType;
		this.length = length;
		this.isPk = isPk;
		this.isIndex = isIndex;
		
		hash = CaseIgnoreMapx.caseIgnoreHash(columnName);
	}
	
	public boolean isPk() {
		return isPk;
	}
	
	public boolean isIndex() {
		return isIndex;
	}

//	private DataColumn(String columnName, DataTypes columnType, boolean allowNull) {
//		this(columnName, columnType);
//		this.isAllowNull = allowNull;
//	}

	/**
	 * @return 字段名称
	 */
	public String getColumnName() {
		return this.columnName;
	}

	/**
	 * 设置字段名称
	 * 
	 * @param columnName 字段名称
	 * @return 实例本身
	 */
	public DataColumn setColumnName(String columnName) {
		this.columnName = columnName;
		hash = CaseIgnoreMapx.caseIgnoreHash(columnName);
		return this;
	}

	/**
	 * @return 字段数据类型
	 */
	public DataTypes getColumnType() {
		return this.ColumnType;
	}

	/**
	 * 设置字段数据类型
	 * 
	 * @param columnType 字段数据类型
	 * @return 实例本身
	 */
	public DataColumn setColumnType(DataTypes columnType) {
		this.ColumnType = columnType;
		return this;
	}
	
	public DataColumn setColumnType(int columnType) {
		this.ColumnType = DataTypes.valueOf(columnType);
		return this;
	}

	/**
	 * @return 是否允许为空
	 */
	public boolean isAllowNull() {
		return this.isAllowNull;
	}

	/**
	 * 设置是否允许为空
	 * 
	 * @param isAllowNull 是否允许为空
	 * @return 实例本身
	 */
	public DataColumn setAllowNull(boolean isAllowNull) {
		this.isAllowNull = isAllowNull;
		return this;
	}

	/**
	 * @return 字段的日期格式
	 */
	public String getDateFormat() {
		return this.dateFormat;
	}

	/**
	 * 设置字段的日期格式，字段的日期格式会影响相应列的getString()方法的输出结果
	 * 
	 * @param dateFormat 日期格式
	 * @return 实例本身
	 */
	public DataColumn setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
		return this;
	}
	
	@Override
	public String toString() {
		return "ColumnName:" + getColumnName() + ",ColumnType:" + getColumnType() + ",isAllowNull:" + isAllowNull() + ",DateFormat:" + getDateFormat();
	}
	
	/**
	 * 输出成JSON字符串
	 */
	@Override
	public String toJSONString() {
		HashMap<String, Object> mapx = new HashMap<>();
		mapx.put("Name", columnName);
		mapx.put("Type", ColumnType.code());
		return JSON.toJSONString(mapx);
	}
}