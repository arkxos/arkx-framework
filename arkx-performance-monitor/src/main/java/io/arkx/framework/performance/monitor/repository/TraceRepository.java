package io.arkx.framework.performance.monitor.repository;

import io.arkx.framework.performance.monitor.model.TraceNode;

import java.sql.SQLException;
import java.util.List;

/**
 * @author Nobody
 * @date 2025-06-06 0:42
 * @since 1.0
 */
/* ====================== 跟踪存储仓库 ====================== */
public interface TraceRepository {

	void saveBatch(List<TraceNode> nodes) throws SQLException;

	void shutdown();
}