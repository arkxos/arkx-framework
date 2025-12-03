package io.arkx.framework.performance.monitor;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;

/**
 * @author Nobody
 * @date 2025-06-06 11:58
 * @since 1.0
 */
@Component
public class SystemInfoService {

    private static final long MIN_CPU_INTERVAL = 500;

    private final SystemInfo systemInfo;

    private final HardwareAbstractionLayer hardware;

    private final OperatingSystem os;

    private final CentralProcessor processor;

    // CPU 监控状态
    private long[] prevTicks;

    private double currentCpuUsage;

    private long lastCpuUpdate;

    public SystemInfoService() {
        this.systemInfo = new SystemInfo();
        this.hardware = systemInfo.getHardware();
        this.os = systemInfo.getOperatingSystem();
        this.processor = hardware.getProcessor();

        // 初始化 CPU 监控
        this.prevTicks = processor.getSystemCpuLoadTicks();
        this.currentCpuUsage = 0;
        this.lastCpuUpdate = 0;
    }

    public String getSystemInfo() {
        StringBuilder sb = new StringBuilder();

        // 操作系统信息
        sb.append("OS: ").append(os.toString()).append("\n");
        sb.append("Version: ").append(os.getVersionInfo()).append("\n");
        sb.append("Boot Time: ").append(new Date(os.getSystemBootTime() * 1000)).append("\n");

        // 处理器信息
        sb.append("\nProcessor: ").append(processor.getProcessorIdentifier().getName()).append("\n");
        sb.append("Logical Cores: ").append(processor.getLogicalProcessorCount()).append("\n");
        sb.append("Physical Cores: ").append(processor.getPhysicalProcessorCount()).append("\n");
        sb.append("Max Frequency: ").append(String.format("%.2f GHz", processor.getMaxFreq() / 1e9)).append("\n");

        // 内存信息
        GlobalMemory memory = hardware.getMemory();
        sb.append("\nMemory Total: ").append(formatBytes(memory.getTotal())).append("\n");
        sb.append("Memory Available: ").append(formatBytes(memory.getAvailable())).append("\n");
        sb.append("Swap Total: ").append(formatBytes(memory.getVirtualMemory().getSwapTotal())).append("\n");
        sb.append("Swap Used: ").append(formatBytes(memory.getVirtualMemory().getSwapUsed())).append("\n");

        // 磁盘信息
        List<HWDiskStore> disks = hardware.getDiskStores();
        for (HWDiskStore disk : disks) {
            sb.append("\nDisk: ").append(disk.getName()).append("\n");
            sb.append("Model: ").append(disk.getModel()).append("\n");
            sb.append("Size: ").append(formatBytes(disk.getSize())).append("\n");
        }

        return sb.toString();
    }

