package io.arkx.framework.performance.monitor;

/**
 * @author Nobody
 * @date 2025-06-06 11:57
 * @since 1.0
 */
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.LongAdder;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.arkx.framework.performance.monitor.config.MonitorConfig;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Getter
@Slf4j
@Component
public class SystemMonitor {

	// 请求跟踪
	private final AtomicInteger currentRequests = new AtomicInteger(0);

	private final AtomicInteger maxConcurrentRequests = new AtomicInteger(0);

	private final LongAdder totalRequests = new LongAdder();

	// 性能指标
	private final LongAdder totalRequestLatency = new LongAdder();

	private final LongAdder totalProcessingTime = new LongAdder();

	private final AtomicLong maxRequestLatency = new AtomicLong(0);

	private final AtomicLong maxProcessingTime = new AtomicLong(0);

	// 系统指标
	private final AtomicInteger maxSampledCpuUsage = new AtomicInteger(0);

	private final AtomicInteger maxSampledMemoryUsage = new AtomicInteger(0);

	private final AtomicLong monitorSampleCount = new AtomicLong(0);

	private final LongAdder cpuUsageSum = new LongAdder();

	private final LongAdder memoryUsageSum = new LongAdder();

	// 队列指标
	private final AtomicInteger maxQueueSize = new AtomicInteger(0);

	private final LongAdder queueSizeSum = new LongAdder();

	private final AtomicLong queueSampleCount = new AtomicLong(0);

	private final AtomicLong queueLatencySampleCount = new AtomicLong(0);

	// 系统状态
	private final AtomicBoolean overloaded = new AtomicBoolean(false);

	private final AtomicLong lastOverloadReport = new AtomicLong(0);

	// 依赖服务
	private final MonitorConfig config;

	private final TraceRecorder recorder;

	private final SystemInfoService systemInfoService;

	public SystemMonitor(MonitorConfig config, TraceRecorder recorder, SystemInfoService systemInfoService) {
		this.config = config;
		this.recorder = recorder;
		this.systemInfoService = systemInfoService;
	}

	/**
	 * 系统监控定时任务
	 */
	@Scheduled(fixedRate = 5000)
	public void monitorSystemHealth() {
		try {
			// 获取当前状态
			int concurrentRequests = getCurrentRequests();
			int currentQueueSize = recorder.queueSize();
			int queueCapacity = recorder.getQueueCapacity();

			// 获取系统指标
			int cpuUsage = (int) (systemInfoService.getSystemCpuUsage() * 100);
			int memoryUsage = (int) (systemInfoService.getMemoryUsage() * 100);

			// 更新最大值
			maxSampledCpuUsage.updateAndGet(prev -> Math.max(prev, cpuUsage));
			maxSampledMemoryUsage.updateAndGet(prev -> Math.max(prev, memoryUsage));
			maxQueueSize.updateAndGet(prev -> Math.max(prev, currentQueueSize));

			// 更新平均值计算器
			cpuUsageSum.add(cpuUsage);
			memoryUsageSum.add(memoryUsage);
			queueSizeSum.add(currentQueueSize);

			queueLatencySampleCount.incrementAndGet();

			// 计算负载
			double systemLoad = calculateSystemLoad();
			updateOverloadStatus(systemLoad);

			// 日志系统状态
			log.info(
					"System Status: " + "Concurrent={}/{}, " + "Queue={}/{}, " + "CPU={}%, " + "Mem={}%, " + "Load={}, "
							+ "Overloaded={}",
					concurrentRequests, maxConcurrentRequests.get(), currentQueueSize, queueCapacity, cpuUsage,
					memoryUsage, (int) (systemLoad * 100), overloaded.get());

			// 自适应调整采样率
			adaptiveSampling(systemLoad);

			// 更新采样计数器
			monitorSampleCount.incrementAndGet();
			queueSampleCount.incrementAndGet();
		}
		catch (Exception e) {
			log.error("Error during system monitoring", e);
		}
	}

	/**
	 * 更新系统过载状态
	 */
	private void updateOverloadStatus(double systemLoad) {
		double criticalLoad = config.getCriticalLoadThreshold();

		// 检测过载
		if (systemLoad > criticalLoad) {
			overloaded.set(true);
			lastOverloadReport.set(System.currentTimeMillis());
		}
		// 清除过载状态（连续3个周期低于阈值）
		else if (overloaded.get() && systemLoad < criticalLoad * 0.9) {
			if (System.currentTimeMillis() - lastOverloadReport.get() > 15_000) {
				overloaded.set(false);
			}
		}
	}

	/**
	 * 判断系统是否过载
	 */
	public boolean isSystemOverloaded() {
		return overloaded.get();
	}

	/**
	 * 获取队列统计信息
	 */
	public Map<String, Object> getQueueStats() {
		int current = recorder.queueSize();
		return Map.of("current", current, "max", maxQueueSize.get(), "average", getAverageQueueSize(), "capacity",
				recorder.getQueueCapacity(), "usagePercent", (int) (100.0 * current / recorder.getQueueCapacity()));
	}

	/**
	 * 获取平均队列大小
	 */
	public double getAverageQueueSize() {
		long count = queueSampleCount.get();
		return count > 0 ? (double) queueSizeSum.sum() / count : 0;
	}

