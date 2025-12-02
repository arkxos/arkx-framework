package io.arkx.framework.data.db.core.util;

@FunctionalInterface
public interface ColumHandler {

    /**
     * 自定义字段处理器
     *
     * @param originalResult
     *            {@link Object[]} 原始数据集（源表查询的结果集）
     * @param index
     *            {@link Integer} 要处理的数据索引
     */
    void handle(Object[] originalResult, int index);

}
