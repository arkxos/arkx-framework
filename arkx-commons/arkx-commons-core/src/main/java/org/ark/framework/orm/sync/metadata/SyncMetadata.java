package org.ark.framework.orm.sync.metadata;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:30
 * @since 1.0
 */

import java.util.Date;

import lombok.Data;

/**
 * 同步元数据 用于存储同步任务的状态和元数据信息
 */
@Data
public class SyncMetadata {

    /**
     * 源数据库
     */
    private String sourceDb;

    /**
     * 目标数据库
     */
    private String targetDb;

    /**
     * 表编码
     */
    private String tableCode;

    /**
     * 同步时间
     */
    private Date syncTime;

    /**
     * 同步记录数量
     */
    private int recordCount;

    /**
     * 同步状态
     */
    private SyncStatus status;

    /**
     * 同步消息或错误信息
     */
    private String message;

    /**
     * 同步持续时间(毫秒)
     */
    private long durationMs;
}