	/**
	 * 获取平均CPU使用率（百分比）
	 */
	public int getAverageCpuUsage() {
		long count = monitorSampleCount.get();
		return count > 0 ? (int) (cpuUsageSum.sum() / count) : 0;
	}

	/**
	 * 获取平均内存使用率（百分比）
	 */
	public int getAverageMemoryUsage() {
		long count = monitorSampleCount.get();
		return count > 0 ? (int) (memoryUsageSum.sum() / count) : 0;
	}

	/**
	 * 综合计算系统负载
	 */
	private double calculateSystemLoad() {
		// 1. 并发请求负载因子 (0.0-1.0)
		double requestLoad = calculateRequestLoad();

		// 2. CPU负载因子 (0.0-1.0)
		double cpuLoad = getAverageCpuUsage() / 100.0;

		// 3. 内存负载因子 (0.0-1.0)
		double memoryLoad = getAverageMemoryUsage() / 100.0;

		// 4. 队列负载因子 (0.0-1.0)
		double queueLoad = calculateQueueLoad();

		// 综合加权负载
		return (requestLoad * 0.4) + (cpuLoad * 0.3) + (memoryLoad * 0.2) + (queueLoad * 0.1);
	}

	/**
	 * 基于请求的系统负载
	 */
	private double calculateRequestLoad() {
		int maxCapacity = config.getMaxConcurrentRequests();
		int currentRequests = getCurrentRequests();

		if (maxCapacity <= 0) {
			// 默认计算最大并发能力
			maxCapacity = systemInfoService.getLogicalProcessorCount() * 50;
		}

		double loadFactor = currentRequests / (double) maxCapacity;
		return Math.min(1.0, loadFactor);
	}

	/**
	 * 队列负载因子
	 */
	private double calculateQueueLoad() {
		int queueCapacity = recorder.getQueueCapacity();
		int currentSize = recorder.queueSize();

		if (queueCapacity <= 0) {
			return 0.0;
		}

		double loadFactor = currentSize / (double) queueCapacity;
		return Math.min(1.0, loadFactor);
	}

	/**
	 * 请求开始时的回调方法
	 */
	public void onRequestStart() {
		int current = currentRequests.incrementAndGet();
		maxConcurrentRequests.updateAndGet(prev -> Math.max(prev, current));
		totalRequests.increment();
	}

	/**
	 * 请求结束时的回调方法（带性能指标）
	 */
	public void onRequestEnd(long latency, long processingTime) {
		currentRequests.decrementAndGet();

		// 记录性能指标
		totalRequestLatency.add(latency);
		totalProcessingTime.add(processingTime);

		// 更新最大值
		maxRequestLatency.updateAndGet(prev -> Math.max(prev, latency));
		maxProcessingTime.updateAndGet(prev -> Math.max(prev, processingTime));
	}

	/**
	 * 获取请求统计数据
	 */
	public Map<String, Object> getRequestStats() {
		long total = totalRequests.sum();
		return Map.of("concurrent", currentRequests.get(), "maxConcurrent", maxConcurrentRequests.get(), "total", total,
				"avgLatency", total > 0 ? totalRequestLatency.sum() / total : 0, "maxLatency", maxRequestLatency.get(),
				"avgProcessingTime", total > 0 ? totalProcessingTime.sum() / total : 0);
	}

	/**
	 * 获取系统总体负载评分（0-100）
	 */
	public int getSystemLoadScore() {
		return (int) (calculateSystemLoad() * 100);
	}

	/**
	 * 获取当前并发请求数
	 */
	public int getCurrentRequests() {
		return currentRequests.get();
	}

	/**
	 * 获取最大并发请求数
	 */
	public int getMaxConcurrentRequests() {
		return maxConcurrentRequests.get();
	}

	/**
	 * 获取请求总数
	 */
	public long getTotalRequests() {
		return totalRequests.sum();
	}

	/**
	 * 自适应采样方法
	 */
	private void adaptiveSampling(double systemLoad) {
		int currentRate = config.getSamplingRate();
		int newRate = currentRate;

		if (systemLoad > config.getHighLoadThreshold()) {
			// 高负载：显著降低采样率
			int reduction = config.getHighLoadReductionPercent();
			newRate = Math.max(config.getMinSamplingRate(), currentRate - (int) (currentRate * reduction / 100.0));
		}
		else if (systemLoad > config.getMediumLoadThreshold()) {
			// 中等负载：适度降低采样率
			newRate = Math.max(config.getMinSamplingRate(), currentRate - config.getMediumLoadReductionPercent());
		}
		else if (systemLoad < config.getLowLoadThreshold()) {
			// 低负载：增加采样率
			newRate = Math.min(config.getMaxSamplingRate(), currentRate + config.getLowLoadIncreasePercent());
		}

		if (newRate != currentRate) {
			config.setSamplingRate(newRate);
			log.info("Sampling rate adjusted to {}% due to system load: {:.2f}", newRate, systemLoad);
		}
	}

	public int getMaxSCpuUsage() {
		return maxSampledCpuUsage.get();
	}

	public int getMaxMemoryUsage() {
		return maxSampledMemoryUsage.get();
	}

	public int getSamplingRate() {
		return config.getSamplingRate();
	}

}
