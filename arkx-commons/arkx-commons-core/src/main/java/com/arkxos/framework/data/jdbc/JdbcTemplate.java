package com.arkxos.framework.data.jdbc;

import java.io.UnsupportedEncodingException;
import java.sql.Blob;
import java.sql.CallableStatement;
import java.sql.Clob;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ark.framework.orm.sql.LobUtil;

import com.arkxos.framework.Config;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.Objects;
import io.arkx.framework.commons.util.StringFormat;
import io.arkx.framework.config.LogSQL;
import com.arkxos.framework.data.db.QueryException;
import com.arkxos.framework.data.db.connection.Connection;
import com.arkxos.framework.data.db.connection.ConnectionConfig;
import com.arkxos.framework.data.db.dbtype.DBTypeService;
import com.arkxos.framework.data.db.dbtype.IDBType;
import com.arkxos.framework.data.db.exception.AlterException;
import com.arkxos.framework.data.db.exception.CreateException;
import com.arkxos.framework.data.db.exception.DatabaseException;
import com.arkxos.framework.data.db.exception.DeleteException;
import com.arkxos.framework.data.db.exception.DropException;
import com.arkxos.framework.data.db.exception.InsertException;
import com.arkxos.framework.extend.ExtendManager;
import com.arkxos.framework.extend.action.AfterSQLExecutedAction;

import lombok.extern.slf4j.Slf4j;

/**
 * 数据库存取器，是对数据库连接的简单封装。
 * @author Darkness
 * @date 2011-12-13 下午06:56:00
 * @version V1.0
 */
@Slf4j
public class JdbcTemplate {
	
	/**
	 * 输出数据库操作相关日志
	 * 
	 * @param startTime 执行开始时间
	 * @param sql SQL语句
	 * @param params SQL变量
	 */
	public static void log(long startTime, String sql, List<Object> params) {
		String message = sql;
		long time = System.currentTimeMillis() - startTime;
		if (params != null && !sql.startsWith("Error:")) {
			Object[] arr = new String[params.size()];
			for (int i = 0; i < params.size(); i++) {
				Object obj = params.get(i);
				if (obj == null) {
					arr[i] = "null";
				} else if (obj instanceof String) {
					arr[i] = "'" + obj + "'";
				} else if (obj instanceof Date) {
					arr[i] = "'" + DateUtil.toDateTimeString((Date) obj) + "'";
				} else {
					arr[i] = obj.toString();
				}
			}
			message = StringFormat.format(sql, arr);
		}
		if (LogSQL.getValue()) {
			LogUtil.debug(time + "ms\t" + message);
		}
		// 扩展点,主要用于SQL日志分析
		ExtendManager.invoke(AfterSQLExecutedAction.ID, new Object[] { time, message });
	}
	
	public static String getPagedSQL(ConnectionConfig connectionConfig, String sql, int pageSize, int pageIndex) {
		IDBType dbType = DBTypeService.getInstance().get(connectionConfig.getDatabaseType());
		String pagedSql = dbType.getPagedSQL(sql, pageSize,pageIndex,  Connection.getConnID());

		return pagedSql;
	}
	
	/**
	 * 数据库连接
	 */
	public Connection conn;
	
	/**
	 * 使用默认连接池中的连接构造实例
	 */
//	public JdbcTemplate() {
//	}
	
	/**
	 * 使用指定的连接构造实例
	 */
	public JdbcTemplate(Connection conn) {
		this.conn = conn;
	}
	
	//[start] 连接相关操作
	
	/**
	 * @return 内部使用的JDBC连接
	 */
	public Connection getConnection() {
//		if (conn == null) {
//			conn = TransactionFactory.getCurrentTransaction().getConnection();
//		}
		return conn;
	}
	
	// [end]
	
