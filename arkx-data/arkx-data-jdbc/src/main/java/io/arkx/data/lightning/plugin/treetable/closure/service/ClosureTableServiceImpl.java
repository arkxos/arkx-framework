package io.arkx.data.lightning.plugin.treetable.closure.service;

/**
 * @author Nobody
 * @date 2025-07-28 2:00
 * @since 1.0
 */

import java.util.ArrayList;
import java.util.List;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.arkx.data.lightning.jdbc.GenericReflectionRowMapper;
import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.factory.ClosureTableSqlProviderFactory;
import io.arkx.data.lightning.plugin.treetable.closure.sql.ClosureTableSqlProvider;
import io.arkx.framework.commons.collection.tree.TreeNode;
import io.arkx.framework.commons.collection.tree.TreeNodeData;
import io.arkx.framework.commons.collection.tree.TreeUtil;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.data.common.entity.IdType;

@Service
public class ClosureTableServiceImpl implements ClosureTableService {

    private final JdbcTemplate jdbcTemplate;

    private final ClosureTableSqlProviderFactory sqlProviderFactory;

    public ClosureTableServiceImpl(JdbcTemplate jdbcTemplate, ClosureTableSqlProviderFactory sqlProviderFactory) {
        this.jdbcTemplate = jdbcTemplate;
        this.sqlProviderFactory = sqlProviderFactory;
    }

    // ------------------------------ 核心方法（仅闭包表操作） ------------------------------

    /**
     * 插入节点的闭包关系（仅闭包表，业务表插入由业务侧完成）
     *
     * @param nodeId
     *            当前节点ID
     * @param parentId
     *            父节点ID（null表示根节点）
     * @param meta
     *            业务表名
     * @param idType
     *            业务表ID类型（Long/String）
     */
    @Transactional
    public <T> void insertClosureRelations(T nodeId, T parentId, BizTableMeta meta, IdType idType) {
        String closureTable = resolveClosureTableName(meta);
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();

        // 1. 插入自引用关系（所有节点都需要）
        insertSelfClosureRelation(closureTable, nodeId, meta, idType);

        // 2. 如果有父节点，则插入祖先关系
        if (parentId != null) {
            // 使用单条 SQL 插入所有祖先关系
            String insertSql = provider.insertAncestorRelationsSql(closureTable, idType, meta);

            if (meta.isUseIndependent()) {
                jdbcTemplate.update(insertSql, nodeId, parentId);
            } else {
                jdbcTemplate.update(insertSql, nodeId, meta.getBizTable(), parentId, meta.getBizTable());
            }
        }
    }

    /**
     * 查询节点的所有后代ID（仅闭包表查询）
     *
     * @param nodeId
     *            当前节点ID
     * @param meta
     *            业务表名
     * @return 后代ID列表（类型与输入ID一致）
     */
    public <T> List<T> queryDescendantIds(T nodeId, BizTableMeta meta) {

        // 2. 解析闭包表名
        String closureTable = resolveClosureTableName(meta);

        // 3. 查询后代ID列表（动态适配ID类型）
        return queryDescendantIdsFromClosureTable(closureTable, nodeId, meta, meta.getIdType());
    }

    // ------------------------------ 私有辅助方法（闭包表操作） ------------------------------

    /**
     * 解析闭包表名（公共/独立 + ID类型）
     */
    private String resolveClosureTableName(BizTableMeta meta) {
        if (meta.isUseIndependent()) {
            return meta.getBizTable() + "_closure";
        } else {
            return "ark_tree_closure_" + meta.getIdType().name().toLowerCase();
        }
    }

