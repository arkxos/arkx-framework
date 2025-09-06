package io.arkx.data.lightning.plugin.treetable.closure.service;

/**
 * @author Nobody
 * @date 2025-07-28 2:00
 * @since 1.0
 */

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.entity.IdType;
import io.arkx.data.lightning.plugin.treetable.closure.factory.ClosureTableSqlProviderFactory;
import io.arkx.data.lightning.plugin.treetable.closure.sql.ClosureTableSqlProvider;
import io.arkx.data.lightning.repository.BusinessTableMetaJdbcRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SmartClosureTableServiceImpl implements SmartClosureTableService {

	private final JdbcTemplate jdbcTemplate;
	private final ClosureTableSqlProviderFactory sqlProviderFactory;
	private final BusinessTableMetaJdbcRepository metaRepository;

	public SmartClosureTableServiceImpl(JdbcTemplate jdbcTemplate,
										ClosureTableSqlProviderFactory sqlProviderFactory,
										BusinessTableMetaJdbcRepository metaRepository) {
		this.jdbcTemplate = jdbcTemplate;
		this.sqlProviderFactory = sqlProviderFactory;
		this.metaRepository = metaRepository;
	}

	// ------------------------------ 核心方法（仅闭包表操作） ------------------------------
	/**
	 * 插入节点的闭包关系（仅闭包表，业务表插入由业务侧完成）
	 * @param nodeId 当前节点ID
	 * @param parentId 父节点ID（null表示根节点）
	 * @param businessTable 业务表名
	 * @param idType 业务表ID类型（Long/String）
	 */
	@Transactional
	public <T> void insertClosureRelations(T nodeId, T parentId, BusinessTableMeta meta, IdType idType) {

		// 2. 解析闭包表名
		String closureTable = resolveClosureTableName(meta);

		// 3. 插入自引用关系（depth=0）
		insertSelfClosureRelation(closureTable, nodeId, meta, idType);

		// 4. 插入直接子节点关系（depth=1）并递归插入祖先关系
		if (parentId != null) {
			insertDirectChildRelation(closureTable, nodeId, parentId, meta, idType);
			insertAncestorRelations(closureTable, nodeId, parentId, meta, idType, 2);
		}
	}

	/**
	 * 查询节点的所有后代ID（仅闭包表查询）
	 * @param nodeId 当前节点ID
	 * @param businessTable 业务表名
	 * @param idType 业务表ID类型（Long/String）
	 * @return 后代ID列表（类型与输入ID一致）
	 */
	public <T> List<T> queryDescendantIds(T nodeId, String businessTable, IdType idType) {
		// 1. 获取业务表元数据（校验闭包表类型）
		BusinessTableMeta meta = metaRepository.findByBusinessTable(businessTable)
				.orElseThrow(() -> new IllegalArgumentException("业务表未配置: " + businessTable));

		// 2. 解析闭包表名
		String closureTable = resolveClosureTableName(meta);

		// 3. 查询后代ID列表（动态适配ID类型）
		return queryDescendantIdsFromClosureTable(closureTable, nodeId, meta, idType);
	}

	// ------------------------------ 私有辅助方法（闭包表操作） ------------------------------
	/**
	 * 解析闭包表名（公共/独立 + ID类型）
	 */
	private String resolveClosureTableName(BusinessTableMeta meta) {
		if (meta.isUseIndependent()) {
			return meta.getBizTable() + "_closure";
		} else {
			return "ark_tree_closure_" + meta.getIdType().name().toLowerCase();
		}
	}

	/**
	 * 插入闭包表自引用关系（depth=0）
	 */
	private <T> void insertSelfClosureRelation(String closureTable, T nodeId, BusinessTableMeta meta, IdType idType) {
		ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
		String insertSql = meta.isUseIndependent() ?
				provider.insertIndependentClosureSql(closureTable, idType) :
				provider.insertCommonClosureSql(closureTable, idType);

		if (meta.isUseIndependent()) {
			jdbcTemplate.update(insertSql, nodeId, nodeId); // 独立闭包表：ancestor=nodeId, descendant=nodeId
		} else {
			jdbcTemplate.update(insertSql, nodeId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=nodeId, descendant=nodeId, biz_table=业务表名
		}
	}

	/**
	 * 插入直接子节点关系（depth=1）
	 */
	private <T> void insertDirectChildRelation(String closureTable, T nodeId, T parentId, BusinessTableMeta meta, IdType idType) {
		ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
		String insertSql = meta.isUseIndependent() ?
				provider.insertIndependentClosureSql(closureTable, idType) :
				provider.insertCommonClosureSql(closureTable, idType);

		if (meta.isUseIndependent()) {
			jdbcTemplate.update(insertSql, nodeId, parentId); // 独立闭包表：ancestor=parentId, descendant=nodeId
		} else {
			jdbcTemplate.update(insertSql, parentId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=parentId, descendant=nodeId, biz_table=业务表名
		}
	}

	/**
	 * 递归插入祖先关系（depth>=2）
	 * @param closureTable 闭包表名
	 * @param nodeId 当前节点ID
	 * @param ancestorId 当前祖先节点ID（初始为父节点ID）
	 * @param meta 业务表元数据
	 * @param idType ID类型
	 * @param currentDepth 当前深度（初始为2）
	 */
	private <T> void insertAncestorRelations(String closureTable, T nodeId, T ancestorId, BusinessTableMeta meta, IdType idType, int currentDepth) {
		ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();

		// 1. 插入当前祖先关系（ancestorId -> nodeId，depth=currentDepth）
		String insertSql = meta.isUseIndependent() ?
				provider.insertIndependentClosureSql(closureTable, idType) :
				provider.insertCommonClosureSql(closureTable, idType);

		if (meta.isUseIndependent()) {
			jdbcTemplate.update(insertSql, nodeId, ancestorId); // 独立闭包表：ancestor=ancestorId, descendant=nodeId
		} else {
			jdbcTemplate.update(insertSql, ancestorId, nodeId, meta.getBizTable()); // 公共闭包表：ancestor=ancestorId, descendant=nodeId, biz_table=业务表名
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
	private  <T>  T getParentIdFromClosureTable(String closureTable, T ancestorId, BusinessTableMeta meta, IdType idType) {
		ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
		String querySql =
				provider.findParentDescendantSql(closureTable, idType, meta);

		// 公共闭包表需要biz_table条件，独立闭包表不需要
		Object[] params = meta.isUseIndependent() ?
				new Object[]{ancestorId} :
				new Object[]{ancestorId, meta.getBizTable()};

		return jdbcTemplate.queryForObject(querySql, (rs, rowNum) -> (T)rs.getObject("ancestor_id"), params);
	}

	/**
	 * 从闭包表查询后代ID列表（动态适配ID类型）
	 */
	private  <T>  List<T> queryDescendantIdsFromClosureTable(String closureTable, T nodeId, BusinessTableMeta meta, IdType idType) {
		ClosureTableSqlProvider provider = sqlProviderFactory.getProvider();
		String querySql = meta.isUseIndependent() ?
				provider.findDescendantsIndependentSql(closureTable, idType) :
				provider.findDescendantsCommonSql(closureTable, idType);

		// 公共闭包表需要biz_table条件，独立闭包表不需要
		Object[] params = meta.isUseIndependent() ?
				new Object[]{nodeId} :
				new Object[]{nodeId, meta.getBizTable()};

		return jdbcTemplate.query(querySql, (rs, rowNum) -> (T)rs.getObject("descendant_id"), params);
	}
}