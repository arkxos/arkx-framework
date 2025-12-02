// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.oscar;

import java.sql.*;
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
public class OscarMetadataQueryProvider extends AbstractMetadataProvider {

    private static final String SHOW_CREATE_TABLE_SQL = "SELECT \"SYS_GET_TABLEDEF\" FROM \"V_SYS_TABLE\" WHERE \"SCHEMANAME\"= ? AND \"TABLENAME\"= ? ";
    private static final String SHOW_CREATE_VIEW_SQL = "SELECT \"DEFINITION\" FROM \"V_SYS_VIEWS\" WHERE \"SCHEMANAME\"= ? AND \"VIEWNAME\"= ?";

    public OscarMetadataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public String getTableDDL(Connection connection, String schemaName, String tableName) {
        String sql = SHOW_CREATE_TABLE_SQL.formatted(tableName, schemaName);
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, schemaName);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs != null && rs.next()) {
                    return rs.getString(1);
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
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, schemaName);
            ps.setString(2, tableName);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs != null && rs.next()) {
                    return rs.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public List<ColumnDescription> querySelectSqlColumnMeta(Connection connection, String sql) {
        String querySQL = "SELECT * from (%s) tmp LIMIT 0 ".formatted(sql.replace(";", ""));
        return this.getSelectSqlColumnMeta(connection, querySQL);
    }

    @Override
    protected String getTableFieldsQuerySQL(String schemaName, String tableName) {
        return "SELECT * FROM \"%s\".\"%s\"  ".formatted(schemaName, tableName);
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
            case ColumnMetaData.TYPE_BIGNUMBER :
            case ColumnMetaData.TYPE_INTEGER :
                retval.append("BIGINT");
                break;
            case ColumnMetaData.TYPE_NUMBER :
                if (null != pks && !pks.isEmpty() && pks.contains(fieldname)) {
                    retval.append("BIGINT");
                } else {
                    if (length > 0) {
                        if (precision > 0 || length > 18) {
                            if ((length + precision) > 0 && precision > 0) {
                                // Numeric(Precision, Scale): Precision = total length; Scale = decimal places
                                retval.append("NUMERIC(" + (length + precision) + ", " + precision + ")");
                            } else {
                                retval.append("DOUBLE PRECISION");
                            }
                        } else {
                            if (length > 9) {
                                retval.append("BIGINT");
                            } else {
                                if (length < 5) {
                                    retval.append("SMALLINT");
                                } else {
                                    retval.append("INTEGER");
                                }
                            }
                        }

                    } else {
                        retval.append("DOUBLE PRECISION");
                    }
                }
                break;
            case ColumnMetaData.TYPE_STRING :
                if (2 * length >= Constants.CLOB_LENGTH) {
                    retval.append("TEXT");
                } else {
                    if (length == 1) {
                        retval.append("VARCHAR(2)");
                    } else if (length > 0 && length < 2048) {
                        retval.append("VARCHAR(").append(2 * length).append(')');
                    } else {
                        retval.append("TEXT");
                    }
                }
                break;
            case ColumnMetaData.TYPE_BINARY :
                retval.append("BLOB");
                break;
            default :
                retval.append("TEXT");
                break;
        }

        if (addCr) {
            retval.append(Constants.CR);
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
