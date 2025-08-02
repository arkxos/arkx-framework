package io.arkx.data.common.treetable.closure.sql;

/**
 * @author Nobody
 * @date 2025-07-28 1:56
 * @since 1.0
 */

import io.arkx.data.common.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.common.treetable.closure.entity.IdType;

import java.util.List;

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
	String findParentDescendantSql(String closureTable, IdType idType, BusinessTableMeta meta);
	String findDescendantsIndependentSql(String closureTable, IdType idType);
}