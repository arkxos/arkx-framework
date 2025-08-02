package io.arkx.data.common.treetable.closure.sql;

/**
 * @author Nobody
 * @date 2025-07-28 1:57
 * @since 1.0
 */

import io.arkx.data.common.treetable.closure.entity.IdType;
import org.springframework.stereotype.Component;

@Component("oracleSqlProvider")
public class OracleClosureTableSqlProvider extends DefaultClosureTableSqlProvider {

	@Override
	public String rebuildCommonClosureSql(String closureTable, String bizTable, IdType idType) {
		return String.format("""
            TRUNCATE TABLE %s;
            
            -- 插入自引用关系（depth=0）
            INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table)
            SELECT id, id, 0, '%s' FROM %s;
            
            -- 使用CONNECT BY递归插入祖先-后代关系
            INSERT INTO %s (ancestor_id, descendant_id, depth, biz_table)
            SELECT 
                CONNECT_BY_ROOT id AS ancestor_id,
                id AS descendant_id,
                LEVEL - 1 AS depth,
                '%s'
            FROM %s
            CONNECT BY PRIOR id = parent_id
            START WITH parent_id IS NOT NULL;
            """, closureTable, closureTable, bizTable, bizTable,
				closureTable, bizTable, bizTable);
	}

	@Override
	public String rebuildIndependentClosureSql(String closureTable, String businessTable, IdType idType) {
		return String.format("""
            TRUNCATE TABLE %s;
            
            -- 插入自引用关系（depth=0）
            INSERT INTO %s (ancestor_id, descendant_id, depth)
            SELECT id, id, 0 FROM %s;
            
            -- 使用CONNECT BY递归插入祖先-后代关系
            INSERT INTO %s (ancestor_id, descendant_id, depth)
            SELECT 
                CONNECT_BY_ROOT id AS ancestor_id,
                id AS descendant_id,
                LEVEL - 1 AS depth
            FROM %s
            CONNECT BY PRIOR id = parent_id
            START WITH parent_id IS NOT NULL;
            """, closureTable, closureTable, businessTable,
				closureTable, businessTable);
	}
}