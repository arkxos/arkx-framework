// package org.ark.framework.orm.sync;
//
// import io.arkx.framework.Config;
// import io.arkx.framework.commons.util.ClassLoadUtil;
// import io.arkx.framework.data.db.connection.ConnectionConfig;
// import io.arkx.framework.data.db.connection.ConnectionPoolManager;
// import io.arkx.framework.extend.plugin.ExtendPluginProvider;
// import io.arkx.framework.extend.plugin.PluginManager;
// import org.ark.framework.orm.*;
// import org.ark.framework.orm.sql.DBContext;
// import org.ark.framework.orm.sync.source.SchemaSetDataSource;
//
// import com.google.common.collect.ImmutableSet;
//
// import lombok.extern.slf4j.Slf4j;
//
// import java.io.File;
// import java.net.MalformedURLException;
// import java.util.Arrays;
// import java.util.List;
// import java.sql.Timestamp;
// import java.util.*;
//
// import org.ark.framework.orm.Schema;
// import org.ark.framework.orm.SchemaSet;
// import org.ark.framework.orm.sync.metadata.SyncStatus;
//
// import io.arkx.framework.commons.util.DateUtil;
// import io.arkx.framework.data.jdbc.SessionFactory;
//
/// **
// * 增量同步演示类
// * 演示如何使用增量同步框架
// */
// @Slf4j
// public class Demo {
//
// /**
// * 演示基本用法（数据库到数据库）
// */
// public static void basicDemo() {
// // 初始化同步管理器 - 数据库到数据库模式
// IncrementalSyncManager syncManager = new IncrementalSyncManager("sourceDb",
// "targetDb");
//
// // 配置要同步的表
// syncManager.registerTableConfig("USER_INFO",
// TableSyncConfig.basic("UPDATE_TIME", "USER_ID"));
// syncManager.registerTableConfig("ORDER_DETAIL",
// TableSyncConfig.basic("MODIFY_TIME", "ORDER_ID"));
//
// // 配置高级选项的表
// TableSyncConfig productConfig = TableSyncConfig.builder()
// .timestampField("LAST_MODIFIED")
// .primaryKeyField("PRODUCT_ID")
// .conflictStrategy(TableSyncConfig.ConflictStrategy.MERGE)
// .batchSize(500)
// .build();
// syncManager.registerTableConfig("PRODUCT", productConfig);
//
// // 执行同步
// SyncSummary summary = syncManager.syncAllTables();
//
// // 输出结果
// log.info(summary.getSummary());
//
// // 检查结果并处理
// if (summary.isAllSuccess()) {
// log.info("所有表同步成功");
// } else {
// log.warn("部分表同步失败，详情：");
// summary.getTableErrors().forEach((table, error) -> {
// log.error("表 {} 同步失败: {}", table, error.getMessage());
// });
// }
// }
//
// /**
// * 演示使用SchemaSet作为数据源
// */
// public static void schemaSetSourceDemo(String dbFilePath) {
// log.info("开始演示SchemaSet数据源同步...");
//
// // 检查文件是否存在
// File dbFile = new File(dbFilePath);
// if (!dbFile.exists() || !dbFile.isFile()) {
// log.error("数据文件不存在: {}", dbFilePath);
// return;
// }
//
// // 使用SchemaSetImporter从文件加载数据
// SchemaSetImporter importer = new SchemaSetImporter();
// SchemaSetImporter.ImportResult importResult =
// importer.importFromFile(dbFilePath);
//
// log.info("从文件 {} 导入了 {} 个表, {} 条记录",
// dbFilePath, importResult.getTotalTables(), importResult.getTotalRecords());
//
// // 创建SchemaSet数据源的同步管理器
// SchemaSetDataSource schemaSetDataSource = new
// SchemaSetDataSource("FileDataSource");
// List<SchemaSet<?>> schemaSets = importResult.getSchemaSets();
//
// // 将导入的所有SchemaSet添加到数据源
// for (SchemaSet<?> schemaSet : importResult.getSchemaSets()) {
// String tableCode = schemaSet.getSchema().getTableCode();
//
//// SchemaColumn[] primaryKeyColumns =
// SchemaUtil.getPrimaryKeyColumns(SchemaUtil.getPrimaryKeyColumns(schemaSet.getSchema()
//// .getColumns()));
//// //没有设置主键 默认第一个为主键
//// if (primaryKeyColumns.length == 0) {
//// SchemaColumn[] columns = schemaSet.getSchema().getColumns();
//// Arrays.stream(columns).findFirst().ifPresent(column -> {
//// column.setPrimaryKey(true);
//// });
//// }
//
// schemaSetDataSource.addSchemaSet(tableCode, schemaSet);
// log.info("添加表 {} 的SchemaSet到数据源，包含 {} 条记录", tableCode, schemaSet.size());
// }
//
// // 使用这个数据源创建同步管理器
// IncrementalSyncManager syncManager = new IncrementalSyncManager(
// schemaSetDataSource,
// new org.ark.framework.orm.sync.source.DatabaseDataSource("TestMysql02"));
//
// // 配置同步表 - 这里我们假设知道要同步哪些表及其时间戳和主键字段
// // 在实际使用中，您需要根据实际表结构配置
// // 这里仅为示例，您需要根据实际情况修改
// List<String> allTables = importer.getAllTableCodes(dbFilePath);
// for (String tableCode : allTables) {
// // 这里假设每个表都有 UPDATE_TIME 和 ID 字段作为时间戳和主键
// // 在实际应用中应该根据实际表结构配置
// TableSyncConfig config = TableSyncConfig.basic("UPDATE_TIME", "ID");
// syncManager.registerTableConfig(tableCode, config);
// log.info("注册表 {} 的同步配置", tableCode);
// }
//
// // 也可以针对特定表添加自定义配置
// if (allTables.contains("USER_INFO")) {
// TableSyncConfig userConfig = TableSyncConfig.builder()
// .timestampField("LAST_MODIFIED")
// .primaryKeyField("USER_ID")
// .conflictStrategy(TableSyncConfig.ConflictStrategy.MERGE)
// .build();
// syncManager.registerTableConfig("USER_INFO", userConfig);
// }
//
// // 执行同步
// log.info("开始执行所有表的同步...");
// SyncSummary summary = syncManager.syncAllTables();
//
// // 输出结果
// log.info("同步结果: {}", summary.getSummary());
//
// // 检查结果并处理
// if (summary.isAllSuccess()) {
// log.info("所有表同步成功");
// } else {
// log.warn("部分表同步失败，详情：");
// summary.getTableErrors().forEach((table, error) -> {
// log.error("表 {} 同步失败: {}", table, error.getMessage());
// });
// }
// }
//
// /**
// * 演示单表同步
// */
// public static void singleTableDemo() {
// IncrementalSyncManager syncManager = new IncrementalSyncManager("sourceDb",
// "targetDb");
//
// // 创建高级配置
// TableSyncConfig config = TableSyncConfig.builder()
// .timestampField("UPDATE_TIME")
// .primaryKeyField("USER_ID")
// // 只同步指定字段
// .includedFields(ImmutableSet.of("USER_ID", "USER_NAME", "EMAIL", "PHONE",
// "UPDATE_TIME"))
// // 启用验证
// .enableValidation(true)
// .validationStrategy(TableSyncConfig.ValidationStrategy.PRIMARY_KEY_SAMPLE)
// .build();
//
// // 执行单表同步
// SyncResult result = syncManager.syncTable("USER_INFO", config);
//
// // 检查结果
// if (result.isSuccess()) {
// log.info("表 USER_INFO 同步成功，共处理 {} 条记录", result.getRecordsProcessed());
// } else {
// log.error("表 USER_INFO 同步失败: {}",
// result.getError() != null ? result.getError().getMessage() :
// result.getStatus());
// }
// }
//
// /**
// * 创建独立的SchemaSet数据源示例
// */
// public static void standaloneSchemaSetDataSourceDemo(String dbFilePath) {
// log.info("开始演示独立SchemaSet数据源同步...");
//
// // 使用SchemaSetImporter从文件加载数据
// SchemaSetImporter importer = new SchemaSetImporter();
// SchemaSetImporter.ImportResult importResult =
// importer.importFromFile(dbFilePath);
//
// // 创建SchemaSet数据源
// SchemaSetDataSource schemaSetDataSource = new
// SchemaSetDataSource("DynamicFileDataSource");
//
// // 添加所有导入的SchemaSet
// for (SchemaSet<?> schemaSet : importResult.getSchemaSets()) {
// String tableCode = schemaSet.getSchema().getTableCode();
// schemaSetDataSource.addSchemaSet(tableCode, schemaSet);
// log.info("添加表 {} 的SchemaSet到独立数据源", tableCode);
// }
//
// // 使用这个数据源创建同步管理器
// IncrementalSyncManager syncManager = new IncrementalSyncManager(
// schemaSetDataSource,
// new org.ark.framework.orm.sync.source.DatabaseDataSource("targetDb"));
//
// // 注册配置 - 根据实际表结构设置
// List<String> tableCodes = importer.getAllTableCodes(dbFilePath);
// for (String tableCode : tableCodes) {
// // 默认配置，实际使用时需要根据表结构调整
// syncManager.registerTableConfig(tableCode,
// TableSyncConfig.basic("UPDATE_TIME", "ID"));
// }
//
// // 执行同步
// SyncSummary summary = syncManager.syncAllTables();
// log.info("同步结果: {}", summary.getSummary());
// }
//
// /**
// * 主方法
// *
// * @param args 命令行参数
// */
// public static void main(String[] args) {
// try {
// log.info("开始演示增量同步功能");
//
// Config.withTestMode();
// Config.loadConfig();
// PluginManager.initTestPlugin();
//
// ExtendPluginProvider.getInstance().start();
//
// String databaseType =
// ConnectionPoolManager.getDBConnConfig("TestMysql02").getDatabaseType();
//
// //先加载jar包
// try {
// ClassLoadUtil.addJarPath("E:\\db-data\\");
//
// } catch (MalformedURLException e) {
// throw new RuntimeException(e);
// }
//
// // 指定.db文件路径
// String dbFilePath = "E:\\db-data\\arkx.db";
//
// // SchemaSet数据源模式 - 使用真实的数据文件
// schemaSetSourceDemo(dbFilePath);
//
// // 独立SchemaSet数据源 - 使用真实的数据文件
// // standaloneSchemaSetDataSourceDemo(dbFilePath);
//
// log.info("增量同步演示完成");
// } catch (Exception e) {
// log.error("演示过程中发生错误", e);
// }
// }
//
// /**
// * 并行同步演示类
// * 用于展示并行同步功能的使用方法和性能优势
// */
// public void runDemo() {
// log.info("开始并行同步演示...");
//
// // 准备测试数据
// String sourceTableName = "DemoTable";
// int recordCount = 10000;
// SchemaSet<TestSchema> testData = createTestData(sourceTableName,
// recordCount);
//
// log.info("创建了 {} 条测试数据", recordCount);
//
// // 准备数据源
// SchemaSetDataSource dataSource = new SchemaSetDataSource("TestSource");
// dataSource.addSchemaSet(sourceTableName, testData);
//
// // 创建配置
// TableSyncConfig config = new TableSyncConfig();
// config.setTimestampField("update_time");
//
// // 创建进度监听器
// ProgressListener listener = createProgressListener();
//
// // 1. 串行处理
// long startTimeSerial = System.currentTimeMillis();
//
// log.info("开始串行处理...");
// ParallelSyncProcessor serialProcessor = new ParallelSyncProcessor(1,
// recordCount, "default");
// serialProcessor.addProgressListener(listener);
// SyncResult serialResult = serialProcessor.processSchemaSet(sourceTableName,
// testData);
//
// long serialDuration = System.currentTimeMillis() - startTimeSerial;
// log.info("串行处理完成，耗时: {}ms, 处理记录数: {}", serialDuration,
// serialResult.getRecordsProcessed());
//
// // 2. 并行处理（4线程）
// long startTimeParallel4 = System.currentTimeMillis();
//
// log.info("开始4线程并行处理...");
// ParallelSyncProcessor parallelProcessor4 = new ParallelSyncProcessor(4, 500,
// "default");
// parallelProcessor4.addProgressListener(listener);
// SyncResult parallelResult4 =
// parallelProcessor4.processSchemaSet(sourceTableName, testData);
//
// long parallel4Duration = System.currentTimeMillis() - startTimeParallel4;
// log.info("4线程并行处理完成，耗时: {}ms, 处理记录数: {}", parallel4Duration,
// parallelResult4.getRecordsProcessed());
//
// // 3. 并行处理（8线程）
// long startTimeParallel8 = System.currentTimeMillis();
//
// log.info("开始8线程并行处理...");
// ParallelSyncProcessor parallelProcessor8 = new ParallelSyncProcessor(8, 500,
// "default");
// parallelProcessor8.addProgressListener(listener);
// SyncResult parallelResult8 =
// parallelProcessor8.processSchemaSet(sourceTableName, testData);
//
// long parallel8Duration = System.currentTimeMillis() - startTimeParallel8;
// log.info("8线程并行处理完成，耗时: {}ms, 处理记录数: {}", parallel8Duration,
// parallelResult8.getRecordsProcessed());
//
// // 性能对比
// log.info("性能对比:");
// log.info("串行处理: {}ms", serialDuration);
// log.info("4线程并行: {}ms (提升: {}%)", parallel4Duration,
// calculateSpeedup(serialDuration, parallel4Duration));
// log.info("8线程并行: {}ms (提升: {}%)", parallel8Duration,
// calculateSpeedup(serialDuration, parallel8Duration));
//
// // 关闭资源
// serialProcessor.shutdown();
// parallelProcessor4.shutdown();
// parallelProcessor8.shutdown();
//
// log.info("并行同步演示完成");
// }
//
// /**
// * 创建测试数据
// *
// * @param tableName 表名
// * @param count 记录数
// * @return 测试数据集
// */
// private SchemaSet<TestSchema> createTestData(String tableName, int count) {
// SchemaSet<TestSchema> schemaSet = new TestSchemaSet(count);
// Date baseTime = new Date();
//
// for (int i = 0; i < count; i++) {
// TestSchema schema = new TestSchema();
// schema.setId(UUID.randomUUID().toString());
// schema.setName("TestData-" + i);
// schema.setValue("Value-" + i);
// schema.setCreateTime(new Timestamp(baseTime.getTime()));
// schema.setUpdateTime(new Timestamp(baseTime.getTime() + i * 1000)); //
// 每条记录间隔1秒
//
// schemaSet.add(schema);
// }
//
// return schemaSet;
// }
//
// /**
// * 创建进度监听器
// *
// * @return 进度监听器
// */
// private ProgressListener createProgressListener() {
// return new ProgressListener() {
// private long lastLogTime = System.currentTimeMillis();
//
// @Override
// public void onProgressUpdate(String tableCode, int processedItems, int
// totalItems) {
// long now = System.currentTimeMillis();
// // 每秒最多输出一次日志，避免日志过多
// if (now - lastLogTime > 1000) {
// lastLogTime = now;
// log.info("处理进度: 表={}, {}/{} ({}%)", tableCode, processedItems, totalItems,
// Math.round((float)processedItems / totalItems * 100));
// }
// }
//
// @Override
// public void onChunkComplete(String chunkId, String tableCode, SyncStatus
// status) {
// log.debug("分片完成: id={}, 表={}, 状态={}", chunkId, tableCode, status);
// }
//
// @Override
// public void onAllChunksComplete(String tableCode, SyncSummary summary) {
// log.info("表处理完成: 表={}, 状态={}", tableCode,
// summary.getTableResult(tableCode).getStatus());
// }
// };
// }
//
// /**
// * 计算性能提升百分比
// *
// * @param oldTime 原始时间
// * @param newTime 新时间
// * @return 提升百分比
// */
// private double calculateSpeedup(long oldTime, long newTime) {
// if (oldTime <= 0 || newTime <= 0) {
// return 0;
// }
// return Math.round(((double)(oldTime - newTime) / oldTime) * 100);
// }
//
// /**
// * 测试模式Schema
// */
// public static class TestSchema extends Schema {
// private String id;
// private String name;
// private String value;
// private Timestamp createTime;
// private Timestamp updateTime;
//
// public TestSchema() {
// this.TableCode = "DemoTable";
// }
//
// public String getId() {
// return id;
// }
//
// public void setId(String id) {
// this.id = id;
// }
//
// public String getName() {
// return name;
// }
//
// public void setName(String name) {
// this.name = name;
// }
//
// public String getValue() {
// return value;
// }
//
// public void setValue(String value) {
// this.value = value;
// }
//
// public Timestamp getCreateTime() {
// return createTime;
// }
//
// public void setCreateTime(Timestamp createTime) {
// this.createTime = createTime;
// }
//
// public Timestamp getUpdateTime() {
// return updateTime;
// }
//
// public void setUpdateTime(Timestamp updateTime) {
// this.updateTime = updateTime;
// }
// }
//
// /**
// * 测试模式SchemaSet
// */
// public static class TestSchemaSet extends SchemaSet<TestSchema> {
// public TestSchemaSet(int capacity) {
// super(capacity);
// }
//
// @Override
// public TestSchema[] createSchemaSet(int initialCapacity) {
// return new TestSchema[initialCapacity];
// }
// }
// }
