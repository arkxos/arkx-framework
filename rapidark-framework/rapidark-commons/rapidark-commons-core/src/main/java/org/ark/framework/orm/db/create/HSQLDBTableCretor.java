package org.ark.framework.orm.db.create;

import org.ark.framework.orm.SchemaColumn;

/**
 * @class org.ark.framework.orm.db.create.HSQLDBTableCretor
 * @author Darkness
 * @date 2013-1-29 下午04:32:21
 * @version V1.0
 */
public class HSQLDBTableCretor extends AbstractTableCreator {

	@Override
	public String convert(int columnType, int length, int precision) {
		if (columnType == 3)
			return "double";
		else if (columnType == 2)
			return "binary varying(MAX)";
		else if (columnType == 12)
			return "datetime";
		else if (columnType == 4)
			return "decimal";
		else if (columnType == 6)
			return "double";
		else if (columnType == 5)
			return "float";
		else if (columnType == 8)
			return "int";
		else if (columnType == 7)
			return "bigint";
		else if (columnType == 9)
			return "int";
		else if (columnType == 1)
			return "varchar";
		else if (columnType == 10) {
			return "longvarchar";
		}
		return null;
	}

	@Override
	public String createTableSql(SchemaColumn[] scs, String tableCode) {
		// TODO Auto-generated method stub
		return null;
	}

}
