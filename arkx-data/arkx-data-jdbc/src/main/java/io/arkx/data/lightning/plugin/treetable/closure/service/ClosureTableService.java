package io.arkx.data.lightning.plugin.treetable.closure.service;

/**
 * @author Nobody
 * @date 2025-07-28 3:22
 * @since 1.0
 */

import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.framework.commons.collection.tree.TreeNodeData;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.data.common.entity.IdType;

/**
 * 树状结构闭包表服务接口（支持多ID类型+多数据库优化）
 *
 * @author Nobody
 * @date 2025-07-28
 * @since 1.0
 */
public interface ClosureTableService {

    /**
     * 插入节点的闭包关系（仅闭包表，业务表插入由业务侧完成）
     *
     * @param nodeId
     *            当前节点ID
     * @param parentId
     *            父节点ID（null表示根节点）
     * @param meta
     *            业务表名（如"dept"、"menu"）
     * @param idType
     *            业务表ID类型（LONG/STRING）
     */
    @Transactional
    <T> void insertClosureRelations(T nodeId, T parentId, BizTableMeta meta, IdType idType);

    /**
     * 查询节点的所有后代ID（仅闭包表查询）
     *
     * @param nodeId
     *            当前节点ID
     * @return 后代ID列表（类型与输入ID一致）
     */
    <T> List<T> queryDescendantIds(T nodeId, BizTableMeta meta);

    <T> void updateClosureRelations(T nodeId, T parentId, BizTableMeta meta);

    <T> void deleteClosureRelations(T nodeId, BizTableMeta meta);

    <T, R extends TreeNodeData<T>> Treex<T, R> queryTreeData(T nodeId, BizTableMeta meta, Class<R> targetClass);

}
