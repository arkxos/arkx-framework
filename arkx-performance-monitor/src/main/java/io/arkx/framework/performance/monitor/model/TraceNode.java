package io.arkx.framework.performance.monitor.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
	private String rawSql;
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

	private List<TraceNode> children = new ArrayList<>();

	public void addChild(TraceNode node) {
		children.add(node);
	}

	public String toTreeString() {
		return toTreeString(this.duration, 0, new StringBuilder(), true).toString();
	}

	private StringBuilder toTreeString(long totalduration, int depth, StringBuilder sb, boolean isLast) {
		buildNodeLine(totalduration, sb, depth, isLast);
		sb.append("\n");

		for (int i = 0; i < children.size(); i++) {
			boolean childIsLast = (i == children.size() - 1);
			children.get(i).toTreeString(totalduration, depth + 1, sb, childIsLast);
		}

		return sb;
	}

	private void buildNodeLine(long totalduration, StringBuilder sb, int depth, boolean isLast) {
		// 非根节点：缩进+连接线
		if (depth > 0) {
			for (int i = 0; i < depth - 1; i++) {
				sb.append("│   ");
			}

			if (isLast) {
				sb.append("└── ");
			} else {
				sb.append("├── ");
			}
		}

		// 添加耗时
		long ms = duration > 1_000000 ?
				TimeUnit.NANOSECONDS.toMillis(duration) :
				duration / 1000;

		// 37_536500
		String durationUnit = duration > 1_000000 ? "ms" : "μs";
		double percent = (this.getTotalDuration(totalduration) > 0) ?
				(double) duration / this.getTotalDuration(totalduration) * 100 : 0;

		// 高耗时警告
		boolean isSlow = ms > 100;
		boolean isWarning = ms > 50;

//		if (isSlow) sb.append("\033[31m");
//		else if (isWarning) sb.append("\033[33m");

//		if (percent > 0.1) {
//			sb.append(String.format("[%3d%s (%.2f%%)] ", ms, durationUnit, percent));
//		} else {
//			sb.append(String.format("[%3d%s] ", ms, durationUnit));
//		}
		String formattedDuration = formatDuration(duration);
		if (percent > 0.1) {
			sb.append(String.format("[%s (%.2f%%)] ", formattedDuration, percent));
		} else {
			sb.append(String.format("[%s] ", formattedDuration));
		}

//		if (isSlow || isWarning) sb.append("\033[0m");

		// 添加节点图标
		sb.append(getNodeIcon()).append(" ");

		// 添加节点关键信息
		if ("METHOD".equals(type)) {
			String shortClassName = className;
			if (shortClassName != null && shortClassName.lastIndexOf('.') != -1) {
				shortClassName = shortClassName.substring(shortClassName.lastIndexOf('.') + 1);
			}
			sb.append(shortClassName).append(".").append(methodName);
		} else if ("SQL".equals(type)) {
			sb.append(summarizeSql());
		}

		// 添加错误标记
		if (!success && StringUtils.hasText(errorMessage)) {
			sb.append(" \033[31m❌ ").append(abbreviateError(errorMessage)).append("\033[0m");
		} else if (!success) {
			sb.append(" \033[31m❌\033[0m");
		}
	}

	// 新增时间格式化方法
	private String formatDuration(long nanos) {
		// 微秒级处理 (<1ms)
		if (nanos < 1_000_000) {
			return String.format("%dμs", nanos / 1_000);
		}

		// 毫秒级处理 (1ms~999ms)
		if (nanos < 1_000_000_000) {
			double millis = nanos / 1_000_000.0;
			return String.format("%.3fms", millis); // 保留3位小数
		}

		// 秒级处理 (1s~59s)
		if (nanos < 60_000_000_000L) {
			double seconds = nanos / 1_000_000_000.0;
			return String.format("%.3fs", seconds); // 保留3位小数
		}

		// 分钟级处理 (≥1min)
		long minutes = TimeUnit.NANOSECONDS.toMinutes(nanos);
		long remainingNanos = nanos % TimeUnit.MINUTES.toNanos(1);
		double seconds = remainingNanos / 1_000_000_000.0;
		return String.format("%dm %.3fs", minutes, seconds);
	}


	public long getTotalDuration(long totalduration) {
//		TraceNode root = this;
//		while (root.parentId != null && root.depth > 0) {
//			// 遍历直到根节点
//			if (root.parentId == null) break;
//			// 实际应用中应优化查找逻辑
//			// 这里简化为假设当前节点可能是根节点
//			if (root.depth == 0) break;
//			root = root.getRootNode();
//		}
//		return root.duration;
		return totalduration;
	}

	private TraceNode getRootNode() {
		// 在实际应用中应该有更好的实现
		return this;
	}

	private String getNodeIcon() {
		if (depth == 0) {
			if (!success) return "🛑";
			return "⚡";
		}

		if (!success) return "❌";
		if ("SQL".equals(type)) return "🗃";
		if ("METHOD".equals(type)) {
			if (className != null) {
				if (className.contains("Service")) return "⚡";
				if (className.contains("Controller")) return "⚡";
				if (className.contains("Helper") || className.contains("Util")) return "⚙";
				if (className.contains("Repository") || className.contains("Dao")) return "📦";
			}
			return "◦";
		}
		return "◦";
	}

	private String summarizeSql() {
		if (!StringUtils.hasText(rawSql)) return "Unknown SQL";

		String workingSql = StringUtils.hasText(fullSql) ? fullSql : rawSql;
		String sqlType = getSqlType(workingSql);
		String table = extractTableName(workingSql);

		return sqlType + " " + table;
	}

	private String getSqlType(String sql) {
		if (sql.regionMatches(true, 0, "select", 0, 6)) return "SELECT";
		if (sql.regionMatches(true, 0, "insert", 0, 6)) return "INSERT";
		if (sql.regionMatches(true, 0, "update", 0, 6)) return "UPDATE";
		if (sql.regionMatches(true, 0, "delete", 0, 6)) return "DELETE";
		if (sql.regionMatches(true, 0, "call", 0, 4)) return "CALL";
		if (sql.regionMatches(true, 0, "exec", 0, 4)) return "EXEC";
		return "SQL";
	}

	private String extractTableName(String sql) {
		Pattern pattern = Pattern.compile(
				"\\b(?:from|into|update|join|table)\\s+(\\w+)",
				Pattern.CASE_INSENSITIVE
		);
		Matcher matcher = pattern.matcher(sql);
		return matcher.find() ? matcher.group(1) : "table";
	}

	private String abbreviateError(String error) {
		if (error == null) return "";
		if (error.length() <= 50) return error;
		return error.substring(0, 47) + "...";
	}

	public void complete() {
		if (endTime > 0) {
			duration = endTime - startTime;
		}
	}

}