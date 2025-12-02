package io.arkx.data.lightning.plugin.treetable.closure.sql;

/**
 * @author Nobody
 * @date 2025-07-28 1:57
 * @since 1.0
 */
import org.springframework.stereotype.Component;

import io.arkx.framework.data.common.entity.IdType;

@Component("mysql57SqlProvider")
public class MySql57ClosureTableSqlProvider extends DefaultClosureTableSqlProvider {

    @Override
    public boolean support(String dbtype) {
        return "mysql".equalsIgnoreCase(dbtype);
    }

    @Override
    public String rebuildCommonClosureSql(String closureTable, String bizTable, IdType idType) {
        String ancestorType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";
        String descendantType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";

        return String.format("""
                TRUNCATE TABLE %s;

                -- 创建临时表存储层级关系
                CREATE TEMPORARY TABLE IF NOT EXISTS temp_hierarchy (
                    ancestor_id %s,
                    descendant_id %s,
                    depth INT
                );

                -- 初始化根节点（depth=0）
                INSERT INTO temp_hierarchy (ancestor_id, descendant_id, depth)
                SELECT id, id, 0 FROM %s;

                -- 循环插入子节点（模拟递归）
                REPEAT
                    INSERT INTO temp_hierarchy (ancestor_id, descendant_id, depth)
                    SELECT th.ancestor_id, d.id, th.depth + 1
                    FROM temp_hierarchy th
                    JOIN %s d ON th.descendant_id = d.parent_id
                    WHERE th.descendant_id NOT IN (SELECT descendant_id FROM temp_hierarchy);
                UNTIL ROW_COUNT() = 0 END REPEAT;

                -- 插入最终结果到闭包表
                INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table)
                SELECT ancestor_id, descendant_id, depth, '%s' FROM temp_hierarchy;

                -- 清理临时表
                DROP TEMPORARY TABLE IF EXISTS temp_hierarchy;
                """, ancestorType, descendantType, bizTable, bizTable, closureTable, bizTable);
    }

    @Override
    public String rebuildIndependentClosureSql(String closureTable, String businessTable, IdType idType) {
        String ancestorType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";
        String descendantType = idType == IdType.LONG ? "BIGINT" : "VARCHAR(64)";

        return String.format("""
                TRUNCATE TABLE %s;

                -- 创建临时表存储层级关系
                CREATE TEMPORARY TABLE IF NOT EXISTS temp_hierarchy (
                    ancestor_id %s,
                    descendant_id %s,
                    depth INT
                );

                -- 初始化根节点（depth=0）
                INSERT INTO temp_hierarchy (ancestor_id, descendant_id, depth)
                SELECT id, id, 0 FROM %s;

                -- 循环插入子节点（模拟递归）
                REPEAT
                    INSERT INTO temp_hierarchy (ancestor_id, descendant_id, depth)
                    SELECT th.ancestor_id, d.id, th.depth + 1
                    FROM temp_hierarchy th
                    JOIN %s d ON th.descendant_id = d.parent_id
                    WHERE th.descendant_id NOT IN (SELECT descendant_id FROM temp_hierarchy);
                UNTIL ROW_COUNT() = 0 END REPEAT;

                -- 插入最终结果到闭包表
                INSERT INTO %s (ancestor_id, descendant_id, depth)
                SELECT ancestor_id, descendant_id, depth FROM temp_hierarchy;

                -- 清理临时表
                DROP TEMPORARY TABLE IF EXISTS temp_hierarchy;
                """, ancestorType, descendantType, businessTable, businessTable, closureTable);
    }
}
