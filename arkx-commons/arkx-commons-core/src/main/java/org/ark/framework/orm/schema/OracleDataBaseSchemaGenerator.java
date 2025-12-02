package org.ark.framework.orm.schema;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

/**
 * @class org.ark.framework.orm.schema.OracleDataBaseSchemaGenerator
 * @author Darkness
 * @date 2012-4-10 下午9:49:40
 * @version V1.0
 */
public class OracleDataBaseSchemaGenerator extends SchemaGenerator {

    public OracleDataBaseSchemaGenerator(String databaseName) {
        super(databaseName);
        isOracle = true;
    }

    public OracleDataBaseSchemaGenerator(String namespace, String outputDir) {
        super(namespace, outputDir);
        isOracle = true;
    }

    @Override
    public SchemaTable[] getSchemaTables() {

        List<SchemaGenerator.SchemaTable> schemaTables = new ArrayList<>();

        String sql = "select table_name,'' from user_tables";
        DataTable dataTable = SessionFactory.openSession().readOnly().createQuery(sql).executeDataTable();
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

        List<SchemaColumn> schemaColumns = new ArrayList<>();
        String sql = "select column_name,data_type,DATA_LENGTH as character_maximum_length, nullable as is_nullable from user_tab_columns where table_name=? order by column_id";

        Query qb = getSession().createQuery(sql, tableName);
        DataTable dataTable = qb.executeDataTable();
        DataRow[] dataRows = dataTable.getDataRows();

        DataTable primaryKeyColumnNames = getSession()
                .createQuery("select col.column_name " + "from user_constraints con,  user_cons_columns col "
                        + "where con.constraint_name = col.constraint_name " + "and con.constraint_type='P' "
                        + "and col.table_name = ?", tableName.toUpperCase())
                .executeDataTable();

        for (DataRow dataRow : dataRows) {
            SchemaColumn sc = new SchemaColumn();

            sc.ID = dataRow.getString(0);
            sc.Name = dataRow.getString(0);
            sc.Code = dataRow.getString(0);
            sc.Comment = "";
            sc.DataType = dataRow.getString(1);
            sc.setLength(dataRow.getString(2));

            sc.Mandatory = "N".equals(dataRow.getString("is_nullable"));

            if (primaryKeyColumnNames != null) {
                for (DataRow dataRow2 : primaryKeyColumnNames.getDataRows()) {
                    if (sc.Name.equalsIgnoreCase(dataRow2.getString(0))) {
                        sc.isPrimaryKey = true;
                        break;
                    }
                }
            }

            schemaColumns.add(sc);
        }

        return schemaColumns.toArray(new SchemaColumn[0]);
    }

    public static void main(String[] args) {

        new OracleDataBaseSchemaGenerator("bbs").generate();
    }

    public static Session getSession() {
        return SessionFactory.currentSession();
    }
}
