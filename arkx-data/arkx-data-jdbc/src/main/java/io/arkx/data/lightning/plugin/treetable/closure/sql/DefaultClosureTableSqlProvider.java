package io.arkx.data.lightning.plugin.treetable.closure.sql;

/**
 * @author Nobody
 * @date 2025-07-28 2:39
 * @since 1.0
 */

import org.springframework.stereotype.Component;

import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.framework.data.common.entity.IdType;

@Component("defaultSqlProvider")
public class DefaultClosureTableSqlProvider implements ClosureTableSqlProvider {

	@Override
	public String rebuildCommonClosureSql(String closureTable, String bizTable, IdType idType) {
		String ancestorType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";
		String descendantType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";

		return String.format("""
				TRUNCATE TABLE %s;

				-- 插入自引用关系（depth=0）
				INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table)
				SELECT id, id, 0, '%s' FROM %s;

				-- 递归插入祖先-后代关系（depth>=1）
				WITH RECURSIVE hierarchy AS (
				    SELECT id AS descendant_id, parent_id, 1 AS depth
				    FROM %s WHERE parent_id IS NOT NULL
				    UNION ALL
				    SELECT h.descendant_id, b.parent_id, h.depth + 1
				    FROM hierarchy h
				    INNER JOIN %s b ON h.descendant_id = b.parent_id
				)
				INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table)
				SELECT parent_id AS ancestor_id, descendant_id, depth, '%s' FROM hierarchy;
				""", closureTable, closureTable, bizTable, bizTable, bizTable, bizTable, closureTable, bizTable);
	}

	@Override
	public String insertCommonClosureSql(String closureTable, IdType idType) {
		return "INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table) VALUES (?, ?, 0, ?)"
			.formatted(closureTable);
	}

	@Override
	public String findDescendantsCommonSql(String closureTable, IdType idType) {
		return "SELECT descendant_id FROM %s WHERE ancestor_id = ? AND biz_table = ? ORDER BY depth ASC"
			.formatted(closureTable);
	}

	// 独立闭包表默认实现（与公共表结构一致，无biz_table字段）
	@Override
	public String rebuildIndependentClosureSql(String closureTable, String businessTable, IdType idType) {
		String ancestorType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";
		String descendantType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";

		return String.format("""
				TRUNCATE TABLE %s;

				-- 插入自引用关系（depth=0）
				INSERT INTO %s (ancestor_id, descendant_id, depth)
				SELECT id, id, 0 FROM %s;

				-- 递归插入祖先-后代关系（depth>=1）
				WITH RECURSIVE hierarchy AS (
				    SELECT id AS descendant_id, parent_id, 1 AS depth
				    FROM %s WHERE parent_id IS NOT NULL
				    UNION ALL
				    SELECT h.descendant_id, b.parent_id, h.depth + 1
				    FROM hierarchy h
				    INNER JOIN %s b ON h.descendant_id = b.parent_id
				)
				INSERT INTO %s (ancestor_id, descendant_id, depth)
				SELECT parent_id AS ancestor_id, descendant_id, depth FROM hierarchy;
				""", closureTable, closureTable, businessTable, businessTable, businessTable, closureTable);
	}

	@Override
	public String insertIndependentClosureSql(String closureTable, IdType idType) {
		return "INSERT INTO %s (ancestor_id, descendant_id, depth) VALUES (?, ?, 0)";
	}

	@Override
	public String findDescendantsIndependentSql(String closureTable, IdType idType) {
		return "SELECT descendant_id FROM %s WHERE ancestor_id = ? ORDER BY depth ASC";
	}

	@Override
	public String findParentDescendantSql(String closureTable, IdType idType, BizTableMeta meta) {
		return String.format("""
				SELECT ancestor_id FROM %s
				WHERE descendant_id = ? %s
				""", closureTable,
				// 公共闭包表需过滤biz_table
				meta.isUseIndependent() ? "" : "AND biz_table = ?");
	}

	@Override
	public String deleteIndependentClosureSql(String closureTable, IdType idType) {
		return "DELETE c1 FROM " + closureTable + " c1 " + "JOIN " + closureTable
				+ " c2 ON c1.descendant_id = c2.descendant_id " + "WHERE c2.ancestor_id = ?";
	}

	@Override
	public String deleteCommonClosureSql(String closureTable, IdType idType) {
		return "DELETE c1 FROM " + closureTable + " c1 " + "JOIN " + closureTable
				+ " c2 ON c1.descendant_id = c2.descendant_id " + "WHERE c1.biz_table = ? " + "  AND c2.biz_table = ? "
				+ "  AND c2.ancestor_id = ?";
	}

	@Override
	public boolean support(String lowerCase) {
		return false;
	}

	@Override
	public String insertAncestorRelationsSql(String closureTable, IdType idType, BizTableMeta meta) {
		if (meta.isUseIndependent()) {
			// 独立闭包表 - 兼容所有数据库的写法
			return "INSERT INTO " + closureTable + " (ancestor_id, descendant_id, depth) "
					+ "SELECT ancestor_id, ?, depth + 1 " + "FROM " + closureTable + " " + "WHERE descendant_id = ?";
		}
		else {
			// 公共闭包表 - 兼容所有数据库的写法
			return "INSERT INTO " + closureTable + " (ancestor_id, descendant_id, depth, biz_table) "
					+ "SELECT ancestor_id, ?, depth + 1, ? " + "FROM " + closureTable + " "
					+ "WHERE descendant_id = ? AND biz_table = ?";
		}
	}

	/**
	 * 查询树状数据的SQL（单次JOIN查询）
	 */
	public String queryTreeDataSql(String closureTable, BizTableMeta meta) {
		if (meta.isUseIndependent()) {
			return "SELECT biz.*, closure.depth " + "FROM " + meta.getBizTable() + " biz " + "JOIN " + closureTable
					+ " closure ON closure.descendant_id = biz.id " + "WHERE closure.ancestor_id = ? "
					+ "ORDER BY closure.depth, biz.id";
		}
		else {
			return "SELECT biz.*, closure.depth " + "FROM " + meta.getBizTable() + " biz " + "JOIN " + closureTable
					+ " closure ON closure.descendant_id = biz.id "
					+ "WHERE closure.ancestor_id = ? AND closure.biz_table = ? " + "ORDER BY closure.depth, biz.id";
		}
	}

}
