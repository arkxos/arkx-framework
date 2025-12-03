// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: Li ZeMin (2413957313@qq.com)
// Date : 2024/12/16
// Location: nanjing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.tdengine;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.data.db.common.util.ObjectCastUtils;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.query.DefaultTableDataQueryProvider;
import io.arkx.framework.data.db.core.schema.SchemaTableData;

import cn.hutool.core.util.HexUtil;

public class TdengineTableDataQueryProvider extends DefaultTableDataQueryProvider {

    public TdengineTableDataQueryProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public SchemaTableData queryTableData(Connection connection, String schemaName, String tableName, int rowCount) {
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        String querySQL = "SELECT * FROM %s limit %s".formatted(fullTableName, rowCount);
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

}
