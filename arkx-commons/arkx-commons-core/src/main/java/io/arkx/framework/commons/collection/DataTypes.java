package io.arkx.framework.commons.collection;

import io.arkx.framework.commons.util.ByteUtil;

/**
 * 数据类型，是对各个数据中数据类型的抽象。
 *
 */
public enum DataTypes {

	/**
	 * Varchar
	 */
	STRING((byte) 1, -1),
	/**
	 * Blob
	 */
	BLOB((byte) 2, -1),
	/**
	 * BigDecimal
	 */
	BIGDECIMAL((byte) 3, -1),
	/**
	 * Decimal
	 */
	DECIMAL((byte) 4, -1),
	/**
	 * 浮点型
	 */
	FLOAT((byte) 5, 4),
	/**
	 * 双字节浮点型
	 */
	DOUBLE((byte) 6, 8),
	/**
	 * 长整型
	 */
	LONG((byte) 7, 8),
	/**
	 * 整型
	 */
	INTEGER((byte) 8, 4),
	/**
	 * 小整型
	 */
	SMALLINT((byte) 9, -1),
	/**
	 * Clob
	 */
	CLOB((byte) 10, -1),
	/**
	 * Bit
	 */
	BIT((byte) 11, 1),
	/**
	 * 日期时间
	 */
	DATETIME((byte) 12, 8),
	/**
	 * 对象
	 */
	OBJECT((byte) 13, -1),
	/**
	 * 日期时间
	 */
	DATE((byte) 14, 8),
	/**
	 * 日期时间
	 */
	FIXED_STRING((byte) 15, -1),
	/**
	 * 日期时间
	 */
	CHAR((byte) 16, 2),
	/**
	 * 日期时间
	 */
	SHORT((byte) 17, 2);

	private DataTypes() {
	}

	/**
	 * 输出成字符串
	 */
	public static String toString(int type) {
		DataTypes dataType = valueOf((byte) type);
		switch (dataType) {
			case DATETIME:
				return "DateTime";
			case STRING:
				return "String";
			case BLOB:
				return "BLOB";
			case BIGDECIMAL:
				return "BigDecimal";
			case DECIMAL:
				return "Decimal";
			case FLOAT:
				return "Float";
			case DOUBLE:
				return "Double";
			case LONG:
				return "Long";
			case INTEGER:
				return "Integer";
			case SMALLINT:
				return "SmallInt";
			case CLOB:
				return "Clob";
			case BIT:
				return "Bit";
			default:
				return "Unknown";
		}
	}

	private int length;

	private byte code;

	private DataTypes(byte code, int length) {
		this.code = code;
		this.length = length;
	}

	public int length() {
		return this.length;
	}

	public byte code() {
		return this.code;
	}

	public static DataTypes valueOf(int code) {
		return valueOf((byte) code);
	}

	public static DataTypes valueOf(byte code) {
		for (DataTypes dataColumnType : DataTypes.values()) {
			if (dataColumnType.code == code) {
				return dataColumnType;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return code() + "";
	}

	public static void main(String[] args) {
		byte b = 15;
		System.out.println(b);
		System.out.println(ByteUtil.getBytes((short) 1).length);
		System.out.println(ByteUtil.getBytes('A').length);
		System.out.println(ByteUtil.getBytes(1).length);
		System.out.println(ByteUtil.getBytes(1L).length);
		System.out.println(ByteUtil.getBytes(1.1F).length);
		System.out.println(ByteUtil.getBytes(1.1D).length);
	}

}
