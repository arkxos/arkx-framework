package io.arkx.framework.data.lightning;

import java.io.Serializable;
import java.util.Date;

public class LightningColumn implements Serializable, Cloneable {

	private static final long serialVersionUID = 1L;
	
	public static LightningColumn stringColumn(String columnName, int length) {
		return new LightningColumn(columnName, LightningColumnType.STRING, length);
	}
	
	public static LightningColumn fixedStringColumn(String columnName, int length) {
		return new LightningColumn(columnName, LightningColumnType.FIXED_STRING, length);
	}
	
	public static LightningColumn intColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.INT);
	}
	
	public static LightningColumn longColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.LONG);
	}
	
	public static LightningColumn floatColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.FLOAT);
	}
	
	public static LightningColumn doubleColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.DOUBLE);
	}
	
	public static LightningColumn dateColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.DATE);
	}
	
	public static LightningColumn dateTimeColumn(String columnName) {
		return new LightningColumn(columnName, LightningColumnType.DATETIME);
	}
	
	public static LightningColumn createNstringColumn(String columnName, int length) {
		return new LightningColumn(columnName, LightningColumnType.STRING, length);
	}
	
	protected String ColumnName;
	public int length;
	protected LightningColumnType ColumnType;
	protected boolean isAllowNull = true;

	protected String dateFormat = null;

	/**
	 * 将java类型转换成LightningColumn 的类型
	 * 
	 * @author Darkness
	 * @date 2012-11-26 下午03:08:13 
	 * @version V1.0
	 */
	public static LightningColumnType convertType(Class<?> type) {
		if (String.class == type) {
			return LightningColumnType.STRING;
		}
		if (Date.class == type) {
			return LightningColumnType.DATETIME;
		}
		if (float.class == type || Float.class == type) {
			return LightningColumnType.FLOAT;
		}
		if (double.class == type || Double.class == type) {
			return LightningColumnType.DOUBLE;
		}
		if (long.class == type || Long.class == type) {
			return LightningColumnType.LONG;
		}

		return LightningColumnType.STRING;
	}

//	public LightningColumn(String columnName, LightningColumn otherColumn) {
//		this(otherColumn.ColumnName, otherColumn.ColumnType, otherColumn.length);
//	}
	
	public int length() {
		return this.length;
	}

	public Object clone() {
		return new LightningColumn(this.ColumnName, this.ColumnType, this.length);
	}

	private LightningColumn(String columnName, LightningColumnType columnType) {
		this(columnName, columnType, columnType.length());
	}
	
	public LightningColumn(String columnName, LightningColumnType columnType, int length) {
		this.ColumnName = columnName;
		this.ColumnType = columnType;
		this.length = length;
	}

	private LightningColumn(String columnName, LightningColumnType columnType, boolean allowNull) {
		this(columnName, columnType);
		this.isAllowNull = allowNull;
	}

	public String getColumnName() {
		return this.ColumnName;
	}

	public void setColumnName(String columnName) {
		this.ColumnName = columnName;
	}

	public LightningColumnType getColumnType() {
		return this.ColumnType;
	}

	public void setColumnType(LightningColumnType columnType) {
		this.ColumnType = columnType;
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
	
	@Override
	public String toString() {
		return "ColumnName:" + getColumnName() + ",ColumnType:" + getColumnType() + ",isAllowNull:" + isAllowNull() + ",DateFormat:" + getDateFormat();
	}
}
