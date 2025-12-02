package io.arkx.framework.performance.monitor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.arkx.framework.performance.monitor.config.MonitorConfigService;
import io.arkx.framework.performance.monitor.util.ApplicationContextHolder;

/**
 * @author Nobody
 * @date 2025-06-06 0:43
 * @since 1.0
 */
/* ====================== 初始化系统 ====================== */
@Component
public class MonitoringInitializer {

    private final TraceContext traceContext;
    private final TraceRecorder recorder;

    @Autowired
    public MonitoringInitializer(TraceContext traceContext, TraceRecorder recorder) {
        this.traceContext = traceContext;
        this.recorder = recorder;
    }

    // 周期性刷新配置缓存
    @Scheduled(fixedRate = 10_000)
    public void refreshConfigCache() {
        ApplicationContextHolder.getBean(MonitorConfigService.class).refreshCache();
    }

}
