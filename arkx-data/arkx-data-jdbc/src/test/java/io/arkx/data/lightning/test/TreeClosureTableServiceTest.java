package io.arkx.data.lightning.test;

/**
 * @author Nobody
 * @date 2025-07-28 2:42
 * @since 1.0
 */

import io.arkx.data.lightning.plugin.treetable.closure.entity.BusinessTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.service.SmartClosureTableServiceImpl;
import io.arkx.data.lightning.config.EntityAutoConfiguration;
import io.arkx.data.lightning.repository.BusinessTableMetaJdbcRepository;
import io.arkx.data.lightning.repository.support.SqlToyJdbcRepositoryFactoryBean;
import io.arkx.data.lightning.sample.model.Dept;
import io.arkx.data.lightning.sample.repository.DeptRepository;
import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.configure.SqltoyAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@Import({SqltoyAutoConfiguration.class, EntityAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = { "io.arkx", "org.sagacity.sqltoy" })
@EnableJdbcRepositories(
		repositoryFactoryBeanClass = SqlToyJdbcRepositoryFactoryBean.class,
		basePackages={"io.arkx.data.lightning.sample.repository"})
@SpringBootTest // 仅加载 JDBC 相关 Bean
@ActiveProfiles("test") // 使用测试配置（可选）
public class TreeClosureTableServiceTest {

	@Autowired
	private JdbcTemplate jdbcTemplate;
	@Autowired
	private BusinessTableMetaJdbcRepository businessTableMetaJdbcRepository;
	@Autowired
	private SmartClosureTableServiceImpl closureService;
	@Autowired
	private DeptRepository deptRepository;

	@Test
	public void createDept() {
		// 1. 业务表插入（由业务侧完成）
		Dept dept = new Dept();
		dept.setName("总公司");
		deptRepository.insert(dept);

		Dept childDept1 = new Dept();
		childDept1.setName("研发部门");
		childDept1.setParentId(dept.getId());
		deptRepository.save(childDept1);

		Dept childDept2 = new Dept();
		childDept2.setName("测试部门");
		childDept2.setParentId(dept.getId());
		deptRepository.insert(childDept2);

		Optional<Dept> found = deptRepository.findById(childDept1.getId());
		assertThat(found).isPresent();
		assertThat(found.get().getName()).isEqualTo("研发部门");
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

