package io.arkx.framework.performance.monitor.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.arkx.framework.performance.monitor.SystemMonitor;

/**
 * @author Nobody
 * @date 2025-06-06 11:40
 * @since 1.0
 */
@RestController
public class PrometheusMetricsController {

	private final SystemMonitor systemMonitor;

	public PrometheusMetricsController(SystemMonitor systemMonitor) {
		this.systemMonitor = systemMonitor;
	}

	@GetMapping("/metrics")
	public String getPrometheusMetrics() {
		return buildPrometheusFormat();
	}

	private String buildPrometheusFormat() {
		StringBuilder sb = new StringBuilder();

		// 基本指标
		sb.append("# HELP system_load System load score (0-100)\n");
		sb.append("# TYPE system_load gauge\n");
		sb.append("system_load ").append(systemMonitor.getSystemLoadScore()).append("\n\n");

		sb.append("# HELP cpu_usage CPU usage percentage\n");
		sb.append("# TYPE cpu_usage gauge\n");
		sb.append("cpu_usage ").append(systemMonitor.getAverageCpuUsage()).append("\n\n");

		// 请求相关指标
		sb.append("# HELP requests_current Current concurrent requests\n");
		sb.append("# TYPE requests_current gauge\n");
		sb.append("requests_current ").append(systemMonitor.getCurrentRequests()).append("\n\n");

		sb.append("# HELP requests_total Total requests processed\n");
		sb.append("# TYPE requests_total counter\n");
		sb.append("requests_total ").append(systemMonitor.getTotalRequests()).append("\n\n");

		// 队列指标
		sb.append("# HELP queue_size Current monitoring queue size\n");
		sb.append("# TYPE queue_size gauge\n");
		sb.append("queue_size ").append(systemMonitor.getRecorder().queueSize()).append("\n\n");

		// 采样率指标
		sb.append("# HELP sampling_rate Current monitoring sampling rate\n");
		sb.append("# TYPE sampling_rate gauge\n");
		sb.append("sampling_rate ").append(systemMonitor.getConfig().getSamplingRate()).append("\n\n");

		return sb.toString();
	}

}