    /**
     * 获取系统摘要信息
     */
    public Map<String, Object> getSystemSummary() {
        Map<String, Object> summary = new LinkedHashMap<>();

        // 操作系统信息
        Map<String, Object> osInfo = new LinkedHashMap<>();
        osInfo.put("name", os.getFamily());
        osInfo.put("version", os.getVersionInfo().toString());
        osInfo.put("bitness", os.getBitness() + "-bit");
        osInfo.put("bootTime", new Date(os.getSystemBootTime() * 1000));
        summary.put("os", osInfo);

        // 处理器信息
        Map<String, Object> cpuInfo = new LinkedHashMap<>();
        cpuInfo.put("name", processor.getProcessorIdentifier().getName());
        cpuInfo.put("logicalCores", processor.getLogicalProcessorCount());
        cpuInfo.put("physicalCores", processor.getPhysicalProcessorCount());
        cpuInfo.put("maxFrequency", String.format("%.2f GHz", processor.getMaxFreq() / 1e9));
        summary.put("cpu", cpuInfo);

        // 内存信息
        GlobalMemory memory = hardware.getMemory();
        Map<String, Object> memInfo = new LinkedHashMap<>();
        memInfo.put("total", FormatUtil.formatBytes(memory.getTotal()));
        memInfo.put("available", FormatUtil.formatBytes(memory.getAvailable()));
        memInfo.put("usedPercent",
                String.format("%.1f%%", (memory.getTotal() - memory.getAvailable()) * 100.0 / memory.getTotal()));
        summary.put("memory", memInfo);

        // 磁盘信息摘要
        List<HWDiskStore> disks = hardware.getDiskStores();
        Map<String, Object> diskSummary = new LinkedHashMap<>();
        long totalDisk = 0;
        long usedDisk = 0;

        for (HWDiskStore disk : disks) {
            totalDisk += disk.getSize();
            // 更准确的使用量计算
            usedDisk += disk.getSize() - disk.getReadBytes();
        }

        diskSummary.put("total", FormatUtil.formatBytes(totalDisk));
        diskSummary.put("used", FormatUtil.formatBytes(usedDisk));
        diskSummary.put("disksCount", disks.size());
        summary.put("disk", diskSummary);

        // 交换空间
        VirtualMemory virtualMemory = memory.getVirtualMemory();
        Map<String, Object> swapInfo = new LinkedHashMap<>();
        swapInfo.put("total", FormatUtil.formatBytes(virtualMemory.getSwapTotal()));
        swapInfo.put("used", FormatUtil.formatBytes(virtualMemory.getSwapUsed()));
        summary.put("swap", swapInfo);

        // 网络信息摘要
        List<NetworkIF> networks = hardware.getNetworkIFs();
        Map<String, Object> netSummary = new LinkedHashMap<>();
        netSummary.put("interfacesCount", networks.size());

        if (!networks.isEmpty()) {
            NetworkIF net = networks.get(0);
            netSummary.put("primaryInterface", net.getName());
            netSummary.put("ipv4", net.getIPv4addr());
            netSummary.put("ipv6", net.getIPv6addr());
        }
        summary.put("network", netSummary);

        // 系统时间
        summary.put("reportTime", new Date());

        return summary;
    }

    /**
     * 获取系统级CPU使用率
     */
    public double getSystemCpuUsage() {
        long now = System.currentTimeMillis();
        if (now - lastCpuUpdate < MIN_CPU_INTERVAL) {
            return currentCpuUsage;
        }

        double newCpuUsage = calculateSystemCpuUsage();
        currentCpuUsage = newCpuUsage;
        lastCpuUpdate = now;
        return newCpuUsage;
    }

    /**
     * 实际计算系统CPU使用率
     */
    private double calculateSystemCpuUsage() {
        long[] currentTicks = processor.getSystemCpuLoadTicks();
        if (prevTicks == null) {
            prevTicks = currentTicks;
            return 0.0;
        }

        long[] prev = prevTicks;
        prevTicks = currentTicks;

        long totalDelta = 0;
        for (int i = 0; i < currentTicks.length; i++) {
            totalDelta += currentTicks[i] - prev[i];
        }

        if (totalDelta == 0)
            return 0.0;

        long idleDelta = (currentTicks[CentralProcessor.TickType.IDLE.getIndex()]
                - prev[CentralProcessor.TickType.IDLE.getIndex()])
                + (currentTicks[CentralProcessor.TickType.IOWAIT.getIndex()]
                        - prev[CentralProcessor.TickType.IOWAIT.getIndex()]);

        return 1.0 - ((double) idleDelta / totalDelta);
    }

    /**
     * 获取进程级CPU使用率（使用JMX）
     */
    public double getProcessCpuUsage() {
        java.lang.management.OperatingSystemMXBean osBean = java.lang.management.ManagementFactory
                .getOperatingSystemMXBean();

        if (osBean instanceof com.sun.management.OperatingSystemMXBean) {
            return ((com.sun.management.OperatingSystemMXBean) osBean).getProcessCpuLoad();
        }
        return 0.0;
    }

    /**
     * 获取内存使用率
     */
    public double getMemoryUsage() {
        GlobalMemory memory = hardware.getMemory();
        return 1.0 - (memory.getAvailable() / (double) memory.getTotal());
    }

    /**
     * 获取交换空间使用率
     */
    public double getSwapUsage() {
        VirtualMemory virtualMemory = hardware.getMemory().getVirtualMemory();
        long swapTotal = virtualMemory.getSwapTotal();
        long swapUsed = virtualMemory.getSwapUsed();
        return swapTotal > 0 ? (double) swapUsed / swapTotal : 0.0;
    }

