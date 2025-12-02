// Copyright tang.  All rights reserved.
// https://gitee.com/inrgihc/dbswitch
//
// Use of this source code is governed by a BSD-style license
//
// Author: tang (inrgihc@126.com)
// Date : 2020/1/2
// Location: beijing , china
/////////////////////////////////////////////////////////////
package io.arkx.framework.data.db.core.provider.sync;

import java.util.*;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;

import io.arkx.framework.data.db.core.provider.AbstractCommonProvider;
import io.arkx.framework.data.db.core.provider.ProductFactoryProvider;
import io.arkx.framework.data.db.core.util.MyArgumentTypePreparedStatementSetter;
import io.arkx.framework.data.db.core.util.SyncUtil;
import io.arkx.framework.data.db.core.util.TablePrinter;

import com.alibaba.fastjson2.JSON;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据同步抽象基类
 *
 * @author tang
 */
@Slf4j
public class DefaultTableDataSynchronizeProvider extends AbstractCommonProvider
        implements
            TableDataSynchronizeProvider {

    private JdbcTemplate jdbcTemplate;
    private PlatformTransactionManager tx;

    protected String dbSyncMode;
    protected String slaveDbCode;
    protected String schemaName;
    protected String tableName;

    protected Map<String, Integer> columnType;
    protected Map<String, String> columnTypeNames;
    protected List<String> fieldOrders;
    protected List<String> pksOrders;
    protected List<String> rawFieldOrders;
    protected List<String> rawPksOrders;
    protected String insertStatementSql;
    protected String updateStatementSql;
    protected String deleteStatementSql;
    protected int[] insertArgsType;
    protected int[] updateArgsType;
    protected int[] deleteArgsType;

    public DefaultTableDataSynchronizeProvider(ProductFactoryProvider factoryProvider) {
        super(factoryProvider);
        this.jdbcTemplate = new JdbcTemplate(factoryProvider.getDataSource());
        this.tx = new DataSourceTransactionManager(factoryProvider.getDataSource());
        this.columnType = new HashMap<>();
    }

    @Override
    public void prepare(String schemaName, String tableName, List<String> fieldNames, List<String> pks,
            String dbSyncMode, String slaveDbCode) {
        // if (fieldNames.isEmpty() || pks.isEmpty() || fieldNames.size() < pks.size())
        // {
        // throw new IllegalArgumentException("字段列表和主键列表不能为空，或者字段总个数应不小于主键总个数");
        // }
        if (!fieldNames.containsAll(pks)) {
            throw new IllegalArgumentException("字段列表必须包含主键列表");
        }

        if (pks.isEmpty()) {
            if (fieldNames.contains("ark_shard_db_code") && fieldNames.contains("ark_sync_raw_id")) {
                pks.add("ark_shard_db_code");
                pks.add("ark_sync_raw_id");
            }
        }

        this.dbSyncMode = dbSyncMode;
        this.slaveDbCode = slaveDbCode;

        this.schemaName = schemaName;
        this.tableName = tableName;

        this.rawPksOrders = new ArrayList<>(pks);
        this.rawFieldOrders = new ArrayList<>(fieldNames);

        if (SyncUtil.isShardDbSync(dbSyncMode)) {
            fieldNames.addFirst("ark_shard_db_code");
            pks.addFirst("ark_shard_db_code");
        }

        this.columnType = getTableColumnMetaData(schemaName, tableName, fieldNames);
        this.columnTypeNames = getTableColumnTypeNames(schemaName, tableName, fieldNames);

        this.fieldOrders = new ArrayList<>(fieldNames);
        this.pksOrders = new ArrayList<>(pks);

        this.insertStatementSql = getInsertPrepareStatementSql(schemaName, tableName, fieldNames);
        this.updateStatementSql = getUpdatePrepareStatementSql(schemaName, tableName, fieldNames, pks);
        this.deleteStatementSql = getDeletePrepareStatementSql(schemaName, tableName, pks);

        insertArgsType = new int[fieldNames.size()];
        for (int k = 0; k < fieldNames.size(); ++k) {
            String field = fieldNames.get(k);
            insertArgsType[k] = this.columnType.get(field);
        }

        updateArgsType = new int[fieldNames.size()];
        int idx = 0;
        for (int i = 0; i < fieldNames.size(); ++i) {
            String field = fieldNames.get(i);
            if (!pks.contains(field)) {
                updateArgsType[idx++] = this.columnType.get(field);
            }
        }
        for (String pk : pks) {
            updateArgsType[idx++] = this.columnType.get(pk);
        }

        deleteArgsType = new int[pks.size()];
        for (int j = 0; j < pks.size(); ++j) {
            String pk = pks.get(j);
            deleteArgsType[j] = this.columnType.get(pk);
        }
    }

    @Override
    public long executeInsert(List<Object[]> rawRecords) {
        TransactionStatus status = tx.getTransaction(getDefaultTransactionDefinition());
        if (log.isDebugEnabled()) {
            log.debug("Execute Insert SQL : {}", this.insertStatementSql);
        }

        List<Object[]> records = rawRecords;
        try {
            try {
                if (SyncUtil.isShardDbSync(dbSyncMode)) {
                    records = new ArrayList<>(rawRecords.size());
                    for (Object[] rawRecord : rawRecords) {
                        List<Object> newRecord = new ArrayList<>(rawRecord.length + 1);
                        newRecord.add(this.slaveDbCode);
                        for (int i = 0; i < rawRecord.length; i++) {
                            newRecord.add(rawRecord[i]);
                        }
                        records.add(newRecord.toArray());
                    }
                }

                TablePrinter.printTable(tableName, fieldOrders, columnTypeNames, records);

                jdbcTemplate.batchUpdate(this.insertStatementSql, records, this.insertArgsType);
            } catch (Exception e) {
                // if (e instanceof java.sql.BatchUpdateException) {
                int i = 0;
                for (Object[] dataList : records) {
                    try {
                        i++;
                        jdbcTemplate.update(this.insertStatementSql, new MyArgumentTypePreparedStatementSetter(
                                fieldOrders, columnTypeNames, dataList, this.insertArgsType));
                    } catch (Exception ex) {
                        System.err.println("[" + i + "]Failed to insert by SQL: " + this.insertStatementSql
                                + ", value: +" + JSON.toJSONString(dataList));
                        List<Object[]> oneRecord = new ArrayList<>(1);
                        oneRecord.add(records.get(i - 1));
                        TablePrinter.printTable(tableName, fieldOrders, columnTypeNames, oneRecord);
                        throw ex;
                    }
                }
                // } else {
                // throw e;
                // }
            }

            tx.commit(status);
            return rawRecords.size();
        } catch (Exception e) {
            tx.rollback(status);
            throw e;
        }
    }

    @Override
    public long executeUpdate(List<Object[]> records) {
        List<Object[]> dataLists = new LinkedList<>();
        for (Object[] r : records) {
            Object[] nr = new Object[this.rawFieldOrders.size()];
            if (SyncUtil.isShardDbSync(dbSyncMode)) {
                nr = new Object[this.rawFieldOrders.size() + 1];
            }
            int idx = 0;
            for (int i = 0; i < this.rawFieldOrders.size(); ++i) {
                String field = this.rawFieldOrders.get(i);
                if (!this.rawPksOrders.contains(field)) {
                    int index = this.rawFieldOrders.indexOf(field);
                    nr[idx++] = r[index];
                }
            }
            if (SyncUtil.isShardDbSync(dbSyncMode)) {
                nr[idx++] = this.slaveDbCode;
            }
            for (int j = 0; j < this.rawPksOrders.size(); ++j) {
                String pk = this.rawPksOrders.get(j);
                int index = this.rawFieldOrders.indexOf(pk);
                nr[idx++] = r[index];
            }
            dataLists.add(nr);
        }

        TransactionStatus status = tx.getTransaction(getDefaultTransactionDefinition());
        if (log.isDebugEnabled()) {
            log.debug("Execute Update SQL : {}", this.updateStatementSql);
        }

        try {
            try {
                jdbcTemplate.batchUpdate(this.updateStatementSql, dataLists, this.updateArgsType);
            } catch (Exception e) {
                if (e instanceof java.sql.BatchUpdateException) {
                    for (Object[] dataList : dataLists) {
                        try {
                            jdbcTemplate.update(this.updateStatementSql, dataList, this.updateArgsType);
                        } catch (Exception ex) {
                            log.error("Failed to update by SQL: {}, value: {}", this.updateStatementSql,
                                    JSON.toJSONString(dataList));
                            throw ex;
                        }
                    }
                } else {
                    throw e;
                }
            }

            tx.commit(status);
            return dataLists.size();
        } catch (Exception e) {
            tx.rollback(status);
            throw e;
        }
    }

    @Override
    public long executeDelete(List<Object[]> records) {
        List<Object[]> dataLists = new LinkedList<>();
        for (Object[] r : records) {
            Object[] nr = new Object[this.pksOrders.size()];
            int startIndex = 0;
            if (SyncUtil.isShardDbSync(dbSyncMode)) {
                nr[startIndex++] = this.slaveDbCode;
            }
            for (int i = 0; i < this.rawPksOrders.size(); ++i) {
                String pk = this.rawPksOrders.get(i);
                int index = this.rawFieldOrders.indexOf(pk);
                nr[startIndex + i] = r[index];
            }
            dataLists.add(nr);
        }

        TransactionStatus status = tx.getTransaction(getDefaultTransactionDefinition());
        if (log.isDebugEnabled()) {
            log.debug("Execute Delete SQL : {}", this.deleteStatementSql);
        }

        try {
            jdbcTemplate.batchUpdate(this.deleteStatementSql, dataLists, this.deleteArgsType);
            tx.commit(status);
            return dataLists.size();
        } catch (Exception e) {
            tx.rollback(status);
            throw e;
        } finally {
            dataLists.clear();
        }
    }

    /**
     * 生成Insert操作的SQL语句
     *
     * @param schemaName
     *            模式名称
     * @param tableName
     *            表名称
     * @param fieldNames
     *            字段列表
     * @return Insert操作的SQL语句
     */
    protected String getInsertPrepareStatementSql(String schemaName, String tableName, List<String> fieldNames) {
        List<String> placeHolders = Collections.nCopies(fieldNames.size(), "?");
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        return "INSERT INTO %s ( %s ) VALUES ( %s )".formatted(fullTableName,
                quoteName(StringUtils.join(fieldNames, quoteName(","))), StringUtils.join(placeHolders, ","));
    }

    /**
     * 生成Update操作的SQL语句
     *
     * @param schemaName
     *            模式名称
     * @param tableName
     *            表名称
     * @param fieldNames
     *            字段列表
     * @param pks
     *            主键列表
     * @return Update操作的SQL语句
     */
    protected String getUpdatePrepareStatementSql(String schemaName, String tableName, List<String> fieldNames,
            List<String> pks) {
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        List<String> uf = fieldNames.stream().filter(field -> !pks.contains(field))
                .map(field -> "%s=?".formatted(quoteName(field))).collect(Collectors.toList());
        List<String> uw = pks.stream().map(pk -> "%s=?".formatted(quoteName(pk))).collect(Collectors.toList());
        return "UPDATE %s SET %s WHERE %s".formatted(fullTableName, StringUtils.join(uf, " , "),
                StringUtils.join(uw, " AND "));
    }

    /**
     * 生成Delete操作的SQL语句
     *
     * @param schemaName
     *            模式名称
     * @param tableName
     *            表名称
     * @param pks
     *            主键列表
     * @return Delete操作的SQL语句
     */
    protected String getDeletePrepareStatementSql(String schemaName, String tableName, List<String> pks) {
        String fullTableName = quoteSchemaTableName(schemaName, tableName);
        List<String> uw = pks.stream().map(pk -> "%s=?".formatted(quoteName(pk))).collect(Collectors.toList());
        return "DELETE FROM %s WHERE %s ".formatted(fullTableName, StringUtils.join(uw, "  AND  "));
    }

}
