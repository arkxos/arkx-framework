package org.ark.framework.orm;

import java.io.Serializable;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.commons.collection.*;
import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.db.DataCollection;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.jdbc.*;
import org.ark.framework.orm.sql.ClobUtils;
import org.ark.framework.orm.sql.LobUtil;

import lombok.extern.slf4j.Slf4j;

import org.ark.framework.orm.util.SqlKeywordEscaper;

/**
 * @class org.ark.framework.orm.Schema
 * CRUD SQL 表编码 列信息 类包名 指定操作列【查询、更新】 是否操作列状态
 *
 * 插入 更新 删除 删除、插入？ 删除、备份 备份 获取备份schema
 *
 * schema功能： 插入 删除 根据主键填充对象 更新 设置需要操作的列【填充】 查询所有结果集SchemaSet 查询分页的结果集SchemaSet
 * 获取备份的schema：系统应该有对应的备份表、备份类
 *
 * 外部设置数据源 map、DataCollection、DataRow
 *
 * JSON格式toString，方便查看 提供深度拷贝clone
 *
 * 转换成外部数据 map、DataRow
 *
 * P 获取主键值 设置主键值
 *
 * @author Darkness
 *
 *         Copyright (c) 2009 by Darkness
 *
 * @date 2010-8-4 下午07:29:15
 * @version 1.0
 */
@Slf4j
public abstract class Schema implements Serializable, Cloneable {

//	private static Logger logger = log.getLogger(Schema.class);

	private static final long serialVersionUID = 1L;
	public String TableCode;
	public String TableComment;
	public SchemaColumn[] Columns;

	/**
	 * 表代码到Schema类的映射注册表
	 * 用于通过表代码动态创建对应的Schema实例
	 */
	private static final ConcurrentHashMap<String, Class<? extends Schema>> schemaRegistry = new ConcurrentHashMap<>();

	/**
	 * 注册表代码和对应的Schema类
	 *
	 * @param tableCode 表代码
	 * @param schemaClass 对应的Schema类
	 */
	public static void registerSchema(String tableCode, Class<? extends Schema> schemaClass) {
		schemaRegistry.put(tableCode, schemaClass);
		log.debug("注册Schema类: {} -> {}", tableCode, schemaClass.getName());
	}

	/**
	 * 获取所有已注册的Schema类信息
	 *
	 * @return 包含表代码和对应Schema类的映射
	 */
	public static Map<String, Class<? extends Schema>> getAllRegisteredSchemas() {
		return new HashMap<>(schemaRegistry);
	}

	/**
	 * 获取所有已注册的表代码列表
	 *
	 * @return 已注册的表代码列表
	 */
	public static List<String> getAllRegisteredTableCodes() {
		return new ArrayList<>(schemaRegistry.keySet());
	}

	/**
	 * 检查指定的表代码是否已注册
	 *
	 * @param tableCode 表代码
	 * @return 如果已注册返回true，否则返回false
	 */
	public static boolean isTableCodeRegistered(String tableCode) {
		return schemaRegistry.containsKey(tableCode);
	}

	protected String InsertAllSQL;
	protected String UpdateAllSQL;
	protected String FillAllSQL;
	protected String DeleteSQL;

	protected String NameSpace;
	protected int bConnFlag = 0;

	protected boolean bOperateFlag = false;
	protected int[] operateColumnOrders;
	protected boolean[] HasSetFlag;
	protected transient JdbcTemplate mDataAccess;
	protected Object[] OldKeys;

	/**
	 * 检查当前Session是否已经在事务中
	 * 无法直接检查事务状态，所以使用这个方法标记是否由外部管理事务
	 */
	private boolean isExternallyManagedTransaction = false;

	/**
	 * 设置当前对象的事务是否由外部管理
	 *
	 * @param managed 如果为true，表示事务由外部管理
	 */
	public void setExternallyManagedTransaction(boolean managed) {
		this.isExternallyManagedTransaction = managed;
	}

	public Schema() {
		bOperateFlag = false;
	}

	/**
	 * 设置主键值
	 */
	public void setPrimaryKey(Object... v) {

		if (v == null)
			return;

		SchemaColumn[] primaryKeys = SchemaUtil.getPrimaryKeyColumns(Columns);

		if (v.length != primaryKeys.length) {
			log.info("设置主键值跟主键列长度不匹配...");
			return;
		}

		for (int i = 0; i < primaryKeys.length; i++) {
			SchemaColumn schemaColumn = primaryKeys[i];
			setV(schemaColumn.getColumnOrder(), v[i]);
		}
	}

	/**
	 * 设置insert操作的所有参数
	 */
	void setInsertParams(Query sqlBuilder) throws SQLException {
		// 使用LinkedHashMap存储字段,自动去重并保持顺序,忽略大小写
		Map<String, SchemaColumn> uniqueColumns = new LinkedHashMap<>();
		for (int i = 0; i < Columns.length; ++i) {
			SchemaColumn sc = Columns[i];
			uniqueColumns.put(sc.getColumnName().toLowerCase(), sc);
		}

		for (SchemaColumn sc : uniqueColumns.values()) {
			int i = sc.getColumnOrder();
			if ((sc.isMandatory()) && (getV(i) == null)) {
				throw new SQLException("表" + TableCode + "的列" + sc.getColumnName() + "不能为空");
			}

			sqlBuilder.add(getV(i));
		}
	}

	public boolean insert() {
		try {
			// 使用LinkedHashMap存储字段,自动去重并保持顺序,忽略大小写
			Map<String, SchemaColumn> uniqueColumns = new LinkedHashMap<>();
			// 先收集所有字段
			for (int i = 0; i < Columns.length; ++i) {
				SchemaColumn sc = Columns[i];
				uniqueColumns.put(sc.getColumnName().toLowerCase(), sc);
			}

			// 动态构建INSERT SQL语句，只包含去重后的字段
			StringBuilder insertSQL = new StringBuilder();
			insertSQL.append("INSERT INTO ").append(TableCode).append(" (");

			StringBuilder valuesPlaceholder = new StringBuilder();
			boolean first = true;

			// 用于存储最终要设置的参数
			List<Object> params = new ArrayList<>();

			for (SchemaColumn sc : uniqueColumns.values()) {
				int i = sc.getColumnOrder();

				// 只有在参数值可用的情况下才添加该字段到SQL
				Object value = getV(i);
				if (sc.isMandatory() && value == null) {
					throw new SQLException("表" + TableCode + "的列" + sc.getColumnName() + "不能为空");
				}

				if (!first) {
					insertSQL.append(", ");
					valuesPlaceholder.append(", ");
				}
				insertSQL.append(sc.getColumnName());
				valuesPlaceholder.append("?");
				params.add(value);

				first = false;
			}

			insertSQL.append(") VALUES (").append(valuesPlaceholder).append(")");
			String finalSql = insertSQL.toString();

			// 使用SqlKeywordEscaper处理INSERT语句中的关键字
			finalSql = SqlKeywordEscaper.escapeInsertSql(finalSql);

			// 添加日志，帮助调试
			log.debug("执行插入SQL: " + finalSql);

			Query queryBuilder = getSession().createQuery(finalSql);

			// 使用收集的参数列表设置参数
			for (Object param : params) {
				queryBuilder.add(param);
			}
			int i = queryBuilder.executeNoQuery();
			return i != 0;
		} catch (SQLException e) {
			log.error("操作表" + this.TableCode + "时发生错误:" + e.getMessage(), e);
		} catch (Exception e) {
			log.error("操作表" + this.TableCode + "时发生未知错误", e);
		}
		return false;
	}

