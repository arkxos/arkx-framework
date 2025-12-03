// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.elasticsearch;

import java.sql.Connection;
import java.sql.Statement;
import java.util.*;

import org.apache.commons.collections4.CollectionUtils;

import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.write.DefaultTableDataWriteProvider;

import com.alibaba.fastjson2.JSON;
import com.google.common.collect.Lists;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ElasticsearchTableDataWriteProvider extends DefaultTableDataWriteProvider {

    private String indexName;

    public ElasticsearchTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
    }

    @Override
    public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
        this.indexName = tableName;
    }

    @Override
    public long write(List<String> fieldNames, List<Object[]> recordValues) {
        if (CollectionUtils.isEmpty(fieldNames) || CollectionUtils.isEmpty(recordValues)) {
            return 0L;
        }
        Map<String, Object> bulkDocuments = new HashMap<>();
        bulkDocuments.put("indexName", indexName);
        try (Connection connection = getDataSource().getConnection()) {
            Statement statement = connection.createStatement();
            for (List<Object[]> partRecordValues : Lists.partition(recordValues, 500)) {
                bulkDocuments.put("sources", asString(fieldNames, partRecordValues));
                String sql = JSON.toJSONString(bulkDocuments);
                statement.executeUpdate(sql);
            }
            return recordValues.size();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private List<String> asString(List<String> fieldNames, List<Object[]> recordValues) {
        int fieldCount = Math.min(fieldNames.size(), recordValues.getFirst().length);
        List<String> rows = new ArrayList<>(recordValues.size());
        for (Object[] row : recordValues) {
            Map<String, Object> columns = new LinkedHashMap<>(fieldCount);
            for (int i = 0; i < fieldCount; ++i) {
                columns.put(fieldNames.get(i), row[i]);
            }
            rows.add(JSON.toJSONString(columns));
        }
        return rows;
    }

}
