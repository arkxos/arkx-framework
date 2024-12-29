package org.ark.framework.orm;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.ark.framework.orm.sql.LobUtil;

import com.arkxos.framework.commons.collection.CaseIgnoreMapx;
import com.arkxos.framework.commons.collection.DataColumn;
import com.arkxos.framework.commons.collection.DataRow;
import com.arkxos.framework.commons.collection.DataTable;
import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.DateUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.data.db.DataCollection;
import com.arkxos.framework.data.db.connection.Connection;
import com.arkxos.framework.data.db.connection.ConnectionPoolManager;
import com.arkxos.framework.data.jdbc.ICallbackStatement;
import com.arkxos.framework.data.jdbc.JdbcTemplate;
import com.arkxos.framework.data.jdbc.Query;
import com.arkxos.framework.data.jdbc.Session;
import com.arkxos.framework.data.jdbc.SessionFactory;

import lombok.extern.slf4j.Slf4j;


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
		for (int i = 0; i < Columns.length; ++i) {
			SchemaColumn sc = Columns[i];
			if ((sc.isMandatory()) && (getV(i) == null)) {
				throw new SQLException("表" + TableCode + "的列" + sc.getColumnName() + "不能为空");
			}

			sqlBuilder.add(getV(i));
		}
	}
	
	public boolean insert() {
		try {
			Query queryBuilder = getSession().createQuery(InsertAllSQL);
			setInsertParams(queryBuilder);

			return queryBuilder.executeNoQuery() != -1;
		} catch (SQLException e) {
			log.warn("操作表" + this.TableCode + "时发生错误:" + e.getMessage());
		}
		return false;
	}
	
	void setUpdataParams(Query sqlBuilder) {
		if (bOperateFlag) {
			for (int i = 0; i < operateColumnOrders.length; ++i) {
				sqlBuilder.add(getV(operateColumnOrders[i]));
			}
		} else {
			for (int i = 0; i < Columns.length; ++i) {
				sqlBuilder.add(getV(i));
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
	 * @tag category
	 * name = "OP"
	 * color = "Blue"
	 */
	public boolean delete() {
		Query queryBuilder = getSession().createQuery(this.DeleteSQL);
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
			for (int i = 0; i < this.operateColumnOrders.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(this.Columns[this.operateColumnOrders[i]].getColumnName());
				sb.append("=?");
			}
			sb.append(sql.substring(sql.indexOf(" where")));
			sql = sb.toString();
		}
		return sql;
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

	
	public SchemaSet<? extends Schema> querySet(Query qb) {

		try {

			final Schema schema = this;
			return (SchemaSet) qb.executeQuery(new ICallbackStatement() {

				public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {

					SchemaSet set = schema.newSet();
					while (rs.next()) {

						Schema newSchema = schema.newInstance();
						int i;
						if (schema.bOperateFlag) {
							for (i = 0; i < schema.operateColumnOrders.length; ++i) {
								if (schema.Columns[schema.operateColumnOrders[i]].getColumnType() == 10) {
									if (connection.getDBConfig().isOracle() 
											|| connection.getDBConfig().isDB2())
										newSchema.setV(schema.operateColumnOrders[i], LobUtil.clobToString(rs.getClob(i + 1)));
									else
										newSchema.setV(schema.operateColumnOrders[i], rs.getObject(i + 1));
								} else if (schema.Columns[schema.operateColumnOrders[i]].getColumnType() == 2)
									newSchema.setV(schema.operateColumnOrders[i], LobUtil.blobToBytes(rs.getBlob(i + 1)));
								else
									newSchema.setV(schema.operateColumnOrders[i], rs.getObject(i + 1));
							}
						} else {
							for (i = 0; i < schema.Columns.length; ++i) {
								if (schema.Columns[i].getColumnType() == 10) {
									if (connection.getDBConfig().isOracle() 
											|| connection.getDBConfig().isDB2())
										newSchema.setV(i, LobUtil.clobToString(rs.getClob(i + 1)));
									else
										newSchema.setV(i, rs.getObject(i + 1));
								} else if (schema.Columns[i].getColumnType() == 2)
									newSchema.setV(i, LobUtil.blobToBytes(rs.getBlob(i + 1)));
								else {
									newSchema.setV(i, rs.getObject(i + 1));
								}
							}
						}
						set.add(newSchema);
					}
					set.setOperateColumns(schema.operateColumnOrders);
					return set;
				}
			});

			// if ((pageSize > 0) && (!(conn.getDBType().equals("MSSQL2000"))))
			// {
			// qb.getParams().remove(qb.getParams().size() - 1);
			// qb.getParams().remove(qb.getParams().size() - 1);
			// }

		} catch (Exception e) {
			log.error("操作表" + TableCode + "时发生错误!");
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

		StringBuffer sb = new StringBuffer("select ");

		int i;
		if (bOperateFlag) {
			for (i = 0; i < operateColumnOrders.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Columns[operateColumnOrders[i]].getColumnName());
			}
		} else if (ConnectionPoolManager.getDBConnConfig().isSQLServer()) {
			for (i = 0; i < Columns.length; ++i) {
				if (i != 0) {
					sb.append(",");
				}
				sb.append(Columns[i].getColumnName());
			}
		} else
			sb.append("*");

		sb.append(" from " + TableCode);

		sb.append(" ");
		sb.append(queryBuilder.getSQL());

		queryBuilder.setSQL(sb.toString());

		queryBuilder.setPagedQuery(true);
		if (pageSize == -1) {
			pageSize = Integer.MAX_VALUE;
		}
		if (pageIndex == -1) {
			pageIndex = 0;
		}
		queryBuilder.setPageSize(pageSize);
		queryBuilder.setPageIndex(pageIndex);
		return querySet(queryBuilder);
	}
	
	private void setVAll(Connection conn, Schema schema, ResultSet rs) throws Exception {
		ArrayList pklist = new ArrayList(4);
		boolean latin1Flag = (conn.getDBConfig().isLatin1Charset()) && 
		(conn.getDBConfig().isOracle());
		if (this.bOperateFlag) {
			for (int i = 0; i < this.operateColumnOrders.length; i++) {
				int order = this.operateColumnOrders[i];
				if (this.Columns[order].getColumnType() == 10) {
					if ((conn.getDBConfig().isOracle()) 
							|| (conn.getDBConfig().isDB2())) {
						schema.setV(order, LobUtil.clobToString(rs.getClob(i + 1)));
					} else if (conn.getDBConfig().isSybase()) {
						String str = rs.getString(i + 1);
						if (" ".equals(str)) {
							str = "";
						}
						schema.setV(order, str);
					} else {
						schema.setV(order, rs.getObject(i + 1));
					}
				} else if (this.Columns[order].getColumnType() == 2)
					schema.setV(order, LobUtil.blobToBytes(rs.getBlob(i + 1)));
				else {
					schema.setV(order, rs.getObject(i + 1));
				}
				if ((this.Columns[order].getColumnType() == 10) || (this.Columns[order].getColumnType() == 1)) {
					String str = (String) schema.getV(order);
					if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
						try {
							str = new String(str.getBytes("ISO-8859-1"), OrmConstant.GlobalCharset);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					schema.setV(order, str);
				}
				if (this.Columns[order].isPrimaryKey())
					pklist.add(schema.getV(order));
			}
		} else {
			for (int i = 0; i < this.Columns.length; i++) {
				if (this.Columns[i].getColumnType() == 10) {
					if ((conn.getDBConfig().isOracle()) 
							|| (conn.getDBConfig().isDB2())) {
						schema.setV(i, LobUtil.clobToString(rs.getClob(i + 1)));
					} else if (conn.getDBConfig().isSybase()) {
						String str = rs.getString(i + 1);
						if (" ".equals(str)) {
							str = "";
						}
						schema.setV(i, str);
					} else {
						schema.setV(i, rs.getObject(i + 1));
					}
				} else if (this.Columns[i].getColumnType() == 2)
					schema.setV(i, LobUtil.blobToBytes(rs.getBlob(i + 1)));
				else {
					schema.setV(i, rs.getObject(i + 1));
				}
				if ((this.Columns[i].getColumnType() == 10) || (this.Columns[i].getColumnType() == 1)) {
					String str = (String) schema.getV(i);
					if ((latin1Flag) && (StringUtil.isNotEmpty(str))) {
						try {
							str = new String(str.getBytes("ISO-8859-1"), OrmConstant.GlobalCharset);
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
					}
					schema.setV(i, str);
				}
				if (this.Columns[i].isPrimaryKey()) {
					pklist.add(schema.getV(i));
				}
			}
		}
		schema.OldKeys = pklist.toArray();
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
						setV(j, new Double(value.toString()));
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

		if (type == 0) {
			if (value != null && !"".equals(value))
				if (DateUtil.isTime(value.toString()))
					return DateUtil.parseDateTime(value.toString(), "HH:mm:ss");
				else
					return DateUtil.parseDateTime(value.toString());
		}
		if (type == 6)
			return new Double(value.toString());
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

	public Object getV(String columnName) {
		for (int i = 0; i < Columns.length; i++) {
			if (Columns[i].getColumnName().equalsIgnoreCase(columnName)) {
				return getV(i);
			}
		}
		return null;
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
}

