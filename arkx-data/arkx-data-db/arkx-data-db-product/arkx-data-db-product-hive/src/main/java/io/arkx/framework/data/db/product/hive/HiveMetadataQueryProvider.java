// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.hive;

import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.data.db.common.consts.Constants;
import io.arkx.framework.data.db.common.type.ProductTypeEnum;
import io.arkx.framework.data.db.common.util.UuidUtils;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.AbstractMetadataProvider;
import io.arkx.framework.data.db.core.schema.*;
import io.arkx.framework.data.db.core.util.GenerateSqlUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HiveMetadataQueryProvider extends AbstractMetadataProvider {

    private static final String SHOW_CREATE_TABLE_SQL = "SHOW CREATE TABLE `%s`.`%s` ";

    public HiveMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public String getTableDDL(Connection connection, String schemaName, String tableName) {
        String sql = SHOW_CREATE_TABLE_SQL.formatted(schemaName, tableName);
        List<String> result = new ArrayList<>();
        try (Statement st = connection.createStatement()) {
            if (st.execute(sql)) {
                try (ResultSet rs = st.getResultSet()) {
                    if (rs != null) {
                        while (rs.next()) {
                            String value = rs.getString(1);
                            Optional.ofNullable(value).ifPresent(result::add);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return String.join("\n", result);
    }

    @Override
    public String getViewDDL(Connection connection, String schemaName, String tableName) {
        return getTableDDL(connection, schemaName, tableName);
    }

    @Override
    public List<ColumnDescription> queryTableColumnMeta(Connection connection, String schemaName, String tableName) {
        String querySQL = this.getTableFieldsQuerySQL(schemaName, tableName);
        List<ColumnDescription> ret = new ArrayList<>();
        try (Statement st = connection.createStatement()) {
            HivePrepareUtils.prepare(connection, schemaName, tableName);
            try (ResultSet rs = st.executeQuery(querySQL)) {
                ResultSetMetaData m = rs.getMetaData();
                int columns = m.getColumnCount();
                for (int i = 1; i <= columns; i++) {
                    ColumnDescription cd = buildColumnDescription(m, i);
                    cd.setProductType(ProductTypeEnum.HIVE);
                    ret.add(cd);
                }
                return ret;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<String> queryTablePrimaryKeys(Connection connection, String schemaName, String tableName) {
        return Collections.emptyList();
    }

    @Override
    public List<IndexDescription> queryTableIndexes(Connection connection, String schemaName, String tableName) {
        return Collections.emptyList();
    }

    @Override
    public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
        String querySQL = " %s LIMIT 1".formatted(sql.replace(";", ""));
        return this.getSelectSqlColumnMeta(connection, querySQL,
                conn -> HivePrepareUtils.setResultSetColumnNameNotUnique(connection));
    }

    @Override
    public void testQuerySQL(Connection connection, String sql) {
        String testQuerySql = "explain %s".formatted(sql.replace(";", ""));
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
    public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr,
            boolean withRemarks) {
        String fieldname = v.getName();
        int type = v.getType();

        String retval = " `" + fieldname + "`  ";

        switch (type) {
            case ColumnMetaData.TYPE_TIMESTAMP :
            case ColumnMetaData.TYPE_TIME :
                retval += "TIMESTAMP";
                break;
            case ColumnMetaData.TYPE_DATE :
                retval += "DATE";
                break;
            case ColumnMetaData.TYPE_BOOLEAN :
                retval += "TINYINT";
                break;
            case ColumnMetaData.TYPE_NUMBER :
                retval += "DECIMAL(10,2)";
                break;
            case ColumnMetaData.TYPE_INTEGER :
            case ColumnMetaData.TYPE_BIGNUMBER :
                retval += "BIGINT";
                break;
            case ColumnMetaData.TYPE_STRING :
                retval += "STRING";
                break;
            case ColumnMetaData.TYPE_BINARY :
                retval += "BINARY";
                break;
            default :
                retval += "STRING";
                break;
        }

        if (withRemarks && StringUtils.isNotBlank(v.getRemarks())) {
            retval += " COMMENT '%s' ".formatted(v.getRemarks().replace("'", ""));
        }

        if (addCr) {
            retval += Constants.CR;
        }

        return retval;
    }

    @Override
    public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
        return Collections.emptyList();
    }

    @Override
    public void appendPrimaryKeyForCreateTableSql(StringBuilder builder, List<String> primaryKeys) {
        // HIVE表没有主键
    }

    @Override
    public void postAppendCreateTableSql(StringBuilder builder, String tblComment, List<String> primaryKeys,
            SourceProperties tblProperties) {
        if (Objects.nonNull(tblProperties)) {
            Map<String, String> keyValues = HiveTblUtils.getTblProperties(tblProperties);
            builder.append(Constants.CR);
            builder.append("STORED BY 'org.apache.hive.storage.jdbc.JdbcStorageHandler'");
            builder.append(Constants.CR);
            builder.append("TBLPROPERTIES (");
            builder.append(keyValues.entrySet().stream()
                    .map(entry -> "\t\t'%s' = '%s'".formatted(entry.getKey(), entry.getValue()))
                    .collect(Collectors.joining(",\n")));
            builder.append(")");
        } else {
            builder.append(Constants.CR);
            builder.append("STORED AS ORC");
        }
    }

    @Override
    public List<String> getCreateTableSqlList(List<ColumnDescription> fieldNames, List<String> primaryKeys,
            String schemaName, String tableName, String tableRemarks, boolean autoIncr,
            SourceProperties tblProperties) {
        List<String> sqlLists = new ArrayList<>();
        String tmpTableName = "tmp_" + UuidUtils.generateUuid();
        String createTableSql = GenerateSqlUtils.getDDLCreateTableSQL(this, fieldNames, primaryKeys, schemaName,
                tmpTableName, true, tableRemarks, autoIncr, tblProperties);
        sqlLists.add(createTableSql);

        HiveFeatures features = getProductFeatures();
        if (features.useCTAS()) {
            String createAsTableSql = "CREATE TABLE `%s`.`%s` STORED AS ORC AS (SELECT * FROM `%s`.`%s`)"
                    .formatted(schemaName, tableName, schemaName, tmpTableName);
            sqlLists.add(createAsTableSql);
        } else {
            String createAsTableSql = GenerateSqlUtils.getDDLCreateTableSQL(this, fieldNames, primaryKeys, schemaName,
                    tableName, true, tableRemarks, autoIncr, null);
            sqlLists.add(createAsTableSql);
            String selectColumns = fieldNames.stream().map(s -> "`%s`".formatted(s.getFieldName()))
                    .collect(Collectors.joining(","));
            String insertIntoSql = "INSERT INTO `%s`.`%s` SELECT %s FROM `%s`.`%s`".formatted(schemaName, tableName,
                    selectColumns, schemaName, tmpTableName);
            sqlLists.add(insertIntoSql);
        }
        String dropTmpTableSql = "DROP TABLE IF EXISTS `%s`.`%s`".formatted(schemaName, tmpTableName);
        sqlLists.add(dropTmpTableSql);
        return sqlLists;
    }

}
