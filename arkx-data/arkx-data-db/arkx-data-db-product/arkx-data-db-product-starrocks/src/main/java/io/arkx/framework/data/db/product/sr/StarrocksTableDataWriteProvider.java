// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.product.sr;

import java.util.List;

import io.arkx.framework.data.db.common.entity.CloseableDataSource;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.provider.write.AutoCastTableDataWriteProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StarrocksTableDataWriteProvider extends AutoCastTableDataWriteProvider {

    private final CloseableDataSource dataSource;

    private final StarRocksUtils starRocksUtils = new StarRocksUtils();

    public StarrocksTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
        dataSource = (CloseableDataSource) factoryProvider.getDataSource();
    }

    @Override
    public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
        super.prepareWrite(schemaName, tableName, fieldNames);
        try {
            starRocksUtils.init(schemaName, tableName, dataSource);
        } catch (Exception e) {
            log.warn("Failed to init by StarRocksUtils#init(),information: {}", e.getMessage());
        }
    }

    @Override
    public long write(List<String> fieldNames, List<Object[]> recordValues) {
        try {
            return starRocksUtils.addOrUpdateData(fieldNames, recordValues);
        } catch (Exception e) {
            log.warn("Failed to insertOrUpdate data by StarRocksUtils#addOrUpdateData(),information: {}",
                    e.getMessage());
            return super.write(fieldNames, recordValues);
        }
    }

}