	public static void setPreparedStatementParams(PreparedStatement stmt, ArrayList<Object> params, Connection conn) throws SQLException {
		if (params == null || params.isEmpty()) {
			return;
		}
		
		IDBType db = DBTypeService.getInstance().get(conn.getDBConfig().getDatabaseType());
		for (int i = 1; i <= params.size(); i++) {
			Object o = params.get(i - 1);
			if (o == null) {
				stmt.setNull(i, java.sql.Types.VARCHAR);
			} else if (o instanceof Byte) {
				stmt.setByte(i, ((Byte) o).byteValue());
			} else if (o instanceof Short) {
				stmt.setShort(i, ((Short) o).shortValue());
			} else if (o instanceof Integer) {
				stmt.setInt(i, ((Integer) o).intValue());
			} else if (o instanceof Long) {
				stmt.setLong(i, ((Long) o).longValue());
			} else if (o instanceof Float) {
				stmt.setFloat(i, ((Float) o).floatValue());
			} else if (o instanceof Double) {
				stmt.setDouble(i, ((Double) o).doubleValue());
			} else if (o instanceof Date) {
				stmt.setTimestamp(i, new Timestamp(((Date) o).getTime()));
			} else if (o instanceof String) {
				String str = (String) o;
				if (conn.getDBConfig().isLatin1Charset() && conn.getDBConfig().isOracle()) {// Oracle必须特别处理
					try {
						str = new String(str.getBytes(Config.getGlobalCharset()), "ISO-8859-1");
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
				}
				stmt.setString(i, str);
			} else if (o instanceof Clob) {
				db.setClob(conn, stmt, i, o);
			} else if (o instanceof byte[]) {
				db.setBlob(conn, stmt, i, (byte[]) o);
			} else {
				stmt.setObject(i, o);
			}
		}
	}
	
	protected PreparedStatement prepareStatement(String sql) throws SQLException {
		return getConnection().prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
	}
	
	protected PreparedStatement initStmt(String sql, ArrayList<Object> params) throws SQLException {
		PreparedStatement stmt = prepareStatement(sql);

		setPreparedStatementParams(stmt, params, this.conn);

		return stmt;
	}
	
	protected void dispose(ResultSet rs, PreparedStatement stmt) {
		try {
			if (rs != null) {
				rs.close();
				rs = null;
			}
			if (stmt != null) {
				stmt.close();
				stmt = null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			log.info("连接关闭失败！");
		}
	}
	
	public static void setBatchParams(PreparedStatement stmt, List<ArrayList<Object>> batches, Connection conn) throws SQLException {
		for (int k = 0; k < batches.size(); k++) {
			ArrayList<Object> params = batches.get(k);
			setPreparedStatementParams(stmt, params,conn);
			stmt.addBatch();
		}
	}
	
	private PreparedStatement initBatchStmt(String sql, List<ArrayList<Object>> batches) throws SQLException {
		PreparedStatement stmt = prepareStatement(sql);

		setBatchParams(stmt, batches, this.conn);

		return stmt;
	}
	
	public int executeUpdate(String sql) throws SQLException {
		return executeUpdate(sql, null);
	}
	
	/**
	 * 无返回值查询
	 * 
	 * @param q 查询器
	 * @return SQL影响到的记录条数
	 */
	public int executeUpdate(String sql, ArrayList<Object> params) throws SQLException {
		long current = System.currentTimeMillis();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = initStmt(sql, params);

			int r = stmt.executeUpdate();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());

			return r;
		} catch (SQLException e) {
			String lowerSQL = sql.trim().toLowerCase();
			if (lowerSQL.startsWith("delete ")) {
				throw new DeleteException(e);
			} else if (lowerSQL.startsWith("update ")) {
				throw new DeleteException(e);
			} else if (lowerSQL.startsWith("insert ")) {
				throw new InsertException(e);
			} else if (lowerSQL.startsWith("create ")) {
				throw new CreateException(e);
			} else if (lowerSQL.startsWith("drop ")) {
				throw new DropException(e);
			} else if (lowerSQL.startsWith("alter ")) {
				throw new AlterException(e);
			} else {
				throw new DatabaseException(e);
			}
		} finally {
			dispose(rs, stmt);
			if (Config.isDebugLoglevel()) {
				long time = System.currentTimeMillis() - current;
				log.info(time + "ms\t" + sql);
			}
		}
	}
	
