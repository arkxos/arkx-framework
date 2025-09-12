package io.arkx.data.lightning.plugin.treetable.closure.sql;

/**
 * @author Nobody
 * @date 2025-07-28 1:56
 * @since 1.0
 */

import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.framework.data.common.entity.IdType;

public interface ClosureTableSqlProvider {
	// ------------------------------ 公共闭包表 ------------------------------
	String rebuildCommonClosureSql(String closureTable, String bizTable, IdType idType);
	String insertCommonClosureSql(String closureTable, IdType idType);
	String findDescendantsCommonSql(String closureTable, IdType idType);

	// ------------------------------ 独立闭包表 ------------------------------
	String rebuildIndependentClosureSql(String closureTable, String businessTable, IdType idType);
	String insertIndependentClosureSql(String closureTable, IdType idType);
	/**
	 * 查询闭包表中指定节点的父节点ID（用于递归插入祖先关系）
	 * @param closureTable 闭包表名
	 * @param idType ID类型
	 * @return SQL语句
	 */
	String findParentDescendantSql(String closureTable, IdType idType, BizTableMeta meta);
	String findDescendantsIndependentSql(String closureTable, IdType idType);

	// ------------------------------ 删除闭包关系 ------------------------------
	/**
	 * 删除独立闭包表中的节点关系
	 * @param closureTable 闭包表名
	 * @param idType ID类型
	 * @return SQL语句
	 */
	String deleteIndependentClosureSql(String closureTable, IdType idType);

	/**
	 * 删除公共闭包表中的节点关系
	 * @param closureTable 闭包表名
	 * @param idType ID类型
	 * @return SQL语句
	 */
	String deleteCommonClosureSql(String closureTable, IdType idType);

    boolean support(String lowerCase);

	String insertAncestorRelationsSql(String closureTable, IdType idType, BizTableMeta meta);

	String queryTreeDataSql(String closureTable, BizTableMeta meta);
}