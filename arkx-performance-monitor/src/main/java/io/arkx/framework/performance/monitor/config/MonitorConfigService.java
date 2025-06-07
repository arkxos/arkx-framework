package io.arkx.framework.performance.monitor.config;

import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.ConcurrentReferenceHashMap;

import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;

/**
 * @author Nobody
 * @date 2025-06-06 19:22
 * @since 1.0
 */
@Slf4j
@Service
public class MonitorConfigService {

	private MonitorConfig config;

	public MonitorConfigService(MonitorConfig monitorConfig) {
		this.config = monitorConfig;
	}

	private MonitorConfig config() {
		if (config != null) {
			return config;
		}

		this.config = ApplicationContextHolder.getBean(MonitorConfig.class);
		return config;
	}

	// 缓存和状态
	private transient final AtomicBoolean dirty = new AtomicBoolean(false);
	private transient final ConcurrentMap<String, Boolean> classCache = new ConcurrentReferenceHashMap<>(2048);
	private transient final ConcurrentMap<String, Boolean> methodCache = new ConcurrentReferenceHashMap<>(10_000);
	private transient final AntPathMatcher pathMatcher = new AntPathMatcher(".");


	// 检查类是否应该监控
	public boolean shouldMonitorClass(String className) {
		Boolean cached = classCache.get(className);
		if (cached != null) {
			return cached;
		}

		// 1. 检查排除包
		for (String pattern : config().getExcludePackages()) {
			if (pathMatcher.match(pattern, className)) {
				classCache.put(className, false);
				return false;
			}
		}

		// 2. 检查包含包
		boolean includeByPackage = config().getIncludePackages().isEmpty();
		for (String pattern : config().getIncludePackages()) {
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
		if (!config().getIncludeClasses().isEmpty()) {
			boolean includeByClass = false;
			for (String pattern : config().getIncludeClasses()) {
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
		for (String pattern : config().getExcludeClasses()) {
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
		for (String pattern : config().getExcludeMethods()) {
			if (Pattern.matches(pattern, methodName)) {
				methodCache.put(cacheKey, false);
				return false;
			}
		}

		// 3. 检查方法包含规则
		if (!config().getIncludeMethods().isEmpty()) {
			boolean includeByMethod = false;
			for (String pattern : config().getIncludeMethods()) {
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
		if (config().getSamplingRate() >= 100) return true;
		if (config().getSamplingRate() <= 0) return false;

		// 高性能采样算法（使用内置哈希）
		int hash = identifier.hashCode() & 0x7FFFFFFF; // 取正数
		return (hash % 100) < config().getSamplingRate();
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

	public long getSlowThreshold() {
		return config().getSlowThreshold();
	}

	public boolean isCaptureSqlParameters() {
		return config().isCaptureSqlParameters();
	}
}
