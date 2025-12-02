package org.ark.framework.orm.db.create;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:10
 * @since 1.0
 */
import java.util.*;

import org.ark.framework.orm.SchemaColumn;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * 达梦数据库建表实现
 */
public class DMTableCreator extends AbstractTableCreator {

	// 达梦数据库保留关键字列表
	private static final Set<String> RESERVED_KEYWORDS = new HashSet<>();

	static {
		// 初始化达梦数据库关键字列表
		String[] damengKeywords = { "ADD", "ALL", "ALTER", "AND", "ANY", "AS", "ASC", "AUDIT", "BETWEEN", "BY",
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
				"COLUMN_COUNT", "ROW_SIZE", "MODIFY_TIME", "DELETE_TRXID", "DELETE_OPTRTID", "DROP_TIME",
				"AUTOINCREMENT", "IDENTITY" };

		for (String keyword : damengKeywords) {
			RESERVED_KEYWORDS.add(keyword.toUpperCase());
		}
	}

	/**
	 * 检查并处理保留关键字 如果列名是保留关键字，则使用双引号包裹
	 * @param columnName 列名
	 * @return 处理后的列名
	 */
	protected String escapeColumnNameIfReserved(String columnName) {
		if (columnName == null || columnName.isEmpty()) {
			return columnName;
		}

		// 达梦数据库对关键字的比较不区分大小写
		if (RESERVED_KEYWORDS.contains(columnName.toUpperCase())) {
			// 使用双引号包裹关键字
			return "\"" + columnName + "\"";
		}

		return columnName;
	}

	@Override
	protected String convert(int columnType, int length, int precision) {
		// 优先判断是否应转换为 text
		if (columnTypeConverter.shouldConvertToText(columnType, length)) {
			return "text";
		}

		Map<Integer, String> typeMap = new HashMap<>() {
			{
				put(1, "VARCHAR2"); // VARCHAR
				put(2, "BLOB"); // BLOB
				put(3, "DOUBLE PRECISION"); // DOUBLE
				put(4, "DECIMAL"); // DECIMAL
				put(5, "NUMBER"); // NUMBER
				put(6, "NUMBER"); // NUMBER
				put(7, "BIGINT"); // INTEGER
				put(8, "INTEGER"); // INTEGER
				put(9, "INTEGER"); // INTEGER
				put(10, "TEXT"); // 自定义 TEXT 类型
				put(12, "DATETIME"); // DATETIME
				put(22, "DATETIME"); // DATETIME
			}
		};

		return typeMap.getOrDefault(columnType, null);
	}

	@Override
	public String createTableSql(SchemaColumn[] scs, String tableCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table " + escapeColumnNameIfReserved(tableCode) + "(\n");
		StringBuilder ksb = new StringBuilder();

		// 使用LinkedHashMap存储字段,自动去重并保持顺序,忽略大小写
		Map<String, SchemaColumn> uniqueColumns = new LinkedHashMap<>();
		for (SchemaColumn sc : scs) {
			uniqueColumns.put(sc.getColumnName().toLowerCase(), sc);
		}

		// 处理去重后的字段
		int i = 0;
		for (SchemaColumn sc : uniqueColumns.values()) {
			if (i != 0) {
				sb.append(",\n");
			}

			// 处理可能是保留关键字的列名
			String escapedColumnName = escapeColumnNameIfReserved(sc.getColumnName());

			sb.append("\t" + escapedColumnName + " ");

			int length = sc.getLength();

			// 达梦 varchar2 扩大一倍
			String convert = convert(sc.getColumnType(), 0, 0);
			if ("VARCHAR2".equals(convert)) {
				length = length + length;
			}

			String sqlType = toSQLType(sc.getColumnType(), length, sc.getPrecision());
			sb.append(sqlType + " ");
			if (sc.isMandatory()) {
				sb.append("not null");
			}
			if (sc.isPrimaryKey()) {
				if (ksb.length() == 0) {
					String constraintName = "PK_" + (tableCode.length() > 3 ? tableCode.substring(3) : tableCode);
					ksb.append("\tconstraint " + constraintName + " primary key (");
				}
				else {
					ksb.append(",");
				}
				ksb.append(escapedColumnName);
			}
			i++;
		}
		if (ksb.length() != 0) {
			ksb.append(")");
			sb.append(",\n" + ksb);
		}
		sb.append("\n)");
		return sb.toString();
	}

	/**
	 * 获取字段的长度和精度描述部分 如(50) 或 (10,2)
	 * @param length 长度
	 * @param precision 精度
	 * @return 格式化后的长度精度声明
	 */
	public String getFieldExtDesc(int length, int precision) {
		if (length != 0) {
			StringBuilder sb = new StringBuilder();
			sb.append("(");
			sb.append(length);
			if (precision != 0) {
				sb.append(",");
				sb.append(precision);
			}
			sb.append(") ");
			return sb.toString();
		}
		return "";
	}

	public String toSQLType(int columnType, int length, int precision) {
		String type = convert(columnType, length, precision);
		if ("CLOB".equals(type)) {
			length = 0;
		}
		if ("BLOB".equals(type)) {
			length = 0;
		}
		if ("DATE".equals(type)) {
			length = 0;
		}
		if ("INTEGER".equals(type)) {
			length = 0;
		}
		if ("BIGINT".equals(type)) {
			length = 0;
		}
		if ("DATETIME".equals(type)) {
			length = 0;
		}
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DataType" + columnType);
		}
		if ((length == 0) && (columnType == 1)) {
			throw new RuntimeException("varchar's length can't be empty!");
		}
		return type + getFieldExtDesc(length, precision);
	}

}
