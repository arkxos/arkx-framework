package io.arkx.data.lightning.plugin.treetable.closure.service;

/**
 * @author Nobody
 * @date 2025-07-28 3:22
 * @since 1.0
 */

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.entity.IdType;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 树状结构闭包表服务接口（支持多ID类型+多数据库优化）
 *
 * @param <ID> 节点ID类型（Long/String）
 * @author Nobody
 * @date 2025-07-28
 * @since 1.0
 */
public interface SmartClosureTableService {

	/**
	 * 插入节点的闭包关系（仅闭包表，业务表插入由业务侧完成）
	 *
	 * @param nodeId      当前节点ID
	 * @param parentId    父节点ID（null表示根节点）
	 * @param businessTable 业务表名（如"dept"、"menu"）
	 * @param idType      业务表ID类型（LONG/STRING）
	 */
	@Transactional
	<T> void insertClosureRelations(
			T nodeId,
			T parentId,
			BusinessTableMeta meta,
			IdType idType
	);

	/**
	 * 查询节点的所有后代ID（仅闭包表查询）
	 *
	 * @param nodeId      当前节点ID
	 * @param businessTable 业务表名（如"dept"、"menu"）
	 * @param idType      业务表ID类型（LONG/STRING）
	 * @return 后代ID列表（类型与输入ID一致）
	 */
	<T> List<T> queryDescendantIds(
			T nodeId,
			String businessTable,
			IdType idType
	);
}
