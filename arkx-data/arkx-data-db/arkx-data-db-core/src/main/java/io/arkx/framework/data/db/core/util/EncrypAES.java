package io.arkx.framework.data.db.core.util;

import java.nio.charset.StandardCharsets;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @version V1.0
 * @desc AES 加密工具类
 */
public class EncrypAES {

    private static final Logger LOGGER = LoggerFactory.getLogger(EncrypAES.class);

    /* 密钥 */
    private static byte KEY = 0X63;

    private static final String KEY_ALGORITHM = "AES";

    private static final String DEFAULT_CIPHER_ALGORITHM = "AES/ECB/PKCS5Padding";// 默认的加密算法

    /**
     * 密钥
     */
    public static final String ENCRYPAESKEY = "AJGSDHREWBMQWESAD";

    public static String encrypt(String oldStr) {
        if (oldStr != null && oldStr.trim().length() > 0) {
            byte[] bytes = null;
            bytes = oldStr.getBytes(StandardCharsets.UTF_8);
            StringBuffer buffer = new StringBuffer();
            String tmpStr;
            if (bytes != null) {
                for (int i = 0; i < bytes.length; i++) {
                    // 加密
                    tmpStr = Integer.toString(bytes[i] & 0XFF ^ KEY, 16);
                    if (tmpStr.length() == 1) {
                        buffer.append("0").append(tmpStr);
                    } else {
                        buffer.append(tmpStr);
                    }
                }
            }
            return buffer.toString().toUpperCase();
        }
        return oldStr;
    }

    public static String decrypt(String hexStr) {
        String originStr = hexStr;
        try {
            // 判断待解密字符串是否合法，长度非偶不处理
            if (hexStr != null && hexStr.trim().length() > 0 && hexStr.length() % 2 == 0) {
                byte[] bytes = new byte[hexStr.length() / 2];
                for (int i = 0; i < hexStr.length(); i += 2) {
                    bytes[i / 2] = (byte) ((byte) Integer.parseInt(hexStr.substring(i, i + 2), 16) & 0XFF ^ KEY);
                }
                return new String(bytes, StandardCharsets.UTF_8);
            }
        } catch (NumberFormatException e) {
            return originStr;
        } catch (Exception e) {
            LOGGER.error(">>>>>>>>>>>>>>加密字符串解密异常，返回源字符串", e);
            return originStr;
        }

        return hexStr;
    }

}
