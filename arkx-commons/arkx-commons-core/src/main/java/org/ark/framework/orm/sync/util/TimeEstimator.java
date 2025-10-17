package org.ark.framework.orm.sync.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:31
 * @since 1.0
 */

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * 时间估算器
 * 用于算任务的剩余时间和完成百分比
 */
@Slf4j
public class TimeEstimator {

    // 记录开始时间
    private final long startTime;

    // 记录上次更新时间
    private long lastUpdateTime;

    // 总计数量
    private final int totalCount;

    // 当前已处理数量
    private int currentCount;

    // 移动平均处理速度 (条/秒)
    private double averageSpeed;

    // 移动平均窗口大小 (更新次数)
    private static final int AVERAGE_WINDOW_SIZE = 10;

    // 速度历史记录
    private final double[] speedHistory = new double[AVERAGE_WINDOW_SIZE];

    // 历史记录当前位置
    private int historyIndex = 0;

    // 历史记录填充数量
    private int historyCount = 0;

    // 最小样本量，在收集到这么多样本前不提供预估
    private static final int MIN_SAMPLES_FOR_ESTIMATE = 5;

    // 是否已经有足够样本用于估算
    @Getter
    private boolean hasEnoughSamplesForEstimate = false;

    /**
     * 构造函数
     *
     * @param totalCount 总计数量
     */
    public TimeEstimator(int totalCount) {
        this.startTime = System.currentTimeMillis();
        this.lastUpdateTime = this.startTime;
        this.totalCount = totalCount;
        this.currentCount = 0;
        this.averageSpeed = 0;
    }

    /**
     * 更新进度
     *
     * @param currentCount 当前已处理数量
     * @return 是否有足够样本用于估算
     */
    public boolean update(int currentCount) {
        // 检查参数有效性
        if (currentCount < this.currentCount) {
            log.warn("当前计数 ({}) 小于上次计数 ({}), 忽略此次更新", currentCount, this.currentCount);
            return hasEnoughSamplesForEstimate;
        }

        // 如果没有实际进展，则仅更新时间
        if (currentCount == this.currentCount) {
            lastUpdateTime = System.currentTimeMillis();
            return hasEnoughSamplesForEstimate;
        }

        // 计算时间和进度增量
        long now = System.currentTimeMillis();
        long timeElapsed = now - lastUpdateTime;
        int countIncrement = currentCount - this.currentCount;

        if (timeElapsed > 0) {
            // 计算本次速度 (条/秒)
            double currentSpeed = (countIncrement * 1000.0) / timeElapsed;

            // 添加到历史记录
            speedHistory[historyIndex] = currentSpeed;
            historyIndex = (historyIndex + 1) % AVERAGE_WINDOW_SIZE;
            if (historyCount < AVERAGE_WINDOW_SIZE) {
                historyCount++;
            }

            // 计算移动平均速度
            double speedSum = 0;
            for (int i = 0; i < historyCount; i++) {
                speedSum += speedHistory[i];
            }
            this.averageSpeed = speedSum / historyCount;

            // 更新足够样本的标志
            if (historyCount >= MIN_SAMPLES_FOR_ESTIMATE) {
                hasEnoughSamplesForEstimate = true;
            }
        }

        // 更新状态
        this.currentCount = currentCount;
        this.lastUpdateTime = now;

        return hasEnoughSamplesForEstimate;
    }

    /**
     * 获取进度百分比 (0-100)
     *
     * @return 进度百分比
     */
    public int getProgressPercentage() {
        if (totalCount <= 0) return 0;
        return Math.min(100, Math.max(0, (int)((currentCount * 100.0) / totalCount)));
    }

    /**
     * 获取已运行时间 (毫秒)
     *
     * @return 已运行时间
     */
    public long getElapsedTimeMs() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * 获取已运行时间 (秒)
     *
     * @return 已运行时间
     */
    public long getElapsedTimeSec() {
        return getElapsedTimeMs() / 1000;
    }

    /**
     * 获取预计剩余时间 (毫秒)
     *
     * @return 预计剩余时间，如果无法估算则返回-1
     */
    public long getEstimatedRemainingTimeMs() {
        if (!hasEnoughSamplesForEstimate || averageSpeed <= 0) {
            return -1;
        }

        int remainingCount = totalCount - currentCount;
        return (long)(remainingCount / averageSpeed * 1000);
    }

    /**
     * 获取预计剩余时间 (秒)
     *
     * @return 预计剩余时间，如果无法估算则返回-1
     */
    public long getEstimatedRemainingTimeSec() {
        long remainingMs = getEstimatedRemainingTimeMs();
        return (remainingMs < 0) ? -1 : remainingMs / 1000;
    }

    /**
     * 获取预计总时间 (毫秒)
     *
     * @return 预计总时间，如果无法估算则返回-1
     */
    public long getEstimatedTotalTimeMs() {
        long remainingMs = getEstimatedRemainingTimeMs();
        return (remainingMs < 0) ? -1 : getElapsedTimeMs() + remainingMs;
    }

    /**
     * 获取平均处理速度 (条/秒)
     *
     * @return 平均处理速度
     */
    public double getAverageSpeed() {
        return averageSpeed;
    }

    /**
     * 格式化时间为友好字符串
     *
     * @param timeMs 时间(毫秒)
     * @return 格式化后的字符串 (HH:mm:ss 或 mm:ss)
     */
    public static String formatTime(long timeMs) {
        if (timeMs < 0) return "未知";

        long seconds = timeMs / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;

        seconds %= 60;
        minutes %= 60;

        if (hours > 0) {
            return String.format("%d:%02d:%02d", hours, minutes, seconds);
        } else {
            return String.format("%d:%02d", minutes, seconds);
        }
    }

    /**
     * 获取友好格式的已运行时间
     *
     * @return 格式化的已运行时间
     */
    public String getFormattedElapsedTime() {
        return formatTime(getElapsedTimeMs());
    }

    /**
     * 获取友好格式的预计剩余时间
     *
     * @return 格式化的预计剩余时间
     */
    public String getFormattedRemainingTime() {
        return formatTime(getEstimatedRemainingTimeMs());
    }

    /**
     * 获取任务完成情况的描述
     *
     * @return 包含进度、用时和预计剩余时间的描述
     */
    public String getStatusDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("进度: %d/%d (%d%%)", currentCount, totalCount, getProgressPercentage()));
        sb.append(String.format(", 已用时: %s", getFormattedElapsedTime()));

        if (hasEnoughSamplesForEstimate) {
            sb.append(String.format(", 预计剩余: %s", getFormattedRemainingTime()));
            sb.append(String.format(", 处理速度: %.1f条/秒", averageSpeed));
        }

        return sb.toString();
    }
}
