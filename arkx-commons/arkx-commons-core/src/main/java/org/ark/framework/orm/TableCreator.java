package org.ark.framework.orm;

import java.util.ArrayList;

import org.ark.framework.orm.db.create.*;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @class org.ark.framework.orm.TableCreator
 * @author Darkness
 * @date 2012-3-8 下午2:01:08
 * @version V1.0
 */
public class TableCreator {

	// private static Logger logger = Logger.getLogger(TableCreator.class);

	private ArrayList<String> list = new ArrayList<String>();

	private IDBType DBType;

	public TableCreator(IDBType dbType) {
		this.DBType = dbType;
	}

	public void createTable(SchemaColumn[] scs, String tableComment, String tableCode) throws Exception {
		createTable(scs, tableComment, tableCode, true);
	}

	public void createTable(SchemaColumn[] scs, String tableComment, String tableCode, boolean create)
			throws Exception {
		if (!create) {
			// this.list.add("delete from " + tableCode);
		}
		else {
			// dropTable(tableCode);
			this.list.add(createTable(scs, tableCode, this.DBType));
			if ("oracle".equalsIgnoreCase(this.DBType.getExtendItemID())) {
				this.list.add("COMMENT ON TABLE " + tableCode + " IS '" + tableComment + "'");
				for (SchemaColumn schemaColumn : scs) {
					this.list.add("COMMENT ON COLUMN " + tableCode + "." + schemaColumn.getColumnName() + " IS '"
							+ schemaColumn.getMemo() + "'");
				}
			}
		}
	}

	public static String createTable(SchemaColumn[] scs, String tableCode, IDBType DBType) throws Exception {
		if (DBType.getExtendItemID().equalsIgnoreCase("MSSQL")) {
			return new MSSQLTableCreator().createTableSql(scs, tableCode);
		}
		if ((DBType.getExtendItemID().equalsIgnoreCase("MYSQL"))
				|| (DBType.getExtendItemID().equalsIgnoreCase("HSQLDB"))) {
			return new MYSQLTableCreator().createTableSql(scs, tableCode);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("ORACLE")) {
			return new OracleTableCreator().createTableSql(scs, tableCode);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("DB2")) {
			return new DB2TableCreator().createTableSql(scs, tableCode);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("SYBASE")) {
			return new SYBASETableCreator().createTableSql(scs, tableCode);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("DM")) {
			return new DMTableCreator().createTableSql(scs, tableCode);
		}
		throw new NullPointerException(DBType.getClass() + ": createTable 不支持");
	}

	public void executeAndClear() {
		executeAndClear(getSession());
	}

	public void executeAndClear(Session tran) {
		for (int i = 0; i < this.list.size(); i++) {
			Query qb = tran.createQuery(this.list.get(i));
			qb.executeNoQuery();
		}
		this.list.clear();
	}

	public void writeSqlToFile(String filePath) {
		for (int i = 0; i < this.list.size(); i++) {
			String sql = this.list.get(i) + ";\r\n";
			FileUtil.writeText(filePath, sql, true);
		}
		// this.list.clear();
	}

	public void executeAndClear(Connection conn) {

		for (int i = 0; i < this.list.size(); i++) {
			Query qb = getSession().createQuery(this.list.get(i));
			try {
				qb.executeNoQuery();
			}
			catch (Exception e) {
				if (qb.getSQL().startsWith("drop")) {
					String table = qb.getSQL();
					table = table.substring(table.indexOf(" ", 8)).trim();

					LogUtil.warn("Can't drop table，may be not exist：" + table);
				}
				else {
					if (qb.getSQL().indexOf(" drop index ") > 0)
						continue;
					LogUtil.warn(qb.getSQL());
					e.printStackTrace();
				}
			}
		}
		this.list.clear();
	}

	public String[] getSQLArray() {
		String[] arr = new String[this.list.size()];
		for (int i = 0; i < arr.length; i++) {
			arr[i] = ((String) this.list.get(i)).toString();
		}
		return arr;
	}

