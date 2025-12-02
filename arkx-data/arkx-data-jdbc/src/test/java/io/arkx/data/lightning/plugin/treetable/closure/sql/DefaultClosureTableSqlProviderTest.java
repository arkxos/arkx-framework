package io.arkx.data.lightning.plugin.treetable.closure.sql;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import io.arkx.framework.data.common.entity.IdType;

@SpringBootTest
public class DefaultClosureTableSqlProviderTest {

	@Autowired
	private ClosureTableSqlProvider sqlProvider;

	@Test
	public void testRebuildCommonClosureSql() {
		String sql = sqlProvider.rebuildCommonClosureSql("t_closure", "t_dept", IdType.LONG);
		assertTrue(sql.contains("TRUNCATE TABLE t_closure"));
		assertTrue(sql.contains("INSERT INTO t_closure"));
	}

	@Test
	public void testInsertCommonClosureSql() {
		String sql = sqlProvider.insertCommonClosureSql("t_closure", IdType.LONG);
		assertTrue(sql.contains("INSERT INTO t_closure"));
	}

}
