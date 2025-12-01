package io.arkx.framework.performance.monitor.controller;

import io.arkx.framework.performance.monitor.TraceTreeService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Nobody
 * @date 2025-06-06 17:14
 * @since 1.0
 */
@RestController
@RequestMapping("/api/performance/monitoring")
public class TraceController {

	private final TraceTreeService traceTreeService;

	public TraceController(TraceTreeService traceTreeService) {
		this.traceTreeService = traceTreeService;
	}

	/**
	 * 获取树状结构输出（带颜色）
	 */
	@GetMapping(path = "/{requestId}/tree", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getTraceTree(@PathVariable String requestId) {
		return traceTreeService.buildTraceTree(requestId);
	}

	/**
	 * 获取纯文本树状结构（无颜色）
	 */
	@GetMapping(path = "/{requestId}/tree-plain", produces = MediaType.TEXT_PLAIN_VALUE)
	public String getPlainTraceTree(@PathVariable String requestId) {
		String tree = traceTreeService.buildTraceTree(requestId);
		// 移除终端颜色代码
		return tree.replaceAll("\033\\[[;\\d]*m", "");
	}

}