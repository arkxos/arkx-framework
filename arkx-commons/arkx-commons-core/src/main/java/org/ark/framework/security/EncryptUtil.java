package org.ark.framework.security;

import javax.crypto.spec.SecretKeySpec;

import io.arkx.framework.commons.util.StringUtil;

/**
 * @class org.ark.framework.security.EncryptUtil
 * @author Darkness
 * @date 2013-1-31 下午12:24:22
 * @version V1.0
 */
public class EncryptUtil {

    public static final String DEFAULT_KEY = "27jrWz3sxrVbR+pnyg6j";

    public static String encrypt3DES(String str, String password) {
        String strResult = null;
        try {
            byte[] key = password.getBytes();
            byte[] encodeString = str.getBytes();
            SecretKeySpec skeySpec = new SecretKeySpec(key, "DESede");
            // Z3DESCipher cipher = new Z3DESCipher();
            // cipher.init(1, skeySpec);
            byte[] cipherByte = null;// cipher.doFinal(encodeString);
            strResult = StringUtil.base64Encode(cipherByte);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return strResult;
    }

    public static String decrypt3DES(String srcStr, String password) {
        String strResult = null;
        try {
            byte[] key = password.getBytes();
            byte[] src = StringUtil.base64Decode(srcStr);
            SecretKeySpec skeySpec = new SecretKeySpec(key, "DESede");
            // Z3DESCipher cipher = new Z3DESCipher();
            // cipher.init(2, skeySpec);
            byte[] cipherByte = null;// cipher.doFinal(src);
            strResult = new String(cipherByte);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return strResult;
    }

    public static void main(String[] args) {
        System.out.println(encrypt3DES("TEST", "27jrWz3sxrVbR+pnyg6j"));
        System.out.println(decrypt3DES(encrypt3DES("TEST", "27jrWz3sxrVbR+pnyg6j"), "27jrWz3sxrVbR+pnyg6j"));
    }

}
