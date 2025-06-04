package io.arkx.framework.data.fasttable;

import io.arkx.framework.commons.util.ByteUtil;

public enum FastColumnType {
	
	Byte((byte)1,BufferReader.BYTE_LENGTH),
	Char((byte)2, 2),
	Short((byte)3, 2),
	Int((byte)4,BufferReader.INT_LENGTH),
	Long((byte)5,BufferReader.LONG_LENGTH),
	Float((byte)6,BufferReader.FLOAT_LENGTH),
	Double((byte)7,BufferReader.DOUBLE_LENGTH),
	Boolean((byte)8,BufferReader.BYTE_LENGTH),
	
	FixedString((byte)9,-1),
	String((byte)10,-1),

	Date((byte)11,BufferReader.LONG_LENGTH),
	DateTime((byte)12,BufferReader.LONG_LENGTH);
	
	private int length;
	private byte code;
	
	private FastColumnType(byte code, int length) {
		this.code = code;
		this.length = length;
	}
	
	public int length() {
		return this.length;
	}
	
	public byte code() {
		return this.code;
	}
	
	public static FastColumnType valueOf(byte code) {
		for (FastColumnType dataColumnType : FastColumnType.values()) {
			if(dataColumnType.code == code) {
				return dataColumnType;
			}
		}
		return null;
	}
	
	public static void main(String[] args) {
		byte b = 15;
		System.out.println(b);
		System.out.println(ByteUtil.getBytes((short)1).length);
		System.out.println(ByteUtil.getBytes('A').length);
		System.out.println(ByteUtil.getBytes(1).length);
		System.out.println(ByteUtil.getBytes(1L).length);
		System.out.println(ByteUtil.getBytes(1.1F).length);
		System.out.println(ByteUtil.getBytes(1.1D).length);
	}

	
}
