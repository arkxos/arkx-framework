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
	private final Connection dedicatedConn;
//	private final PreparedStatement insertStmt;

	// SQL模板
	// 使用PreparedStatement优化性能
	private static final String INSERT_TRACE = "INSERT INTO monitor_trace (" +
			"trace_id, parent_id, node_type, class_name, method_name, " +
			"sql, sql_parameters, full_sql, start_time, end_time, duration, " +
			"success, error_message, depth, request_id, endpoint" +
			") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

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

	private MonitorConfig config;

	public JdbcTraceRepository(@Lazy DataSource dataSource, MonitorConfig config) throws SQLException {
		this.dataSource = dataSource;
		this.config = config;
		this.dedicatedConn = dataSource.getConnection();
		this.dedicatedConn.setAutoCommit(false);
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

			conn.setAutoCommit(false);

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

			conn.commit();

		} catch (SQLException e) {
			log.error("Failed to save trace batch", e);
			// 添加重试逻辑处理
		}
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

		// 基本参数
		stmt.setString(index++, node.getTraceId());
		stmt.setString(index++, node.getParentId());
		stmt.setString(index++, node.getType());
		stmt.setString(index++, node.getClassName());
		stmt.setString(index++, node.getMethodName());
		stmt.setString(index++, node.getSignature());

		// SQL信息
		stmt.setString(index++, node.getSql());
		stmt.setString(index++, node.getSqlParameters());
		stmt.setString(index++, node.getFullSql());

		// 时间信息
		stmt.setInt(index++, node.getDepth());
		stmt.setLong(index++, node.getStartTime());
		stmt.setLong(index++, node.getEndTime());
		stmt.setLong(index++, node.getDuration());

		// 状态信息
		stmt.setBoolean(index++, node.isSuccess());
		stmt.setString(index++, node.getErrorMessage());

		// 上下文
		stmt.setString(index++, node.getRequestId());
		stmt.setString(index++, node.getSessionId());
		stmt.setString(index++, node.getEndpoint());
	}

	private void bindSlowSqlParameters(PreparedStatement stmt, TraceNode node) throws SQLException {
		String sql = StringUtils.isNotBlank(node.getFullSql()) ?
				node.getFullSql() : node.getSql();
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
			if (dedicatedConn != null && !dedicatedConn.isClosed()) {
				dedicatedConn.rollback();
			}
//			if (insertStmt != null) {
//				insertStmt.clearBatch();
//			}
		} catch (SQLException e) {
			log.error("Failed to reset connection", e);
		}
	}

	@Override
	public void shutdown() {
		try {
//			if (insertStmt != null) insertStmt.close();
			if (dedicatedConn != null) dedicatedConn.close();
		} catch (SQLException e) {
			log.error("Error closing JDBC resources", e);
		}
	}

	// 创建表结构
	@PostConstruct
	public void createTableIfMissing() {
		try (Connection conn = dataSource.getConnection();
			 Statement stmt = conn.createStatement()) {

			stmt.execute("CREATE TABLE IF NOT EXISTS trace_nodes (" +
					"id BIGINT AUTO_INCREMENT PRIMARY KEY," +
					"trace_id VARCHAR(36) NOT NULL," +
					"parent_id VARCHAR(36)," +
					"type VARCHAR(10) NOT NULL," +
					"class_name VARCHAR(255)," +
					"method_name VARCHAR(100)," +
					"signature TEXT," +
					"sql TEXT," +
					"sql_parameters TEXT," +
					"full_sql TEXT," +
					"depth INT," +
					"start_time BIGINT," +
					"end_time BIGINT," +
					"duration BIGINT," +
					"success BOOLEAN," +
					"error_message TEXT," +
					"request_id VARCHAR(36)," +
					"session_id VARCHAR(36)," +
					"endpoint VARCHAR(255)," +
					"INDEX idx_trace_id (trace_id)," +
					"INDEX idx_parent_id (parent_id)," +
					"INDEX idx_type (type)," +
					"INDEX idx_class (class_name)," +
					"INDEX idx_method (method_name)," +
					"INDEX idx_start_time (start_time)" +
					")");

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
