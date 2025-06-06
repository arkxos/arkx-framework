package io.arkx.framework.performance.monitor;

import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.model.TraceNode;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.LinkedList;

/**
 * 跟踪上下文管理
 * @author Nobody
 * @date 2025-06-06 0:40
 * @since 1.0
 */
@Component
public  class TraceContext {

	// 当前请求ID
	private static final ThreadLocal<String> requestId = new ThreadLocal<>();

	// 上下文栈
	private static final ThreadLocal<LinkedList<TraceNode>> stack =
			ThreadLocal.withInitial(LinkedList::new);

	// 配置最大深度
	private final int maxDepth;

	public TraceContext(MonitorConfig config) {
		this.maxDepth = config.getMaxTraceDepth();
	}

	// 开始新请求
	public void startRequest(String id) {
		requestId.set(id);
		stack.remove(); // 清除之前的调用栈
	}

	// 结束请求
	public void endRequest() {
		requestId.remove();
		stack.remove();
	}

	// 推入新节点
	public TraceNode pushNode(TraceNode node) {
		LinkedList<TraceNode> currentStack = stack.get();

		// 设置调用深度
		node.setDepth(currentStack.size());

		// 避免过深调用栈导致内存问题
		if (node.getDepth() >= maxDepth) {
			return null;
		}

		// 设置父节点
		if (!currentStack.isEmpty()) {
			node.setParentId(currentStack.peek().getTraceId());
		}

		// 设置请求ID
		if (requestId.get() != null) {
			node.setRequestId(requestId.get());
		}

		// 记录Web端点信息
		if (node.getDepth() == 0 && RequestContextHolder.getRequestAttributes() != null) {
			HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
			node.setEndpoint(request.getMethod() + " " + request.getRequestURI());
		}

		currentStack.push(node);
		return node;
	}

	// 弹出节点
	public void popNode() {
		LinkedList<TraceNode> stack = TraceContext.stack.get();
		if (stack != null && !stack.isEmpty()) {
			stack.pop();
		}
	}

	// 获取当前节点
	public TraceNode current() {
		LinkedList<TraceNode> stack = TraceContext.stack.get();
		return stack != null && !stack.isEmpty() ? stack.peek() : null;
	}
}