	public String getAllSQL() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < this.list.size(); i++) {
			sb.append((String) this.list.get(i));
			if ((this.DBType.getExtendItemID().equalsIgnoreCase("MSSQL"))
					|| (this.DBType.getExtendItemID().equalsIgnoreCase("SYBASE")))
				sb.append("\ngo\n");
			else {
				sb.append(";\n");
			}
		}
		return sb.toString();
	}

	public static String toSQLType(int columnType, int length, int precision, IDBType DBType) {

		if (DBType.getExtendItemID().equalsIgnoreCase("MSSQL")) {
			return new MSSQLTableCreator().toSQLType(columnType, length, precision);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("SYBASE")) {
			return new SYBASETableCreator().toSQLType(columnType, length, precision);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("ORACLE")) {
			return new OracleTableCreator().toSQLType(columnType, length, precision);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("DB2")) {
			return new DB2TableCreator().toSQLType(columnType, length, precision);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("MYSQL")) {
			return new MYSQLTableCreator().toSQLType(columnType, length, precision);
		}
		if (DBType.getExtendItemID().equalsIgnoreCase("HSQLDB")) {
			return new HSQLDBTableCretor().toSQLType(columnType, length, precision);
		}

		return null;

	}

	public void dropTable(String tableCode) {
		this.list.add(dropTable(tableCode, this.DBType));
	}

	public void createIndexes(String tableCode, String indexInfo) {
		if (ObjectUtil.empty(indexInfo)) {
			return;
		}
		String[] arr = StringUtil.splitEx(indexInfo, ";");
		for (String str : arr) {
			if (ObjectUtil.empty(str)) {
				continue;
			}
			int index = str.indexOf(":");
			if (index < 1) {
				continue;
			}
			String name = str.substring(0, index);
			str = str.substring(index + 1);
			String[] arr2 = StringUtil.splitEx(str, ",");
			ArrayList columns = new ArrayList();
			for (String c : arr2) {
				columns.add(c);
			}
			this.list.add(dropIndex(tableCode, name, this.DBType));
			this.list.add(createIndex(tableCode, name, columns));
		}
	}

	public static String dropTable(String tableCode, IDBType dbType) {
		String dropSQL = null;
		if ((dbType.getExtendItemID().equalsIgnoreCase("MSSQL"))
				|| (dbType.getExtendItemID().equalsIgnoreCase("SYBASE"))) {
			dropSQL = "if exists (select 1 from  sysobjects where id = object_id('" + tableCode
					+ "') and type='U') drop table " + tableCode;
		}
		if (dbType.getExtendItemID().equalsIgnoreCase("ORACLE")) {
			// dropSQL = "drop table " + tableCode + " cascade constraints";
			dropSQL = "call drop_if_exists('" + tableCode + "')";
		}
		if (dbType.getExtendItemID().equalsIgnoreCase("DB2")) {
			dropSQL = "drop table " + tableCode;
		}
		if ((dbType.getExtendItemID().equalsIgnoreCase("MYSQL"))
				|| (dbType.getExtendItemID().equalsIgnoreCase("HSQLDB"))) {
			dropSQL = "drop table if exists " + tableCode;
		}
		if (dbType.getExtendItemID().equalsIgnoreCase("DM")) {
			dropSQL = "DROP TABLE IF EXISTS " + tableCode;
		}
		if (dbType == null) {
			throw new NullPointerException(dbType.getExtendItemID() + ": 未适配 dropTable");
		}

		return dropSQL;
	}

	public static String dropIndex(String table, String name, IDBType dbtype) {
		if (dbtype.getExtendItemID().equalsIgnoreCase("MYSQL"))
			return "alter table " + table + " drop index " + name;
		if (dbtype.getExtendItemID().equalsIgnoreCase("MSSQL")) {
			return "drop index " + name + " on " + table;
		}
		return "drop index " + name;
	}

	public static String createIndex(String table, String name, ArrayList<String> columns) {
		StringBuilder sb = new StringBuilder();
		sb.append("create index ");
		sb.append(name);
		sb.append(" on ");
		sb.append(table);
		sb.append(" (");
		boolean first = true;
		for (String column : columns) {
			if (StringUtil.isEmpty(column)) {
				continue;
			}
			if (!first) {
				sb.append(",");
			}
			sb.append(column);
			first = false;
		}
		sb.append(")");
		return sb.toString();
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}

}
