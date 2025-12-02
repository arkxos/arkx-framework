package io.arkx.framework.data.mybatis.pro.sample.springboot.mapper;

import static com.ninja_squad.dbsetup.Operations.insertInto;
import static com.ninja_squad.dbsetup.Operations.truncate;
import static org.junit.jupiter.api.Assertions.assertEquals;

import javax.sql.DataSource;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.arkx.framework.data.mybatis.pro.sample.springboot.domain.User;

import com.ninja_squad.dbsetup.DbSetup;
import com.ninja_squad.dbsetup.destination.DataSourceDestination;
import com.ninja_squad.dbsetup.operation.Insert;

import jakarta.annotation.Resource;

/**
 * 描述：
 *
 * @author w.dehai.2023/6/8.11:31
 */
@SpringBootTest
class UserMapperTest {

	@Resource
	private DataSource dataSource;

	@Resource
	private UserMapper userMapper;

	@BeforeEach
	void init() {

		// init smat_user
		new DbSetup(new DataSourceDestination(dataSource), truncate("smart_user")).launch();
		Insert insert = insertInto("smart_user").columns("name", "password", "phone_no", "version", "addr_info")
			.values("w.dehai", "123456", "1306006", 1L, "成都")
			.build();
		new DbSetup(new DataSourceDestination(dataSource), insert).launch();
	}

	@Test
	void testTrim() {
		User user = userMapper.testTrim(null, "123456");
		assertEquals("w.dehai", user.getName());
	}

	@Test
	void testWhere() {
		User user = userMapper.testWhere(null, "123456");
		assertEquals("w.dehai", user.getName());
	}

}
