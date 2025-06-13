package io.arkx.framework.performance.monitor.repository;

import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Nobody
 * @date 2025-06-06 0:42
 * @since 1.0
 */
@Slf4j
@Component
public  class JdbcTraceRepository implements TraceRepository {

	// 数据库连接参数
	private static final int MAX_RETRY = 3;
	private static final int RETRY_DELAY = 100;

	// JDBC资源
	private final DataSource dataSource;
//	private final Connection dedicatedConn;
//	private final PreparedStatement insertStmt;

	// SQL模板
	// 使用PreparedStatement优化性能
	private static final String INSERT_TRACE = "INSERT INTO monitor_trace (" +
			" trace_id, parent_id, node_type, class_name, method_name, signature," + // 新增 signature
			" raw_sql, sql_parameters, full_sql, depth, start_time, end_time," +        // 调整字段顺序
			" duration, success, error_message, request_id, session_id, endpoint" + // 新增 session_id
			") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";      // 18个?

	private static final String INSERT_SLOW_SQL = "INSERT INTO monitor_slow_sql (" +
			"sql_hash, sql_template, max_duration, avg_duration, occurrence_count, last_occurrence" +
			") VALUES (?, ?, ?, ?, 1, NOW()) " +
			"ON DUPLICATE KEY UPDATE " +
			"max_duration = GREATEST(max_duration, VALUES(max_duration)), " +
			"avg_duration = ((avg_duration * occurrence_count) + VALUES(avg_duration)) / (occurrence_count + 1), " +
			"occurrence_count = occurrence_count + 1, " +
			"last_occurrence = NOW()";

	private static final String INSERT_SLOW_METHOD = "INSERT INTO monitor_slow_method (" +
			"class_name, method_name, max_duration, avg_duration, occurrence_count, last_occurrence" +
			") VALUES (?, ?, ?, ?, 1, NOW()) " +
			"ON DUPLICATE KEY UPDATE " +
			"max_duration = GREATEST(max_duration, VALUES(max_duration)), " +
			"avg_duration = ((avg_duration * occurrence_count) + VALUES(avg_duration)) / (occurrence_count + 1), " +
			"occurrence_count = occurrence_count + 1, " +
			"last_occurrence = NOW()";

	// 新增查询SQL
	private static final String SELECT_BY_REQUEST_ID =
			"SELECT * FROM monitor_trace WHERE request_id = ? ORDER BY start_time ASC";

	private MonitorConfig config;

	public JdbcTraceRepository(@Lazy DataSource dataSource, @Lazy MonitorConfig config) throws SQLException {
		this.dataSource = dataSource;
		this.config = config;
//		this.dedicatedConn = dataSource.getConnection();
//		this.dedicatedConn.setAutoCommit(false);
//		this.insertStmt = dedicatedConn.prepareStatement(INSERT_SQL);
	}

