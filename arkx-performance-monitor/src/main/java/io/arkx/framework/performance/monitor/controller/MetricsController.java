package io.arkx.framework.performance.monitor.controller;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.arkx.framework.performance.monitor.SystemMonitor;

/**
 * @author Nobody
 * @date 2025-06-06 11:37
 * @since 1.0
 */
@RestController
@RequestMapping("/api/monitoring/metrics")
public class MetricsController {

	private final SystemMonitor systemMonitor;

	public MetricsController(SystemMonitor systemMonitor) {
		this.systemMonitor = systemMonitor;
	}

	@GetMapping("/overview")
	public Map<String, Object> getSystemOverview() {
		return Map.of("loadScore", systemMonitor.getSystemLoadScore(), "cpuUsage", systemMonitor.getAverageCpuUsage(),
				"memoryUsage", systemMonitor.getAverageMemoryUsage(), "requestStats", systemMonitor.getRequestStats(),
				"queueStats", systemMonitor.getQueueStats(), "isOverloaded", systemMonitor.isSystemOverloaded());
	}

	@GetMapping("/detailed")
	public Map<String, Object> getDetailedMetrics() {
		Map<String, Object> metrics = new LinkedHashMap<>();
		metrics.put("concurrentRequests", systemMonitor.getCurrentRequests());
		metrics.put("maxConcurrentRequests", systemMonitor.getMaxConcurrentRequests());
		metrics.put("totalRequests", systemMonitor.getTotalRequests());
		metrics.put("avgRequestLatency", systemMonitor.getRequestStats().get("avgLatency"));
		metrics.put("maxRequestLatency", systemMonitor.getRequestStats().get("maxLatency"));
		metrics.put("queueSize", systemMonitor.getQueueStats().get("current"));
		metrics.put("maxQueueSize", systemMonitor.getQueueStats().get("max"));
		metrics.put("avgQueueSize", systemMonitor.getQueueStats().get("average"));
		metrics.put("avgCpuUsage", systemMonitor.getAverageCpuUsage());
		metrics.put("maxCpuUsage", systemMonitor.getMaxSCpuUsage());
		metrics.put("avgMemoryUsage", systemMonitor.getAverageMemoryUsage());
		metrics.put("maxMemoryUsage", systemMonitor.getMaxMemoryUsage());
		metrics.put("samplingRate", systemMonitor.getSamplingRate());
		return metrics;
	}

}
