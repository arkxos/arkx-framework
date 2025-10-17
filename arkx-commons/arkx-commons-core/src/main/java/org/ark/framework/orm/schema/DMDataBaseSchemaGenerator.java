package org.ark.framework.orm.schema;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:20
 * @since 1.0
 */

import cn.hutool.core.util.StrUtil;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;
import com.google.common.collect.Sets;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * @class org.ark.framework.orm.schema.OracleDataBaseSchemaGenerator
 * @author Darkness
 * @date 2012-4-10 下午9:49:40
 * @version V1.0
 */
public class DMDataBaseSchemaGenerator extends SchemaGenerator {

    public DMDataBaseSchemaGenerator(String databaseName) {
        super(databaseName);
        isOracle = false;
    }

    public DMDataBaseSchemaGenerator(String namespace, String outputDir) {
        super(namespace, outputDir);
        isOracle = false;
    }


    public DMDataBaseSchemaGenerator(String namespace, String outputDir, ConnectionConfig config) {
        super(namespace, outputDir);
        isOracle = false;
        connectionConfig = config;
    }

    @Override
    public SchemaTable[] getSchemaTables() {



        List<SchemaTable> schemaTables = new ArrayList<>();
        Session dm = SessionFactory.openSessionInThread(connectionConfig.getPoolName());
        Connection connection = dm.readOnly().getConnection();

        //获取指定数据库的所有表
        String sql = String.format("select t.table_name,c.comments from ALL_TABLES t\n" +
                "LEFT JOIN\n" +
                "    ALL_TAB_COMMENTS c ON t.OWNER = c.OWNER AND t.TABLE_NAME = c.TABLE_NAME where t.OWNER = '%s'",connection.getDBConfig().getDatabaseName());


        DataTable dataTable = dm.readOnly().createQuery(sql).executeDataTable();
        DataRow[] dataRows = dataTable.getDataRows();
        Set<String> excludeTable = new HashSet<>();
//		excludeTable.add("SYS_JOB_LOG");
//		excludeTable.add("DS_ORGAN_CODE");

        HashSet<String> includeSet = Sets.newHashSet(connectionConfig.getTableList());



        int i = 0;
        for (DataRow dataRow : dataRows) {

            SchemaTable schemaTable = new SchemaTable();
            schemaTable.tableName = dataRow.getString(0);

            if (!includeSet.contains(schemaTable.tableName)&&connectionConfig.isExport) {
                continue;
            }
//			if (!excludeTable.contains(schemaTable.tableName)&& connectionConfig.isExport) {
//				continue;
//			}
            schemaTable.tableCode = dataRow.getString(0);
            schemaTable.tableComment = dataRow.getString(1);


            // 获取字段并过滤重复字段
            SchemaColumn[] originalColumns = getSchemaColumnsInternal(schemaTable.tableName);
            schemaTable.schemaColumns = SchemaColumnUtils.filterDuplicateColumns(originalColumns, schemaTable.tableName);

            //去除部分影响生成schema的注释
            for (SchemaColumn schemaColumn : schemaTable.schemaColumns) {
                if (StrUtil.isNotBlank(schemaColumn.Comment)){
                    schemaColumn.Comment = schemaColumn.Comment.replaceAll("\"", "'");
                }
            }

            schemaTables.add(schemaTable);
            i++;
        }

        return schemaTables.toArray(new SchemaTable[0]);
    }

    // 重命名原始方法，保持内部实现不变
    private SchemaColumn[] getSchemaColumnsInternal(String tableName) {

        List<SchemaColumn> schemaColumns = new ArrayList<>();
//		String sql = "select column_name,data_type, max(DATA_LENGTH) as character_maximum_length, nullable as is_nullable from user_tab_columns where table_name=? group by column_name, data_type, nullable order by column_id";
        String sql = """
				select a.column_name, a.data_type, a.data_length, a.nullable as is_nullable, cc.comments
				from all_tab_cols a
				         left join user_col_comments cc on cc.column_name = a.column_name
				
				where a.OWNER = UPPER(?)
				  AND a.TABLE_NAME = ?
				  AND cc.TABLE_NAME = ?	
				""";


        Query qb = getSession().createQuery(sql, connectionConfig.getDatabaseName(),tableName,tableName);
        DataTable dataTable = qb.executeDataTable();
        DataRow[] dataRows = dataTable.getDataRows();

        DataTable primaryKeyColumnNames = getSession().createQuery("select col.column_name " +
                "from user_constraints con,  user_cons_columns col " +
                "where con.constraint_name = col.constraint_name " +
                "and con.constraint_type='P' " +
                "and col.table_name = ?", tableName.toUpperCase()).executeDataTable();

        for (DataRow dataRow : dataRows) {
            SchemaColumn sc = new SchemaColumn();

            sc.ID = dataRow.getString(0);
            sc.Name = dataRow.getString(0);
            sc.Code = dataRow.getString(0);
            sc.Comment = dataRow.getString(4);
            sc.DataType = dataRow.getString(1);
            sc.setLength(dataRow.getString(2));

            sc.Mandatory = "N".equals(dataRow.getString("is_nullable"));

            if(primaryKeyColumnNames != null) {
                for (DataRow dataRow2 : primaryKeyColumnNames.getDataRows()) {
                    if( sc.Name.equalsIgnoreCase(dataRow2.getString(0))) {
                        sc.isPrimaryKey = true;
                        break;
                    }
                }
            }

            schemaColumns.add(sc);
        }

        return schemaColumns.toArray(new SchemaColumn[0]);
    }

    // 公开方法，使用内部实现和过滤器
    public SchemaColumn[] getSchemaColumns(String tableName) {
        SchemaColumn[] originalColumns = getSchemaColumnsInternal(tableName);
        return SchemaColumnUtils.filterDuplicateColumns(originalColumns, tableName);
    }

    public static void main(String[] args) {

        new DMDataBaseSchemaGenerator("bbs").generate();
    }

    public static Session getSession() {
        return SessionFactory.currentSession();
    }
}