    /**
     * 插入闭包表自引用关系（depth=0）
     */
    private <T> void insertSelfClosureRelation(String closureTable, T nodeId, BizTableMeta meta, IdType idType) {
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String insertSql = meta.isUseIndependent()
                ? provider.insertIndependentClosureSql(closureTable, idType)
                : provider.insertCommonClosureSql(closureTable, idType);

        if (meta.isUseIndependent()) {
            jdbcTemplate.update(insertSql, nodeId, nodeId); // 独立闭包表：ancestor=nodeId,
                                                            // descendant=nodeId
        } else {
            jdbcTemplate.update(insertSql, nodeId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=nodeId,
                                                                                // descendant=nodeId,
                                                                                // biz_table=业务表名
        }
    }

    /**
     * 插入直接子节点关系（depth=1）
     */
    private <T> void insertDirectChildRelation(String closureTable, T nodeId, T ancestorId, BizTableMeta meta,
            IdType idType) {
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String insertSql = meta.isUseIndependent()
                ? provider.insertIndependentClosureSql(closureTable, idType)
                : provider.insertCommonClosureSql(closureTable, idType);

        if (meta.isUseIndependent()) {
            jdbcTemplate.update(insertSql, ancestorId, nodeId); // 独立闭包表：ancestor=parentId,
                                                                // descendant=nodeId
        } else {
            jdbcTemplate.update(insertSql, ancestorId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=parentId,
                                                                                    // descendant=nodeId,
                                                                                    // biz_table=业务表名
        }
    }

    /**
     * 递归插入祖先关系（depth>=2）
     *
     * @param closureTable
     *            闭包表名
     * @param nodeId
     *            当前节点ID
     * @param ancestorId
     *            当前祖先节点ID（初始为父节点ID）
     * @param meta
     *            业务表元数据
     * @param idType
     *            ID类型
     * @param currentDepth
     *            当前深度（初始为2）
     */
    private <T> void insertAncestorRelations(String closureTable, T nodeId, T ancestorId, BizTableMeta meta,
            IdType idType, int currentDepth) {
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();

        // 1. 插入当前祖先关系（ancestorId -> nodeId，depth=currentDepth）
        String insertSql = meta.isUseIndependent()
                ? provider.insertIndependentClosureSql(closureTable, idType)
                : provider.insertCommonClosureSql(closureTable, idType);

        if (meta.isUseIndependent()) {
            jdbcTemplate.update(insertSql, ancestorId, nodeId); // 独立闭包表：ancestor=ancestorId,
                                                                // descendant=nodeId
        } else {
            jdbcTemplate.update(insertSql, ancestorId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=ancestorId,
                                                                                    // descendant=nodeId,
                                                                                    // biz_table=业务表名
        }

        // 2. 递归查询上一级祖先（ancestorId的父节点）
        T parentOfAncestor = getParentIdFromClosureTable(closureTable, ancestorId, meta, idType);
        if (parentOfAncestor != null && !parentOfAncestor.equals(ancestorId)) {
            insertAncestorRelations(closureTable, nodeId, parentOfAncestor, meta, idType, currentDepth + 1);
        }
    }

    /**
     * 从闭包表查询父节点ID（用于递归插入祖先关系）
     */
    private <T> T getParentIdFromClosureTable(String closureTable, T ancestorId, BizTableMeta meta, IdType idType) {
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String querySql = provider.findParentDescendantSql(closureTable, idType, meta);

        // 公共闭包表需要biz_table条件，独立闭包表不需要
        Object[] params = meta.isUseIndependent()
                ? new Object[]{ancestorId}
                : new Object[]{ancestorId, meta.getBizTable()};

        return jdbcTemplate.queryForObject(querySql, (rs, rowNum) -> (T) rs.getObject("ancestor_id"), params);
    }

    /**
     * 从闭包表查询后代ID列表（动态适配ID类型）
     */
    private <T> List<T> queryDescendantIdsFromClosureTable(String closureTable, T nodeId, BizTableMeta meta,
            IdType idType) {
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String querySql = meta.isUseIndependent()
                ? provider.findDescendantsIndependentSql(closureTable, idType)
                : provider.findDescendantsCommonSql(closureTable, idType);

        // 公共闭包表需要biz_table条件，独立闭包表不需要
        Object[] params = meta.isUseIndependent() ? new Object[]{nodeId} : new Object[]{nodeId, meta.getBizTable()};

        return jdbcTemplate.query(querySql, (rs, rowNum) -> (T) rs.getObject("descendant_id"), params);
    }

    /**
     * 更新闭包表中的父子关系（仅闭包表操作）
     *
     * @param nodeId
     *            当前节点ID
     * @param parentId
     *            新的父节点ID（null表示根节点）
     * @param meta
     *            业务表名
     */
    @Transactional
    public <T> void updateClosureRelations(T nodeId, T parentId, BizTableMeta meta) {
        // 2. 解析闭包表名
        String closureTable = resolveClosureTableName(meta);

        // 3. 删除旧的闭包关系
        deleteClosureRelations(nodeId, meta);

        // 4. 插入新的闭包关系
        insertClosureRelations(nodeId, parentId, meta, meta.getIdType());
    }

    /**
     * 删除闭包表中的节点关系（仅闭包表操作）
     *
     * @param nodeId
     *            当前节点ID
     * @param meta
     *            业务表名
     */
    @Transactional
    public <T> void deleteClosureRelations(T nodeId, BizTableMeta meta) {
        // 2. 解析闭包表名
        String closureTable = resolveClosureTableName(meta);

        // 3. 删除闭包关系
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String deleteSql = meta.isUseIndependent()
                ? provider.deleteIndependentClosureSql(closureTable, meta.getIdType())
                : provider.deleteCommonClosureSql(closureTable, meta.getIdType());

        if (meta.isUseIndependent()) {
            jdbcTemplate.update(deleteSql, nodeId);
        } else {
            jdbcTemplate.update(deleteSql, meta.getBizTable(), meta.getBizTable(), nodeId);
        }
    }

    /**
     * 查询树状结构数据（包含业务数据）使用单次JOIN查询
     *
     * @param nodeId
     *            起始节点ID（null表示查询整棵树）
     * @param meta
     *            业务表元数据
     * @return 树节点列表（包含子节点嵌套结构）
     */
    @Override
    public <T, R extends TreeNodeData<T>> Treex<T, R> queryTreeData(T nodeId, BizTableMeta meta, Class<R> targetClass) {
        // 1. 解析闭包表名
        String closureTable = resolveClosureTableName(meta);

        // 2. 构建单次JOIN查询SQL
        ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
        String sql = "";
        // 3. 执行查询并获取带层级关系的平面列表
        List<R> flatNodes = new ArrayList<>();
        if (nodeId == null) {
            sql = "select * from " + meta.getBizTable();
            flatNodes = executeTreeQuery(sql, targetClass);
        } else {
            sql = "select * from " + meta.getBizTable() + " where parent_id=?";
            // System.out.println("parentId = " + nodeId);
            flatNodes = executeTreeQuery(sql, new Object[]{nodeId}, targetClass);
            // sql = provider.queryTreeDataSql(closureTable, meta);
            // flatNodes = executeTreeQuery(sql, nodeId, meta, targetClass);
        }

        // 4. 构建树形结构
        List<TreeNode<T, R>> treeNodes = TreeUtil.buildTreeFromData(flatNodes);
        Treex<T, R> treex = new Treex<>();
        treex.getRoot().addChildren(treeNodes);
        return treex;
    }

    /**
     * 执行树查询并返回带层级信息的平面列表
     */
    private <T, R> List<R> executeTreeQuery(String sql, T nodeId, BizTableMeta meta, Class<R> targetClass) {
        RowMapper<R> rowMapper = new GenericReflectionRowMapper<>(targetClass);
        if (meta.isUseIndependent()) {
            return jdbcTemplate.query(sql, new Object[]{nodeId}, rowMapper);
        } else {
            return jdbcTemplate.query(sql, new Object[]{nodeId, meta.getBizTable()}, rowMapper);
        }
    }

    private <T, R> List<R> executeTreeQuery(String sql, Class<R> targetClass) {
        return executeTreeQuery(sql, new Object[]{}, targetClass);
    }

    private <T, R> List<R> executeTreeQuery(String sql, Object[] params, Class<R> targetClass) {
        RowMapper<R> rowMapper = new GenericReflectionRowMapper<>(targetClass);
        return jdbcTemplate.query(sql, params, rowMapper);
    }

}
