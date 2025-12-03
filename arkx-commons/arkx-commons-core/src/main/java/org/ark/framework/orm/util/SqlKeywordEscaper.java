package org.ark.framework.orm.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:58
 * @since 1.0
 */

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * SQL关键字转义工具类 用于处理SQL语句中的保留关键字，解析UPDATE语句并为关键字字段添加双引号
 */
public class SqlKeywordEscaper {

    // 达梦数据库保留关键字列表
    private static final Set<String> DM_RESERVED_KEYWORDS = new HashSet<>();

    // 初始化达梦数据库关键字
    static {
        String[] damengKeywords = {"ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BY",
                "CASCADE", "CHECK", "CLUSTER", "COLUMN", "COMMENT", "COMPRESS", "CONNECT", "CREATE", "CURRENT", "DATE",
                "DECIMAL", "DEFAULT", "DELETE", "DESC", "DROP", "ELSE", "EXCLUSIVE", "EXISTS", "FILE", "FLOAT", "FOR",
                "FOREIGN", "FROM", "GRANT", "GROUP", "HAVING", "IDENTIFIED", "IMMEDIATE", "IN", "INCREMENT", "INDEX",
                "INITIAL", "INSERT", "INTEGER", "INTERSECT", "INTO", "IS", "LEVEL", "LIKE", "LOCK", "LONG",
                "MAXEXTENTS", "MINUS", "MODE", "MODIFY", "NOAUDIT", "NOCOMPRESS", "NOT", "NOWAIT", "NULL", "NUMBER",
                "OF", "OFFLINE", "ON", "ONLINE", "OPTION", "OR", "ORDER", "PCTFREE", "PRIOR", "PRIVILEGES", "PUBLIC",
                "RAW", "RENAME", "RESOURCE", "REVOKE", "ROW", "ROWID", "ROWNUM", "ROWS", "SELECT", "SESSION", "SET",
                "SHARE", "SIZE", "SMALLINT", "START", "SYNONYM", "SYSDATE", "TABLE", "THEN", "TO", "TRIGGER", "UID",
                "UNION", "UNIQUE", "UPDATE", "USER", "VALIDATE", "VALUES", "VARCHAR", "VARCHAR2", "VIEW", "WHENEVER",
                "WHERE", "WITH", "ROWID", "TRXID", "OPTRTID", "RROWID", "CLUSTER_ID", "DATA_MAGIC", "LENGTH",
                "COLUMN_COUNT", "ROW_SIZE", "CREATE_TIME", "MODIFY_TIME", "DELETE_TRXID", "DELETE_OPTRTID", "DROP_TIME",
                "AUTOINCREMENT", "IDENTITY"};

        Arrays.stream(damengKeywords).forEach(keyword -> DM_RESERVED_KEYWORDS.add(keyword.toUpperCase()));
    }

    /**
     * 检查指定的名称是否是数据库保留关键字
     *
     * @param name
     *            要检查的名称
     * @return 如果是保留关键字则返回true，否则返回false
     */
    public static boolean isReservedKeyword(String name) {
        if (name == null || name.isEmpty()) {
            return false;
        }

        // 如果已经被双引号包围，则不需要再处理
        if (name.startsWith("\"") && name.endsWith("\"")) {
            return false;
        }

        return DM_RESERVED_KEYWORDS.contains(name.toUpperCase());
    }

    /**
     * 如果是保留关键字，则使用双引号包围
     *
     * @param name
     *            要处理的名称
     * @return 处理后的名称
     */
    public static String escapeIfReserved(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }

        // 如果已经被双引号包围，则不需要再处理
        if (name.startsWith("\"") && name.endsWith("\"")) {
            return name;
        }

        if (isReservedKeyword(name)) {
            return "\"" + name + "\"";
        }

