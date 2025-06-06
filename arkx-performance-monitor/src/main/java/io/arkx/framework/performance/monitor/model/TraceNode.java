package io.arkx.framework.performance.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * @author Nobody
 * @date 2025-06-06 0:39
 * @since 1.0
 */
/* ====================== 跟踪节点模型 ====================== */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TraceNode {

	// 基础信息
	private String traceId = UUID.randomUUID().toString();
	private String parentId;
	private String type; // METHOD or SQL
	private String className;
	private String methodName;
	private String signature;
	private int depth;

	// SQL专用信息
	private String sql;
	private String sqlParameters;
	private String fullSql;

	// 时间指标
	private long startTime;
	private long endTime;
	private long duration;

	// 状态信息
	private boolean success = true;
	private String errorMessage;

	// 上下文信息
	private String requestId;
	private String sessionId;
	private String endpoint;

	public void complete() {
		if (endTime > 0) {
			duration = endTime - startTime;
		}
	}
}