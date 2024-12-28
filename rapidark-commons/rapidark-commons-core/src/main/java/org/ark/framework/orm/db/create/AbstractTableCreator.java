package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;

import com.rapidark.framework.commons.util.ObjectUtil;

/**
 * @class org.ark.framework.orm.db.create.AbstractTableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:20:26
 * @version V1.0
 */
public abstract class AbstractTableCreator {

	protected abstract String convert(int columnType, int length, int precision);

	public abstract String createTableSql(SchemaColumn[] scs, String tableCode);

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
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DataType" + columnType);
		}
		if ((length == 0) && (columnType == 1)) {
			throw new RuntimeException("varchar's length can't be empty!");
		}
		return type + getFieldExtDesc(length, precision);
	}

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
}
