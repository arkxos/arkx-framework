package io.arkx.framework.performance.monitor.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * 配置管理
 *
 * @author Nobody
 * @date 2025-06-06 0:39
 * @since 1.0
 */
@Getter
@Setter
@Slf4j
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

    private String pointcut;

    // 包含/排除规则
    private volatile List<String> includePackages = new ArrayList<>();
    private volatile List<String> includeClasses = new ArrayList<>();
    private volatile List<String> includeMethods = new ArrayList<>();

    private volatile List<String> excludePackages = Collections.singletonList("org.springframework..*");
    private volatile List<String> excludeClasses = new ArrayList<>();
    private volatile List<String> excludeMethods = Arrays.asList("get.*", "set.*", "is.*");

    @PostConstruct
    public void init() {
        log.info("Monitoring system configuration loaded. Enabled: {}, Sampling: {}%", enabled, samplingRate);
    }

}
