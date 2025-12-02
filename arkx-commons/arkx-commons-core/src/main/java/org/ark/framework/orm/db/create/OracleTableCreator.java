package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;

/**
 * @class org.ark.framework.orm.db.create.OracleTableCreator
 * @author Darkness
 * @date 2013-1-29 下午04:18:26
 * @version V1.0
 */
public class OracleTableCreator extends AbstractTableCreator {

	@Override
	protected String convert(int columnType, int length, int precision) {
		if (columnType == 3)
			return "DOUBLE PRECISION";
		else if (columnType == 2)
			return "BLOB";
		else if (columnType == 12)
			return "DATE";
		else if (columnType == 4)
			return "DECIMAL";
		else if (columnType == 6)
			return "NUMBER";
		else if (columnType == 5)
			return "NUMBER";
		else if (columnType == 8)
			return "INTEGER";
		else if (columnType == 7)
			return "INTEGER";
		else if (columnType == 9)
			return "INTEGER";
		else if (columnType == 1)
			return "VARCHAR2";
		else if (columnType == 10) {
			return "CLOB";
		}
		return null;
	}

	@Override
	public String createTableSql(SchemaColumn[] scs, String tableCode) {
		StringBuilder sb = new StringBuilder();
		sb.append("create table " + tableCode + "(\n");
		StringBuilder ksb = new StringBuilder();
		for (int i = 0; i < scs.length; i++) {
			SchemaColumn sc = scs[i];
			if (i != 0) {
				sb.append(",\n");
			}
			sb.append("\t" + sc.getColumnName() + " ");
			String sqlType = toSQLType(sc.getColumnType(), sc.getLength(), sc.getPrecision());
			sb.append(sqlType + " ");
			if (sc.isMandatory()) {
				sb.append("not null");
			}
			if (sc.isPrimaryKey()) {
				// sb.append(" primary key ");
				if (ksb.length() == 0)
					ksb.append("\tconstraint PK_" + tableCode.substring(3) + " primary key (");
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

}
