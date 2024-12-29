package com.arkxos.framework.data.fasttable;

import java.io.Serializable;
import java.util.Date;

public class FastColumn implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public static FastColumn stringColumn(String columnName, int length) {
		return new FastColumn(columnName, FastColumnType.String, length);
	}
	
	public static FastColumn fixedStringColumn(String columnName, int length) {
		return new FastColumn(columnName, FastColumnType.FixedString, length);
	}
	
	public static FastColumn intColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.Int);
	}
	
	public static FastColumn longColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.Long);
	}
	
	public static FastColumn floatColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.Float);
	}
	
	public static FastColumn doubleColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.Double);
	}
	
	public static FastColumn dateColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.Date);
	}
	
	public static FastColumn dateTimeColumn(String columnName) {
		return new FastColumn(columnName, FastColumnType.DateTime);
	}
	
	/**
	 * 将java类型转换成LightningColumn 的类型
	 * 
	 * @author Darkness
	 * @date 2012-11-26 下午03:08:13 
	 * @version V1.0
	 */
	public static FastColumnType convertType(Class<?> type) {
		if (String.class == type) {
			return FastColumnType.String;
		}
		if (Date.class == type) {
			return FastColumnType.DateTime;
		}
		if (float.class == type || Float.class == type) {
			return FastColumnType.Float;
		}
		if (double.class == type || Double.class == type) {
			return FastColumnType.Double;
		}
		if (long.class == type || Long.class == type) {
			return FastColumnType.Long;
		}

		return FastColumnType.String;
	}
	
	private String name;
	private int length;
	private FastColumnType type;
	private String description;
	
	private FastColumnIndexType indexType;
	protected boolean isAllowNull = true;
	protected String dateFormat = null;

	public FastColumn(String columnName, FastColumnType columnType) {
		this(columnName, columnType, columnType.length());
	}
	
	public FastColumn(String columnName, FastColumnType columnType, int length) {
		this(columnName, columnType, length, FastColumnIndexType.Normal);
	}
	
	public FastColumn(String columnName, FastColumnType columnType, int length, FastColumnIndexType indexType) {
		this.name = columnName;
		this.type = columnType;
		this.length = length;
		this.indexType = indexType;
	}
	
	public FastColumn(String columnName, FastColumnType columnType, boolean allowNull) {
		this(columnName, columnType);
		this.isAllowNull = allowNull;
	}
	
	public Object clone() {
		return new FastColumn(this.name, this.type, this.length);
	}
	
	public FastColumnIndexType getIndexType() {
		return indexType;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String columnName) {
		this.name = columnName;
	}
	
	public int getLength() {
		return this.length;
	}

	public FastColumnType getType() {
		return this.type;
	}

	public void setType(FastColumnType columnType) {
		this.type = columnType;
	}

	public boolean isAllowNull() {
		return this.isAllowNull;
	}

	public void setAllowNull(boolean isAllowNull) {
		this.isAllowNull = isAllowNull;
	}

	public String getDateFormat() {
		return this.dateFormat;
	}

	public void setDateFormat(String dateFormat) {
		this.dateFormat = dateFormat;
	}
	
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	public String toString() {
		return "ColumnName:" + getName() + ",ColumnType:" + getType() + ",isAllowNull:" + isAllowNull() + ",DateFormat:" + getDateFormat();
	}
}
