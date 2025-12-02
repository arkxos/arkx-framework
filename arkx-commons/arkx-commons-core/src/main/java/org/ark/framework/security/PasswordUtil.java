package org.ark.framework.security;

import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.StringUtil;

/**
 * @class org.ark.framework.security.PasswordUtil 密码工具类
 *
 * @author Darkness
 * @date 2012-12-16 下午07:24:11
 * @version V1.0
 */
public class PasswordUtil {

    /**
     * 加密密码
     *
     * @author Darkness
     * @date 2012-12-16 下午07:24:20
     * @version V1.0
     */
    public static String generate(String password) {
        String salt = StringUtil.leftPad(String.valueOf(NumberUtil.getRandomInt(99999999)), '0', 8)
                + StringUtil.leftPad(String.valueOf(NumberUtil.getRandomInt(99999999)), '0', 8);
        password = StringUtil.md5Hex(password + salt);
        char[] cs1 = password.toCharArray();
        char[] cs2 = salt.toCharArray();
        char[] cs = new char[48];
        for (int i = 0; i < 48; i += 3) {
            cs[i] = cs1[(i / 3 * 2)];
            cs[(i + 1)] = cs2[(i / 3)];
            cs[(i + 2)] = cs1[(i / 3 * 2 + 1)];
        }
        return new String(cs);
    }

    /**
     * 判断密码跟加密字符密码是否相等
     *
     * @param password
     *            原始密码
     * @param md5
     *            加密后的md5
     * @return 如果password跟加密后的md5相同，返回true
     *
     * @author Darkness
     * @date 2012-12-16 下午07:24:31
     * @version V1.0
     */
    public static boolean verify(String password, String md5) {
        char[] cs = md5.toCharArray();
        char[] cs1 = new char[32];
        char[] cs2 = new char[16];
        for (int i = 0; i < 48; i += 3) {
            cs1[(i / 3 * 2)] = cs[i];
            cs1[(i / 3 * 2 + 1)] = cs[(i + 2)];
            cs2[(i / 3)] = cs[(i + 1)];
        }
        String salt = new String(cs2);
        return StringUtil.md5Hex(password + salt).equals(new String(cs1));
    }

}
