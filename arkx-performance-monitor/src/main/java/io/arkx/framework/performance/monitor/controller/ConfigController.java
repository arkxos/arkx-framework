package io.arkx.framework.performance.monitor.controller;

import java.util.Map;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import org.springframework.web.bind.annotation.*;

import io.arkx.framework.performance.monitor.config.MonitorConfig;
import io.arkx.framework.performance.monitor.config.MonitorConfigService;

/**
 * @author Nobody
 * @date 2025-06-06 0:43
 * @since 1.0
 */
/* ====================== 配置控制器 ====================== */
@RestController
@RequestMapping("/api/monitoring/config")
public class ConfigController {

    private final MonitorConfig config;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true);
    private final MonitorConfigService monitorConfigService;

    public ConfigController(MonitorConfig config, MonitorConfigService monitorConfigService) {
        this.config = config;
        this.monitorConfigService = monitorConfigService;
    }

    @GetMapping
    public Map<String, Object> getConfig() {
        lock.readLock().lock();
        try {
            return Map.of("enabled", config.isEnabled(), "samplingRate", config.getSamplingRate(), "slowThreshold",
                    config.getSlowThreshold(), "captureSqlParameters", config.isCaptureSqlParameters(),
                    "storeToDatabase", config.isStoreToDatabase());
        } finally {
            lock.readLock().unlock();
        }
    }

    @PostMapping
    public String updateConfig(@RequestBody Map<String, Object> updates) {
        lock.writeLock().lock();
        try {
            if (updates.containsKey("enabled"))
                config.setEnabled((Boolean) updates.get("enabled"));

            if (updates.containsKey("samplingRate"))
                config.setSamplingRate((Integer) updates.get("samplingRate"));

            if (updates.containsKey("slowThreshold"))
                config.setSlowThreshold(((Number) updates.get("slowThreshold")).longValue());

            if (updates.containsKey("captureSqlParameters"))
                config.setCaptureSqlParameters((Boolean) updates.get("captureSqlParameters"));

            if (updates.containsKey("storeToDatabase"))
                config.setStoreToDatabase((Boolean) updates.get("storeToDatabase"));

            // 标记配置已变更
            monitorConfigService.markDirty();

            return "Config updated successfully";
        } finally {
            lock.writeLock().unlock();
        }
    }
}