	public SchemaUpsertEntity insertGetSql() {
		String execSql = "";
		try {
			// 使用LinkedHashMap存储字段,自动去重并保持顺序,忽略大小写
			Map<String, SchemaColumn> uniqueColumns = new LinkedHashMap<>();
			// 先收集所有字段
			for (int i = 0; i < Columns.length; ++i) {
				SchemaColumn sc = Columns[i];
				uniqueColumns.put(sc.getColumnName().toLowerCase(), sc);
			}

			// 动态构建INSERT SQL语句，只包含去重后的字段
			StringBuilder insertSQL = new StringBuilder();
			insertSQL.append("INSERT INTO ").append(TableCode).append(" (");

			StringBuilder valuesPlaceholder = new StringBuilder();
			boolean first = true;

			// 用于存储最终要设置的参数
			List<Object> params = new ArrayList<>();

			for (SchemaColumn sc : uniqueColumns.values()) {
				int i = sc.getColumnOrder();

				// 只有在参数值可用的情况下才添加该字段到SQL
				Object value = getV(i);
				if (sc.isMandatory() && value == null) {
					throw new SQLException("表" + TableCode + "的列" + sc.getColumnName() + "不能为空");
				}

				if (!first) {
					insertSQL.append(", ");
					valuesPlaceholder.append(", ");
				}
				insertSQL.append(sc.getColumnName());
				valuesPlaceholder.append("?");
				params.add(value);

				first = false;
			}

			insertSQL.append(") VALUES (").append(valuesPlaceholder).append(")");
			String finalSql = insertSQL.toString();

			// 使用SqlKeywordEscaper处理INSERT语句中的关键字
			finalSql = SqlKeywordEscaper.escapeInsertSql(finalSql);

			// 添加日志，帮助调试
			log.debug("执行插入SQL: " + finalSql);

			Query queryBuilder = getSession().createQuery(finalSql);

			// 使用收集的参数列表设置参数
			for (Object param : params) {
				queryBuilder.add(param);
			}
			execSql = SQLFormatter.format(queryBuilder.getSQL(), params);
			int i = queryBuilder.executeNoQuery();
			return SchemaUpsertEntity.builder().success(i != 0).execSql(execSql).build();
		} catch (SQLException e) {
			log.error("操作表" + this.TableCode + "时发生错误:" + e.getMessage(), e);
		} catch (Exception e) {
			log.error("操作表" + this.TableCode + "时发生未知错误", e);
		}
		return SchemaUpsertEntity.builder().success(false).execSql(execSql).build();
	}

	void setUpdataParams(Query sqlBuilder) {
		if (bOperateFlag) {
			for (int i = 0; i < operateColumnOrders.length; ++i) {
				// 跳过主键列，主键不应该被更新
				if (!Columns[operateColumnOrders[i]].isPrimaryKey()) {
					sqlBuilder.add(getV(operateColumnOrders[i]));
				}
			}
		} else {
			for (int i = 0; i < Columns.length; ++i) {
				// 跳过主键列，主键不应该被更新
				if (!Columns[i].isPrimaryKey()) {
					sqlBuilder.add(getV(i));
				}
			}
		}
		sqlBuilder.add(getPrimaryKeyValue("update"));
	}


	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean update() {
		Query queryBuilder = getSession().createQuery(getUpdateSql());

		setUpdataParams(queryBuilder);

		try {
			return queryBuilder.executeNoQuery() != -1;
		} catch (Throwable e) {
			log.warn("操作表" + this.TableCode + "时发生错误:" + e.getMessage());
			throw new RuntimeException(e);
		}
	}

	/**
	 * 执行更新或插入操作（upsert）
	 * 首先尝试更新记录，如果更新影响的行数为0（即没有找到匹配的记录），则执行插入操作
	 *
	 * @return 如果操作成功则返回true，否则返回false
	 */
	public SchemaUpsertEntity upsert() {
		Session session = null;
		boolean startedNewTransaction = false;
		try {
			// 获取当前会话
			session = getSession();

			// 只有在外部未管理事务的情况下才开始新事务
			if (!isExternallyManagedTransaction) {
				session.beginTransaction();
				startedNewTransaction = true;
			}
			ArrayList<Object> params = new ArrayList<>();
			//dev code
//			if (String.valueOf(getPrimaryKeyValue()).equals("545")) {
//				SchemaColumn[] columns = getColumns();
////				ReflectUtil.setFieldValue(this, "unit_fuctional", "123");
//				ReflectUtil.setFieldValue(this, "CREATE_TIME", null);
//				ReflectUtil.setFieldValue(this, "UPDATE_TIME", null);
//				System.out.println();
//			}

			//如果这个表没有主键 则跳过随机验证
			SchemaColumn[] primaryKeyColumns = SchemaUtil.getPrimaryKeyColumns(SchemaUtil.getPrimaryKeyColumns(getColumns()));
			//必须有主键 才更新
			if (primaryKeyColumns.length > 0) {
				// 首先尝试更新
				Query queryBuilder = session.createQuery(getUpdateSql());
				setUpdataParams(queryBuilder);

				int affected = queryBuilder.executeNoQuery();

				// 如果更新影响了记录，则操作成功
				if (affected > 0) {
					if (startedNewTransaction) {
						session.commit();
					}
					params = queryBuilder.getParams();
					String formattedSql = SQLFormatter.format(getUpdateSql(), params);

					return SchemaUpsertEntity.builder().success(true).type("update").execSql(formattedSql).build();
				}
			}


			// 如果更新没有影响任何记录，则尝试插入
			SchemaUpsertEntity schemaUpsertEntity = this.insertGetSql();

			if (startedNewTransaction) {
				if (schemaUpsertEntity.getSuccess()) {
					session.commit();
				} else {
					session.rollback();
				}
			}
			return SchemaUpsertEntity.builder().success(true).execSql(schemaUpsertEntity.getExecSql()).build();
		} catch (Exception e) {
			// 如果操作失败，则回滚，但只回滚我们自己开始的事务
			if (session != null && startedNewTransaction) {
				try {
					session.rollback();
				} catch (Exception rollbackEx) {
					log.error("回滚事务时发生错误", rollbackEx);
				}
			} else {
				throw e;
			}
			log.error("执行表 " + this.TableCode + " 的upsert操作时发生错误: " + e.getMessage(), e);
			return SchemaUpsertEntity.builder().errorMessage(e.getMessage()).success(false).build();
		}
	}

	/**
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean delete() {
		// 使用SqlKeywordEscaper处理DeleteSQL中的关键字
		String escapedDeleteSQL = SqlKeywordEscaper.escapeUpdateSql(this.DeleteSQL);
		Query queryBuilder = getSession().createQuery(escapedDeleteSQL);
		queryBuilder.add(this.getPrimaryKeyValue("delete"));
		try {
			return queryBuilder.executeNoQuery() != -1;
		} catch (Exception e) {
			log.warn("操作表" + this.TableCode + "时发生错误!");
			e.printStackTrace();
		}
		return false;
	}
	/**
	 * 获取更新的SQL语句
	 *
	 * @return
	 */
	protected String getUpdateSql() {
		String sql = this.UpdateAllSQL;
		if (this.bOperateFlag) {
			StringBuffer sb = new StringBuffer("update " + this.TableCode + " set ");
			boolean first = true;
			for (int i = 0; i < this.operateColumnOrders.length; ++i) {
				// 跳过主键列，主键不应该被更新
				if (!this.Columns[this.operateColumnOrders[i]].isPrimaryKey()) {
					if (!first) {
						sb.append(",");
					}
					// 使用SqlKeywordEscaper处理列名，如果是保留关键字则添加双引号
					String columnName = this.Columns[this.operateColumnOrders[i]].getColumnName();
					sb.append(SqlKeywordEscaper.escapeIfReserved(columnName));
					sb.append("=?");
					first = false;
				}
			}
			sb.append(sql.substring(sql.indexOf(" where")));
			sql = sb.toString();

			// 使用SqlKeywordEscaper处理完整的UPDATE语句
			sql = SqlKeywordEscaper.escapeUpdateSql(sql);
		} else {
			// 如果使用的是完整的UpdateAllSQL，需要移除其中的主键列
			sql = removePrivateKeyFromUpdateSQL(sql);
			// 处理SQL关键字
			sql = SqlKeywordEscaper.escapeUpdateSql(sql);
		}

		return sql;
	}

