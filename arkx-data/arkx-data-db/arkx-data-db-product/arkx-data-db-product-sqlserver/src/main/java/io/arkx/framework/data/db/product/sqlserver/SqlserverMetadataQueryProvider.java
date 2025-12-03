// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sqlserver;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.data.db.common.consts.Constants;
import io.arkx.framework.data.db.common.util.DDLFormatterUtils;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.AbstractMetadataProvider;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.schema.ColumnMetaData;
import io.arkx.framework.data.db.core.schema.TableDescription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlserverMetadataQueryProvider extends AbstractMetadataProvider {

    private static final String SHOW_CREATE_VIEW_SQL = "SELECT VIEW_DEFINITION from INFORMATION_SCHEMA.VIEWS where TABLE_SCHEMA ='%s' and TABLE_NAME ='%s'";

    private static Set<String> excludesSchemaNames;

    static {
        excludesSchemaNames = new HashSet<>();
        excludesSchemaNames.add("db_denydatawriter");
        excludesSchemaNames.add("db_datawriter");
        excludesSchemaNames.add("db_accessadmin");
        excludesSchemaNames.add("db_ddladmin");
        excludesSchemaNames.add("db_securityadmin");
        excludesSchemaNames.add("db_denydatareader");
        excludesSchemaNames.add("db_backupoperator");
        excludesSchemaNames.add("db_datareader");
        excludesSchemaNames.add("db_owner");
        excludesSchemaNames.add("sys");
        excludesSchemaNames.add("INFORMATION_SCHEMA");
        excludesSchemaNames.add("cdc");
    }

    public SqlserverMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    private int getDatabaseMajorVersion(Connection connection) {
        try {
            return connection.getMetaData().getDatabaseMajorVersion();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> querySchemaList(Connection connection) {
        Set<String> ret = new HashSet<>();
        try (ResultSet schemas = connection.getMetaData().getSchemas();) {
            while (schemas.next()) {
                String name = schemas.getString("TABLE_SCHEM");
                if (!excludesSchemaNames.contains(name)) {
                    ret.add(name);
                }
            }
            return new ArrayList<>(ret);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<TableDescription> queryTableList(Connection connection, String schemaName) {
        int majorVersion = getDatabaseMajorVersion(connection);
        if (majorVersion <= 8) {
            return super.queryTableList(connection, schemaName);
        }

        List<TableDescription> ret = new ArrayList<>();
        String sql = String.format("SELECT DISTINCT " + "  t.TABLE_SCHEMA as TABLE_SCHEMA, "
                + "  t.TABLE_NAME as TABLE_NAME, " + "  t.TABLE_TYPE as TABLE_TYPE, "
                + "  CONVERT(nvarchar(50),ISNULL(g.[value], '')) as COMMENTS " + " FROM INFORMATION_SCHEMA.TABLES t "
                + " LEFT JOIN sys.sysobjects d on t.TABLE_NAME = d.name "
                + " LEFT JOIN sys.extended_properties g on g.major_id = d.id and g.minor_id = 0 and g.name = 'MS_Description'"
                + " WHERE t.TABLE_SCHEMA='%s'", schemaName);
        try (PreparedStatement ps = connection.prepareStatement(sql); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                TableDescription td = new TableDescription();
                td.setSchemaName(rs.getString("TABLE_SCHEMA"));
                td.setTableName(rs.getString("TABLE_NAME"));
                td.setRemarks(rs.getString("COMMENTS"));
                String tableType = rs.getString("TABLE_TYPE").trim();
                if (tableType.equalsIgnoreCase("VIEW")) {
                    td.setTableType("VIEW");
                } else {
                    td.setTableType("TABLE");
                }

                ret.add(td);
            }

            return ret;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TableDescription queryTableMeta(Connection connection, String schemaName, String tableName) {
        try (ResultSet tables = connection.getMetaData().getTables(connection.getCatalog(), schemaName, tableName,
                new String[]{"TABLE", "VIEW"})) {
            if (tables.next()) {
                TableDescription td = new TableDescription();
                td.setSchemaName(schemaName);
                td.setTableName(tableName);
                td.setRemarks(tables.getString("REMARKS"));
                td.setTableType(tables.getString("TABLE_TYPE").toUpperCase());
                return td;
            }
            return null;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getTableDDL(Connection connection, String schemaName, String tableName) {
        String dropTempTableSql = SQLServerConst.DROP_TEMPTABLE_SQL;
        String createTempTableSql = SQLServerConst.CREATE_TEMPTABLE_SQL.formatted(schemaName, tableName);
        String selectDdlSql = SQLServerConst.SELECT_DDL_SQL.formatted(schemaName, tableName);
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(dropTempTableSql);
            st.executeUpdate(createTempTableSql);
            ResultSet rs = st.executeQuery(selectDdlSql);
            if (rs.next()) {
                return DDLFormatterUtils.format(rs.getString("createTableStatement"));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public String getViewDDL(Connection connection, String schemaName, String tableName) {
        String sql = SHOW_CREATE_VIEW_SQL.formatted(schemaName, tableName);
        try (Statement st = connection.createStatement()) {
            if (st.execute(sql)) {
                try (ResultSet rs = st.getResultSet()) {
                    if (rs != null && rs.next()) {
                        return rs.getString(1);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName, String tableName) {
        int majorVersion = getDatabaseMajorVersion(connection);
        if (majorVersion <= 8) {
            return super.queryTableColumnMeta(connection, schemaName, tableName);
        }

        String sql = this.getTableFieldsQuerySQL(schemaName, tableName);
        List<ColumnDescription> ret = this.querySelectSqlColumnMeta(connection, sql);
        String querySql = """
                SELECT a.name AS COLUMN_NAME,CONVERT(nvarchar(50),ISNULL(g.[value], '')) AS REMARKS FROM sys.columns a
                LEFT JOIN sys.extended_properties g ON ( a.object_id = g.major_id AND g.minor_id = a.column_id )
                WHERE object_id = (SELECT top 1 object_id FROM sys.tables st INNER JOIN INFORMATION_SCHEMA.TABLES t on st.name=t.TABLE_NAME
                WHERE	st.name = '%s' and t.TABLE_SCHEMA='%s')"""
                .formatted(tableName, schemaName);
        try (PreparedStatement ps = connection.prepareStatement(querySql); ResultSet rs = ps.executeQuery();) {
            while (rs.next()) {
                String columnName = rs.getString("COLUMN_NAME");
                String remarks = rs.getString("REMARKS");
                for (ColumnDescription cd : ret) {
                    if (columnName.equalsIgnoreCase(cd.getFieldName())) {
                        cd.setRemarks(remarks);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return ret;
    }

    @Override
    public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
        String querySQL = "SELECT TOP 1 * from (%s) tmp ".formatted(sql.replace(";", ""));
        return this.getSelectSqlColumnMeta(connection, querySQL);
    }

    @Override
    protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
        return "select top 1 * from [%s].[%s] ".formatted(schemaName, tableName);
    }

    @Override
    public void testQuerySQL(Connection connection, String sql) {
        String testQuerySql = "SELECT top 1 * from ( %s ) tmp".formatted(sql.replace(";", ""));
        if (log.isDebugEnabled()) {
            log.debug("Execute sql :{}", testQuerySql);
        }
        try (Statement st = connection.createStatement()) {
            st.execute(testQuerySql);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String getQuotedSchemaTableCombination(String schemaName, String tableName) {
        return "  [%s].[%s] ".formatted(schemaName, tableName);
    }

    @Override
    public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr,
            boolean withRemarks) {
        String fieldname = v.getName();
        int length = v.getLength();
        int precision = v.getPrecision();
        int type = v.getType();

        String retval = " [" + fieldname + "]  ";

        switch (type) {
            case ColumnMetaData.TYPE_TIMESTAMP :
                retval += "DATETIME";
                break;
            case ColumnMetaData.TYPE_TIME :
                retval += "TIME";
                break;
            case ColumnMetaData.TYPE_DATE :
                retval += "DATE";
                break;
            case ColumnMetaData.TYPE_BOOLEAN :
                retval += "BIT";
                break;
            case ColumnMetaData.TYPE_NUMBER :
            case ColumnMetaData.TYPE_INTEGER :
            case ColumnMetaData.TYPE_BIGNUMBER :
                if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
                    if (useAutoInc) {
                        retval += "BIGINT IDENTITY(0,1)";
                    } else {
                        retval += "BIGINT";
                    }
                } else {
                    if (precision == 0) {
                        if (length > 18) {
                            retval += "DECIMAL(" + length + ",0)";
                        } else {
                            if (length > 9) {
                                retval += "BIGINT";
                            } else {
                                retval += "INT";
                            }
                        }
                    } else {
                        if (precision > 0 && length > 0) {
                            length = (length > 38) ? 38 : length;
                            precision = (precision > length) ? Math.min(precision, length) : precision;
                            retval += "DECIMAL(" + length + "," + precision + ")";
                        } else {
                            retval += "FLOAT(53)";
                        }
                    }
                }
                break;
            case ColumnMetaData.TYPE_STRING :
                if (length < 8000) {
                    // Maybe use some default DB String length in case length<=0
                    if (length > 0) {
                        // VARCHAR(n)最多能存n个字节，一个中文是两个字节。
                        length = 2 * length;
                        if (length > 8000) {
                            length = 8000;
                        }
                        retval += "VARCHAR(" + length + ")";
                    } else {
                        retval += "VARCHAR(100)";
                    }
                } else {
                    retval += "TEXT"; // Up to 2bilion characters.
                }
                break;
            case ColumnMetaData.TYPE_BINARY :
                retval += "VARBINARY(MAX)";
                break;
            default :
                retval += "TEXT";
                break;
        }

        if (addCr) {
            retval += Constants.CR;
        }

        return retval;
    }

    @Override
    public String getPrimaryKeyAsString(List<String> pks) {
        if (null != pks && !pks.isEmpty()) {
            return "[" + StringUtils.join(pks.stream().distinct().collect(Collectors.toList()), "] , [") + "]";
        }

        return StringUtils.EMPTY;
    }

    @Override
    public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(td.getRemarks())) {
            results.add("EXEC [sys].sp_addextendedproperty 'MS_Description', N'%s', 'schema', N'%s', 'table', N'%s' "
                    .formatted(td.getRemarks().replace("'", ""), td.getSchemaName(), td.getTableName()));
        }

        for (ColumnDescription cd : cds) {
            if (StringUtils.isNotBlank(cd.getRemarks())) {
                results.add(
                        "EXEC [sys].sp_addextendedproperty 'MS_Description', N'%s', 'schema', N'%s', 'table', N'%s', 'column', N'%s' "
                                .formatted(cd.getRemarks().replace("'", ""), td.getSchemaName(), td.getTableName(),
                                        cd.getFieldName()));
            }
        }

        return results;
    }

}
