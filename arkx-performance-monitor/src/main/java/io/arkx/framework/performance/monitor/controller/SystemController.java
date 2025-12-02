package io.arkx.framework.performance.monitor.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.arkx.framework.performance.monitor.SystemInfoService;
import io.arkx.framework.performance.monitor.SystemMonitor;
import io.arkx.framework.performance.monitor.config.MonitorConfig;

/**
 * @author Nobody
 * @date 2025-06-06 12:00
 * @since 1.0
 */
@RestController
@RequestMapping("/api/system")
public class SystemController {

	private final SystemInfoService systemInfoService;

	private final SystemMonitor systemMonitor;

	private final MonitorConfig monitorConfig;

	public SystemController(SystemInfoService systemInfoService, SystemMonitor systemMonitor,
			MonitorConfig monitorConfig) {
		this.systemInfoService = systemInfoService;
		this.systemMonitor = systemMonitor;
		this.monitorConfig = monitorConfig;
	}

	/**
	 * 系统基本信息摘要 包括OS、CPU、内存和磁盘总览
	 */
	@GetMapping("/summary")
	public Map<String, Object> getSystemSummary() {
		return systemInfoService.getSystemSummary();
	}

	/**
	 * 详细硬件与系统信息 包括所有磁盘、网络接口的详细信息
	 * 包括CPU、内存、磁盘、网络、BIOS、BIOS版本、BIOS发布日期、BIOS供应商、BIOS版本号、BIOS语言、BIOS描述符、BIOS序列号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、BIOS版本号、
	 */
	@GetMapping("/details")
	public Map<String, Object> getSystemDetails() {
		return systemInfoService.getSystemDetails();
	}

	@GetMapping("/info")
	public Map<String, Object> getSystemInfo() {
		return systemInfoService.getSystemSummary();
	}

	/**
	 * 系统健康状态+实时监控指标 包括资源使用率和监控系统状态 包括当前并发请求数、采样率、是否过载、负载得分
	 */
	@GetMapping("/health")
	public Map<String, Object> getSystemHealth() {
		// 从监控服务获取当前动态指标
		Map<String, Object> health = systemInfoService.getSystemHealth();

		// 添加实时监控数据
		health.put("concurrentRequests", systemMonitor.getCurrentRequests());
		health.put("samplingRate", monitorConfig.getSamplingRate());
		health.put("isOverloaded", systemMonitor.isSystemOverloaded());
		health.put("loadScore", systemMonitor.getSystemLoadScore());

		return health;
	}

	/**
	 * 系统性能指标 包括请求统计、队列状态、资源使用率等 包括请求统计、队列统计、CPU使用率、内存使用率、采样率、负载得分
	 */
	@GetMapping("/performance")
	public Map<String, Object> getPerformanceMetrics() {
		return Map.of("requestStats", systemMonitor.getRequestStats(), "queueStats", systemMonitor.getQueueStats(),
				"cpuUsage", systemMonitor.getAverageCpuUsage(), "memoryUsage", systemMonitor.getAverageMemoryUsage(),
				"samplingRate", monitorConfig.getSamplingRate(), "loadScore", systemMonitor.getSystemLoadScore());
	}

}
