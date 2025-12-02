// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider.write;

import java.util.*;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import io.arkx.framework.data.db.core.provider.AbstractCommonProvider;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.util.SyncUtil;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DefaultTableDataWriteProvider extends AbstractCommonProvider implements TableDataWriteProvider {

	protected String dbSyncMode;

	protected String slaveDbCode;

	protected JdbcTemplate jdbcTemplate;

	protected String schemaName;

	protected String tableName;

	protected Map<String, Integer> columnType;

	public DefaultTableDataWriteProvider(ProductFactoryProvider factoryProvider) {
		super(factoryProvider);
		this.jdbcTemplate = new JdbcTemplate(factoryProvider.getDataSource());
		this.schemaName = null;
		this.tableName = null;
		this.columnType = null;
	}

	protected String getPrepareInsertTableSql(List<String> fieldNames) {
		String fullTableName = quoteSchemaTableName(schemaName, tableName);
		return "INSERT INTO %s ( %s ) VALUES ( %s )".formatted(fullTableName,
				quoteName(StringUtils.join(fieldNames, quoteName(","))),
				StringUtils.join(Collections.nCopies(fieldNames.size(), "?"), ","));
	}

	@Override
	public void initDbSyncInfo(String dbSyncMode, String slaveDbCode) {
		this.dbSyncMode = dbSyncMode;
		this.slaveDbCode = slaveDbCode;
	}

	@Override
	public void prepareWrite(String schemaName, String tableName, List<String> fieldNames) {
		List<String> myFiledNames = new ArrayList<>(fieldNames);
		if (SyncUtil.isShardDbSync(dbSyncMode)) {
			myFiledNames.addFirst("ark_shard_db_code");
		}
		this.columnType = getTableColumnMetaData(schemaName, tableName, myFiledNames);
		if (this.columnType.isEmpty()) {
			throw new RuntimeException("获取表:%s.%s 的字段的元信息时失败. 请联系 DBA 核查该库、表信息.".formatted(schemaName, tableName));
		}
		this.schemaName = schemaName;
		this.tableName = tableName;
	}

	@Override
	public long write(List<String> rawFieldNames, List<Object[]> rawRecordValues) {
		if (rawRecordValues.isEmpty()) {
			return 0;
		}
		List<Object[]> recordValues = rawRecordValues;
		List<String> fieldNames = new ArrayList<>(rawFieldNames);
		if (SyncUtil.isShardDbSync(dbSyncMode)) {
			fieldNames.addFirst("ark_shard_db_code");
			recordValues = new ArrayList<>(rawRecordValues.size());

			for (Object[] recordValue : rawRecordValues) {
				List<Object> newRow = new ArrayList<>(fieldNames.size());
				newRow.add(slaveDbCode);

				newRow.addAll(Arrays.asList(recordValue));

				recordValues.add(newRow.toArray());
			}
		}

		String sqlInsert = getPrepareInsertTableSql(fieldNames);
		int[] argTypes = new int[fieldNames.size()];
		for (int i = 0; i < fieldNames.size(); ++i) {
			String col = fieldNames.get(i);
			argTypes[i] = this.columnType.get(col);
		}

		PlatformTransactionManager tx = new DataSourceTransactionManager(getDataSource());

		// 动态分批控制
		long maxPacketSize = 1 * 1024 * 1024; // 达梦默认最大包大小4MB（可根据实际情况调整）
		int defaultBatchSize = 500; // 默认批次大小
		int currentBatchSize = defaultBatchSize;
		List<Object[]> currentBatch = new ArrayList<>(currentBatchSize);
		long totalCount = 0;

		for (Object[] record : recordValues) {
			currentBatch.add(record);
			long batchSize = ByteUtil.calculateBatchSize(currentBatch);

			// 如果当前批次超过最大包大小或默认批次限制，则执行插入
			if (batchSize >= maxPacketSize || currentBatch.size() >= defaultBatchSize) {
				totalCount += executeBatch(tx, sqlInsert, currentBatch, argTypes);
				currentBatch.clear();
				currentBatchSize = Math.max(1, defaultBatchSize / 2); // 动态缩小批次
			}
		}

		// 插入剩余记录
		if (!currentBatch.isEmpty()) {
			totalCount += executeBatch(tx, sqlInsert, currentBatch, argTypes);
		}

		if (log.isDebugEnabled()) {
			log.debug("{} insert data affect count: {}", getProductType(), totalCount);
		}

		recordValues.clear();
		return totalCount;
	}

	/**
	 * 执行单批次插入
	 */
	private int executeBatch(PlatformTransactionManager tx, String sql, List<Object[]> batch, int[] argTypes) {
		TransactionStatus status = tx.getTransaction(getDefaultTransactionDefinition());
		try {
			jdbcTemplate.batchUpdate(sql, batch, argTypes);
			tx.commit(status);
			if (log.isDebugEnabled()) {
				log.debug("Inserted batch: {} records, ~{} bytes", batch.size(), ByteUtil.calculateBatchSize(batch));
			}
			return batch.size();
		}
		catch (Exception e) {
			tx.rollback(status);
			throw e;
		}
	}

}
