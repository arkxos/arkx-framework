package io.arkx.framework.performance.monitor;

import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedList;
import java.util.Stack;
import java.util.UUID;

/**
 * 跟踪上下文管理
 *
 * @author Nobody
 * @date 2025-06-06 0:40
 * @since 1.0
 */
@Component
public class TraceContext {

	// 当前请求ID
	private static final ThreadLocal<String> requestIdLocal = new ThreadLocal<>();

	// 上下文栈
	private static final ThreadLocal<Stack<TraceNode>> callStack = new ThreadLocal<>();

	// 配置最大深度
//	private final int maxDepth;

	private MonitorConfig config;

	private MonitorConfig config() {
		if (config != null) {
			return config;
		}

		this.config = ApplicationContextHolder.getBean(MonitorConfig.class);
		return config;
	}

	public TraceContext() {
//		this.maxDepth = config.getMaxTraceDepth();
	}

	// 开始新请求
	public void startRequest(String requestId) {
		requestIdLocal.set(requestId);
		callStack.set(new Stack<>());
	}

	public String currentRequestId() {
		String requestId = requestIdLocal.get();
		if (requestId == null) {
			requestId = "SYS_" + UUID.randomUUID();
			requestIdLocal.set(requestId);
		}
		return requestId;
	}

	// 结束请求
	public void endRequest() {
		requestIdLocal.remove();
		callStack.remove();
	}

	// 推入新节点
	public TraceNode pushNode(TraceNode node) {
		Stack<TraceNode> currentStack = callStack.get();

		if (currentStack == null) return node;

		// 设置父节点（如果存在）
		if (!currentStack.isEmpty()) {
			node.setParentId(currentStack.peek().getTraceId());
		}

		// 设置调用深度
		node.setDepth(currentStack.size());

        // 设置请求ID
		node.setRequestId(currentRequestId());

		// 记录Web端点信息
		if (node.getDepth() == 0 && RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			node.setEndpoint(request.getMethod() + " " + request.getRequestURI());
		}

		currentStack.push(node);
		return node;
	}

	// 弹出节点
	public TraceNode popNode() {
		Stack<TraceNode> stack = callStack.get();
		return (stack != null && !stack.isEmpty()) ? stack.pop() : null;
	}

	// 获取当前节点
	public TraceNode current() {
		Stack<TraceNode> stack = callStack.get();
		return stack != null && !stack.isEmpty() ? stack.peek() : null;
	}

}