    /**
     * 获取磁盘使用率
     */
    public double getDiskUsage() {
        OperatingSystem os = systemInfo.getOperatingSystem();
        FileSystem fs = os.getFileSystem();

        long total = 0;
        long used = 0;

        for (OSFileStore store : fs.getFileStores()) {
            long usableSpace = store.getUsableSpace();
            long totalSpace = store.getTotalSpace();
            total += totalSpace;
            used += (totalSpace - usableSpace);
        }

        return total > 0 ? used / (double) total : 0.0;
    }

    /**
     * 获取系统健康状态
     */
    public Map<String, Object> getSystemHealth() {
        Map<String, Object> health = new LinkedHashMap<>();
        health.put("cpuUsage", Math.round(getSystemCpuUsage() * 100));
        health.put("memoryUsage", Math.round(getMemoryUsage() * 100));
        health.put("diskUsage", Math.round(getDiskUsage() * 100));
        health.put("swapUsage", Math.round(getSwapUsage() * 100));

        // 传感器数据
        Sensors sensors = hardware.getSensors();
        if (sensors != null) {
            health.put("cpuTemperature", sensors.getCpuTemperature());
            health.put("fanSpeeds", sensors.getFanSpeeds());
        }

        // 系统负载
        double[] loadAverages = processor.getSystemLoadAverage(3);
        health.put("loadAverage1", loadAverages[0]);
        health.put("loadAverage5", loadAverages[1]);
        health.put("loadAverage15", loadAverages[2]);

        return health;
    }

    /**
     * 获取详细的系统信息
     */
    public Map<String, Object> getSystemDetails() {
        Map<String, Object> details = new LinkedHashMap<>();

        // 添加摘要信息
        details.put("summary", getSystemSummary());

        // 添加更多细节
        details.put("fullProcessorName", processor.toString());
        details.put("processorIdentifier", processor.getProcessorIdentifier().toString());

        // 磁盘详情
        List<Map<String, Object>> disks = new java.util.ArrayList<>();
        for (HWDiskStore disk : hardware.getDiskStores()) {
            Map<String, Object> diskInfo = new LinkedHashMap<>();
            diskInfo.put("name", disk.getName());
            diskInfo.put("model", disk.getModel());
            diskInfo.put("size", FormatUtil.formatBytes(disk.getSize()));
            diskInfo.put("reads", disk.getReads());
            diskInfo.put("writes", disk.getWrites());
            diskInfo.put("readBytes", FormatUtil.formatBytes(disk.getReadBytes()));
            diskInfo.put("writeBytes", FormatUtil.formatBytes(disk.getWriteBytes()));
            disks.add(diskInfo);
        }
        details.put("disks", disks);

        // 网络接口详情
        List<Map<String, Object>> networks = new java.util.ArrayList<>();
        for (NetworkIF net : hardware.getNetworkIFs()) {
            Map<String, Object> netInfo = new LinkedHashMap<>();
            netInfo.put("name", net.getName());
            netInfo.put("displayName", net.getDisplayName());
            netInfo.put("mac", net.getMacaddr());
            netInfo.put("ipv4", net.getIPv4addr());
            netInfo.put("ipv6", net.getIPv6addr());
            netInfo.put("bytesRecv", FormatUtil.formatBytes(net.getBytesRecv()));
            netInfo.put("bytesSent", FormatUtil.formatBytes(net.getBytesSent()));
            networks.add(netInfo);
        }
        details.put("networkInterfaces", networks);

        return details;
    }

    /**
     * 获取逻辑处理器数量
     */
    public int getLogicalProcessorCount() {
        return processor.getLogicalProcessorCount();
    }

    /**
     * getPhysicalProcessorCount
     */
    public int getPhysicalProcessorCount() {
        return processor.getPhysicalProcessorCount();
    }

    private String formatBytes(long bytes) {
        if (bytes < 1024)
            return bytes + " B";
        int exp = (int) (Math.log(bytes) / Math.log(1024));
        char pre = "KMGTPE".charAt(exp - 1);
        return String.format("%.1f %sB", bytes / Math.pow(1024, exp), pre);
    }

}
