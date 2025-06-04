package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;

import io.arkx.framework.commons.util.ObjectUtil;

/**
 * @class org.ark.framework.orm.db.create.MYSQLTableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:31:11
 * @version V1.0
 */
public class MYSQLTableCreator extends AbstractTableCreator {

	@Override
	protected String convert(int columnType, int length, int precision) {
		if (columnType == 3)
			return "int";
		else if (columnType == 2)
			return "longblob";
		else if (columnType == 12)
			return "datetime";
		else if (columnType == 4)
			return "decimal";
		else if (columnType == 6)
			return "int";
		else if (columnType == 5)
			return "int";
		else if (columnType == 8)
			return "int";
		else if (columnType == 7)
			return "bigint";
		else if (columnType == 9)
			return "int";
		else if (columnType == 1)
			return "varchar";
		else if (columnType == 10) {
			return "mediumtext";
		}
		return null;
	}

	@Override
	public String createTableSql(SchemaColumn[] scs, String tableCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table " + tableCode + "\n(\n");
		StringBuilder ksb = new StringBuilder();
		for (int i = 0; i < scs.length; i++) {
			SchemaColumn sc = scs[i];
			if (i != 0) {
				sb.append(",\n");
			}
			sb.append("\t`" + sc.getColumnName() + "` ");// 字段名称可能会是关键词
			String sqlType = toSQLType(sc.getColumnType(), sc.getLength(), sc.getPrecision());
			sb.append(sqlType + " ");
			if (sc.isMandatory()) {
				sb.append("not null");
			}
			if (sc.isPrimaryKey()) {
				if (ksb.length() == 0)
					ksb.append("\tprimary key (");
				else {
					ksb.append(",");
				}
				ksb.append(sc.getColumnName());
			}
		}
		if (ksb.length() != 0) {
			ksb.append(")");
			sb.append(",\n" + ksb);
		}
		sb.append("\n)");
		return sb.toString();
	}
	
	public String toSQLType(int columnType, int length, int precision) {
		String type = convert(columnType, length, precision);
		if (type == "CLOB") {
			length = 0;
		}
		if (ObjectUtil.empty(type)) {
			throw new RuntimeException("Unknown DataType" + columnType);
		}
		if ((length == 0) && (columnType == 1)) {
			throw new RuntimeException("varchar's length can't be empty!");
		}
		
		if("double".equalsIgnoreCase(type)) {
			return type;
		} else if("datetime".equalsIgnoreCase(type)) {
			return type;
		}else if("varchar".equalsIgnoreCase(type) || "mediumtext".equalsIgnoreCase(type)) {
			if(length > 3000) {
				return "text";
			}
		}
		
		return type + getFieldExtDesc(length, precision);
	}

}