	/**
	 * @tag category name = "SqlOperater" color = "Blue"
	 */
	public int[] executeBatch(String sql, List<ArrayList<Object>> params) throws SQLException {
		long current = System.currentTimeMillis();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = initBatchStmt(sql, params);

			int[] r = stmt.executeBatch();
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());

			return r;
		} catch (Exception e) {
//			System.out.println(JSON.toJSONString(params));
			throw e;
		} finally {
			dispose(rs, stmt);
			if (Config.isDebugLoglevel()) {
				long time = System.currentTimeMillis() - current;
				log.info(time + "ms\t" + sql);
			}
		}
	}
	
	public Object executeQuery(String sql, ArrayList<Object> params, ICallbackStatement statement) {
		long current = System.currentTimeMillis();

		PreparedStatement stmt = null;
		ResultSet rs = null;

		try {
			stmt = initStmt(sql, params);

			rs = stmt.executeQuery();
			
			conn.setLastSuccessExecuteTime(System.currentTimeMillis());
			JdbcTemplate.log(current, sql, params);
			return statement.execute(conn, stmt, rs);
		} catch (Throwable e) {
			e.printStackTrace();
			JdbcTemplate.log(System.currentTimeMillis(), "Error:" + e.getMessage(), null);
			if (e instanceof QueryException) {
				throw (QueryException) e;
			}
			System.out.println("error sql: " + sql);
			throw new QueryException(e);
		} finally {
			dispose(rs, stmt);
			if (Config.isDebugLoglevel()) {
				long time = System.currentTimeMillis() - current;
				log.info(time + "ms\t" + sql + ",{" + params + "}");
			}
			
			autoCloseReadOnlyConnection();
		}
	}
	
	public Object executeQuery(String sql, ICallbackStatement statement) {
		return executeQuery(sql, null, statement);
	}
	
	/**
	 * 执行查询，并返回第一条记录的第一个字段的值，如果没有记录，则返回null
	 * @method executeOneValue
	 * @return {Object}
	 */
	public Object executeOneValue(String sql, ArrayList<Object> params) {

		return executeQuery(sql, params, new ICallbackStatement() {

			public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
				Object object = null;
				if (rs.next()) {
					object = rs.getObject(1);
					if (object instanceof Clob) {
						object = LobUtil.clobToString((Clob) object);
					}
					if (object instanceof Blob) {
						object = LobUtil.blobToBytes((Blob) object);
					}
				}

				return object;
			}

		});
	}
	
	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为String，如果没有记录，则返回null
	 * @method executeString
	 * @return {String}
	 * 
	 * @author Darkness
	 * @date 2013-2-22 上午10:49:54 
	 * @version V1.0
	 */
	public String executeString(String sql) {
		return executeString(sql, (ArrayList<Object>)null);
	}
	
	public String executeString(String sql, Object[] params) {
		return this.executeString(sql, Objects.newArrayList(params));
	}
	
	public String executeString(String sql, ArrayList<Object> params) {
		Object o = this.executeOneValue(sql, params);
		if (o == null) {
			return null;
		}
		return o.toString();
	}

	/**
	 * 查询一个值
	 * @method executeInt
	 * @return {int}
	 * 
	 * @author Darkness
	 * @date 2013-2-22 上午10:50:20 
	 * @version V1.0
	 */
	public int executeInt(String sql) {
		return executeInt(sql, (ArrayList<Object>)null);
	}

	public int executeInt(String sql, Object[] params) {
		return this.executeInt(sql, Objects.newArrayList(params));
	}
	
	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为int
	 */
	public int executeInt(String sql, ArrayList<Object> params) {
		Object o = this.executeOneValue(sql, params);
		if (o == null) {
			return 0;
		}
		if (o instanceof Number) {
			return ((Number) o).intValue();
		} else {
			return Integer.parseInt(o.toString());
		}
	}
 
	/**
	 * 查询一个值
	 * @method executeLong
	 * @return {long}
	 * 
	 * @author Darkness
	 * @date 2013-2-22 上午10:50:30 
	 * @version V1.0
	 */
	public long executeLong(String sql, Object[] params) {

		return this.executeLong(sql, Objects.newArrayList(params));
	}

	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为long
	 */
	public long executeLong(String sql, ArrayList<Object> params) {

		Object o = this.executeOneValue(sql, params);
		if (o == null) {
			return 0L;
		}
		if (o instanceof Number) {
			return ((Number) o).longValue();
		} else {
			return Long.parseLong(o.toString());
		}
	}

	public double executeDouble(String sql) {
		return executeDouble(sql, (ArrayList<Object>) null);
	}
	
	public double executeDouble(String sql, Object[] params) {

		return this.executeDouble(sql, Objects.newArrayList(params));
	}

	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为double
	 */
	public double executeDouble(String sql, ArrayList<Object> params) {

		Object o = this.executeOneValue(sql, params);
		if (o == null) {
			return 0D;
		}
		if (o instanceof Number) {
			return ((Number) o).doubleValue();
		} else {
			return Double.parseDouble(o.toString());
		}
	}

	public long executeLong(String sql) {
		return executeLong(sql, (ArrayList<Object>) null);
	}

	public float executeFloat(String sql, Object[] params) {

		return this.executeFloat(sql, Objects.newArrayList(params));
	}

	/**
	 * 执行查询，并返回第一条记录的第一个字段的值并转化为float
	 */
	public float executeFloat(String sql, ArrayList<Object> params) {

		Object o = this.executeOneValue(sql, params);
		if (o == null) {
			return 0f;
		}
		if (o instanceof Number) {
			return ((Number) o).floatValue();
		} else {
			return Float.parseFloat(o.toString());
		}
	}

	public float executeFloat(String sql) {
		return executeFloat(sql, (ArrayList<Object>)null);
	}

	
	/**
	 * 存储过程的调用
	 * @method executeCall
	 * @param {String} call(存储过程名称)
	 * @author Darkness
	 * @date 2013-2-22 上午09:59:22 
	 * @version V1.0
	 */
	public void executeCall(String call) throws SQLException {
		try {
			Date startTime = new Date();
			// {call pt_init}
			CallableStatement cs = getConnection().prepareCall("{ call "+call+" }");
			cs.execute();
			System.out.println("=======execute time:" + (new Date().getTime() - startTime.getTime()) / 1000);
		} catch (SQLException e) {
			throw e;
		}
	}
	
	/**
	 * 查询二维表格数据
	 * @method executeDataTable
	 * @return {DataTable}
	 * @author Darkness
	 * @date 2013-2-22 上午10:34:46 
	 * @version V1.0
	 */
	public DataTable executeDataTable(String sql, Object... params) {
		ArrayList<Object> list = new ArrayList<>();
		if(params != null) {
			for (Object object : params) {
				list.add(object);
			}
		}
		return executeDataTable(sql, list);
	}
	
	public DataTable executeDataTable(String sql, ArrayList<Object> params) {

		log.debug(sql);

		return (DataTable) this.executeQuery(sql, params, new ICallbackStatement() {

			public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) {

				return new ResultDataTable(rs);
			}

		});

	}
	
	private void autoCloseReadOnlyConnection() {
		if(this.getConnection() == null) {
			return;
		}
		try {
			if(!this.getConnection().isClosed() && this.getConnection().isReadOnly()) {
				this.getConnection().close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
