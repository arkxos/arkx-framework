package com.rapidark.framework.data.simplejdbc.sql;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

/**   
 * 分页服务
 * @author Darkness
 * @website www.rapidark.com
 * @date 2013-4-15 下午04:21:13 
 * @version V1.0   
 */
public class PagedService {

	// 分页处理器集合
	private static Map<String, IPagedHandler> pagedHandlers = new HashMap<String, IPagedHandler>();

	static {
		// 注入系统支持的分页处理器
		register(new MySqlPagedHandler());
	}
	
	/**
	 * 注册分页处理器
	 * 
	 * @author Darkness
	 * @date 2013-4-15 下午04:45:04 
	 * @version V1.0
	 */
	public static void register(IPagedHandler pagedHandler) {
		pagedHandlers.put(pagedHandler.getSupportDatabaseType(), pagedHandler);
	}
	
	/**
	 * 创建分页查询statement
	 * 
	 * @author Darkness
	 * @date 2013-4-15 下午04:23:49 
	 * @version V1.0
	 */
	public static PreparedStatement createPagedStatement(Connection conn, String sql, int start, int limit) throws SQLException {
		return findPagedHandler(conn).createPagedStatement(conn, sql, start, limit);
	}

	/**
	 * 根据连接类型获取匹配的分页处理器
	 * 
	 * @author Darkness
	 * @date 2013-4-15 下午04:39:51 
	 * @version V1.0
	 * @throws SQLException 
	 */
	private static IPagedHandler findPagedHandler(Connection conn) throws SQLException {
		DatabaseMetaData dbmd = conn.getMetaData();
		// 获取连接对应的数据库类型
		String dataBaseType = dbmd.getDatabaseProductName();
		for (String key : pagedHandlers.keySet()) {
			if(key.equalsIgnoreCase(dataBaseType)) {
				return pagedHandlers.get(key);
			}
		}
		throw new RuntimeException("未找到"+dataBaseType+"数据库对应的分页处理器");
	}
}
