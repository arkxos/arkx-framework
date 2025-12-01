package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:57
 * @since 1.0
 */

import lombok.Data;
import org.ark.framework.orm.sync.metadata.SyncStatus;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 同步汇总
 * 汇总多个表的同步结果
 */
@Data
public class SyncSummary {

    /**
     * 开始时间
     */
    private final Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 表级别结果
     */
    private final Map<String, SyncResult> tableResults;

    /**
     * 表错误信息
     */
    private final Map<String, Throwable> tableErrors;

    /**
     * 总表数
     */
    private final AtomicInteger totalTables;

    /**
     * 成功表数
     */
    private final AtomicInteger successTables;

    /**
     * 失败表数
     */
    private final AtomicInteger failedTables;

    /**
     * 不一致表数
     */
    private final AtomicInteger inconsistentTables;

    /**
     * 总处理记录数
     */
    private final AtomicInteger totalRecordsProcessed;

    /**
     * 构造函数
     */
    public SyncSummary() {
        this.startTime = new Date();
        this.tableResults = new HashMap<>();
        this.tableErrors = new HashMap<>();
        this.totalTables = new AtomicInteger(0);
        this.successTables = new AtomicInteger(0);
        this.failedTables = new AtomicInteger(0);
        this.inconsistentTables = new AtomicInteger(0);
        this.totalRecordsProcessed = new AtomicInteger(0);
    }

    /**
     * 添加表同步结果
     *
     * @param tableCode 表编码
     * @param result 同步结果
     */
    public void addTableResult(String tableCode, SyncResult result) {
        tableResults.put(tableCode, result);
        totalTables.incrementAndGet();
        totalRecordsProcessed.addAndGet(result.getRecordsProcessed());

        if (result.getStatus() == SyncStatus.SUCCESS) {
            successTables.incrementAndGet();
        } else if (result.getStatus() == SyncStatus.INCONSISTENT) {
            inconsistentTables.incrementAndGet();
        } else if (result.getStatus() == SyncStatus.FAILED) {
            failedTables.incrementAndGet();
            if (result.getError() != null) {
                tableErrors.put(tableCode, result.getError());
            }
        }
    }

    /**
     * 添加表错误
     *
     * @param tableCode 表编码
     * @param error 错误
     */
    public void addTableError(String tableCode, Throwable error) {
        totalTables.incrementAndGet();
        failedTables.incrementAndGet();
        tableErrors.put(tableCode, error);

        SyncResult result = new SyncResult(tableCode);
        result.setStatus(SyncStatus.FAILED);
        result.setError(error);
        tableResults.put(tableCode, result);
    }

    /**
     * 标记同步完成
     */
    public void markCompleted() {
        this.endTime = new Date();
    }

    /**
     * 获取总耗时(毫秒)
     *
     * @return 总耗时
     */
    public long getTotalDurationMs() {
        if (endTime == null) {
            return System.currentTimeMillis() - startTime.getTime();
        }
        return endTime.getTime() - startTime.getTime();
    }

    /**
     * 获取同步成功率
     *
     * @return 成功率(0-100)
     */
    public double getSuccessRate() {
        if (totalTables.get() == 0) {
            return 0.0;
        }
        return (successTables.get() * 100.0) / totalTables.get();
    }

    /**
     * 判断是否全部成功
     *
     * @return 是否全部成功
     */
    public boolean isAllSuccess() {
        return totalTables.get() > 0 && failedTables.get() == 0 && inconsistentTables.get() == 0;
    }

    /**
     * 获取指定表的同步结果
     *
     * @param tableCode 表编码
     * @return 表同步结果，如果不存在则返回null
     */
    public SyncResult getTableResult(String tableCode) {
        return tableResults.get(tableCode);
    }

    /**
     * 获取简要描述
     *
     * @return 简要描述
     */
    public String getSummary() {
        StringBuilder sb = new StringBuilder();
        sb.append("同步任务汇总: 总表数=").append(totalTables.get())
                .append(", 成功=").append(successTables.get())
                .append(", 失败=").append(failedTables.get())
                .append(", 数据不一致=").append(inconsistentTables.get())
                .append(", 总记录数=").append(totalRecordsProcessed.get())
                .append(", 成功率=").append(String.format("%.2f%%", getSuccessRate()))
                .append(", 总耗时=").append(getTotalDurationMs()).append("毫秒");

        return sb.toString();
    }
}
