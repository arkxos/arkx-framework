package org.ark.framework.orm.schema;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;


/**   
 * @class org.ark.framework.orm.schema.MySqlDataBaseSchemaGenerator
 * @author Darkness
 * @date 2012-4-10 下午9:48:07 
 * @version V1.0   
 */
public class MySqlDataBaseSchemaGenerator extends SchemaGenerator {

	public MySqlDataBaseSchemaGenerator(String databaseName) {
		super(databaseName);
	}
	
	public MySqlDataBaseSchemaGenerator(String namespace, String outputDir) {
		super(namespace, outputDir);
	}
	
	public MySqlDataBaseSchemaGenerator(String dbName, String namespace, String outputDir) {
		super(namespace, outputDir);
		databaseName = dbName;
	}

	@Override
	public SchemaTable[] getSchemaTables() {
		
		List<SchemaGenerator.SchemaTable> schemaTables = new ArrayList<SchemaGenerator.SchemaTable>();
		
		String sql = "select table_name, table_comment FROM information_schema.tables t  WHERE t.table_schema = ?";
		Query qb = getSession().createQuery(sql, databaseName);
		DataTable dataTable = qb.executeDataTable();
		DataRow[] dataRows = dataTable.getDataRows();
		for (DataRow dataRow : dataRows) {
			SchemaTable schemaTable = new SchemaTable();
			schemaTable.tableName = dataRow.getString(0);
			schemaTable.tableCode = dataRow.getString(0);
			schemaTable.tableComment = dataRow.getString(1);
			schemaTable.schemaColumns = getSchemaColumns(schemaTable.tableName);
			
			schemaTables.add(schemaTable);
		}
		
		return schemaTables.toArray(new SchemaGenerator.SchemaTable[0]);
	}

	private SchemaColumn[] getSchemaColumns(String tableName) {
		
		List<SchemaColumn> schemaColumns = new ArrayList<SchemaGenerator.SchemaColumn>();
		String sql = "select column_name, data_type, " +
				"character_maximum_length, is_nullable, " +
				"column_key, column_comment" +
				" from information_schema.columns c where c.table_name = ? and c.table_schema=?";
		Query qb = getSession().createQuery(sql, tableName, databaseName);
		DataTable dataTable = qb.executeDataTable();
		DataRow[] dataRows = dataTable.getDataRows();
		for (DataRow dataRow : dataRows) {
			SchemaColumn sc = new SchemaColumn();
			
			sc.ID = dataRow.getString(0);
			sc.Name = dataRow.getString(0);
			sc.Code = dataRow.getString(0);
			sc.Comment = dataRow.getString(5);
			sc.DataType = dataRow.getString(1);
			sc.setLength(dataRow.getString(2));
			sc.Mandatory = "NO".equals(dataRow.getString("is_nullable"));
			sc.isPrimaryKey = "PRI".equals(dataRow.getString("column_key"));
			
			schemaColumns.add(sc);
		}
		
		return schemaColumns.toArray(new SchemaColumn[0]);
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}
}


