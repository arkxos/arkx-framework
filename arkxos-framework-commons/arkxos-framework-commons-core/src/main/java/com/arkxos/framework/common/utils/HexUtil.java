package com.arkxos.framework.common.utils;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/9/12 20:20
 */
public class HexUtil {
    /**
     * UTF-8的三个字节的BOM
     */
    public static final byte[] BOM = new byte[] { (byte) 239, (byte) 187, (byte) 191 };
    /**
     * 十六进制字符
     */
    public static final char HexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String hexEncode(byte[] data) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = HexDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = HexDigits[0x0F & data[i]];
        }
        return new String(out);
    }

}
