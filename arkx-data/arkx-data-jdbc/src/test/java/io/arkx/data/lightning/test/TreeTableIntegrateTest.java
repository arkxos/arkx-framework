package io.arkx.data.lightning.test;

/**
 * @author Nobody
 * @date 2025-07-28 2:42
 * @since 1.0
 */

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.configure.SqltoyAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import io.arkx.data.lightning.config.EntityAutoConfiguration;
import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.data.lightning.plugin.treetable.closure.service.ClosureTableServiceImpl;
import io.arkx.data.lightning.repository.support.SqlToyJdbcRepositoryFactoryBean;
import io.arkx.data.lightning.sample.model.Dept;
import io.arkx.data.lightning.sample.repository.DeptRepository;
import io.arkx.framework.commons.collection.tree.Treex;
import io.arkx.framework.data.common.entity.IdType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;

@Import({SqltoyAutoConfiguration.class, EntityAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = {"io.arkx", "org.sagacity.sqltoy"})
@EnableJdbcRepositories(repositoryFactoryBeanClass = SqlToyJdbcRepositoryFactoryBean.class,
        basePackages = {"io.arkx.data.lightning.sample.repository"})
@SpringBootTest // 仅加载 JDBC 相关 Bean
@ActiveProfiles("test") // 使用测试配置（可选）
public class TreeTableIntegrateTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ClosureTableServiceImpl closureService;

    @Autowired
    private DeptRepository deptRepository;

    private String testDeptRootId;

    @BeforeEach
    public void before() {
        deptRepository.executeSql("delete from test_dept");
        deptRepository.executeSql("delete from ark_tree_closure_long");
        deptRepository.executeSql("delete from ark_tree_closure_string");
    }

    @Rollback(false)
    @Transactional
    @Test
    public void createDept() {
        // 1. 业务表插入（由业务侧完成）
        Dept dept = new Dept();
        dept.setName("总公司");
        deptRepository.insert(dept);
        testDeptRootId = dept.getId();

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

    @Test
    public void testTreeCrudOperations() {
        // 1. 创建根节点
        Dept root = createAndVerifyDept("根部门", null);

        // 2. 创建子节点
        Dept child1 = createAndVerifyDept("子部门1", root.getId());
        Dept child2 = createAndVerifyDept("子部门2", root.getId());

        // 3. 验证后代查询
        assertDescendants(root.getId(), 2, "子部门1", "子部门2");

        // 4. 更新节点（移动子部门1到子部门2下）
        updateParentAndVerify(child1, child2.getId());

        // 5. 验证更新后的后代关系
        assertDescendants(root.getId(), 2, "子部门1", "子部门2");
        assertDescendants(child2.getId(), 1, "子部门1");

        // 6. 删除节点
        deleteAndVerify(child1);

        // 7. 验证删除后的关系
        assertDescendants(root.getId(), 1, "子部门2");
    }

    @Test
    public void testMultiLevelTree() {
        // 1. 创建三级树结构
        Dept root = createAndVerifyDept("总公司", null);
        Dept branch = createAndVerifyDept("分公司", root.getId());
        Dept team = createAndVerifyDept("研发组", branch.getId());

        // 2. 验证多级后代
        assertDescendants(root.getId(), 2, "分公司", "研发组");
        assertDescendants(branch.getId(), 1, "研发组");

        // 3. 移动团队到根节点
        updateParentAndVerify(team, root.getId());

        // 4. 验证移动后的关系
        assertDescendants(root.getId(), 2, "分公司", "研发组");
        assertDescendants(branch.getId(), 0);
    }

    @Test
    public void testDeleteWithChildren() {
        // 1. 创建父子节点
        Dept parent = createAndVerifyDept("父部门", null);
        Dept child = createAndVerifyDept("子部门", parent.getId());

        // 2. 删除父节点（应级联删除闭包关系）
        deleteAndVerify(parent);

        // 3. 验证子节点闭包关系已清理
        assertThat(deptRepository.findById(child.getId())).isPresent();
        // assertThat(closureService.queryDescendantIds(parent.getId(), "dept",
        // IdType.LONG)).isEmpty();
    }

    private Dept createAndVerifyDept(String name, String parentId) {
        Dept dept = new Dept();
        dept.setName(name);
        dept.setParentId(parentId);
        deptRepository.insert(dept);
        assertThat(dept.getId()).isNotNull();
        return dept;
    }

    private void updateParentAndVerify(Dept dept, String newParentId) {
        dept.setParentId(newParentId);
        deptRepository.update(dept);
        assertThat(deptRepository.findById(dept.getId())).get().extracting(Dept::getParentId).isEqualTo(newParentId);
    }

    private void deleteAndVerify(Dept dept) {
        deptRepository.delete(dept);
        assertThat(deptRepository.findById(dept.getId())).isEmpty();
    }

    private void assertDescendants(String deptId, int expectedSize, String... expectedNames) {
        List<Dept> descendants = getDescendants(deptId);
        assertThat(descendants).hasSize(expectedSize);
        if (expectedNames.length > 0) {
            assertThat(descendants).extracting(Dept::getName).containsExactlyInAnyOrder(expectedNames);
        }
    }

    public List<Dept> getDescendants(String deptId) {
        // 1. 获取dept元数据
        // BizTableMeta meta =
        // businessTableMetaJdbcRepository.findByBusinessTable("dept")
        // .orElseThrow(() -> new RuntimeException("dept元数据未配置"));
        BizTableMeta meta = new BizTableMeta();
        meta.setBizTable("TEST_DEPT");
        meta.setIdType(IdType.LONG);
        // 2. 查询后代ID列表（闭包服务）
        List<String> descendantIds = closureService.queryDescendantIds(deptId, // nodeId
                meta);

        // 3. 查询业务表节点（业务侧）
        String sql = "SELECT * FROM TEST_DEPT WHERE id IN ("
                + String.join(", ", Collections.nCopies(descendantIds.size(), "?")) + ")";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Dept dept = new Dept();
            dept.setId(rs.getString("id"));
            dept.setParentId(rs.getString("parent_id"));
            dept.setIsLeaf(rs.getInt("is_leaf"));
            dept.setSortOrder(rs.getInt("sort_order"));
            dept.setName(rs.getString("name"));
            return dept;
        }, descendantIds.toArray());
    }

    @Rollback(false)
    @Transactional(propagation = Propagation.REQUIRED)
    @Test
    public void testQueryTree() throws JsonProcessingException {
        createDept();

        dumpTreeByParentId(null);

        dumpTreeByParentId(testDeptRootId);
    }

    private void dumpTreeByParentId(String parentId) throws JsonProcessingException {
        Treex<String, Dept> treex = deptRepository.queryTreeByParentId(parentId);
        treex.setWarpTreeNode(false);
        System.out.println("======================");
        System.out.println(treex);
        System.out.println("-------------------");
        // 只序列化子节点
        // treex.setWarpTreeNode(false);
        ObjectMapper objectMapper = new ObjectMapper();
        // 启用美化输出（缩进）
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

        JavaTimeModule javaTimeModule = new JavaTimeModule();
        javaTimeModule.addSerializer(LocalDateTime.class,
                new LocalDateTimeSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        javaTimeModule.addSerializer(LocalDate.class,
                new LocalDateSerializer(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        objectMapper.registerModule(javaTimeModule);

        String childrenJson = objectMapper.writeValueAsString(treex);
        System.out.println(childrenJson);
    }

}
