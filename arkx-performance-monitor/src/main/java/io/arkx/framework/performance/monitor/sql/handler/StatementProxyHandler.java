package io.arkx.framework.performance.monitor.sql.handler;

import io.arkx.framework.performance.monitor.TraceContext;
import io.arkx.framework.performance.monitor.TraceRecorder;
import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Statement;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * @author Nobody
 * @date 2025-06-06 0:41
 * @since 1.0
 */
@Slf4j
// 语句代理处理器
public class StatementProxyHandler implements InvocationHandler {
	private final Statement realStatement;
	private final String sql;
	private final MonitorConfig config;
	private final TraceRecorder recorder;
	private final Map<Integer, Object> parameters = new ConcurrentHashMap<>();

	public StatementProxyHandler(Statement realStatement, String sql,
								 MonitorConfig config, TraceRecorder recorder) {
		this.realStatement = realStatement;
		this.sql = sql;
		this.config = config;
		this.recorder = recorder;
	}

	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		// 拦截参数绑定方法
		if (method.getName().startsWith("set")) {
			handleSetParameter(method, args);
		}

		// 拦截执行方法
		if (method.getName().startsWith("execute")) {
			return handleExecuteMethod(method, args);
		}

		return method.invoke(realStatement, args);
	}

	// 处理参数设置
	private void handleSetParameter(Method method, Object[] args) {
		try {
			if (args != null && args.length > 1 && args[0] instanceof Integer) {
				int index = (Integer) args[0];
				Object value = args[1];
				parameters.put(index, value);
			}
		} catch (Exception e) {
			log.debug("Error capturing SQL parameter", e);
		}
	}

	// 处理执行方法
	private Object handleExecuteMethod(Method method, Object[] args) throws Throwable {
		// 创建跟踪节点
		TraceNode sqlNode = createSqlTraceNode();

		try {
			// 执行SQL
			Object result = method.invoke(realStatement, args);
			sqlNode.setSuccess(true);
			return result;
		} catch (InvocationTargetException e) {
			Throwable cause = e.getCause();
			sqlNode.setSuccess(false);
			sqlNode.setErrorMessage(cause.getMessage());
			throw cause;
		} catch (Exception e) {
			sqlNode.setSuccess(false);
			sqlNode.setErrorMessage(e.getMessage());
			throw e;
		} finally {
			// 完成节点
			sqlNode.setEndTime(System.nanoTime());
			sqlNode.complete();

			// 采样决策
			if (config.shouldSample(sqlNode.getSql())) {
				recorder.record(sqlNode);
			}

			// 慢SQL日志
			if (sqlNode.getDuration() > config.getSlowThreshold() * 1_000_000) {
				log.warn("Slow SQL detected ({}ms): {}",
						TimeUnit.NANOSECONDS.toMillis(sqlNode.getDuration()),
						sqlNode.getFullSql());
			}
		}
	}

	// 创建SQL跟踪节点
	private TraceNode createSqlTraceNode() {
		TraceNode node = new TraceNode();
		node.setType("SQL");
		node.setSql(sql);
		node.setStartTime(System.nanoTime());

		// 生成完整SQL
		if (config.isCaptureSqlParameters()) {
			node.setSqlParameters(serializeParameters());
			node.setFullSql(generateFullSql(sql, parameters));
		}

		// 设置父节点
		TraceContext traceContext = ApplicationContextHolder.getBean(TraceContext.class);
		TraceNode current = traceContext.current();
		if (current != null) {
			node.setParentId(current.getTraceId());
		}

		return node;
	}

	// 序列化参数
	private String serializeParameters() {
		if (parameters.isEmpty()) return "[]";
		return parameters.entrySet().stream()
				.map(e -> e.getKey() + "=" + formatSqlValue(e.getValue()))
				.collect(Collectors.joining(", ", "[", "]"));
	}

	// 格式化SQL参数
	private String formatSqlValue(Object value) {
		if (value == null) return "NULL";
		if (value instanceof CharSequence) return "'" + escapeSql(value.toString()) + "'";
		if (value instanceof Number) return value.toString();
		return value.toString();
	}

	// 转义SQL特殊字符
	private String escapeSql(String value) {
		return value.replace("'", "''");
	}

	// 生成完整SQL
	private String generateFullSql(String sql, Map<Integer, Object> params) {
		if (params.isEmpty()) return sql;

		// 高效替换参数
		StringBuilder fullSql = new StringBuilder(sql);
		int offset = 0;
		for (int i = 1; i <= params.size(); i++) {
			Object param = params.get(i);
			if (param == null) continue;

			String value = param instanceof String ?
					"'" + escapeSql(param.toString()) + "'" :
					param.toString();

			int pos = fullSql.indexOf("?", offset);
			if (pos < 0) break;

			fullSql.replace(pos, pos + 1, value);
			offset = pos + value.length();
		}
		return fullSql.toString();
	}
}