	/**
	 * 从UpdateAllSQL中移除主键列的SET部分
	 * @param originalUpdateSQL 原始的UPDATE SQL语句
	 * @return 移除主键列后的UPDATE SQL语句
	 */
	private String removePrivateKeyFromUpdateSQL(String originalUpdateSQL) {
		if (originalUpdateSQL == null || originalUpdateSQL.isEmpty()) {
			return originalUpdateSQL;
		}

		try {
			// 找到SET和WHERE部分
			int setIndex = originalUpdateSQL.toLowerCase().indexOf(" set ");
			int whereIndex = originalUpdateSQL.toLowerCase().indexOf(" where ");

			if (setIndex == -1 || whereIndex == -1) {
				return originalUpdateSQL;
			}

			String beforeSet = originalUpdateSQL.substring(0, setIndex + 5); // 包含 " set "
			String setPart = originalUpdateSQL.substring(setIndex + 5, whereIndex);
			String wherePart = originalUpdateSQL.substring(whereIndex);

			// 解析SET部分，移除主键列
			String[] setPairs = setPart.split(",");
			StringBuilder newSetPart = new StringBuilder();

			for (String setPair : setPairs) {
				String trimmedPair = setPair.trim();
				if (trimmedPair.isEmpty()) {
					continue;
				}

				// 获取列名（去掉=?部分）
				String columnName = trimmedPair.split("=")[0].trim();

				// 检查是否为主键列
				boolean isPrimaryKey = false;
				for (SchemaColumn column : Columns) {
					if (column.getColumnName().equalsIgnoreCase(columnName)) {
						isPrimaryKey = column.isPrimaryKey();
						break;
					}
				}

				// 如果不是主键列，则添加到新的SET部分
				if (!isPrimaryKey) {
					if (newSetPart.length() > 0) {
						newSetPart.append(",");
					}
					newSetPart.append(trimmedPair);
				}
			}

			// 重新组装SQL
			return beforeSet + newSetPart.toString() + wherePart;
		} catch (Exception e) {
			log.warn("移除主键列时发生错误，使用原始SQL: " + e.getMessage());
			return originalUpdateSQL;
		}
	}

	protected String getSelectSql() {
		String sql = this.FillAllSQL;
		if (this.bOperateFlag) {// 设置过需要操作的列，则只查询设置的这些操作列，默认为全部查询
			StringBuffer sb = new StringBuffer("select ");
			for (int i = 0; i < this.operateColumnOrders.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(this.Columns[this.operateColumnOrders[i]].getColumnName());
			}
			sb.append(sql.substring(sql.indexOf(" from")));
			sql = sb.toString();
		}
		return sql;
	}
	private int pkIndex = 0;

	/**
	 * 获取主键的值
	 */
	protected Object[] getPrimaryKeyValue(String operate) {
		List<Object> primaryKeyValues = new ArrayList<Object>();

		SchemaColumn[] primaryKeys = SchemaUtil.getPrimaryKeyColumns(Columns);
		for (SchemaColumn schemaColumn : primaryKeys) {
			Object v = getV(schemaColumn.getColumnOrder());
			if (this.OldKeys != null) {
				v = this.OldKeys[(pkIndex++)];
			}
			if (v == null) {
				log.warn("不满足" + operate + "的条件，" + this.TableCode + "Schema的" + schemaColumn.getColumnName() + "为空");
			}
			primaryKeyValues.add(v);
		}

		return primaryKeyValues.toArray();
	}
	public boolean backup() {
		return backup(null, null);
	}

