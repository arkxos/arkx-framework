package io.arkx.framework.data.db.core.util;

import org.apache.commons.lang3.StringUtils;

/**
 * 字符串处理工具类
 */
public final class EmptyUtils {

    /**
     * 处理字符串：空字符串或 "undefined" 返回 null，去掉 [ 和 ]
     *
     * @param input
     *            输入字符串
     * @return 处理后的字符串或 null
     */
    public static Long removeSpace(Object input) {

        if ("".equals(input) || "undefined".equals(input)) {
            return null;
        }
        String a = input.toString();
        if (StringUtils.isNoneBlank(a) && a.matches(".*\\d.*")) {
            if (a.contains("[") || a.contains("]")) {
                a = a.replace("[", "").replace("]", "").replace(" ", "");
            }
        }

        return Long.parseLong(a);

    }

}
