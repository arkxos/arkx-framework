package io.arkx.data.jdbc.repository.query.test.tree;

/**
 * @author Nobody
 * @date 2025-07-28 2:42
 * @since 1.0
 */

import io.arkx.data.common.treetable.closure.service.SmartClosureTableServiceImpl;
import io.arkx.data.common.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.common.treetable.closure.repository.BusinessTableMetaJdbcRepository;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
public class SmartClosureTableServiceTest {

	private JdbcTemplate jdbcTemplate;
	private BusinessTableMetaJdbcRepository businessTableMetaJdbcRepository;
	private SmartClosureTableServiceImpl closureService;


	public void createDept(Dept dept) {
		// 1. 业务表插入（由业务侧完成）
		String sql = "INSERT INTO dept (id, parent_id, name, is_leaf, sort_order) VALUES (?, ?, ?, ?, ?)";
		jdbcTemplate.update(sql,
				dept.getId(),
				dept.getParentId(),
				dept.getName(),
				dept.getIsLeaf(),
				dept.getSortOrder());

		// 2. 闭包表关系插入（由闭包服务完成）
		BusinessTableMeta meta = businessTableMetaJdbcRepository.findByBusinessTable("dept")
				.orElseThrow(() -> new RuntimeException("dept元数据未配置"));
		closureService.insertClosureRelations(
				dept.getId(),          // nodeId
				dept.getParentId(),    // parentId
				"dept",                // businessTable
				meta.getIdType()       // idType
		);
	}

	public List<Dept> getDescendants(Long deptId) {
		// 1. 获取dept元数据
		BusinessTableMeta meta = businessTableMetaJdbcRepository.findByBusinessTable("dept")
				.orElseThrow(() -> new RuntimeException("dept元数据未配置"));

		// 2. 查询后代ID列表（闭包服务）
		List<Long> descendantIds = closureService.queryDescendantIds(
				deptId,                // nodeId
				"dept",                // businessTable
				meta.getIdType()       // idType
		);

		// 3. 查询业务表节点（业务侧）
		String sql = "SELECT * FROM dept WHERE id IN (" + String.join(", ", Collections.nCopies(descendantIds.size(), "?")) + ")";
		return jdbcTemplate.query(
				sql,
				(rs, rowNum) -> {
					Dept dept = new Dept();
					dept.setId(rs.getLong("id"));
					dept.setParentId(rs.getLong("parent_id"));
					dept.setIsLeaf(rs.getInt("is_leaf"));
					dept.setSortOrder(rs.getLong("sort_order"));
					dept.setName(rs.getString("name"));
					return dept;
				},
				descendantIds.toArray()
		);
	}
}

