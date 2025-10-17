package org.ark.framework.orm.schema;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:20
 * @since 1.0
 */

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import java.util.ArrayList;
import java.util.List;


/**
 * @class org.ark.framework.orm.schema.MySqlDataBaseSchemaGenerator
 * @author Darkness
 * @date 2012-4-10 下午9:48:07
 * @version V1.0
 */
public class KingBaseDataBaseSchemaGenerator extends SchemaGenerator {

    public KingBaseDataBaseSchemaGenerator(String databaseName) {
        super(databaseName);
    }

    public KingBaseDataBaseSchemaGenerator(String namespace, String outputDir, ConnectionConfig config) {
        super(namespace, outputDir);
        isOracle = false;
        connectionConfig = config;
    }

    public KingBaseDataBaseSchemaGenerator(String dbName, String namespace, String outputDir, ConnectionConfig config) {
        super(namespace, outputDir);
        databaseName = dbName;
        isOracle = false;
        connectionConfig = config;
    }

    @Override
    public SchemaTable[] getSchemaTables() {

        List<SchemaTable> schemaTables = new ArrayList<SchemaTable>();

        String sql = "SELECT \n" +
                "    a.relname AS table_name,\n" +
                "    b.description AS table_comment\n" +
                "FROM \n" +
                "    sys_class a\n" +
                "LEFT JOIN \n" +
                "    sys_description b ON a.oid = b.objoid AND b.objsubid = 0\n" +
                "WHERE \n" +
                "    a.relkind = 'r' \n" +
                "    AND a.relnamespace = (SELECT oid FROM sys_namespace WHERE nspname = 'public')\n" +
                "ORDER BY \n" +
                "    a.relname";
        Session session = SessionFactory.openSessionInThread(connectionConfig.getPoolName());
        session.beginTransaction();
        Query qb = session.createQuery(sql);
        DataTable dataTable = qb.executeDataTable();
        session.close();

        DataRow[] dataRows = dataTable.getDataRows();
        for (DataRow dataRow : dataRows) {
            SchemaTable schemaTable = new SchemaTable();
            schemaTable.tableName = dataRow.getString(0);
//			if (!schemaTable.tableName.toLowerCase().equals("Lz_independent_rewards".toLowerCase())) {
//				continue;
//			}
            schemaTable.tableCode = dataRow.getString(0);
            schemaTable.tableComment = dataRow.getString(1);
            schemaTable.schemaColumns = getSchemaColumns(schemaTable.tableName);

            schemaTables.add(schemaTable);
        }

        return schemaTables.toArray(new SchemaTable[0]);
    }

    private SchemaColumn[] getSchemaColumns(String tableName) {

        List<SchemaColumn> schemaColumns = new ArrayList<SchemaColumn>();
        String sql = "SELECT \n" +
                "    col.column_name,\n" +
                "    case when col.data_type = 'USER-DEFINED' then col.udt_name else col.data_type END AS data_type,\n" +
                "    col.character_maximum_length,\n" +
                "    col.is_nullable,\n" +
                "    CASE \n" +
                "        WHEN kcu.column_name IS NOT NULL THEN 'PRI'\n" +
                "        ELSE ''\n" +
                "    END AS column_key,\n" +
                "    pgd.description AS column_comment\n" +
                "    -- col.numeric_precision AS \"精度\",\n" +
                "    -- col.numeric_scale AS \"小数位数\",\n" +
                "    -- col.column_default AS \"默认值\"\n" +
                "FROM \n" +
                "    information_schema.columns col\n" +
                "LEFT JOIN \n" +
                "    sys_catalog.sys_description pgd ON \n" +
                "    pgd.objoid = (\n" +
                "        SELECT c.oid \n" +
                "        FROM sys_catalog.sys_class c \n" +
                "        JOIN sys_catalog.sys_namespace n ON n.oid = c.relnamespace \n" +
                "        WHERE c.relname = col.table_name \n" +
                "        AND n.nspname = col.table_schema\n" +
                "    ) \n" +
                "    AND pgd.objsubid = col.ordinal_position\n" +
                "LEFT JOIN \n" +
                "    information_schema.key_column_usage kcu ON \n" +
                "    kcu.table_schema = col.table_schema \n" +
                "    AND kcu.table_name = col.table_name \n" +
                "    AND kcu.column_name = col.column_name\n" +
                "    AND EXISTS (\n" +
                "        SELECT 1 \n" +
                "        FROM information_schema.table_constraints tc \n" +
                "        WHERE tc.constraint_schema = kcu.constraint_schema \n" +
                "        AND tc.constraint_name = kcu.constraint_name \n" +
                "        AND tc.constraint_type = 'PRIMARY KEY'\n" +
                "    )\n" +
                "WHERE \n" +
                "    col.table_schema = 'public' \n" +
                "    AND col.table_name = ?\n" +
                "ORDER BY \n" +
                "    col.ordinal_position";
        Query qb = getSession().createQuery(sql, tableName);//, databaseName);
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


