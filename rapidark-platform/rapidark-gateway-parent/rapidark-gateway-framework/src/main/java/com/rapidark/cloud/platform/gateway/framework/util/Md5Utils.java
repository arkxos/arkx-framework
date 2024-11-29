package com.rapidark.cloud.platform.gateway.framework.util;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

/**
 * @Description MD5加密方法
 * @author JL
 * @version V1.0
 */
public class Md5Utils {

	private final static String CHARSET_NAME = "UTF-8";

	private Md5Utils(){
	}

	/**
	 * @Description md5加密
	 * @param source
	 * @return
	 */
	public static String md5Str(String source) {
		return md5Str(source, CHARSET_NAME);
	}

	/**
	 * @Description md5加密
	 * @param source
	 * @param charset
	 * @return
	 */
	public static String md5Str(String source, String charset) {
		int number ;
		MessageDigest md ;
		StringBuffer md5Str = new StringBuffer();
		try {
			md = MessageDigest.getInstance("MD5");
			byte [] bs = md.digest(source.getBytes(charset));
			for (byte b : bs) {
				number = b & 0xff;
				if (number < 16) {
					md5Str.append(0);
				}
				md5Str.append(Integer.toHexString(number));
			}
		}catch(Exception e) {
			e.printStackTrace();
			return "";
		}
		return md5Str.toString();
	}

	private static final int HEX_VALUE_COUNT = 16;

	public static String getMD5(byte[] bytes) {
		char[] hexDigits = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
		char[] str = new char[32];

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(bytes);
			byte[] tmp = md.digest();
			int k = 0;

			for(int i = 0; i < 16; ++i) {
				byte byte0 = tmp[i];
				str[k++] = hexDigits[byte0 >>> 4 & 15];
				str[k++] = hexDigits[byte0 & 15];
			}
		} catch (Exception var8) {
			var8.printStackTrace();
		}

		return new String(str);
	}

	public static String getMD5(String value, String encode) {
		String result = "";

		try {
			result = getMD5(value.getBytes(encode));
		} catch (UnsupportedEncodingException var4) {
			var4.printStackTrace();
		}

		return result;
	}
}