	public boolean backup(String backupOperator, String backupMemo) {
		try {
			Schema bSchema = getBackUpSchema(backupOperator, backupMemo);
			if (this.bConnFlag == 0) {
				return bSchema.insert();
			}
			bSchema.setDataAccess(this.mDataAccess);
			return bSchema.insert();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public boolean deleteAndInsert() {
		if (this.bConnFlag == 1) {
			if (!delete()) {
				return false;
			}
			return insert();
		}
//		this.mDataAccess = new JdbcTemplate();
		this.bConnFlag = 1;
		try {
//			this.mDataAccess.setAutoCommit(false);
			delete();
			insert();
//			this.mDataAccess.commit();
			return true;
		} catch (Exception e) {
			e.printStackTrace();
//				this.mDataAccess.rollback();
			return false;
		} finally {
//				this.mDataAccess.setAutoCommit(true);
//				this.mDataAccess.close();
			this.mDataAccess = null;
			this.bConnFlag = 0;
		}
	}

	public boolean deleteAndBackup() {
		return deleteAndBackup(null, null);
	}

	public Schema getBackUpSchema(String backupOperator, String backupMemo) {

		backupOperator = (StringUtil.isEmpty(backupOperator)) ? "SYSTEM" : backupOperator;
		Class<?> c = null;
		try {
			c = Class.forName("com.xdarkness.schema.B" + this.TableCode + "Schema");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		Schema bSchema = null;
		try {
			bSchema = (Schema) c.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		int i = 0;
		for (; i < this.Columns.length; ++i) {
			bSchema.setV(i, this.getV(i));
		}
		bSchema.setV(i, SchemaUtil.getBackupNo());
		bSchema.setV(i + 1, backupOperator);
		bSchema.setV(i + 2, new Date());
		bSchema.setV(i + 3, backupMemo);
		return bSchema;
	}

	public boolean deleteAndBackup(String backupOperator, String backupMemo) {
		try {
			Schema bSchema = getBackUpSchema(backupOperator, backupMemo);
			if (this.bConnFlag == 0) {
//				this.mDataAccess = new JdbcTemplate();
				this.bConnFlag = 1;
				try {
//					this.mDataAccess.setAutoCommit(false);
					delete();
					bSchema.setDataAccess(this.mDataAccess);
					bSchema.insert();
//					this.mDataAccess.commit();
					return true;
				} catch (Exception e) {
					log.warn("操作表" + this.TableCode + "时发生错误:" + e.getMessage());
					e.printStackTrace();
//						this.mDataAccess.rollback();
					return false;
				} finally {
					this.bConnFlag = 0;
//						this.mDataAccess.setAutoCommit(true);
//						this.mDataAccess.close();
				}
			}
			if (!delete()) {
				return false;
			}
			bSchema.setDataAccess(this.mDataAccess);
			return bSchema.insert();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	public boolean fill() {

		Query queryBuilder = getSession().createQuery(getSelectSql());
		queryBuilder.add(getPrimaryKeyValue("fill"));

		final Schema schema = this;

		return (Boolean) queryBuilder.executeQuery(new ICallbackStatement() {

			public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
				if (rs.next()) {
					if (schema.bOperateFlag) {
						for (int i = 0; i < schema.operateColumnOrders.length; ++i)
							if (schema.Columns[schema.operateColumnOrders[i]].getColumnType() == 10) {
								if (connection.getDBConfig().isOracle()
										|| connection.getDBConfig().isDB2())
									schema.setV(schema.operateColumnOrders[i], LobUtil.clobToString(rs.getClob(i + 1)));
								else
									schema.setV(schema.operateColumnOrders[i], rs.getObject(i + 1));
							} else if (schema.Columns[schema.operateColumnOrders[i]].getColumnType() == 2)
								schema.setV(schema.operateColumnOrders[i], LobUtil.blobToBytes(rs.getBlob(i + 1)));
							else
								schema.setV(schema.operateColumnOrders[i], rs.getObject(i + 1));

						// if ((this.Columns[order].getColumnType() == 10)
						// || (this.Columns[order].getColumnType() == 1)) {
						// String str = (String) schema.getV(order);
						// if ((latin1Flag) && (StringUtil.isNotEmpty(str)))
						// {
						// try {
						// str = new String(str.getBytes("ISO-8859-1"),
						// Constant.GlobalCharset);
						// } catch (UnsupportedEncodingException e) {
						// e.printStackTrace();
						// }
						// }
						// schema.setV(order, str);
						// }
						// if (this.Columns[order].isPrimaryKey())
						// pklist.add(schema.getV(order));
					} else {

						for (int i = 0; i < schema.Columns.length; ++i) {
							if (schema.Columns[i].getColumnType() == 10) {
								if (connection.getDBConfig().isOracle()
										|| connection.getDBConfig().isDB2())
									schema.setV(i, LobUtil.clobToString(rs.getClob(i + 1)));
								else
									schema.setV(i, rs.getObject(i + 1));
							} else if (schema.Columns[i].getColumnType() == 2)
								schema.setV(i, LobUtil.blobToBytes(rs.getBlob(i + 1)));
							else {
								schema.setV(i, rs.getObject(i + 1));
							}

						}
					}
					return true;
				}
				return false;
			}
		});
	}

	/**
	 * 确保值类型与列定义类型匹配的安全转换方法
	 * 主要处理数据库VARCHAR字段返回数值类型的情况
	 */
	private Object ensureCorrectType(Object value, SchemaColumn column) {
		if (value == null) {
			return null;
		}

		try {
			// 检查列定义类型
			int columnType = column.getColumnType();

			// 日期类型处理
			if (columnType == 12) {
				// 如果已经是java.time的日期时间类型，转换为java.util.Date
				if (value instanceof java.time.LocalDateTime) {
					LogUtil.debug("类型转换: 将 LocalDateTime 类型的值 '" + value + "' 转换为Date类型");
					return Date.from(((java.time.LocalDateTime)value).atZone(java.time.ZoneId.systemDefault()).toInstant());
				} else if (value instanceof java.time.LocalDate) {
					LogUtil.debug("类型转换: 将 LocalDate 类型的值 '" + value + "' 转换为Date类型");
					return Date.from(((java.time.LocalDate)value).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
				} else if (value instanceof String && ((String)value).contains("T")) {
					// 处理ISO格式的日期时间字符串
					LogUtil.debug("类型转换: 将ISO格式字符串 '" + value + "' 转换为Date类型");
					try {
						java.time.LocalDateTime ldt = java.time.LocalDateTime.parse((String)value);
						return Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
					} catch (Exception e) {
						// 如果解析失败，交给convertValueByType处理
						return convertValueByType(columnType, value);
					}
				} else if (!(value instanceof Date)) {
					// 其他类型转换为Date
					return convertValueByType(columnType, value);
				}
				return value;
			}

			// 字符串类型处理
			if ((columnType == 1 || columnType == 10) && !(value instanceof String)) {
				// 如果列是字符串类型但值不是字符串，转换为字符串
				LogUtil.debug("类型转换: 将 " + value.getClass().getName() +
						" 类型的值 '" + value + "' 转换为String类型");
				return value.toString();
			}

			// 数值类型处理
			if (columnType == 8 && value instanceof String) {
				// Integer类型
				LogUtil.debug("类型转换: 将String类型的值 '" + value + "' 转换为Integer类型");
				return Integer.valueOf(value.toString());
			}

			if (columnType == 7 && value instanceof String) {
				// Long类型
				LogUtil.debug("类型转换: 将String类型的值 '" + value + "' 转换为Long类型");
				return Long.valueOf(value.toString());
			}

			if ((columnType == 5 || columnType == 6) && value instanceof String) {
				// Float/Double类型
				LogUtil.debug("类型转换: 将String类型的值 '" + value + "' 转换为浮点数类型");
				return Double.valueOf(value.toString());
			}

			// 如果没有特殊处理，返回原值
			return value;
		} catch (Exception e) {
			LogUtil.warn("类型转换失败: " + e.getMessage() + ", 使用原始值");
			return value;
		}
	}

	public SchemaSet querySet(Query qb) {
		try {
			final Schema schema = this;
			return (SchemaSet) qb.executeQuery(new ICallbackStatement() {
				public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
					SchemaSet set = schema.newSet();

					// 获取ResultSet的元数据
					ResultSetMetaData rsMetaData = rs.getMetaData();
					int columnCount = rsMetaData.getColumnCount();

					// 创建列名到列索引的映射，用于安全获取值
					Map<String, Integer> columnNameToIndex = new HashMap<>();
					for (int i = 1; i <= columnCount; i++) {
						columnNameToIndex.put(rsMetaData.getColumnName(i).toUpperCase(), i);
						// 同时添加小写版本，确保大小写不敏感匹配
						columnNameToIndex.put(rsMetaData.getColumnName(i).toLowerCase(), i);
					}

					LogUtil.debug("查询结果包含" + columnCount + "列");

					while (rs.next()) {
						Schema newSchema = schema.newInstance();
						if (schema.bOperateFlag) {
							for (int i = 0; i < schema.operateColumnOrders.length; ++i) {
								int columnOrder = schema.operateColumnOrders[i];
								SchemaColumn column = schema.Columns[columnOrder];
								Object value = null;

								try {
									// 记录列信息
									String columnName = column.getColumnName();
									int columnType = column.getColumnType();

									// 检查该列是否在结果集中
									Integer rsIndex = columnNameToIndex.get(columnName);
									if (rsIndex == null) {
										// 尝试用大写和小写再次查找
										rsIndex = columnNameToIndex.get(columnName.toUpperCase());
										if (rsIndex == null) {
											rsIndex = columnNameToIndex.get(columnName.toLowerCase());
										}
									}

									if (rsIndex == null) {
										// 列不在结果集中
										LogUtil.warn("列[" + columnName + "]不在查询结果中，将设置为null");
										newSchema.setV(columnOrder, null);
										continue;
									}

									// 获取JDBC类型信息
									int jdbcType = rsMetaData.getColumnType(rsIndex);
									String jdbcTypeName = rsMetaData.getColumnTypeName(rsIndex);

									// 获取值前记录
									LogUtil.debug("准备获取列[" + columnName + "]值，结果集索引=" + rsIndex +
											", 模型类型=" + columnType + ", JDBC类型=" + jdbcType + "(" + jdbcTypeName + ")");

									// 根据列类型获取值
									if (columnType == 10) {
										if (connection.getDBConfig().isOracle() || connection.getDBConfig().isDB2())
											value = LobUtil.clobToString(rs.getClob(rsIndex));
										else
											value = rs.getObject(rsIndex);
									} else if (columnType == 2) {
										value = LobUtil.blobToBytes(rs.getBlob(rsIndex));
									} else if (columnType == 12) {
										// 日期类型特殊处理
										try {
											// 先尝试获取为标准Date
											value = rs.getTimestamp(rsIndex);
										} catch (Exception e) {
											// 如果失败，尝试获取为对象
											value = rs.getObject(rsIndex);
											// 检查是否为java.time日期时间类型并转换
											if (value instanceof java.time.LocalDateTime) {
												LogUtil.debug("获取到LocalDateTime类型，转换为Date: " + value);
												value = java.util.Date.from(((java.time.LocalDateTime)value)
														.atZone(java.time.ZoneId.systemDefault()).toInstant());
											} else if (value instanceof java.time.LocalDate) {
												LogUtil.debug("获取到LocalDate类型，转换为Date: " + value);
												value = java.util.Date.from(((java.time.LocalDate)value)
														.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
											}
										}
									} else {
										value = rs.getObject(rsIndex);
									}

									// 记录实际获取的值类型
									String valueType = (value == null) ? "null" : value.getClass().getName();
									LogUtil.debug("获取到列[" + columnName + "]值: " + value +
											", 实际类型=" + valueType);

									// 类型安全转换：确保值类型与列定义匹配
									value = ensureCorrectType(value, column);

									if (value != null && !value.getClass().getName().equals(valueType)) {
										LogUtil.debug("列[" + columnName + "]值类型已转换: " +
												valueType + " -> " + value.getClass().getName());
									}

									// 执行赋值前记录
									LogUtil.debug("准备设置对象[" + newSchema.getClass().getName() +
											"]属性，索引=" + columnOrder + ", 值类型=" + (value == null ? "null" : value.getClass().getName()));

									// 设置值
									newSchema.setV(columnOrder, value);

									LogUtil.debug("成功设置列[" + columnName + "]值");
								} catch (Exception e) {
									// 记录异常详情
									LogUtil.error("设置列值异常: 列索引=" + i +
											", 列顺序=" + columnOrder + ", 列名=" + column.getColumnName() +
											", 值=" + value +
											", 值类型=" + (value != null ? value.getClass().getName() : "null") +
											", 异常: " + e.getMessage());
									e.printStackTrace();
									// 继续处理其他列而不是直接抛出异常
									continue;
								}
							}
						} else {
							for (int i = 0; i < schema.Columns.length; ++i) {
								SchemaColumn column = schema.Columns[i];
								Object value = null;

								try {
									// 记录列信息
									String columnName = column.getColumnName();
									int columnType = column.getColumnType();

									// 检查该列是否在结果集中
									Integer rsIndex = columnNameToIndex.get(columnName);
									if (rsIndex == null) {
										// 尝试用大写和小写再次查找
										rsIndex = columnNameToIndex.get(columnName.toUpperCase());
										if (rsIndex == null) {
											rsIndex = columnNameToIndex.get(columnName.toLowerCase());
										}
									}

									if (rsIndex == null) {
										// 列不在结果集中
										LogUtil.warn("列[" + columnName + "]不在查询结果中，将设置为null");
										newSchema.setV(i, null);
										continue;
									}

									// 获取JDBC类型信息
									int jdbcType = rsMetaData.getColumnType(rsIndex);
									String jdbcTypeName = rsMetaData.getColumnTypeName(rsIndex);

									// 获取值前记录
									LogUtil.debug("准备获取列[" + columnName + "]值，结果集索引=" + rsIndex +
											", 模型类型=" + columnType + ", JDBC类型=" + jdbcType + "(" + jdbcTypeName + ")");

									// 根据列类型获取值
									if (columnType == 10) {
										if (connection.getDBConfig().isOracle() || connection.getDBConfig().isDB2()) {
											value = LobUtil.clobToString(rs.getClob(rsIndex));
										} else if (connection.getDBConfig().isDM()){
											value = LobUtil.clobToString(rs.getClob(rsIndex));
										}else {
											value = rs.getObject(rsIndex);
										}
									} else if (columnType == 2) {
										value = LobUtil.blobToBytes(rs.getBlob(rsIndex));
									} else if (columnType == 12) {
										// 日期类型特殊处理
										try {
											// 先尝试获取为标准Date
											value = rs.getTimestamp(rsIndex);
										} catch (Exception e) {
											// 如果失败，尝试获取为对象
											value = rs.getObject(rsIndex);
											// 检查是否为java.time日期时间类型并转换
											if (value instanceof java.time.LocalDateTime) {
												LogUtil.debug("获取到LocalDateTime类型，转换为Date: " + value);
												value = java.util.Date.from(((java.time.LocalDateTime)value)
														.atZone(java.time.ZoneId.systemDefault()).toInstant());
											} else if (value instanceof java.time.LocalDate) {
												LogUtil.debug("获取到LocalDate类型，转换为Date: " + value);
												value = java.util.Date.from(((java.time.LocalDate)value)
														.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
											}
										}
									}else if("TEXT".equals(jdbcTypeName)&&connection.getDBConfig().isDM()) {
										value = ClobUtils.clobToString(rs.getClob(rsIndex));
									}
									else {
										value = rs.getObject(rsIndex);
									}

									// 记录实际获取的值类型
									String valueType = (value == null) ? "null" : value.getClass().getName();
									LogUtil.debug("获取到列[" + columnName + "]值: " + value +
											", 实际类型=" + valueType);

									// 类型安全转换：确保值类型与列定义匹配
									value = ensureCorrectType(value, column);

									if (value != null && !value.getClass().getName().equals(valueType)) {
										LogUtil.debug("列[" + columnName + "]值类型已转换: " +
												valueType + " -> " + value.getClass().getName());
									}

									// 执行赋值前记录
									LogUtil.debug("准备设置对象[" + newSchema.getClass().getName() +
											"]属性，索引=" + i + ", 值类型=" + (value == null ? "null" : value.getClass().getName()));

									// 设置值
									newSchema.setV(i, value);

									LogUtil.debug("成功设置列[" + columnName + "]值");
								} catch (Exception e) {
									// 记录异常详情
									LogUtil.error("设置列值异常: 列索引=" + i +
											", 列名=" + column.getColumnName() + ", 值=" + value +
											", 值类型=" + (value != null ? value.getClass().getName() : "null") +
											", 异常: " + e.getMessage());
									e.printStackTrace();
									// 继续处理其他列而不是直接抛出异常
									continue;
								}
							}
						}
						set.add(newSchema);
					}
					set.setOperateColumns(schema.operateColumnOrders);
					return set;
				}
			});
		} catch (Exception e) {
			LogUtil.error("查询表[" + TableCode + "]时发生错误: " + e.getClass().getName() + ": " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public SchemaSet<? extends Schema> querySet(int pageSize, int pageIndex) {
		return querySet(null, pageSize, pageIndex);
	}

	public SchemaSet<? extends Schema> querySet(Query queryBuilder, int pageSize, int pageIndex) {
		if (queryBuilder == null) {
			queryBuilder = getSession().createQuery(" WHERE 1=1 ");
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < Columns.length; ++i) {
				SchemaColumn sc = Columns[i];
				if (getV(sc.getColumnOrder()) != null) {
					Object v = getV(sc.getColumnOrder());
					if (v == null) {// TODO null值暂时跳过，除非主动设置null
						continue;
					}
					queryBuilder.add(v);
					sb.append(" and ");
					sb.append(sc.getColumnName());
					sb.append("=?");

				}
			}
			queryBuilder.append(sb.toString());
		}

		if ((queryBuilder != null) && queryBuilder.getSQL() != null && (!(queryBuilder.getSQL().trim().toLowerCase().startsWith("where")))) {
			throw new RuntimeException("QueryBuilder中的SQL语句不是以where开头的字符串");
		}
		if ((queryBuilder != null) && queryBuilder.getSQL() == null) {
			queryBuilder.setSQL("");
		}

		// 构建SQL查询语句
		StringBuffer sb = new StringBuffer();

		// 根据数据库类型构建不同的分页查询
		Connection conn = null;
		conn = SessionFactory.currentSession().getConnection();

		// 处理分页查询前的基础部分
		buildBaseQueryPart(sb, queryBuilder);

		// 设置分页参数
		if (pageSize == -1) {
			pageSize = Integer.MAX_VALUE;
		}
		if (pageIndex == -1) {
			pageIndex = 0;
		}

		// 尝试适配不同数据库的分页语法
		if (conn != null) {
			adaptPaginationSyntax(queryBuilder, conn, sb, pageSize, pageIndex);
		} else {
			// 如果获取连接失败，使用通用分页方式
			queryBuilder.setSQL(sb.toString());
			queryBuilder.setPagedQuery(true);
			queryBuilder.setPageSize(pageSize);
			queryBuilder.setPageIndex(pageIndex);
		}

		return querySet(queryBuilder);
	}

	/**
	 * 构建查询的基础部分
	 */
	private void buildBaseQueryPart(StringBuffer sb, Query queryBuilder) {
		sb.append("select ");

		int i;
		if (bOperateFlag) {
			for (i = 0; i < operateColumnOrders.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Columns[operateColumnOrders[i]].getColumnName());
			}
		} else if (this.getSession().getConnection().getDBConfig().isSQLServer()) {
			for (i = 0; i < Columns.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Columns[i].getColumnName());
			}
		} else {
			sb.append("*");
		}

		sb.append(" from " + TableCode);

		sb.append(" ");
		sb.append(queryBuilder.getSQL());
	}

	/**
	 * 根据不同数据库类型适配分页语法
	 */
	private void adaptPaginationSyntax(Query queryBuilder, Connection conn, StringBuffer baseSql,
									   int pageSize, int pageIndex) {
		String dbType = "UNKNOWN";
		try {
			dbType = conn.getDBConfig().DBType.toUpperCase();
		} catch (Exception e) {
			log.warn("获取数据库类型失败", e);
		}

		// 计算偏移量
		int offset = pageIndex * pageSize;
		String baseQuery = baseSql.toString();
		String pagedSql = baseQuery;

		log.debug("正在为数据库类型[{}]构建分页查询, pageSize={}, pageIndex={}", dbType, pageSize, pageIndex);

		// 根据不同数据库类型构建分页SQL
		switch (dbType) {
			case "DM":
				// 达梦数据库分页
				pagedSql = generateDmPagingQuery(baseQuery, pageSize, offset);
				queryBuilder.setPagedQuery(false);  // 使用手动处理的分页，不需要框架额外处理
				break;

			case "ORACLE":
				// Oracle分页
				pagedSql = generateOraclePagingQuery(baseQuery, pageSize, offset);
				queryBuilder.setPagedQuery(false);
				break;

			case "POSTGRESQL":
			case "MYSQL":
			case "MARIADB":
				// PostgreSQL/MySQL/MariaDB使用LIMIT OFFSET
				pagedSql = baseQuery + " LIMIT " + pageSize + " OFFSET " + offset;
				queryBuilder.setPagedQuery(false);
				break;

			case "SQLSERVER":
				// 检查SQL Server版本，新版本支持OFFSET FETCH
				boolean isNewVersion = isNewSqlServerVersion(conn);
				if (isNewVersion) {
					// SQL Server 2012+
					// 需要ORDER BY子句
					if (!baseQuery.toLowerCase().contains("order by")) {
						// 如果没有ORDER BY，添加一个基于主键或第一列的ORDER BY
						String orderColumn = getPrimaryKeyOrFirstColumn();
						pagedSql = baseQuery + " ORDER BY " + orderColumn +
								" OFFSET " + offset + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
					} else {
						pagedSql = baseQuery + " OFFSET " + offset + " ROWS FETCH NEXT " + pageSize + " ROWS ONLY";
					}
					queryBuilder.setPagedQuery(false);
				} else {
					// 旧版SQL Server - 使用ROW_NUMBER()
					pagedSql = generateOldSqlServerPagingQuery(baseQuery, pageSize, offset);
					queryBuilder.setPagedQuery(false);
				}
				break;

			case "DB2":
				// DB2分页
				pagedSql = generateDB2PagingQuery(baseQuery, pageSize, offset);
				queryBuilder.setPagedQuery(false);
				break;

			default:
				// 默认使用框架内置的分页功能
				queryBuilder.setPagedQuery(true);
				queryBuilder.setPageSize(pageSize);
				queryBuilder.setPageIndex(pageIndex);
				break;
		}

		// 如果使用了自定义分页SQL，设置到queryBuilder
		if (!queryBuilder.isPagedQuery()) {
			queryBuilder.setSQL(pagedSql);
			log.debug("生成的分页SQL: {}", pagedSql);
		} else {
			queryBuilder.setSQL(baseQuery);
		}
	}

	/**
	 * 获取主键列名或第一列名，用于ORDER BY
	 */
	private String getPrimaryKeyOrFirstColumn() {
		// 尝试获取主键列
		for (SchemaColumn column : Columns) {
			if (column.isPrimaryKey()) {
				return column.getColumnName();
			}
		}
		// 如果没有主键，返回第一列
		return Columns[0].getColumnName();
	}

	/**
	 * 检查是否是新版SQL Server (2012+)
	 */
	private boolean isNewSqlServerVersion(Connection conn) {
		try {
			DatabaseMetaData metaData = conn.getMetaData();
			int majorVersion = metaData.getDatabaseMajorVersion();
			return majorVersion >= 11;  // SQL Server 2012 是版本 11
		} catch (Exception e) {
			log.warn("检查SQL Server版本失败", e);
			return false;  // 默认使用旧版语法
		}
	}

	/**
	 * 生成达梦数据库的分页查询
	 */
	private String generateDmPagingQuery(String baseQuery, int pageSize, int offset) {
		return "SELECT * FROM (SELECT ROWNUM RN, T.* FROM (" + baseQuery + ") T) " +
				"WHERE RN > " + offset + " AND RN <= " + (offset + pageSize);
	}

	/**
	 * 生成Oracle的分页查询
	 */
	private String generateOraclePagingQuery(String baseQuery, int pageSize, int offset) {
		return "SELECT * FROM (SELECT ROWNUM RN, T.* FROM (" + baseQuery + ") T WHERE ROWNUM <= " +
				(offset + pageSize) + ") WHERE RN > " + offset;
	}

	/**
	 * 生成旧版SQL Server的分页查询
	 */
	private String generateOldSqlServerPagingQuery(String baseQuery, int pageSize, int offset) {
		String orderBy = "";
		if (!baseQuery.toLowerCase().contains("order by")) {
			orderBy = " ORDER BY " + getPrimaryKeyOrFirstColumn();
		}

		return "SELECT * FROM (SELECT ROW_NUMBER() OVER (" +
				(orderBy.isEmpty() ? "ORDER BY (SELECT 0)" : orderBy) +
				") AS RowNum, * FROM (" + baseQuery + ") AS T) AS RowConstrainedResult " +
				"WHERE RowNum > " + offset + " AND RowNum <= " + (offset + pageSize);
	}

	/**
	 * 生成DB2的分页查询
	 */
	private String generateDB2PagingQuery(String baseQuery, int pageSize, int offset) {
		String orderBy = "";
		if (!baseQuery.toLowerCase().contains("order by")) {
			orderBy = " ORDER BY " + getPrimaryKeyOrFirstColumn();
		}

		return "SELECT * FROM (SELECT ROW_NUMBER() OVER (" +
				(orderBy.isEmpty() ? "ORDER BY 1" : orderBy) +
				") AS RowNum, T.* FROM (" + baseQuery + ") AS T) AS RowConstrainedResult " +
				"WHERE RowNum BETWEEN " + (offset + 1) + " AND " + (offset + pageSize);
	}

	/*
	 * 设置需要操作的列
	 */
	public void setOperateColumns(String[] colNames) {
		if ((colNames == null) || (colNames.length == 0)) {
			this.bOperateFlag = false;
			return;
		}
		this.operateColumnOrders = new int[colNames.length];
		int i = 0;
		for (int k = 0; i < colNames.length; i++) {
			boolean flag = false;
			for (int j = 0; j < this.Columns.length; j++) {// 判断给定的列是否存在
				if (colNames[i].toString().toLowerCase().equals(this.Columns[j].getColumnName().toLowerCase())) {
					this.operateColumnOrders[k] = j;
					k++;
					flag = true;
					break;
				}
			}
			if (!flag) {
				throw new RuntimeException("指定的列名" + colNames[i] + "不正确");
			}
		}
		this.bOperateFlag = true;
	}

	public void setOperateColumns(int[] colOrder) {
		if ((colOrder == null) || (colOrder.length == 0)) {
			this.bOperateFlag = false;
			return;
		}
		this.operateColumnOrders = colOrder;
		this.bOperateFlag = true;
	}

	public void setDataAccess(JdbcTemplate dAccess) {
		this.mDataAccess = dAccess;
		this.bConnFlag = 1;
	}

	protected boolean isNull(SchemaColumn sc) {
		return getV(sc.getColumnOrder()) == null;
	}

	public void setValue(DataCollection dc) {
		String value = null;
		String key = null;
		Object[] ks = dc.keyArray().toArray();
		Object[] vs = dc.valueArray().toArray();
		for (int i = 0; i < dc.size(); i++) {
			if ((!(vs[i] instanceof String)) && (vs[i] != null)) {
				continue;
			}
			value = (String) vs[i];
			key = (String) ks[i];
			for (int j = 0; j < this.Columns.length; j++) {
				SchemaColumn sc = this.Columns[j];
				if (!key.equalsIgnoreCase(sc.getColumnName()))
					continue;
				try {
					int type = sc.getColumnType();
					if (type == 0) {
						if ((value == null) || ("".equals(value)))
							break;
						if (DateUtil.isTime(value.toString())) {
							setV(j, DateUtil.parseDateTime(value.toString(), "HH:mm:ss"));
							break;
						}
						setV(j, DateUtil.parseDateTime(value.toString()));
						break;
					}

					if (type == 6) {
						setV(j, Double.valueOf(value.toString()));
						break;
					}
					if (type == 5) {
						setV(j, new Float(value.toString()));
						break;
					}
					if (type == 7) {
						setV(j, new Long(value.toString()));
						break;
					}
					if (type == 8) {
						setV(j, new Integer(value.toString()));
						break;
					}
					setV(j, value);
				} catch (Exception localException) {
				}
			}
		}
	}

	public void setValue(Mapx<?,?> map) {
		Object ks[] = map.keyArray().toArray();
		Object vs[] = map.valueArray().toArray();
		for (int i = 0; i < map.size(); i++) {
			setValue(ks[i],  vs[i]);
		}

	}

	public void setValue(DataRow dr) {
		boolean webMode = dr.getTable().isWebMode();
		dr.getTable().setWebMode(false);
		for (int i = 0; i < dr.getColumnCount(); i++) {
			String value = dr.getString(i);
			String key = dr.getDataColumns()[i].getColumnName();
			setValue(key, value);
		}

		dr.getTable().setWebMode(webMode);
	}

	private void setValue(Object key, Object value) {
		for (int j = 0; j < Columns.length;j++) {
			SchemaColumn sc = Columns[j];

			if (key==null || !sc.getColumnName().equalsIgnoreCase(key.toString()))
				continue;

			try {
				int type = sc.getColumnType();

				setV(j, convertValueByType(type, value));

				break;
			} catch (Exception exception) {
			}
		}
	}

	private Object convertValueByType(int type, Object value) {
		if (value == null) {
			return null;
		}

		if (type == 0 || type == 12) {
			// 处理日期类型
			if (!"".equals(value)) {
				if (value instanceof java.time.LocalDateTime) {
					// 如果已经是LocalDateTime，转换为java.util.Date
					return java.util.Date.from(((java.time.LocalDateTime)value).atZone(java.time.ZoneId.systemDefault()).toInstant());
				} else if (value instanceof java.time.LocalDate) {
					// 如果是LocalDate，转换为java.util.Date
					return java.util.Date.from(((java.time.LocalDate)value).atStartOfDay(java.time.ZoneId.systemDefault()).toInstant());
				} else if (value instanceof java.util.Date) {
					// 已经是Date类型，直接返回
					return value;
				} else {
					// 字符串或其他类型需要解析
					String strValue = value.toString();
					if (DateUtil.isTime(strValue)) {
						java.util.Date parsedDate = DateUtil.parseDateTime(strValue, "HH:mm:ss");
						if (parsedDate == null && strValue.contains("T")) {
							// 尝试处理ISO格式的日期时间字符串
							try {
								java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(strValue);
								return java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
							} catch (Exception e) {
								// 解析失败，回退到原始值
								return value;
							}
						}
						return parsedDate;
					} else {
						java.util.Date parsedDate = DateUtil.parseDateTime(strValue);
						if (parsedDate == null && strValue.contains("T")) {
							// 尝试处理ISO格式的日期时间字符串
							try {
								java.time.LocalDateTime ldt = java.time.LocalDateTime.parse(strValue);
								return java.util.Date.from(ldt.atZone(java.time.ZoneId.systemDefault()).toInstant());
							} catch (Exception e) {
								// 解析失败，回退到原始值
								return value;
							}
						}
						return parsedDate;
					}
				}
			}
		}
		if (type == 6)
			return Double.valueOf(value.toString());
		if (type == 5)
			return new Float(value.toString());
		if (type == 7)
			return new Long(value.toString());
		if (type == 8)
			return new Integer(value.toString());

		return value;
	}


	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("{");
		for (int i = 0; i < this.Columns.length; i++) {
			sb.append(this.Columns[i].getColumnName());
			sb.append(":");
			sb.append(getV(i));
			sb.append(" ");
		}
		sb.append("}");
		return sb.toString();
	}

	public Object clone() {
		Schema s = newInstance();
		SchemaUtil.copyFieldValue(this, s);
		return s;
	}

	public Mapx<String, Object> toMapx() {
		Mapx<String, Object> map = new Mapx<String, Object>();
		for (int i = 0; i < Columns.length; i++)
			map.put(Columns[i].getColumnName(), getV(i));

		return map;
	}

	public Mapx<Object, Object> toMapx(boolean toLowerCase) {
		Mapx<Object, Object> map = new Mapx<Object, Object>();
		for (int i = 0; i < Columns.length; i++) {
			String colName = Columns[i].getColumnName();
			if (toLowerCase)
				colName = colName.toLowerCase();
			map.put(colName, getV(i));
		}

		return map;
	}

	public Mapx<String, Object> toCaseIgnoreMapx() {
		return new CaseIgnoreMapx<String, Object>(toMapx());
	}
	public DataRow toDataRow() {
		int len = this.Columns.length;
		DataColumn[] dcs = new DataColumn[len];
		Object[] values = new Object[len];
		for (int i = 0; i < len; i++) {
			DataColumn dc = new DataColumn();
			dc.setColumnName(this.Columns[i].getColumnName());
			dc.setColumnType(this.Columns[i].getColumnType());
			dcs[i] = dc;
			values[i] = getV(i);
		}
		return new DataRow(new DataTable(dcs), values);
	}

	public int getColumnCount() {
		return this.Columns.length;
	}

	protected Object[] getOldKeys() {
		return this.OldKeys;
	}

	/**
	 * 获取主键值
	 *
	 * @return 对象的主键值
	 */
	public Object getPrimaryKeyValue() {
		SchemaColumn[] primaryKeys = SchemaUtil.getPrimaryKeyColumns(Columns);
		if (primaryKeys.length == 0) {
			return null;
		}

		if (primaryKeys.length == 1) {
			// 单主键情况下直接返回主键值
			return getV(primaryKeys[0].getColumnOrder());
		} else {
			// 多主键情况下返回主键值数组
			Object[] keyValues = new Object[primaryKeys.length];
			for (int i = 0; i < primaryKeys.length; i++) {
				keyValues[i] = getV(primaryKeys[i].getColumnOrder());
			}
			return keyValues;
		}
	}

	public Object getV(String columnName) {
		for (int i = 0; i < Columns.length; i++) {
			if (Columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getV(i);
			}
		}
		return null;
	}

	/**
	 * 获取字段值
	 * 作为getV(String columnName)的别名方法
	 *
	 * @param fieldName 字段名称
	 * @return 字段值
	 */
	public Object getFieldValue(String fieldName) {
		return getV(fieldName);
	}

	/**
	 * 获取所有字段名称列表
	 *
	 * @return 字段名称列表
	 */
	public List<String> getFieldNames() {
		List<String> fieldNames = new ArrayList<>();
		for (SchemaColumn column : Columns) {
			fieldNames.add(column.getColumnName());
		}
		return fieldNames;
	}

	public String getTableCode() {
		return TableCode;
	}

	public void setTableCode(String tableCode) {
		TableCode = tableCode;
	}
	public SchemaColumn[] getColumns() {
		return Columns;
	}

	public void setColumns(SchemaColumn[] columns) {
		Columns = columns;
	}
	public abstract void setV(int paramInt, Object paramObject);

	public abstract Object getV(int paramInt);

	public abstract Schema newInstance();

	public abstract SchemaSet<? extends Schema> newSet();

	public SchemaColumn getColumn(String columnName) {
		for (SchemaColumn column : Columns) {
			if(column.getColumnName().equals(columnName)) {
				return column;
			}
		}
		return null;
	}

	private Session session;

	public Session getSession() {
		if(this.session ==  null) {
			session = SessionFactory.currentSession();
		}
		return session;
	}

	public void setSession(Session session) {
		this.session = session;
	}

	public <T extends Serializable> T querySet(String fieldQuery, String valueQuery, String orderBy, int pageSize, int pageIndex) throws SQLException {
		// 构建基础查询
		StringBuilder sqlBuilder = new StringBuilder();
		sqlBuilder.append("SELECT ").append(fieldQuery);
		sqlBuilder.append(" FROM ").append(this.TableCode);  // 使用TableCode替代getTableName()

		if (valueQuery != null && !valueQuery.isEmpty()) {
			sqlBuilder.append(" WHERE ").append(valueQuery);
		}

		if (orderBy != null && !orderBy.isEmpty()) {
			sqlBuilder.append(" ORDER BY ").append(orderBy);
		}

		String sql = sqlBuilder.toString();

		// 创建查询构建器
		Query queryBuilder = getSession().createQuery(sql);

		// 设置分页参数
		if (pageSize > 0) {
			queryBuilder.setPagedQuery(true);
			queryBuilder.setPageSize(pageSize);
			queryBuilder.setPageIndex(pageIndex);
		}

		// 执行查询并返回结果
		@SuppressWarnings("unchecked")
		T result = (T) querySet(queryBuilder);
		return result;
	}

	/**
	 * 根据主键值加载对象数据
	 *
	 * @param keyValues 主键值，如果是复合主键则需提供多个值
	 * @return 如果找到并加载数据则返回true，否则返回false
	 */
	public boolean loadByPrimaryKey(Object... keyValues) {
		if (keyValues == null || keyValues.length == 0) {
			log.warn("加载对象时未提供主键值");
			return false;
		}

		// 检查主键值数量是否匹配
		if (Columns != null) {
			int pkCount = 0;
			for (SchemaColumn column : Columns) {
				if (column.isPrimaryKey()) {
					pkCount++;
				}
			}
			if (keyValues.length != pkCount) {
				log.info("设置主键值跟主键列长度不匹配...");
				return false;
			}
		}

		// 设置主键值
		setPrimaryKey(keyValues);

		// 使用已有的FillAllSQL
		if (FillAllSQL == null || FillAllSQL.isEmpty()) {
			log.warn("FillAllSQL为空，无法加载记录");
			return false;
		}

		// 获取当前的Session
		Session currentSession = getSession();
		if (currentSession == null) {
			log.warn("Session为null，无法执行查询");
			return false;
		}

		try {
			// 使用Session直接创建Query对象
			Query queryBuilder = currentSession.createQuery(FillAllSQL);

			// 设置参数
			for (Object keyValue : keyValues) {
				queryBuilder.add(keyValue);
			}

			// 使用fill方法模式，与现有代码保持一致
			final Schema schema = this;

			Boolean result = (Boolean) queryBuilder.executeQuery(new ICallbackStatement() {
				@Override
				public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {
					if (rs.next()) {
						// 直接从ResultSet提取数据到当前Schema对象
						for (int i = 0; i < schema.Columns.length; i++) {
							SchemaColumn column = schema.Columns[i];
							try {
								Object value = rs.getObject(column.getColumnName());
								schema.setV(i, value);
							} catch (SQLException e) {
								// 如果列名不匹配，尝试使用索引
								try {
									Object value = rs.getObject(i + 1);
									schema.setV(i, value);
								} catch (Exception ex) {
									log.warn("无法获取列 {} 的值: {}", column.getColumnName(), ex.getMessage());
								}
							}
						}
						return true;
					}
					return false;
				}
			});

			return result != null && result;
		} catch (Exception e) {
			log.error("根据主键加载记录时出错: {}", e.getMessage(), e);
			return false;
		}
	}

	/**
	 * 根据指定时间字段获取最新记录
	 *
	 * @param timeField 用于排序的时间字段名
	 * @param fields 需要获取的字段名列表，如果为null则获取所有字段
	 * @return 包含请求字段的最新记录，如果没有找到则返回null
	 */
	public Schema getLatestByTimeField(String timeField, String[] fields) {
		try {
			// 验证时间字段存在
			boolean timeFieldExists = false;
			for (SchemaColumn column : Columns) {
				if (column.getColumnName().equalsIgnoreCase(timeField)) {
					timeFieldExists = true;
					break;
				}
			}

			if (!timeFieldExists) {
				log.error("表 {} 中不存在时间字段 {}", TableCode, timeField);
				return null;
			}

			// 保存原来的操作列设置
			boolean originalOperateFlag = this.bOperateFlag;
			int[] originalOperateColumnOrders = this.operateColumnOrders;

			try {
				// 如果指定了字段，则设置需要操作的列
				if (fields != null && fields.length > 0) {
					setOperateColumns(fields);
				}

				// 构建查询SQL
				StringBuilder sqlBuilder = new StringBuilder();
				sqlBuilder.append("SELECT ");

				// 构建SELECT部分
				if (fields != null && fields.length > 0) {
					for (int i = 0; i < fields.length; i++) {
						if (i > 0) sqlBuilder.append(", ");
						// 处理可能的关键字字段
						sqlBuilder.append(SqlKeywordEscaper.escapeIfReserved(fields[i]));
					}
				} else {
					sqlBuilder.append("*");
				}

				// 构建FROM和WHERE部分，处理表名
				sqlBuilder.append(" FROM ").append(SqlKeywordEscaper.escapeIfReserved(TableCode));
				sqlBuilder.append(" WHERE 1=1 AND ");
				sqlBuilder.append(SqlKeywordEscaper.escapeIfReserved(timeField));
				sqlBuilder.append(" IS NOT NULL");

				// 构建ORDER BY部分，处理排序字段
				sqlBuilder.append(" ORDER BY ").append(SqlKeywordEscaper.escapeIfReserved(timeField)).append(" DESC");

				// 限制只返回一条记录
				sqlBuilder.append(" LIMIT 1");

				// 创建查询
				Query query = getSession().createQuery(sqlBuilder.toString());

				// 执行查询并获取结果
				DataTable result = query.executeDataTable();

				// 如果结果不为空，将第一行数据映射到Schema
				if (result != null && result.getRowCount() > 0) {
					Schema resultSchema = this.newInstance();

					// 遍历所有列，设置值
					for (int colIdx = 0; colIdx < result.getColumnCount(); colIdx++) {
						String colName = result.getDataColumn(colIdx).getColumnName();
						Object value = result.get(0, colIdx);

						// 查找对应的Schema列并设置值
						for (int schemaColIdx = 0; schemaColIdx < Columns.length; schemaColIdx++) {
							if (Columns[schemaColIdx].getColumnName().equalsIgnoreCase(colName)) {
								resultSchema.setV(schemaColIdx, value);
								break;
							}
						}
					}

					return resultSchema;
				}

				// 如果没有找到记录，返回null
				return null;
			} finally {
				// 恢复原来的操作列设置
				this.bOperateFlag = originalOperateFlag;
				this.operateColumnOrders = originalOperateColumnOrders;
			}
		} catch (Exception e) {
			log.error("执行表 " + this.TableCode + " 的getLatestByTimeField操作时发生错误: " + e.getMessage(), e);
			return null;
		}
	}

}

