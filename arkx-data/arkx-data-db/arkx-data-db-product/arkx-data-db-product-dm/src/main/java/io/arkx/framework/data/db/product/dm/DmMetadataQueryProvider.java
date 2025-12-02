// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.dm;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import io.arkx.framework.data.db.common.consts.Constants;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.meta.AbstractMetadataProvider;
import io.arkx.framework.data.db.core.schema.ColumnDescription;
import io.arkx.framework.data.db.core.schema.ColumnMetaData;
import io.arkx.framework.data.db.core.schema.TableDescription;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DmMetadataQueryProvider extends AbstractMetadataProvider {

    private static final String SHOW_CREATE_TABLE_SQL = "SELECT DBMS_METADATA.GET_DDL('TABLE','%s','%s') FROM DUAL ";
    private static final String SHOW_CREATE_VIEW_SQL = "SELECT DBMS_METADATA.GET_DDL('VIEW','%s','%s') FROM DUAL ";

    public DmMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public String getTableDDL(Connection connection, String schemaName, String tableName) {
        String sql = SHOW_CREATE_TABLE_SQL.formatted(tableName, schemaName);
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
    public String getViewDDL(Connection connection, String schemaName, String tableName) {
        String sql = SHOW_CREATE_VIEW_SQL.formatted(tableName, schemaName);
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
    public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
        String querySQL = "SELECT * from (%s) tmp where ROWNUM<=1 ".formatted(sql.replace(";", ""));
        return this.getSelectSqlColumnMeta(connection, querySQL);
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

    /**
     * https://eco.dameng.com/document/dm/zh-cn/sql-dev/dmpl-sql-datatype.html
     * <p>
     * https://eco.dameng.com/document/dm/zh-cn/pm/dm8_sql-data-types-operators.html
     * <p>
     * 违反表[xxx]唯一性约束: https://www.cnblogs.com/theli/p/12858875.html
     */
    @Override
    public String getFieldDefinition(ColumnMetaData v, List<String> pks, boolean useAutoInc, boolean addCr,
            boolean withRemarks) {
        String fieldname = v.getName();
        int length = v.getLength();
        int precision = v.getPrecision();

        StringBuilder retval = new StringBuilder(128);
        retval.append(" \"").append(fieldname).append("\"    ");

        int type = v.getType();
        switch (type) {
            case ColumnMetaData.TYPE_TIMESTAMP :
            case ColumnMetaData.TYPE_TIME :
                retval.append("TIMESTAMP");
                break;
            case ColumnMetaData.TYPE_DATE :
                retval.append("DATE");
                break;
            case ColumnMetaData.TYPE_BOOLEAN :
                retval.append("BIT");
                break;
            case ColumnMetaData.TYPE_NUMBER :
            case ColumnMetaData.TYPE_BIGNUMBER :
                if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
                    retval.append("BIGINT");
                } else {
                    retval.append("NUMERIC");
                    if (length > 0) {
                        if (length > 38) {
                            length = 38;
                        }

                        retval.append('(').append(length);
                        if (precision > 0) {
                            retval.append(", ").append(precision);
                        }
                        retval.append(')');
                    }
                }
                break;
            case ColumnMetaData.TYPE_INTEGER :
                retval.append("BIGINT");
                break;
            case ColumnMetaData.TYPE_STRING :
                if (null != pks && pks.contains(fieldname)) {
                    retval.append("VARCHAR(" + length + " char)");
                } else if (length > 0 && length < 1900) {
                    // 最大存储长度由数据库页面大小决定，支持按照字节存放字符串，数据库页面大小与实际最大存储长度的关系为:
                    // 4K->1900;8k->3900;16k->8000;32k->8188
                    retval.append("VARCHAR(").append(length).append(" char)");
                } else {
                    retval.append("TEXT");
                }
                break;
            case ColumnMetaData.TYPE_BINARY :
                retval.append("BLOB");
                break;
            default :
                retval.append("CLOB");
                break;
        }

        if (addCr) {
            retval.append(Constants.CR);
        }

        if ("ark_sync_id".equalsIgnoreCase(fieldname)) {
            retval.append(" IDENTITY(1, 1) ");
        }

        return retval.toString();
    }

    @Override
    public List<String> getTableColumnCommentDefinition(TableDescription td, List<ColumnDescription> cds) {
        List<String> results = new ArrayList<>();
        if (StringUtils.isNotBlank(td.getRemarks())) {
            results.add("COMMENT ON TABLE \"%s\".\"%s\" IS '%s' ".formatted(td.getSchemaName(), td.getTableName(),
                    td.getRemarks().replace("'", "")));
        }

        for (ColumnDescription cd : cds) {
            if (StringUtils.isNotBlank(cd.getRemarks())) {
                results.add("COMMENT ON COLUMN \"%s\".\"%s\".\"%s\" IS '%s' ".formatted(td.getSchemaName(),
                        td.getTableName(), cd.getFieldName(), cd.getRemarks().replace("'", "")));
            }
        }

        return results;
    }

}
