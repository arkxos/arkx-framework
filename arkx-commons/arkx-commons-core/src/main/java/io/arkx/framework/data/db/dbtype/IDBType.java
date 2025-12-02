package io.arkx.framework.data.db.dbtype;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.data.db.command.*;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionConfig;
import io.arkx.framework.extend.IExtendItem;

/**
 * 数据库类型接口
 *
 * @author Darkness
 * @date 2013-1-27 下午12:41:49
 * @version V1.0
 */
public interface IDBType extends IExtendItem {

	/**
	 * 是否完全支持此类型的数据库。如果不支持则只能用于外部数据库连接。
	 */
	boolean isFullSupport();

	/**
	 * @return 数据库类型对应的JDBC驱动类
	 */
	String getDriverClass();

	/**
	 * @param dcc 数据库连接池配置信息
	 * @return 根据数据库连接池配置信息创建的JDBC连接
	 * @throws SQLException
	 * @throws ClassNotFoundException
	 */
	java.sql.Connection createConnection(ConnectionConfig dcc) throws SQLException, ClassNotFoundException;

	/**
	 * @param dcc 数据库配置信息
	 * @return 根据数据库配置信息生成的JDBC URL
	 */
	String getJdbcUrl(ConnectionConfig dcc);

	/**
	 * JDBC连接创建后执行的初始化语句（用于指定字符集、设置连接会话变量等）
	 * @param conn 新创建的连接
	 * @throws SQLException
	 */
	void afterConnectionCreate(Connection conn) throws SQLException;

	/**
	 * 返回数据库类型的默认端口
	 */
	int getDefaultPort();

	/**
	 * @param c 创建数据表指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(CreateTableCommand c);

	/**
	 * @param c 添加字段指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(AddColumnCommand c);

	/**
	 * @param c 修改主键指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(AlterKeyCommand c);

	/**
	 * @param c 复杂字段修改指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(AdvanceChangeColumnCommand c);

	/**
	 * @param c 创建索引指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(CreateIndexCommand c);

	/**
	 * @param c 删除字段指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(DropColumnCommand c);

	/**
	 * @param c 删除索引指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(DropIndexCommand c);

	/**
	 * @param c 删除数据表指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(DropTableCommand c);

	/**
	 * @param c 重命名数据表指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(RenameTableCommand c);

	/**
	 * @param c 重命名字段指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(RenameColumnCommand c);

	/**
	 * @param c 修改字段长度指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(ChangeColumnLengthCommand c);

	/**
	 * @param c 修改字段非空属性指令
	 * @return 输出指令在当前数据库类型中对应的SQL
	 */
	String[] toSQLArray(ChangeColumnMandatoryCommand c);

	/**
	 * @param table 数据表名
	 * @return 当前数据类型下的主键的SQL形式
	 */
	String getPKNameFragment(String table);

	/**
	 * 将JAVA类型转换为本数据库类型对应的字段类型
	 * @param dataType 字段数据类型，见DataTypes类
	 * @param length 字段长度
	 * @param precision 字段精度
	 * @return
	 */
	String toSQLType(DataTypes dataType, int length, int precision);

	/**
	 * 设置PreparedStatement中的Blob变量的值
	 * @param conn 数据库连接
	 * @param ps PreparedStatement
	 * @param i 变量序号
	 * @param v 要设置的值
	 * @throws SQLException
	 */
	void setBlob(Connection conn, PreparedStatement ps, int i, byte[] v) throws SQLException;

	/**
	 * 设置PreparedStatement中的Clob变量的值
	 * @param conn 数据库连接
	 * @param ps PreparedStatement
	 * @param i 变量序号
	 * @param v 要设置的值
	 * @throws SQLException
	 */
	void setClob(Connection conn, PreparedStatement ps, int i, Object v) throws SQLException;

	/**
	 * @param conn 数据库连接
	 * @param qb 查询器
	 * @param pageSize 每页条数
	 * @param pageIndex 第几页
	 * @return 分页查询SQL
	 */
	String getPagedSQL(String sql, int pageSize, int pageIndex, int connectionId);

	/**
	 * @return SQL语句分隔符
	 */
	String getSQLSperator();

	/**
	 * @param message 注释
	 * @return 注释在当前数据库类型中的形式
	 */
	String getComment(String message);

	/**
	 * @return select时加锁的语句。例如在Oracle下应该返回" for update"
	 */
	String getForUpdate();

	/**
	 * 从ResultSet中获取当前行的指定列的值
	 * @param rs JDBC查询返回的ResultSet
	 * @param columnIndex 列顺序(下标从1开始)
	 * @param dataType 数据类型
	 */
	Object getValueFromResultSet(ResultSet rs, int columnIndex, DataTypes dataType, boolean latin1Flag)
			throws SQLException;

	/**
	 * 遮掩字段名，当字段名为本数据库专有的关键字时，应该用双引号将字段名包裹
	 */
	String maskColumnName(String columnName);

	/**
	 * 表是否存在
	 *
	 * @author Darkness
	 * @date 2013-1-28 下午03:35:45
	 * @version V1.0
	 */
	boolean isTableExist(String databaseName, String tableName);

}
