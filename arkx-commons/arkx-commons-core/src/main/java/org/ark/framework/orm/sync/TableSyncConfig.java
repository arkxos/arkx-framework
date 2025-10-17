package org.ark.framework.orm.sync;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:57
 * @since 1.0
 */

import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;

import lombok.Builder;
import lombok.Data;

/**
 * 表同步配置
 * 定义表同步的相关参数和策略
 */
@Data
@Builder
public class TableSyncConfig {

    /**
     * 时间戳字段名
     * 用于增量数据捕获的时间戳字段
     */
    private String timestampField;

    /**
     * 主键字段
     * 用于数据一致性验证和更新操作
     */
    private String primaryKeyField;

    /**
     * 需要同步的字段集合
     * 如果为空，则同步所有字段
     */
    @Builder.Default
    private Set<String> includedFields = new HashSet<>();

    /**
     * 排除同步的字段集合
     */
    @Builder.Default
    private Set<String> excludedFields = new HashSet<>();

    /**
     * 用于数据一致性比较的字段列表
     * 如果为空，则比较所有字段
     */
    @Builder.Default
    private List<String> compareFields = new ArrayList<>();

    /**
     * 冲突解决策略
     */
    @Builder.Default
    private ConflictStrategy conflictStrategy = ConflictStrategy.SOURCE_WINS;

    /**
     * 批处理大小
     * 每次批量处理的记录数
     */
    @Builder.Default
    private int batchSize = 1000;

    /**
     * 是否启用数据验证
     */
    @Builder.Default
    private boolean enableValidation = true;

    /**
     * 验证方式
     */
    @Builder.Default
    private ValidationStrategy validationStrategy = ValidationStrategy.PRIMARY_KEY_SAMPLE;

    /**
     * 构建基本配置
     *
     * @param timestampField 时间戳字段
     * @param primaryKeyField 主键字段
     * @return 配置对象
     */
    public static TableSyncConfig basic(String timestampField, String primaryKeyField) {
        return TableSyncConfig.builder()
                .timestampField(timestampField)
                .primaryKeyField(primaryKeyField)
                .build();
    }

    /**
     * 冲突解决策略枚举
     */
    public enum ConflictStrategy {
        /** 源数据优先 */
        SOURCE_WINS,

        /** 目标数据优先 */
        TARGET_WINS,

        /** 合并数据 */
        MERGE,

        /** 跳过冲突 */
        SKIP
    }

    /**
     * 验证策略枚举
     */
    public enum ValidationStrategy {
        /** 不验证 */
        NONE,

        /** 基于主键的抽样验证 */
        PRIMARY_KEY_SAMPLE,

        /** 全表记录数验证 */
        RECORD_COUNT,

        /** 全表哈希验证 */
        TABLE_HASH
    }
}