	@Override
	public void saveBatch(List<TraceNode> nodes) throws SQLException {
		if (nodes.isEmpty()) return;

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement traceStmt = conn.prepareStatement(INSERT_TRACE);
			 PreparedStatement sqlStmt = config.isCaptureSlowSql() ?
					 conn.prepareStatement(INSERT_SLOW_SQL) : null;
			 PreparedStatement methodStmt = config.isCaptureSlowMethods() ?
					 conn.prepareStatement(INSERT_SLOW_METHOD) : null) {

//			conn.setAutoCommit(false);

			// 批量保存原始跟踪数据
			for (TraceNode node : nodes) {
				bindTraceParameters(traceStmt, node);
				traceStmt.addBatch();

				// 保存慢SQL统计数据
				if (sqlStmt != null && isSlowSql(node)) {
					bindSlowSqlParameters(sqlStmt, node);
					sqlStmt.addBatch();
				}

				// 保存慢方法统计数据
				if (methodStmt != null && isSlowMethod(node)) {
					bindSlowMethodParameters(methodStmt, node);
					methodStmt.addBatch();
				}
			}

			// 执行批处理
			traceStmt.executeBatch();
			if (sqlStmt != null) sqlStmt.executeBatch();
			if (methodStmt != null) methodStmt.executeBatch();

//			conn.commit();

//			conn.setAutoCommit(true);
		} catch (SQLException e) {
			log.error("Failed to save trace batch", e);
			// 添加重试逻辑处理
		}
	}

	@Override
	public List<TraceNode> findByRequestId(String requestId) throws SQLException {
		List<TraceNode> nodes = new ArrayList<>();

		try (Connection conn = dataSource.getConnection();
			 PreparedStatement stmt = conn.prepareStatement(SELECT_BY_REQUEST_ID)) {

			stmt.setString(1, requestId);

			try (ResultSet rs = stmt.executeQuery()) {
				while (rs.next()) {
					TraceNode node = mapRowToTraceNode(rs);
					nodes.add(node);
				}
			}
		}

		return nodes;
	}

	private TraceNode mapRowToTraceNode(ResultSet rs) throws SQLException {
		TraceNode node = new TraceNode();
		node.setTraceId(rs.getString("trace_id"));
		node.setParentId(rs.getString("parent_id"));
		node.setType(rs.getString("node_type"));
		node.setClassName(rs.getString("class_name"));
		node.setMethodName(rs.getString("method_name"));
		node.setSignature(rs.getString("signature"));
		node.setRawSql(rs.getString("raw_sql"));
		node.setSqlParameters(rs.getString("sql_parameters"));
		node.setFullSql(rs.getString("full_sql"));
		node.setStartTime(rs.getLong("start_time"));
		node.setEndTime(rs.getLong("end_time"));
		node.setDuration(rs.getLong("duration"));
		node.setSuccess(rs.getBoolean("success"));
		node.setErrorMessage(rs.getString("error_message"));
		node.setDepth(rs.getInt("depth"));
		node.setRequestId(rs.getString("request_id"));
		node.setSessionId(rs.getString("session_id"));
		node.setEndpoint(rs.getString("endpoint"));
		return node;
	}

	private boolean isSlowSql(TraceNode node) {
		return "SQL".equals(node.getType()) &&
				node.getDuration() > config.getSlowThreshold() * 1_000_000;
	}

	private boolean isSlowMethod(TraceNode node) {
		return "METHOD".equals(node.getType()) &&
				node.getDuration() > config.getSlowThreshold() * 1_000_000;
	}


	public static void sleep(long millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	// 参数绑定
	private void bindTraceParameters(PreparedStatement stmt, TraceNode node) throws SQLException {
		int index = 1;

		// 基本参数（与 SQL 字段顺序严格一致）
		stmt.setString(index++, node.getTraceId());      // trace_id
		stmt.setString(index++, node.getParentId());     // parent_id
		stmt.setString(index++, node.getType());         // node_type
		stmt.setString(index++, node.getClassName());    // class_name
		stmt.setString(index++, node.getMethodName());   // method_name
		stmt.setString(index++, node.getSignature());    // signature

		// SQL信息
		stmt.setString(index++, node.getRawSql());          // raw_sql
		stmt.setString(index++, node.getSqlParameters()); // sql_parameters
		stmt.setString(index++, node.getFullSql());       // full_sql

		// 时间信息
		stmt.setInt(index++, node.getDepth());           // depth（位置调整）
		stmt.setLong(index++, node.getStartTime());      // start_time
		stmt.setLong(index++, node.getEndTime());        // end_time
		stmt.setLong(index++, node.getDuration());       // duration

		// 状态信息
		stmt.setBoolean(index++, node.isSuccess());      // success
		stmt.setString(index++, node.getErrorMessage()); // error_message

		// 上下文
		stmt.setString(index++, node.getRequestId());    // request_id
		stmt.setString(index++, node.getSessionId());    // session_id（新增）
		stmt.setString(index++, node.getEndpoint());     // endpoint
	}

	private void bindSlowSqlParameters(PreparedStatement stmt, TraceNode node) throws SQLException {
		String sql = StringUtils.isNotBlank(node.getFullSql()) ?
				node.getFullSql() : node.getRawSql();
		String sqlHash = DigestUtils.md5Hex(sql);

		stmt.setString(1, sqlHash);
		stmt.setString(2, truncateSqlTemplate(sql));
		stmt.setLong(3, node.getDuration());
		stmt.setLong(4, node.getDuration());
	}

	// 简化SQL模板（去除参数）
	private String truncateSqlTemplate(String sql) {
		if (sql == null) return "";
		// 简化实现：删除带引号的内容
		return sql.replaceAll("'[^']*'", "?")
				.replaceAll("\"[^\"]*\"", "?")
				.replaceAll("\\d+", "?");
	}

	private void bindSlowMethodParameters(PreparedStatement stmt, TraceNode node) throws SQLException {
		stmt.setString(1, node.getClassName());
		stmt.setString(2, node.getMethodName());
		stmt.setLong(3, node.getDuration());
		stmt.setLong(4, node.getDuration());
	}

	// 重置数据库连接
	private void resetConnection() {
		try {
//			if (dedicatedConn != null && !dedicatedConn.isClosed()) {
//				dedicatedConn.rollback();
//			}
//			if (insertStmt != null) {
//				insertStmt.clearBatch();
//			}
		} catch (Exception e) {
			log.error("Failed to reset connection", e);
		}
	}

	@Override
	public void shutdown() {
		try {
//			if (insertStmt != null) insertStmt.close();
//			if (dedicatedConn != null) dedicatedConn.close();
		} catch (Exception e) {
			log.error("Error closing JDBC resources", e);
		}
	}

	// 创建表结构
	@PostConstruct
	public void createTableIfMissing() {
		try (Connection conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement()) {

			stmt.execute("CREATE TABLE IF NOT EXISTS monitor_trace  (\n" +
					"  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',\n" +
					"  `trace_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '跟踪ID',\n" +
					"  `parent_id` char(36) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '父节点ID',\n" +
					"  `node_type` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL COMMENT '节点类型(METHOD/SQL)',\n" +
					"  `class_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类名',\n" +
					"  `method_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '方法名',\n" +
					"  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '类方法签名',\n" +
					"  `raw_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '原始SQL',\n" +
					"  `sql_parameters` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT 'SQL参数',\n" +
					"  `full_sql` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '完整SQL(带参数)',\n" +
					"  `start_time` bigint(20) NOT NULL COMMENT '开始时间(纳秒)',\n" +
					"  `end_time` bigint(20) NOT NULL COMMENT '结束时间(纳秒)',\n" +
					"  `duration` bigint(20) NOT NULL COMMENT '执行时长(纳秒)',\n" +
					"  `success` tinyint(1) NOT NULL DEFAULT 1 COMMENT '是否成功',\n" +
					"  `error_message` text CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL COMMENT '错误信息',\n" +
					"  `depth` tinyint(3) NOT NULL DEFAULT 0 COMMENT '调用深度',\n" +
					"  `request_id` varchar(40) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT '请求ID',\n" +
					"  `session_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'sessionId',\n" +
					"  `endpoint` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NULL DEFAULT NULL COMMENT 'API端点',\n" +
					"  PRIMARY KEY (`id`) USING BTREE,\n" +
					"  INDEX `idx_node_type`(`node_type`) USING BTREE,\n" +
					"  INDEX `idx_trace_id`(`trace_id`) USING BTREE,\n" +
					"  INDEX `idx_request_id`(`request_id`) USING BTREE\n" +
					") COMMENT = '方法监控跟踪表' ");

			log.info("Trace node table created/validated");
		} catch (SQLException e) {
			log.error("Failed to create trace table", e);
		}
	}

	// 每天执行的归档任务
	@Scheduled(cron = "0 0 3 * * ?") // 每天凌晨3点执行
	public void archiveOldData() {
		try (Connection conn = dataSource.getConnection();
			 CallableStatement stmt = conn.prepareCall("{call monitor_rotate_partitions()}")) {
			stmt.execute();
			log.info("Monitor tables rotated successfully");
		} catch (SQLException e) {
			log.error("Failed to rotate monitor3 tables", e);
		}
	}

	@Scheduled(fixedRate = 24 * 60 * 60 * 1000) // 每天执行一次
	public void updateTableStatistics() {
		try (Connection conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement()) {

			// 更新所有监控表的统计信息
			stmt.execute("ANALYZE TABLE monitor_trace");
			stmt.execute("ANALYZE TABLE monitor_slow_sql");
			stmt.execute("ANALYZE TABLE monitor_slow_method");

			log.info("Table statistics updated");
		} catch (SQLException e) {
			log.error("Failed to update table statistics", e);
		}
	}

//	@Override
//	public void shutdown() {
//		// 清理资源
//	}
}
