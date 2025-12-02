package io.arkx.framework.security;

import java.security.SecureRandom;
import java.security.spec.AlgorithmParameterSpec;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;

/**
 * 描述: DES 操作<br>
 *
 * @date 2013-12-6
 */
public class DesUtil {

	/**
	 * 描述: DES 加密操作
	 */
	public static String DesEncrypt(String strMing, String strKey) {
		try {
			// 实例化密钥生成器
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
			keyGenerator.init(new SecureRandom(strKey.getBytes()));

			// 生成密钥
			SecretKey deskey = keyGenerator.generateKey();
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES");
			// 加密模式
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(strMing.getBytes("utf-8"));
			String strMi = new String(Base64.getEncoder().encode(byteFina));
			return strMi.replaceAll("\r\n", "");
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	/**
	 * 描述: DES 解密操作
	 */
	public static String DesDecrypt(String strMi, String strKey) {
		try {
			// 实例化密钥生成器
			KeyGenerator keyGenerator = KeyGenerator.getInstance("DES");
			keyGenerator.init(new SecureRandom(strKey.getBytes()));

			// 生成密钥
			SecretKey deskey = keyGenerator.generateKey();
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES");
			// 解密模式
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(Base64.getDecoder().decode(strMi.getBytes()));
			return new String(byteFina, "utf-8");
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	/**
	 * 描述: 三重 DES 加密操作
	 */
	public static String TripleDesEncrypt(String strMing, String strKey) {
		try {
			// 实例化密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// 生成秘钥
			SecretKey deskey = keyFactory.generateSecret(new DESKeySpec(strKey.getBytes()));
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES");
			// 加密模式
			cipher.init(Cipher.ENCRYPT_MODE, deskey);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(strMing.getBytes("utf-8"));
			String strMi = new String(Base64.getEncoder().encode(byteFina));
			return strMi.replaceAll("\r\n", "");
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	/**
	 * 描述: 三重 DES 带IV向量解密操作
	 */
	public static String TripleDesDecrypt(String strMi, String strKey) {
		try {
			// 实例化密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// 生成密钥
			SecretKey deskey = keyFactory.generateSecret(new DESKeySpec(strKey.getBytes()));
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES");
			// 设置工作模式为加密模式，给出密钥和向量
			cipher.init(Cipher.DECRYPT_MODE, deskey);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(Base64.getDecoder().decode(strMi.getBytes()));
			return new String(byteFina, "utf-8");
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	/**
	 * 描述: 三重 DES 带IV向量加密操作
	 */
	public static String TripleDesEncryptWithIv(String strMing, String strKey, String strIv) {
		try {
			// 加密算法的参数接口，IvParameterSpec是它的一个实现
			AlgorithmParameterSpec iv = new IvParameterSpec(strIv.getBytes());
			// 实例化密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// 生成秘钥
			SecretKey deskey = keyFactory.generateSecret(new DESKeySpec(strKey.getBytes()));
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			// 设置工作模式为加密模式，给出密钥和向量
			cipher.init(Cipher.ENCRYPT_MODE, deskey, iv);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(strMing.getBytes("utf-8"));
			String strMi = new String(Base64.getEncoder().encode(byteFina));
			return strMi.replaceAll("\r\n", "");
		}
		catch (Exception e) {
			e.printStackTrace();
			return e.toString();
		}
	}

	/**
	 * 描述: 三重 DES 带IV向量解密操作
	 */
	public static String TripleDesDecryptWithIv(String strMi, String strKey, String strIv) {
		try {
			// 加密算法的参数接口，IvParameterSpec是它的一个实现
			AlgorithmParameterSpec iv = new IvParameterSpec(strIv.getBytes());
			// 实例化密钥工厂
			SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");

			// 生成密钥
			SecretKey deskey = keyFactory.generateSecret(new DESKeySpec(strKey.getBytes()));
			// 加、解密处理
			Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
			// 解密模式
			cipher.init(Cipher.DECRYPT_MODE, deskey, iv);
			// 转成BASE64
			byte[] byteFina = cipher.doFinal(Base64.getDecoder().decode(strMi.getBytes()));
			return new String(byteFina, "utf-8");
		}
		catch (Exception e) {
			return e.toString();
		}
	}

	public static void main(String[] args) {

		String strTriple = DesUtil.TripleDesEncrypt("895423", "ABCDEFGH");

		System.out.println("3DES加密：" + strTriple);

		System.out.println("3DES 解密：" + DesUtil.TripleDesDecrypt(strTriple, "ABCDEFGH"));

		String strTripleDesEncryptWithIv = DesUtil.TripleDesEncryptWithIv("test", "ABCDEFGH", "ABCDEFGH");

		System.out.println("TripleDesEncryptWithIv 加密：" + strTripleDesEncryptWithIv);

		System.out.println("TripleDesEncryptWithIv 解密："
				+ DesUtil.TripleDesDecryptWithIv(strTripleDesEncryptWithIv, "ABCDEFGH", "ABCDEFGH"));
	}

}
