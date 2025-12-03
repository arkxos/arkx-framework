package org.ark.framework.orm.sync.metadata;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:30
 * @since 1.0
 */

/**
 * 同步状态枚举 表示同步任务的状态
 */
public enum SyncStatus {

    /**
     * 初始状态
     */
    INITIAL("初始"),

    /**
     * 等待处理
     */
    PENDING("等待处理"),

    /**
     * 进行中
     */
    IN_PROGRESS("进行中"),

    /**
     * 已准备好，等待最终提交
     */
    PREPARED("已准备好，等待最终提交"),

    /**
     * 成功完成
     */
    SUCCESS("成功"),

    /**
     * 数据不一致
     */
    INCONSISTENT("数据不一致"),

    /**
     * 失败
     */
    FAILED("失败"),

    /**
     * 已取消
     */
    CANCELLED("已取消");

    private final String description;

    SyncStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

}
