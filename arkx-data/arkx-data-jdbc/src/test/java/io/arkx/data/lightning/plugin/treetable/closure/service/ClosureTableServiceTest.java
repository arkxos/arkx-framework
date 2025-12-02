package io.arkx.data.lightning.plugin.treetable.closure.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.sagacity.sqltoy.configure.SqltoyAutoConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.jdbc.repository.config.EnableJdbcRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;

import io.arkx.data.lightning.config.EntityAutoConfiguration;
import io.arkx.data.lightning.plugin.treetable.closure.entity.BizTableMeta;
import io.arkx.data.lightning.repository.support.SqlToyJdbcRepositoryFactoryBean;
import io.arkx.framework.data.common.entity.IdType;

/**
 * 闭包表功能集成测试类
 *
 * 使用H2内存数据库进行真实场景测试，全面验证闭包表服务的核心功能： 1. 公共闭包表和独立闭包表的CRUD操作 2. 多层级关系维护 3.
 * 节点移动和子树删除操作 4. 后代查询功能
 *
 * 测试覆盖以下关键场景： - 根节点插入（无父节点） - 子节点插入（直接父子关系） - 多层级节点插入（祖先关系） - 节点移动（更新关系） -
 * 子树删除（级联删除） - 多种ID类型支持（Long/String）
 */
@Import({SqltoyAutoConfiguration.class, EntityAutoConfiguration.class})
@SpringBootApplication(scanBasePackages = {"io.arkx", "org.sagacity.sqltoy"})
@EnableJdbcRepositories(repositoryFactoryBeanClass = SqlToyJdbcRepositoryFactoryBean.class,
        basePackages = {"io.arkx.data.lightning.sample.repository"})
@SpringBootTest // 仅加载 JDBC 相关 Bean
@ActiveProfiles("test") // 使用测试配置（可选）
public class ClosureTableServiceTest {

    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Autowired
    private ClosureTableService closureTableService;

    private final BizTableMeta commonMeta = new BizTableMeta();
    private final BizTableMeta independentMeta = new BizTableMeta();

    @BeforeAll
    static void setupDatabase() {
    }

    @AfterAll
    static void tearDown() {
    }

    @BeforeEach
    void setupTestData() {
        // 清空表数据
        jdbcTemplate.execute("TRUNCATE TABLE ark_tree_closure_long");

        // 初始化业务表元数据
        commonMeta.setBizTable("TEST_DEPT");
        commonMeta.setUseIndependent(false);
        commonMeta.setIdType(IdType.LONG);

        independentMeta.setBizTable("TEST_MENU");
        independentMeta.setUseIndependent(false);
        independentMeta.setIdType(IdType.STRING);

        // 插入基础数据
        insertDeptData();
        insertMenuData();
    }

    /**
     * 测试公共闭包表根节点插入
     *
     * 验证点： 1. 根节点插入时生成正确的自引用关系 2. 关系深度(depth)为0 3. 公共表特有字段biz_table正确填充
     */
    @Test
    void testInsertRootNode_CommonTable() {
        // 插入根节点
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);

        // 验证闭包关系
        List<Map<String, Object>> relations = jdbcTemplate
                .queryForList("SELECT * FROM ark_tree_closure_long WHERE biz_table='TEST_DEPT' and descendant_id = 1");

