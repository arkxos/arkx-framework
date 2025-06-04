package com.arkxos.framework.commons.util;

import java.nio.charset.Charset;

/**
 * 在Java中字节与十六进制的相互转换主要思想有两点：
	1、二进制字节转十六进制时，将字节高位与0xF0做"&"操作,然后再左移4位，得到字节高位的十六进制A;将字节低位与0x0F做"&"操作，得到低位的十六进制B，将两个十六进制数拼装到一块AB就是该字节的十六进制表示。
	2、十六进制转二进制字节时，将十六进制字符对应的十进制数字右移动4为，得到字节高位A;将字节低位的十六进制字符对应的十进制数字B与A做"|"运算，即可得到十六进制的二进制字节表示
 * 
 * @author Darkness
 * @date 2015-9-11 上午10:56:47 
 * @version V1.0   
 */
public class ByteUtil {
	
	public static byte[] getBytes(short data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		return bytes;
	}

	public static byte[] getBytes(char data) {
		byte[] bytes = new byte[2];
		bytes[0] = (byte) (data);
		bytes[1] = (byte) (data >> 8);
		return bytes;
	}

	public static byte[] getBytes(int data) {
		byte[] bytes = new byte[4];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data & 0xff00) >> 8);
		bytes[2] = (byte) ((data & 0xff0000) >> 16);
		bytes[3] = (byte) ((data & 0xff000000) >> 24);
		return bytes;
	}

	public static byte[] getBytes(long data) {
		byte[] bytes = new byte[8];
		bytes[0] = (byte) (data & 0xff);
		bytes[1] = (byte) ((data >> 8) & 0xff);
		bytes[2] = (byte) ((data >> 16) & 0xff);
		bytes[3] = (byte) ((data >> 24) & 0xff);
		bytes[4] = (byte) ((data >> 32) & 0xff);
		bytes[5] = (byte) ((data >> 40) & 0xff);
		bytes[6] = (byte) ((data >> 48) & 0xff);
		bytes[7] = (byte) ((data >> 56) & 0xff);
		return bytes;
	}

	public static byte[] getBytes(float data) {
		int intBits = Float.floatToIntBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(double data) {
		long intBits = Double.doubleToLongBits(data);
		return getBytes(intBits);
	}

	public static byte[] getBytes(String data, String charsetName) {
		Charset charset = Charset.forName(charsetName);
		return data.getBytes(charset);
	}

	public static byte[] getBytes(String data) {
		return getBytes(data, "GBK");
	}

	public static short getShort(byte[] bytes) {
		return (short) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	public static char getChar(byte[] bytes) {
		return (char) ((0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)));
	}

	public static int getInt(byte[] bytes) {
		return (0xff & bytes[0]) | (0xff00 & (bytes[1] << 8)) | (0xff0000 & (bytes[2] << 16)) | (0xff000000 & (bytes[3] << 24));
	}

	public static long getLong(byte[] bytes) {
		return (0xffL & (long) bytes[0]) | (0xff00L & ((long) bytes[1] << 8)) | (0xff0000L & ((long) bytes[2] << 16)) | (0xff000000L & ((long) bytes[3] << 24)) | (0xff00000000L & ((long) bytes[4] << 32)) | (0xff0000000000L & ((long) bytes[5] << 40)) | (0xff000000000000L & ((long) bytes[6] << 48)) | (0xff00000000000000L & ((long) bytes[7] << 56));
	}

	public static float getFloat(byte[] bytes) {
		return Float.intBitsToFloat(getInt(bytes));
	}

	public static double getDouble(byte[] bytes) {
		long l = getLong(bytes);
		return Double.longBitsToDouble(l);
	}

	public static String getString(byte[] bytes, String charsetName) {
		return new String(bytes, Charset.forName(charsetName));
	}

	public static String getString(byte[] bytes) {
		return getString(bytes, "GBK");
	}

	private static String hexStr =  "0123456789ABCDEF";
	
	private static String[] binaryArray = {
								"0000","0001","0010","0011",
								"0100","0101","0110","0111",
								"1000","1001","1010","1011",
								"1100","1101","1110","1111"};
		
	/**
	 * 
	 * @param str
	 * @return 转换为二进制字符串
	 */
	public static String bytes2BinaryStr(byte... bArray) {

		String outStr = "";
		int pos = 0;
		for (byte b : bArray) {
			// 高四位
			pos = (b & 0xF0) >> 4;
			outStr += binaryArray[pos];
			// 低四位
			pos = b & 0x0F;
			outStr += binaryArray[pos];
			outStr += " ";
		}
		return outStr;
	}
	
	/**
	 * 在Java中字节与十六进制的相互转换主要思想有两点：
		1、二进制字节转十六进制时，将字节高位与0xF0做"&"操作,然后再左移4位，得到字节高位的十六进制A;将字节低位与0x0F做"&"操作，得到低位的十六进制B，将两个十六进制数拼装到一块AB就是该字节的十六进制表示。
		2、十六进制转二进制字节时，将十六进制字符对应的十进制数字右移动4为，得到字节高位A;将字节低位的十六进制字符对应的十进制数字B与A做"|"运算，即可得到十六进制的二进制字节表示
	 */
	/**
	 * 
	 * @param bytes
	 * @return 将二进制转换为十六进制字符输出
	 */
	public static String bytes2HexString(byte... bytes) {
		String result = "";
		if(bytes == null) {
			return "";
		}
		
		String hex = "";
		for (int i = 0; i < bytes.length; i++) {
			// 字节高4位
			hex = String.valueOf(hexStr.charAt((bytes[i] & 0xF0) >> 4));
			// 字节低4位
			hex += String.valueOf(hexStr.charAt(bytes[i] & 0x0F));
			result += hex;
			if(i!=bytes.length-1){
				result += " ";
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param hexString
	 * @return 将十六进制转换为字节数组
	 */
	public static byte[] hexString2Bytes(String hexString) {
		hexString = hexString.toUpperCase();
		hexString = hexString.replaceAll(" ", "");
		// hexString的长度对2取整，作为bytes的长度
		int len = hexString.length() / 2;
		byte[] bytes = new byte[len];
		byte high = 0;// 字节高四位
		byte low = 0;// 字节低四位

		for (int i = 0; i < len; i++) {
			// 右移四位得到高位
			high = (byte) ((hexStr.indexOf(hexString.charAt(2 * i))) << 4);
			low = (byte) hexStr.indexOf(hexString.charAt(2 * i + 1));
			bytes[i] = (byte) (high | low);// 高地位做或运算
		}
		return bytes;
	}
	
	public static boolean match(byte byteValue, String hexByteStr) {
		byte v = Integer.valueOf(hexByteStr, 16).byteValue();  
		return v == byteValue;
	}
	
	public static String toBinaryString(byte tByte) {
		String tString = Integer.toBinaryString((tByte & 0xFF) + 0x100).substring(1);  
		return tString;
	}
	
	public static void maind(String[] args) {
//		String str = "二进制与十六进制互转测试";
//		System.out.println("源字符串：\n"+str);
//		
//		String hexString = bytes2HexString(str.getBytes());
//		System.out.println("转换为十六进制：\n"+hexString);
//		System.out.println("转换为二进制：\n"+bytes2BinaryStr(str.getBytes()));
//		
//		byte [] bArray = hexString2Bytes(hexString);
//		System.out.println("将str的十六进制文件转换为二进制再转为String：\n"+new String(bArray));
//		
		
		String hexString = "01 05 00 01 FF 00 DD FA";
		byte[] bytes = hexString2Bytes(hexString);
		System.out.println(bytes2HexString(bytes));
	}
	public static void main(String[] args) {
		short s = 122;
		int i = 122;
		long l = 1222222;

		char c = 'a';

		float f = 122.22f;
		double d = 122.22;

		String string = "我是好孩子";
		System.out.println(s);
		System.out.println(i);
		System.out.println(l);
		System.out.println(c);
		System.out.println(f);
		System.out.println(d);
		System.out.println(string);

		System.out.println("**************");

//		System.out.println(getShort(getBytes(s)));
//		System.out.println(getInt(getBytes(i)));
//		System.out.println(getLong(getBytes(l)));
//		System.out.println(getChar(getBytes(c)));
//		System.out.println(getFloat(getBytes(f)));
//		System.out.println(getDouble(getBytes(d)));
//		System.out.println(getString(getBytes(string)));
		
		String data = "EC-98-4D-6B-24-45-18-80-19-FC-23-A1-CF-49-53-DF-5D-3D-D0-07-11-05-EF-1E-04-91-A6-D2-D3-49-7A-33-D3-1D-AA-BB-31-61-C9-C1-C3-AA-28-71-3D-2C-82-5E-14-2F-2A-B8-BB-82-CA-86-45-FD-35-3B-EE-E4-5F-58-55-5D-D3-5F-D3-E3-CE-B2-B3-D9-28-0E-21-90-B7-DE-7A-BF-DF-27-DD-F3-DA-41-72-CA-83-B7-DE-7E-F7-1D-17-BA-70-E4-07-94-72-3E-C2-34-50-BF-48-00-46-C4-0F-A2-2C-4D-DD-E8-24-DF-3B-BA-35-BD-75-C6-38-0E-91-07-38-76-D5-CD-11-45-01-02-D0-03-3E-C4-7B-10-8F-21-1B-43-E4-12-0F-8F-28-FB-C7-7B-10-06-3E-1D-51-1E-DC-76-8A-AC-10-53-67-4C-76-9D-89-28-84-33-7E-EF-B6-23-E3-99-90-C7-CE-D8-59-3C-FC-70-FE-C9-E5-FC-E2-CB-F9-C7-8F-9D-5D-27-2F-44-3A-11-72-12-1E-08-19-03-75-8E-89-EB-23-75-30-CB-D2-F8-2C-2C-CE-4E-62-25-04-4A-B0-2F-B3-E3-58-8A-43-F3-B7-0B-B4-E8-24-CB-93-22-C9-D2-30-2F-A4-96-56-71-03-0A-A0-FA-01-E6-43-30-F2-B4-9B-58-26-62-1A-A6-99-52-B3-A2-FD-32-4F-D2-38-CF-C3-83-A9-38-D4-62-00-A0-12-C7-69-21-CB-BC-08-3F-10-67-4A-48-DB-8A-45-32-8B-73-25-44-26-EE-2C-3A-6E-87-27-E3-93-4C-16-95-07-88-3C-93-43-34-4D-94-B9-30-99-68-59-15-12-26-10-60-ED-E6-34-3A-12-E9-61-6C-F2-66-4D-4A-4B-F7-AA-70-DA-F2-32-A3-FE-0D-6C-6E-20-D2-97-A3-35-72-DA-F2-D0-96-93-46-9E-C7-C2-86-8F-D1-40-8C-50-E7-00-5D-46-DA-15-11-B3-AC-4C-0B-75-B2-C7-C0-90-79-60-CC-53-5D-EE-A8-94-D2-54-50-DB-E1-D4-A7-BE-12-6A-9D-D3-26-84-4E-A5-B5-1E-A1-10-FA-ED-83-AA-BC-D0-83-1E-F3-58-35-01-45-B8-2F-A6-22-8D-CC-05-C8-18-74-29-EF-D8-6A-7A-94-45-2A-8A-AE-3A-C2-2E-66-6D-F5-13-99-54-67-BE-4B-40-73-69-20-D3-68-1A-8B-75-D6-E2-C3-24-0D-93-3C-2F-63-13-F1-4E-AD-5E-8F-B9-EB-77-9A-6A-A3-84-F5-68-45-D9-44-0B-18-C0-2A-55-1D-86-9C-C4-D2-8E-92-1D-2F-93-7C-1D-18-64-FE-60-0F-CC-05-97-E9-32-26-69-32-34-59-75-EE-95-F5-A6-BA-F5-81-DA-D2-A2-CC-6D-15-0F-CA-74-12-8A-28-B2-7E-B9-8F-7C-9F-73-DB-4D-D4-74-B3-99-D3-6A-BE-4C-5A-CD-BD-D7-99-DA-05-4E-B9-07-AD-AE-2E-0B-6D-EB-A6-C2-4C-C1-93-CB-FB-57-3F-FE-F4-E4-F7-6F-E7-9F-FE-D0-2C-95-3D-5C-3C-B8-9C-3F-BE-77-F5-E7-57-D6-48-7B-4C-CB-34-CA-66-F1-72-BF-2B-86-A8-5E-1D-D9-29-A2-D5-BE-2F-6B-25-AB-BA-E8-68-AB-4F-6B-1B-F7-97-6B-6F-B7-7C-96-4C-A7-9D-19-25-DC-EB-D7-7D-68-AE-97-41-77-19-58-1F-77-66-89-F4-88-D0-21-8B-8D-A3-BF-27-BA-B7-CA-4A-74-DC-6E-F0-F9-6E-0B-BF-3B-8B-DF-1E-CD-BF-FF-EC-EA-CE-C5-FC-EE-A3-B1-22-92-BA-4B-31-C2-B6-EC-3D-18-DB-10-5E-80-C5-AC-62-B1-72-84-10-63-08-F4-58-DC-96-AF-02-19-B5-F3-AF-80-BC-33-04-E4-66-64-6C-8C-6F-F6-80-0C-B6-09-63-36-0C-E3-D5-CD-43-6B-E4-DB-81-71-7F-B6-6A-10-E0-F5-24-AE-B8-D5-22-31-82-10-6D-42-E2-5A-AF-4B-62-30-80-E0-B5-04-7E-63-80-C0-7D-77-4B-FA-9A-2D-5C-85-2F-5E-83-DE-A5-99-67-62-B7-5F-9A-75-D8-F5-30-53-85-EF-61-97-79-9C-AC-60-77-0D-72-AD-78-80-B8-1D-B0-1A-C3-9D-99-BE-36-D4-82-01-D4-FE-F5-DD-9D-C5-1F-0F-2A-38-6C-84-5A-6B-64-CB-A4-85-83-A4-AD-67-70-53-CC-3E-BD-F7-CB-E2-D7-FB-75-36-03-98-1D-44-AC-6D-73-17-B1-CA-F9-30-5F-D9-33-F8-0A-3C-48-D5-BF-07-84-5F-32-5F-21-62-84-03-BE-C2-D7-46-FE-3F-5F-B7-C3-57-FE-5C-78-65-C4-DB-08-AF-95-DE-AB-C5-2B-BF-3E-BA-72-88-57-E9-8A-FF-DB-74-9D-7F-FE-CD-FC-E7-AF-6F-2E-5D-AB-11-7C-F9-74-C5-5B-A4-2B-F1-88-C7-B9-79-C3-DC-3E-5D-21-AA-9F-5E-B1-F2-63-DE-A1-7A-4F-AF-B5-FC-5F-42-57-88-6E-38-5D-E1-73-3E-BD-9A-EF-3A-36-78-7A-45-9D-2F-2E-5E-09-5E-97-A9-5D-07-5F-7D-D6-E3-2B-C7-E6-15-F5-C5-F0-6A-A6-E7-A6-E2-F5-E9-17-1F-2D-2E-1E-DE-5C-BC-A2-D5-AF-E5-B6-8E-57-DB-E5-CD-E8-AA-BA-79-FE-BE-2A-62-19-45-CA-B2-33-56-76-62-65-4F-CA-4C-1A-6B-40-1B-2A-4A-99-DA-B9-02-E7-23-CA-71-40-46-10-04-10-F8-A3-BF-01-00-00-FF-FF-00";
		System.out.println(new String(hexString2Bytes(data.replaceAll("-", " "))));
	}
}