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
import io.arkx.framework.data.db.core.provider.sync.AutoCastTableDataSynchronizeProvider;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class StarrocksTableDataSynchronizer extends AutoCastTableDataSynchronizeProvider {

	private List<String> fieldNames;

	private final CloseableDataSource dataSource;

	private final StarRocksUtils starRocksUtils = new StarRocksUtils();

	public StarrocksTableDataSynchronizer(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
		dataSource = (CloseableDataSource) factoryProvider.getDataSource();
	}

	@Override
	public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks,
			String dbSyncMode, String slaveDbCode) {
		this.fieldNames = fieldNames;
		super.prepare(schemaName, tableName, fieldNames, pks, dbSyncMode, slaveDbCode);
		try {
			starRocksUtils.init(schemaName, tableName, dataSource);
		}
		catch (Exception e) {
			log.warn("Failed to init by StarRocksUtils#init(),information:: {}", e.getMessage());
		}
	}

	@Override
	public long executeInsert(List<Object[]> recordValues) {
		try {
			return starRocksUtils.addOrUpdateData(fieldNames, recordValues);
		}
		catch (Exception e) {
			log.warn("Failed to addOrUpdateData by StarRocksUtils#addOrUpdateData(),information:: {}", e.getMessage());
			return super.executeInsert(recordValues);
		}
	}

	@Override
	public long executeUpdate(List<Object[]> recordValues) {
		try {
			return starRocksUtils.addOrUpdateData(fieldNames, recordValues);
		}
		catch (Exception e) {
			log.warn("Failed to addOrUpdateData by StarRocksUtils#addOrUpdateData(),information:: {}", e.getMessage());
			return super.executeUpdate(recordValues);
		}
	}

}