        assertEquals(1, relations.size(), "根节点应有1条关系记录");
        Map<String, Object> relation = relations.get(0);
        assertEquals(1L, relation.get("ancestor_id"), "祖先ID应为节点自身");
        assertEquals(1L, relation.get("descendant_id"), "后代ID应为节点自身");
        assertEquals(0, relation.get("depth"), "自引用关系深度应为0");
        assertEquals("TEST_DEPT", relation.get("biz_table"), "业务表名应为dept");
    }

    /**
     * 测试公共闭包表子节点插入
     *
     * 验证点： 1. 子节点生成自引用关系 2. 建立与父节点的直接关系(depth=1) 3. 建立与祖先节点的间接关系(depth>1) 4.
     * 所有关系深度计算正确
     */
    @Test
    void testInsertChildNode_CommonTable() {
        // 先插入父节点
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);
        // 验证闭包关系
        List<Map<String, Object>> relations = jdbcTemplate.queryForList("SELECT * FROM ark_tree_closure_long "
                + "WHERE biz_table='TEST_DEPT' " + "ORDER BY depth ASC, ancestor_id ASC, descendant_id ASC");
        // 验证关系数量
        Map<String, Object> selfRelation1 = relations.get(0);
        assertEquals(1L, selfRelation1.get("ancestor_id"), "节点1自引用祖先ID应为1");
        assertEquals(1L, selfRelation1.get("descendant_id"), "节点1自引用后代ID应为1");
        assertEquals(0, selfRelation1.get("depth"), "节点1自引用深度应为0");

        // 插入子节点
        closureTableService.insertClosureRelations(2L, 1L, commonMeta, IdType.LONG);
        // 验证闭包关系
        relations = jdbcTemplate.queryForList("SELECT * FROM ark_tree_closure_long " + "WHERE biz_table='TEST_DEPT' "
                + "ORDER BY depth ASC, ancestor_id ASC, descendant_id ASC");
        // 验证关系数量
        assertEquals(3, relations.size(), "应有3条关系记录");

        // 验证所有关系 - 现在顺序是确定的
        // 第一条：节点1的自引用
        Map<String, Object> relation1 = relations.get(0);
        assertEquals(1L, relation1.get("ancestor_id"), "第一条记录的祖先ID应为1");
        assertEquals(1L, relation1.get("descendant_id"), "第一条记录的后代ID应为1");
        assertEquals(0, relation1.get("depth"), "第一条记录的深度应为0");

        // 第二条：节点2的自引用
        Map<String, Object> relation2 = relations.get(1);
        assertEquals(2L, relation2.get("ancestor_id"), "第二条记录的祖先ID应为2");
        assertEquals(2L, relation2.get("descendant_id"), "第二条记录的后代ID应为2");
        assertEquals(0, relation2.get("depth"), "第二条记录的深度应为0");

        // 第三条：节点1到节点2的直接关系
        Map<String, Object> relation3 = relations.get(2);
        assertEquals(1L, relation3.get("ancestor_id"), "第三条记录的祖先ID应为1");
        assertEquals(2L, relation3.get("descendant_id"), "第三条记录的后代ID应为2");
        assertEquals(1, relation3.get("depth"), "第三条记录的深度应为1");
    }

    /**
     * 测试公共闭包表多层级节点插入
     *
     * 验证点： 1. 三层级关系正确建立（祖父-父-子） 2. 节点生成完整祖先链关系 3. 深度值正确递增（0,1,2） 4. 所有祖先-后代关系完整
     */
    @Test
    void testInsertMultiLevelNode_CommonTable() {
        // 插入祖父节点
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);

        // 插入父节点
        closureTableService.insertClosureRelations(2L, 1L, commonMeta, IdType.LONG);

        // 插入子节点
        closureTableService.insertClosureRelations(3L, 2L, commonMeta, IdType.LONG);

        // 验证闭包关系
        List<Map<String, Object>> relations = jdbcTemplate.queryForList(
                "SELECT * FROM ark_tree_closure_long " + "WHERE biz_table='TEST_DEPT' and descendant_id = 3 "
                        + "ORDER BY depth ASC, ancestor_id ASC, descendant_id ASC");

        // 验证关系数量 (节点3应该有3个关系)
        assertEquals(3, relations.size(), "节点3应有3条关系记录");

        // 验证关系内容
        assertTrue(relations.stream().anyMatch(
                r -> r.get("ancestor_id").equals(3L) && r.get("descendant_id").equals(3L) && r.get("depth").equals(0)),
                "缺少节点3的自引用关系");

        assertTrue(relations.stream().anyMatch(
                r -> r.get("ancestor_id").equals(2L) && r.get("descendant_id").equals(3L) && r.get("depth").equals(1)),
                "缺少节点2->3的直接关系");

        assertTrue(relations.stream().anyMatch(
                r -> r.get("ancestor_id").equals(1L) && r.get("descendant_id").equals(3L) && r.get("depth").equals(2)),
                "缺少节点1->3的间接关系");
    }

    /**
     * 测试公共闭包表后代查询
     *
     * 验证点： 1. 查询结果包含所有后代节点 2. 结果包含查询节点自身 3. 正确返回多层级结构中的全部后代 4. 结果顺序无关，但内容完整
     */
    @Test
    void testQueryDescendants_CommonTable() {
        // 插入测试数据
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(2L, 1L, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(3L, 1L, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(4L, 2L, commonMeta, IdType.LONG);

        // 查询后代
        List<Long> descendants = closureTableService.queryDescendantIds(1L, commonMeta);

        // 验证结果
        assertEquals(4, descendants.size(), "应有4个后代节点");
        assertTrue(descendants.contains(1L), "结果应包含节点自身");
        assertTrue(descendants.contains(2L), "结果应包含直接子节点");
        assertTrue(descendants.contains(3L), "结果应包含直接子节点");
        assertTrue(descendants.contains(4L), "结果应包含孙子节点");
    }

    /**
     * 测试公共闭包表节点移动
     *
     * 验证点： 1. 节点成功移动到新的父节点下 2. 旧关系完全删除 3. 新关系正确建立 4. 深度值重新计算 5. 祖先链关系更新
     */
    @Test
    void testUpdateClosureRelations() {
        // 初始结构: 1 -> 2 -> 3
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(2L, 1L, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(3L, 2L, commonMeta, IdType.LONG);

        // 验证初始关系总数
        List<Map<String, Object>> allRelations = jdbcTemplate
                .queryForList("SELECT * FROM ark_tree_closure_long WHERE biz_table='TEST_DEPT'");
        assertEquals(6, allRelations.size(), "初始应有6条关系记录");

        // 移动节点3: 从2的子节点变为1的直接子节点
        closureTableService.updateClosureRelations(3L, 1L, commonMeta);

        // 验证整个闭包表的关系
        allRelations = jdbcTemplate.queryForList("SELECT * FROM ark_tree_closure_long " + "WHERE biz_table='TEST_DEPT' "
                + "ORDER BY depth ASC, ancestor_id ASC, descendant_id ASC");

        // 验证关系总数
        assertEquals(5, allRelations.size(), "移动后应有5条关系记录");

        // 使用辅助方法验证关系
        // 验证应存在的关系
        assertRelationExists(allRelations, 1L, 1L, 0);
        assertRelationExists(allRelations, 2L, 2L, 0);
        assertRelationExists(allRelations, 3L, 3L, 0);
        assertRelationExists(allRelations, 1L, 2L, 1);
        assertRelationExists(allRelations, 1L, 3L, 1);

        // 验证应不存在的关系
        assertRelationNotExists(allRelations, 2L, 3L);
        assertRelationNotExists(allRelations, 1L, 3L, 2); // 深度>1的关系
    }

    // 辅助方法：验证关系存在且深度匹配
    private void assertRelationExists(List<Map<String, Object>> relations, long expectedAncestor,
            long expectedDescendant, int expectedDepth) {
        for (Map<String, Object> relation : relations) {
            long ancestor = (long) relation.get("ancestor_id");
            long descendant = (long) relation.get("descendant_id");
            int depth = (int) relation.get("depth");

            if (ancestor == expectedAncestor && descendant == expectedDescendant && depth == expectedDepth) {
                return;
            }
        }
        fail(String.format("未找到关系: (%d->%d, depth=%d)", expectedAncestor, expectedDescendant, expectedDepth));
    }

    // 辅助方法：验证关系不存在（任意深度）
    private void assertRelationNotExists(List<Map<String, Object>> relations, long expectedAncestor,
            long expectedDescendant) {
        for (Map<String, Object> relation : relations) {
            long ancestor = (long) relation.get("ancestor_id");
            long descendant = (long) relation.get("descendant_id");

            if (ancestor == expectedAncestor && descendant == expectedDescendant) {
                fail(String.format("不应存在关系: (%d->%d)", expectedAncestor, expectedDescendant));
            }
        }
    }

    // 辅助方法：验证关系不存在（指定深度）
    private void assertRelationNotExists(List<Map<String, Object>> relations, long expectedAncestor,
            long expectedDescendant, int expectedDepth) {
        for (Map<String, Object> relation : relations) {
            long ancestor = (long) relation.get("ancestor_id");
            long descendant = (long) relation.get("descendant_id");
            int depth = (int) relation.get("depth");

            if (ancestor == expectedAncestor && descendant == expectedDescendant && depth == expectedDepth) {
                fail(String.format("不应存在关系: (%d->%d, depth=%d)", expectedAncestor, expectedDescendant, expectedDepth));
            }
        }
    }

    /**
     * 测试公共闭包表子树删除
     *
     * 验证点： 1. 目标节点及其后代关系完全删除 2. 祖先节点关系不受影响 3. 级联删除正确执行 4. 业务数据不受影响（仅闭包关系）
     */
    @Test
    void testDeleteClosureRelations() {
        // 创建测试数据: 1 -> 2 -> 3
        closureTableService.insertClosureRelations(1L, null, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(2L, 1L, commonMeta, IdType.LONG);
        closureTableService.insertClosureRelations(3L, 2L, commonMeta, IdType.LONG);

        // 验证初始关系总数
        List<Map<String, Object>> allRelations = jdbcTemplate
                .queryForList("SELECT * FROM ark_tree_closure_long WHERE biz_table='TEST_DEPT'");
        assertEquals(6, allRelations.size(), "初始应有6条关系记录");

        // 删除节点2及其后代
        closureTableService.deleteClosureRelations(2L, commonMeta);

        // 验证整个闭包表的关系
        allRelations = jdbcTemplate.queryForList("SELECT * FROM ark_tree_closure_long " + "WHERE biz_table='TEST_DEPT' "
                + "ORDER BY depth ASC, ancestor_id ASC, descendant_id ASC");

        // 验证关系总数
        assertEquals(1, allRelations.size(), "删除后应有1条关系记录");

        // 验证所有关系
        assertRelationExists(allRelations, 1L, 1L, 0);

        // 验证应不存在的关系
        assertRelationNotExists(allRelations, 2L, 2L); // 节点2自引用
        assertRelationNotExists(allRelations, 3L, 3L); // 节点3自引用
        assertRelationNotExists(allRelations, 1L, 2L); // 1->2
        assertRelationNotExists(allRelations, 2L, 3L); // 2->3
        assertRelationNotExists(allRelations, 1L, 3L); // 1->3
    }

    /**
     * 测试独立闭包表全功能
     *
     * 验证点： 1. 字符串ID支持 2. 独立表结构操作 3. 多层级关系建立 4. 后代查询返回正确结果 5. 节点移动功能正常 6. 子树删除功能正常
     */
    @Test
    void testIndependentClosureTableOperations() {
        // 插入测试数据
        closureTableService.insertClosureRelations("root", null, independentMeta, IdType.STRING);
        closureTableService.insertClosureRelations("child1", "root", independentMeta, IdType.STRING);
        closureTableService.insertClosureRelations("child2", "root", independentMeta, IdType.STRING);
        closureTableService.insertClosureRelations("grandchild", "child1", independentMeta, IdType.STRING);

        // 查询后代
        List<String> descendants = closureTableService.queryDescendantIds("root", independentMeta);

        // 验证结果
        assertEquals(4, descendants.size(), "应有4个后代节点");
        assertTrue(descendants.contains("root"), "结果应包含节点自身");
        assertTrue(descendants.contains("child1"), "结果应包含直接子节点");
        assertTrue(descendants.contains("child2"), "结果应包含直接子节点");
        assertTrue(descendants.contains("grandchild"), "结果应包含孙子节点");

        // 更新关系
        // closureTableService.updateClosureRelations("grandchild", "child2",
        // independentMeta);

        // 验证新关系
        List<Map<String, Object>> relations = jdbcTemplate
                .queryForList("SELECT * FROM menu_closure WHERE descendant_id = 'grandchild'");

        // 验证关系内容
        assertTrue(
                relations.stream().anyMatch(
                        r -> r.get("ancestor_id").equals("child2") && r.get("descendant_id").equals("grandchild")),
                "缺少child2->grandchild的关系");

        // 删除节点
        // closureTableService.deleteClosureRelations("child1", independentMeta);

        // 验证删除
        List<Map<String, Object>> deletedRelations = jdbcTemplate
                .queryForList("SELECT * FROM menu_closure WHERE descendant_id = 'child1' OR ancestor_id = 'child1'");
        assertTrue(deletedRelations.isEmpty(), "child1的所有关系应被删除");
    }

    // ============== 辅助方法 ==============

    /**
     * 初始化部门业务表测试数据
     *
     * 插入四层级的部门结构： - 1: 总公司 |- 2: 研发部 | |- 4: 前端组 |- 3: 市场部
     */
    private static void insertDeptData() {
        // jdbcTemplate.update("INSERT INTO dept (id, name) VALUES (1, '总公司')");
        // jdbcTemplate.update("INSERT INTO dept (id, name, parent_id) VALUES (2, '研发部',
        // 1)");
        // jdbcTemplate.update("INSERT INTO dept (id, name, parent_id) VALUES (3, '市场部',
        // 1)");
        // jdbcTemplate.update("INSERT INTO dept (id, name, parent_id) VALUES (4, '前端组',
        // 2)");
    }

    /**
     * 初始化菜单业务表测试数据
     *
     * 插入两层级的菜单结构： - root: 主菜单 |- file: 文件 | |- new: 新建 |- edit: 编辑
     */
    private static void insertMenuData() {
        // jdbcTemplate.update("INSERT INTO menu (id, name) VALUES ('root', '主菜单')");
        // jdbcTemplate.update("INSERT INTO menu (id, name, parent_id) VALUES ('file',
        // '文件', 'root')");
        // jdbcTemplate.update("INSERT INTO menu (id, name, parent_id) VALUES ('edit',
        // '编辑', 'root')");
        // jdbcTemplate.update("INSERT INTO menu (id, name, parent_id) VALUES ('new',
        // '新建', 'file')");
    }

}