        return name;
    }

    /**
     * 解析UPDATE SQL语句，为SET子句中的关键字字段添加双引号
     *
     * @param sql
     *            UPDATE SQL语句
     * @return 处理后的SQL语句
     */
    public static String escapeUpdateSql(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        if (!sql.trim().toUpperCase().startsWith("UPDATE")) {
            return sql;
        }

        try {
            // 分解SQL语句为三部分：UPDATE表部分、SET子句部分和WHERE部分
            Pattern pattern = Pattern.compile("UPDATE\\s+(.+?)\\s+SET\\s+(.+?)(\\s+WHERE\\s+.+|$)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(sql);

            if (matcher.find()) {
                String tablePart = matcher.group(1);
                String setPart = matcher.group(2);
                String wherePart = matcher.group(3);

                // 处理SET子句部分的字段名
                StringBuilder newSetPart = new StringBuilder();
                String[] assignments = setPart.split(",");

                for (int i = 0; i < assignments.length; i++) {
                    if (i > 0) {
                        newSetPart.append(", ");
                    }

                    String assignment = assignments[i].trim();
                    int equalPos = assignment.indexOf('=');

                    if (equalPos > 0) {
                        String columnName = assignment.substring(0, equalPos).trim();
                        String restPart = assignment.substring(equalPos);

                        newSetPart.append(escapeIfReserved(columnName)).append(restPart);
                    } else {
                        // 如果没有等号，保持原样
                        newSetPart.append(assignment);
                    }
                }

                // 处理表名
                String escapedTablePart = escapeIfReserved(tablePart.trim());

                // 重建SQL语句
                return "UPDATE " + escapedTablePart + " SET " + newSetPart + wherePart;
            }

            return sql;
        } catch (Exception e) {
            // 如果解析出错，返回原始SQL语句
            return sql;
        }
    }

    /**
     * 解析WHERE子句，为关键字字段添加双引号
     *
     * @param whereSql
     *            WHERE子句
     * @return 处理后的WHERE子句
     */
    public static String escapeWhereClause(String whereSql) {
        if (whereSql == null || whereSql.isEmpty()) {
            return whereSql;
        }

        try {
            // 此模式匹配条件表达式，如：columnName = value
            Pattern pattern = Pattern.compile("(\\w+)\\s*(=|<>|>|<|>=|<=|LIKE|IN|IS)\\s*", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(whereSql);

            StringBuffer result = new StringBuffer();
            while (matcher.find()) {
                String columnName = matcher.group(1);
                String operator = matcher.group(2);

                // 如果列名是关键字，添加双引号
                String escapedColumn = escapeIfReserved(columnName);
                matcher.appendReplacement(result, escapedColumn + " " + operator + " ");
            }
            matcher.appendTail(result);

            return result.toString();
        } catch (Exception e) {
            // 如果解析出错，返回原始WHERE子句
            return whereSql;
        }
    }

    /**
     * 解析INSERT SQL语句，为列名添加双引号
     *
     * @param sql
     *            INSERT SQL语句
     * @return 处理后的SQL语句
     */
    public static String escapeInsertSql(String sql) {
        if (sql == null || sql.isEmpty()) {
            return sql;
        }

        if (!sql.trim().toUpperCase().startsWith("INSERT")) {
            return sql;
        }

        try {
            // 匹配INSERT INTO table_name (col1, col2, ...) VALUES (...) 或 INSERT INTO
            // table_name VALUES (...)
            Pattern pattern = Pattern.compile(
                    "INSERT\\s+INTO\\s+(\\w+)(?:\\s*\\(([^)]+)\\))?\\s+(VALUES\\s*\\(.+|SELECT\\s+.+)",
                    Pattern.CASE_INSENSITIVE | Pattern.DOTALL);
            Matcher matcher = pattern.matcher(sql);

            if (matcher.find()) {
                StringBuilder result = new StringBuilder();
                String tableName = matcher.group(1);
                String columnListStr = matcher.group(2); // 可能为null，如果是INSERT INTO table
                                                         // VALUES (...)形式
                String valuesOrSelectPart = matcher.group(3);

                // 添加INSERT INTO和表名部分
                result.append("INSERT INTO ").append(escapeIfReserved(tableName));

                // 如果有列名列表，处理每个列名
                if (columnListStr != null && !columnListStr.trim().isEmpty()) {
                    result.append(" (");

                    String[] columns = columnListStr.split(",");
                    for (int i = 0; i < columns.length; i++) {
                        if (i > 0) {
                            result.append(", ");
                        }

                        String columnName = columns[i].trim();
                        result.append(escapeIfReserved(columnName));
                    }

                    result.append(")");
                }

                // 添加VALUES或SELECT部分，不变
                result.append(" ").append(valuesOrSelectPart);

                return result.toString();
            }

            return sql;
        } catch (Exception e) {
            // 如果解析出错，返回原始SQL语句
            return sql;
        }
    }

}
