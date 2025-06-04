package org.ark.framework.orm.schema;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @author Darkness
 * @date 2020-08-26 19:37:56
 * @version V1.0
 */
public class SqlServerDataBaseSchemaGenerator extends SchemaGenerator {

	public SqlServerDataBaseSchemaGenerator(String databaseName) {
		super(databaseName);
	}
	
	public SqlServerDataBaseSchemaGenerator(String namespace, String outputDir) {
		super(namespace, outputDir);
	}
	
	public SqlServerDataBaseSchemaGenerator(String dbName, String namespace, String outputDir) {
		super(namespace, outputDir);
		databaseName = dbName;
	}

	@Override
	public SchemaTable[] getSchemaTables() {
		
		List<SchemaGenerator.SchemaTable> schemaTables = new ArrayList<SchemaGenerator.SchemaTable>();
		
		String sql = "SELECT DISTINCT\r\n" + 
				"	d.name,\r\n" + 
				"	f.value \r\n" + 
				"FROM\r\n" + 
				"	syscolumns a\r\n" + 
				"	LEFT JOIN systypes b ON a.xusertype= b.xusertype\r\n" + 
				"	INNER JOIN sysobjects d ON a.id= d.id \r\n" + 
				"	AND d.xtype= 'U' \r\n" + 
				"	AND d.name<> 'dtproperties'\r\n" + 
				"	LEFT JOIN syscomments e ON a.cdefault= e.id\r\n" + 
				"	LEFT JOIN sys.extended_properties g ON a.id= G.major_id \r\n" + 
				"	AND a.colid= g.minor_id\r\n" + 
				"	LEFT JOIN sys.extended_properties f ON d.id= f.major_id \r\n" + 
				"	AND f.minor_id= 0 "
				+ " WHERE d.name like 'BidProduct'"
				+ " order by d.name asc";
		Query qb = getSession().createQuery(sql);
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
		String sql = "SELECT \r\n" + 
				"(case when a.colorder=1 then d.name else null end) 表名, \r\n" + 
				"a.colorder 字段序号,a.name 字段名,\r\n" + 
				"(case when COLUMNPROPERTY( a.id,a.name,'IsIdentity')=1 then '√'else '' end) 标识, \r\n" + 
				"(case when (SELECT count(*) FROM sysobjects \r\n" + 
				"WHERE (name in (SELECT name FROM sysindexes \r\n" + 
				"WHERE (id = a.id) AND (indid in \r\n" + 
				"(SELECT indid FROM sysindexkeys \r\n" + 
				"WHERE (id = a.id) AND (colid in \r\n" + 
				"(SELECT colid FROM syscolumns WHERE (id = a.id) AND (name = a.name))))))) \r\n" + 
				"AND (xtype = 'PK'))>0 then '√' else '' end) 主键,\r\n" + 
				"(case when (SELECT count(*) FROM sysobjects \r\n" + 
				"WHERE (name in (SELECT name FROM sysindexes \r\n" + 
				"WHERE (id = a.id) AND (indid in \r\n" + 
				"(SELECT indid FROM sysindexkeys \r\n" + 
				"WHERE (id = a.id) AND (colid in \r\n" + 
				"(SELECT colid FROM syscolumns WHERE (id = a.id) AND (name = a.name))))))) \r\n" + 
				"AND (xtype = 'PK'))>0 then 'PRI' else '' end) column_key,\r\n" + 
				"b.name 类型,a.length 占用字节数, \r\n" + 
				"COLUMNPROPERTY(a.id,a.name,'PRECISION') as 长度, \r\n" + 
				"isnull(COLUMNPROPERTY(a.id,a.name,'Scale'),0) as 小数位数,\r\n" + 
				"(case when a.isnullable=1 then '√'else '' end) 允许空, \r\n" + 
				"a.isnullable is_nullable,\r\n" + 
				"isnull(e.text,'') 默认值,\r\n" + 
				"isnull(g.[value], ' ') AS [说明]\r\n" + 
				"\r\n" + 
				"FROM syscolumns a \r\n" + 
				"left join systypes b on a.xtype=b.xusertype \r\n" + 
				"inner join sysobjects d on a.id=d.id and d.xtype='U' and d.name<>'dtproperties' \r\n" + 
				"left join syscomments e on a.cdefault=e.id \r\n" + 
				"left join sys.extended_properties g on a.id=g.major_id AND a.colid=g.minor_id\r\n" + 
				"left join sys.extended_properties f on d.id=f.class and f.minor_id=0\r\n" + 
				"where b.name is not null\r\n" + 
				"and d.name=? --如果只查询指定表,加上此条件\r\n" + 
				"--WHERE d.name='要查询的表' --如果只查询指定表,加上此条件\r\n" + 
				"order by a.id,a.colorder";
		Query qb = getSession().createQuery(sql, tableName);
		DataTable dataTable = qb.executeDataTable();
		DataRow[] dataRows = dataTable.getDataRows();
		for (DataRow dataRow : dataRows) {
			SchemaColumn sc = new SchemaColumn();
			
			sc.ID = dataRow.getString(2);
			sc.Name = dataRow.getString(2);
			sc.Code = dataRow.getString(2);
			sc.Comment = dataRow.getString(13);
			sc.DataType = dataRow.getString(6);
			sc.setLength(dataRow.getString(8));
			sc.Mandatory = "0".equals(dataRow.getString("is_nullable"));
			sc.Precision = dataRow.getInt(9);
			sc.isPrimaryKey = "PRI".equals(dataRow.getString("column_key"));
			
			schemaColumns.add(sc);
		}
		
		return schemaColumns.toArray(new SchemaColumn[0]);
	}

	public static Session getSession() {
		return SessionFactory.currentSession();
	}
}
