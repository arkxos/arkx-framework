package io.arkx.framework.data.jdbc;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:05
 * @since 1.0
 */
import java.util.Date;
import java.util.List;

import io.arkx.framework.commons.util.DateUtil;

/**
 * SQL格式化工具类 用于将SQL语句和参数格式化为可执行的SQL字符串，主要用于日志记录和异常调试
 */
public class SQLFormatter {

    /**
     * 格式化SQL语句，将参数替换到SQL中，生成可直接执行的SQL语句
     *
     * @param sql
     *            SQL语句，使用?作为参数占位符
     * @param params
     *            参数列表
     * @return 格式化后的可执行SQL语句
     */
    public static String format(String sql, List<Object> params) {
        if (params == null || params.isEmpty()) {
            return sql;
        }

        StringBuilder result = new StringBuilder();
        int paramIndex = 0;

        for (int i = 0; i < sql.length(); i++) {
            char c = sql.charAt(i);
            if (c == '?' && paramIndex < params.size()) {
                result.append(formatParameter(params.get(paramIndex)));
                paramIndex++;
            } else {
                result.append(c);
            }
        }

        return result.toString();
    }

    /**
     * 格式化单个参数为SQL中的字面量
     *
     * @param param
     *            参数对象
     * @return 格式化后的SQL字面量
     */
    private static String formatParameter(Object param) {
        if (param == null) {
            return "NULL";
        } else if (param instanceof String) {
            // 对单引号进行转义，并用单引号包围
            return "'" + ((String) param).replace("'", "''") + "'";
        } else if (param instanceof Date) {
            // 日期格式化为标准格式，并用单引号包围
            return "'" + DateUtil.toDateTimeString((Date) param) + "'";
        } else if (param instanceof byte[]) {
            // 二进制数据表示为十六进制字符串
            return formatBinary((byte[]) param);
        } else if (param instanceof Boolean) {
            // 布尔值转换为1或0
            return ((Boolean) param) ? "1" : "0";
        } else {
            // 其他类型直接转为字符串
            return param.toString();
        }
    }

    /**
     * 将二进制数据格式化为十六进制字符串
     *
     * @param bytes
     *            二进制数据
     * @return 格式化后的字符串
     */
    private static String formatBinary(byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            return "''";
        }

        StringBuilder sb = new StringBuilder("0x");
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    /**
     * 格式化错误信息，包含SQL语句和参数
     *
     * @param sql
     *            SQL语句
     * @param params
     *            参数列表
     * @param error
     *            错误信息
     * @return 完整的错误信息字符串
     */
    public static String formatError(String sql, List<Object> params, String error) {
        StringBuilder sb = new StringBuilder();
        sb.append("SQL Error: ").append(error).append("\n");
        sb.append("Executable SQL: ").append(format(sql, params));
        return sb.toString();
    }

}
