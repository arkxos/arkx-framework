package io.arkx.framework.commons.util;

import java.io.File;
import java.security.MessageDigest;
import java.util.Base64;
import java.util.Base64.Decoder;

/**
 * @class org.ark.framework.utility.MD5Util
 * @author Darkness
 * @date 2012-11-29 下午05:38:21
 * @version V1.0
 */
public class MD5Util {

	/**
	 * 将明文md5散列后以Base64方式序列化
	 * @param clearText 明文字符串
	 * @return 加密后的字符串
	 */
	public static String getCryptogram(String clearText) {
		String encryptedPassword = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(clearText.getBytes());
			encryptedPassword = new String(Base64.getEncoder().encode(digest.digest()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedPassword;
	}

	public static String getCryptogram(byte[] bytes) {
		String encryptedPassword = null;
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(bytes);
			encryptedPassword = new String(Base64.getEncoder().encode(digest.digest()));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return encryptedPassword;
	}

	/**
	 * 判断明文和密文是否相符
	 * @param clearText 明文
	 * @param cryptogram 密文
	 * @return 检查结果
	 */
	public static boolean isSame(String clearText, String cryptogram) {
		boolean flag = false;
		if (clearText == null && cryptogram == null) {
			return true;
		}
		try {
			MessageDigest digest = MessageDigest.getInstance("MD5");
			digest.update(clearText.getBytes());
			Decoder decoder = Base64.getDecoder();
			flag = MessageDigest.isEqual(digest.digest(), decoder.decode(cryptogram));
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return flag;
	}

	public static void main(String[] args) {
		String md5 = getCryptogram(FileUtil
			.readByte(new File("D:\\git\\rapid-ark-v2\\rapid-ark-platform\\target\\arkxos-platform-8.0.0.jar")));
		System.out.println(md5);
	}

}
