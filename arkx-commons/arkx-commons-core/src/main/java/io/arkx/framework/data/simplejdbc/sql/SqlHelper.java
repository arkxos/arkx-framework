package io.arkx.framework.data.simplejdbc.sql;

import io.arkx.framework.data.simplejdbc.connection.ConnectionManager;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


/**
 * 数据库操作帮助类
 * 
 * @author Darkness
 * @website www.rapidark.com
 * @date 2013-4-12 下午02:50:51
 * @version V1.0
 */
public class SqlHelper {

	/**
	 * 执行查询
	 * 
	 * @author Darkness
	 * @date 2013-4-14 上午10:50:40 
	 * @version V1.0
	 * @return 
	 */
	public static Object executeQuery(String sql, IExecuteQueryCallback executeQueryCallback) {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			
			// statement用来执行SQL语句
			PreparedStatement statement = conn.prepareStatement(sql);

			System.out.println("sql:" + sql);
			// 结果集
			rs = statement.executeQuery();

			return executeQueryCallback.execute(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ConnectionManager.closeConnection(conn);
		}
		
		return null;
	}

	/**
	 * 分页查询
	 * 
	 * @author Darkness
	 * @date 2013-4-15 下午03:56:33 
	 * @version V1.0
	 */
	public static Object executeQuery(String sql, int start, int limit, IExecuteQueryCallback executeQueryCallback) {
		Connection conn = null;
		ResultSet rs = null;
		try {
			conn = ConnectionManager.getConnection();
			
			PreparedStatement statement = PagedService.createPagedStatement(conn, sql, start, limit);

			// 结果集
			rs = statement.executeQuery();

			return executeQueryCallback.execute(rs);
		} catch (SQLException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
			ConnectionManager.closeConnection(conn);
		}
		
		return null;
	}

}
