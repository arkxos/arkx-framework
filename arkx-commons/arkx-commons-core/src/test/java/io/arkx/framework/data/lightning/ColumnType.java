package io.arkx.framework.data.lightning;

import io.arkx.framework.commons.util.ByteUtil;

/**
 * @author Darkness
 * @date 2015年12月19日 下午4:46:43
 * @version V1.0
 * @since infinity 1.0
 */
public enum ColumnType {

	BIT((byte) 1, 1), CHAR((byte) 2, 2), SHORT((byte) 3, 2), INT((byte) 4, 4), LONG((byte) 5, 8), FLOAT((byte) 6, 4),
	DOUBLE((byte) 7, 8),

	FIXED_STRING((byte) 8, -1), STRING((byte) 9, -1),
	// BLOB(-1),
	// BIGDECIMAL(-1),
	// DECIMAL(-1),
	// CLOB(-1),
	DATE((byte) 10, 8), DATETIME((byte) 11, 8);

	private int length;

	private byte code;

	private ColumnType(byte code, int length) {
		this.code = code;
		this.length = length;
	}

	public int length() {
		return this.length;
	}

	public byte code() {
		return this.code;
	}

	public static ColumnType valueOf(byte code) {
		for (ColumnType dataColumnType : ColumnType.values()) {
			if (dataColumnType.code == code) {
				return dataColumnType;
			}
		}
		return null;
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
