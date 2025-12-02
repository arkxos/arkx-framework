package io.arkx.framework.data.simplejdbc.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * 分页处理器接口
 *
 * @author Darkness
 * @date 2013-4-15 下午04:13:33
 * @version V1.0
 */
public interface IPagedHandler {

	/**
	 * 支持的数据库类型
	 *
	 * @author Darkness
	 * @date 2013-4-15 下午04:33:21
	 * @version V1.0
	 */
	String getSupportDatabaseType();

	/**
	 * 创建分页查询statement
	 *
	 * @author Darkness
	 * @date 2013-4-15 下午04:43:55
	 * @version V1.0
	 */
	PreparedStatement createPagedStatement(Connection conn, String sql, int start, int limit) throws SQLException;

}
