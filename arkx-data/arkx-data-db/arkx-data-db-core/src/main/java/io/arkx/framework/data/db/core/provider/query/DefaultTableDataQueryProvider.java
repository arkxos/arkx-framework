// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/// //////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider.query;

import cn.hutool.core.util.HexUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import io.arkx.framework.data.db.common.consts.Constants;
import io.arkx.framework.data.db.common.entity.IncrementPoint;
import io.arkx.framework.data.db.common.entity.ResultSetWrapper;
import io.arkx.framework.data.db.common.util.JdbcTypesUtils;
import io.arkx.framework.data.db.common.util.ObjectCastUtils;
import io.arkx.framework.data.db.core.features.ProductFeatures;
import io.arkx.framework.data.db.core.provider.AbstractCommonProvider;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.schema.ColumnValue;
import io.arkx.framework.data.db.core.schema.SchemaTableData;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DefaultTableDataQueryProvider
        extends AbstractCommonProvider
        implements TableDataQueryProvider {

    private final int resultSetType = ResultSet.TYPE_FORWARD_ONLY;
    private final int concurrency = ResultSet.CONCUR_READ_ONLY;

    private int fetchSize = Constants.DEFAULT_FETCH_SIZE;

    public DefaultTableDataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public int getQueryFetchSize() {
        return this.fetchSize;
    }

    @Override
    public void setQueryFetchSize(int size) {
        if (size < Constants.MINIMUM_FETCH_SIZE) {
            throw new IllegalArgumentException(
                    "设置的批量处理行数的大小fetchSize=" + size + "不得小于" + Constants.MINIMUM_FETCH_SIZE);
        }
        this.fetchSize = size;
    }

    public ResultSetWrapper queryTableData(String schemaName, String tableName,
                                           String shardDbCode, List<String> fields,
                                           IncrementPoint point, List<String> orders) {
        StringBuilder sb = new StringBuilder("SELECT ");
        sb.append(quoteName(StringUtils.join(fields, quoteName(","))));
        sb.append(" FROM ");
        sb.append(quoteSchemaTableName(schemaName, tableName));

        sb.append(" WHERE 1=1");
        if (StringUtils.isNotBlank(shardDbCode)) {
            sb.append(" AND ark_shard_db_code=").append(shardDbCode);
        }
        if (IncrementPoint.EMPTY != point && point.isWorkable()) {
            sb.append(" AND ").append(toGreaterThanCondition(point));
        }
        if (CollectionUtils.isNotEmpty(orders)) {
            sb.append(" ORDER BY ");
            sb.append(quoteName(StringUtils.join(orders, quoteName(","))));
        }
        ProductFeatures features = getProductFeatures();
        return this.selectTableData(sb.toString(), features.convertFetchSize(this.fetchSize));
    }

    protected String toGreaterThanCondition(IncrementPoint point) {
        StringBuilder sb = new StringBuilder();
        sb.append(quoteName(point.getColumnName()));
        sb.append(" > ");
        if (JdbcTypesUtils.isInteger(point.getJdbcType())) {
            sb.append(point.getMaxValue());
        } else {
            sb.append("'").append(point.getMaxValue()).append("'");
        }
        return sb.toString();
    }

    protected ResultSetWrapper selectTableData(String sql, int fetchSize) {
        if (log.isDebugEnabled()) {
            log.debug("Query table data sql :{}", sql);
        }

        try {
            Connection connection = getDataSource().getConnection();
            Statement statement = connection.createStatement(resultSetType, concurrency);
            statement.setQueryTimeout(Constants.DEFAULT_QUERY_TIMEOUT_SECONDS);
            statement.setFetchSize(fetchSize);
            return ResultSetWrapper.builder()
                    .connection(connection)
                    .statement(statement)
                    .resultSet(statement.executeQuery(sql))
                    .build();
        } catch (Throwable t) {
            log.error("查询数据异常", sql);
            throw new RuntimeException(t);
        }
    }

    @Override
    public SchemaTableData queryTableData(Connection connection, String schemaName, String tableName,
                                          int rowCount) {
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        String querySQL = "SELECT * FROM %s ".formatted(fullTableName);
        SchemaTableData data = new SchemaTableData();
        data.setSchemaName(schemaName);
        data.setTableName(tableName);
        data.setColumns(new ArrayList<>());
        data.setRows(new ArrayList<>());
        try (Statement st = connection.createStatement()) {
            beforeExecuteQuery(connection, schemaName, tableName);
            try (ResultSet rs = st.executeQuery(querySQL)) {
                ResultSetMetaData m = rs.getMetaData();
                int count = m.getColumnCount();
                for (int i = 1; i <= count; i++) {
                    data.getColumns().add(m.getColumnLabel(i));
                }

                int counter = 0;
                while (rs.next() && counter++ < rowCount) {
                    List<Object> row = new ArrayList<>(count);
                    for (int i = 1; i <= count; i++) {
                        Object value = rs.getObject(i);
                        if (value instanceof byte[] bytes) {
                            row.add(HexUtil.encodeHexStr(bytes));
                        } else if (value instanceof java.sql.Clob) {
                            row.add(ObjectCastUtils.castToString(value));
                        } else if (value instanceof java.sql.Blob) {
                            byte[] bytes = ObjectCastUtils.castToByteArray(value);
                            row.add(HexUtil.encodeHexStr(bytes));
                        } else {
                            row.add(null == value ? null : value.toString());
                        }
                    }
                    data.getRows().add(row);
                }

                return data;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ColumnValue queryFieldMaxValue(Connection connection, String schemaName, String tableName, String filedName) {
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        String querySQL = "SELECT MAX(%s) FROM %s ".formatted(quoteName(filedName), fullTableName);
        try (Statement st = connection.createStatement()) {
            try (ResultSet rs = st.executeQuery(querySQL)) {
                ResultSetMetaData m = rs.getMetaData();
                int dataType = m.getColumnType(1);
                if (rs.next()) {
                    return new ColumnValue(dataType, rs.getObject(1));
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    protected void beforeExecuteQuery(Connection connection, String schema, String table) {
        // nothing except for hive
    }

}
