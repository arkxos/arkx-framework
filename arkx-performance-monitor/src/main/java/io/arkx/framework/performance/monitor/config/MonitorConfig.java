package io.arkx.framework.performance.monitor.config;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author Nobody
 * @date 2025-06-06 0:39
 * @since 1.0
 */
/* ====================== 配置管理系统 ====================== */
@Getter
@Setter
@Slf4j
@Configuration
@EnableScheduling
@EnableAspectJAutoProxy
@ConfigurationProperties(prefix = "arkx.performance.monitor")
@Component
public class MonitorConfig {

	// 基础配置
	private boolean enabled = true;
	private int samplingRate = 30; // 默认采样率30%
	private long slowThreshold = 500; // 慢调用阈值500ms
	private boolean captureSqlParameters = true; // 捕获SQL参数
	private boolean storeToDatabase = true; // 存储到数据库
	private boolean captureSlowSql = true;
	private boolean captureSlowMethods = true;

	// 性能优化参数
	private int samplingCacheSize = 10_000; // 采样决策缓存
	private int writeBatchSize = 100; // 批量写入大小
	private int writeBatchTimeout = 50; // 批量写入超时(ms)
	private int maxTraceDepth = 15; // 最大调用深度

	// 新增队列和并发配置
	private int queueCapacity = 100_000; // 监控队列容量
	private int maxConcurrentRequests = 1000; // 最大并发请求数

	// 添加自适应调整阈值配置
	private double highLoadThreshold = 0.85; // 高负载阈值
	private double mediumLoadThreshold = 0.6; // 中等负载阈值
	private double lowLoadThreshold = 0.3; // 低负载阈值
	private double criticalLoadThreshold = 0.95; // 临界负载阈值

	// 添加自适应调整步长配置
	private int highLoadReductionPercent = 50; // 高负载时减少百分比
	private int mediumLoadReductionPercent = 5; // 中等负载时减少百分比
	private int lowLoadIncreasePercent = 20; // 低负载时增加百分比
	private int minSamplingRate = 5; // 最低采样率
	private int maxSamplingRate = 100; // 最高采样率

	// 包含/排除规则
	private volatile List<String> includePackages = new ArrayList<>();
	private volatile List<String> includeClasses = new ArrayList<>();
	private volatile List<String> includeMethods = new ArrayList<>();

	private volatile List<String> excludePackages = Collections.singletonList("org.springframework..*");
	private volatile List<String> excludeClasses = new ArrayList<>();
	private volatile List<String> excludeMethods = Arrays.asList("get.*", "set.*", "is.*");

	// 缓存和状态
	private transient final AtomicBoolean dirty = new AtomicBoolean(false);
	private transient final ConcurrentMap<String, Boolean> classCache = new ConcurrentReferenceHashMap<>(2048);
	private transient final ConcurrentMap<String, Boolean> methodCache = new ConcurrentReferenceHashMap<>(10_000);
	private transient final AntPathMatcher pathMatcher = new AntPathMatcher(".");

	@PostConstruct
	public void init() {
		log.info("Monitoring system configuration loaded. Enabled: {}, Sampling: {}%", enabled, samplingRate);
	}

	// 检查类是否应该监控
	public boolean shouldMonitorClass(String className) {
		Boolean cached = classCache.get(className);
		if (cached != null) return cached;

		// 1. 检查排除包
		for (String pattern : excludePackages) {
			if (pathMatcher.match(pattern, className)) {
				classCache.put(className, false);
				return false;
			}
		}

		// 2. 检查包含包
		boolean includeByPackage = includePackages.isEmpty();
		for (String pattern : includePackages) {
			if (pathMatcher.match(pattern, className)) {
				includeByPackage = true;
				break;
			}
		}
		if (!includeByPackage) {
			classCache.put(className, false);
			return false;
		}

		// 3. 检查类规则
		if (!includeClasses.isEmpty()) {
			boolean includeByClass = false;
			for (String pattern : includeClasses) {
				if (Pattern.matches(pattern, className)) {
					includeByClass = true;
					break;
				}
			}
			if (!includeByClass) {
				classCache.put(className, false);
				return false;
			}
		}

		// 4. 检查类排除规则
		for (String pattern : excludeClasses) {
			if (Pattern.matches(pattern, className)) {
				classCache.put(className, false);
				return false;
			}
		}

		classCache.put(className, true);
		return true;
	}

	// 检查方法是否应该监控
	public boolean shouldMonitorMethod(String className, String methodName) {
		String cacheKey = className + "#" + methodName;
		Boolean cached = methodCache.get(cacheKey);
		if (cached != null) return cached;

		// 1. 检查类是否监控
		if (!shouldMonitorClass(className)) {
			methodCache.put(cacheKey, false);
			return false;
		}

		// 2. 检查方法排除规则
		for (String pattern : excludeMethods) {
			if (Pattern.matches(pattern, methodName)) {
				methodCache.put(cacheKey, false);
				return false;
			}
		}

		// 3. 检查方法包含规则
		if (!includeMethods.isEmpty()) {
			boolean includeByMethod = false;
			for (String pattern : includeMethods) {
				if (Pattern.matches(pattern, methodName)) {
					includeByMethod = true;
					break;
				}
			}
			if (!includeByMethod) {
				methodCache.put(cacheKey, false);
				return false;
			}
		}

		methodCache.put(cacheKey, true);
		return true;
	}

	// 采样决策（高性能版本）
	public boolean shouldSample(String identifier) {
		if (samplingRate >= 100) return true;
		if (samplingRate <= 0) return false;

		// 高性能采样算法（使用内置哈希）
		int hash = identifier.hashCode() & 0x7FFFFFFF; // 取正数
		return (hash % 100) < samplingRate;
	}

	// 标记配置已变更
	public void markDirty() {
		dirty.set(true);
	}

	// 清空缓存（当配置变更时）
	public void refreshCache() {
		if (dirty.compareAndSet(true, false)) {
			classCache.clear();
			methodCache.clear();
			log.info("Configuration cache refreshed");
		}
	}
}
