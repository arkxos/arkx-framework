package org.ark.framework.orm;

import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.jdbc.ICallbackStatement;
import io.arkx.framework.data.jdbc.Query;
import lombok.extern.slf4j.Slf4j;
import org.apache.tools.zip.ZipFile;
import org.ark.framework.orm.sql.LobUtil;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;


/**   
 * @class org.ark.framework.orm.SchemaUtil
 * @author Darkness
 * @date 2012-3-8 下午2:00:42 
 * @version V1.0   
 */
@Slf4j
public class SchemaUtil {
	private static long BackupNoBase = System.currentTimeMillis();

//	private static Logger logger = Logger.getLogger(SchemaUtil.class);
	
	public static SchemaSet querySet(final Schema schema, Query qb) {

		try {

			// String pageSQL = qb.getSQL();
			// if (pageSize > 0) {
			// pageSQL = DataAccess.getPagedSQL(conn.getDBType().toString(),
			// qb, pageSize, pageIndex);
			// }

			// if ((pageSize > 0) && (!conn.getDBConfig().isSQLServer2000())) {
			// qb.getParams().remove(qb.getParams().size() - 1);
			// qb.getParams().remove(qb.getParams().size() - 1);
			// }
			return (SchemaSet) qb.executeQuery(new ICallbackStatement() {

				public Object execute(Connection connection, PreparedStatement stmt, ResultSet rs) throws SQLException {

					// SchemaSet set = newSet();
					// while (rs.next()) {
					// Schema schema = newInstance();
					// setVAll(conn, schema, rs);
					// set.add(schema);
					// }
					// set.setOperateColumns(this.operateColumnOrders);
					// SchemaSet localSchemaSet1 = set;
					// return localSchemaSet1;
					//
					SchemaSet set = schema.newSet();
					while (rs.next()) {

						Schema newSchema = schema.newInstance();
						int i;
						if (schema.bOperateFlag) {
							for (i = 0; i < schema.operateColumnOrders.length; ++i) {
								if (schema.Columns[schema.operateColumnOrders[i]].getColumnType() == 10) {
									if ((ConnectionPoolManager.getConnection().getDBConfig().isOracle()) 
											|| (ConnectionPoolManager.getConnection().getDBConfig().isDB2()))
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
									if ((ConnectionPoolManager.getConnection().getDBConfig().isOracle()) 
											|| (ConnectionPoolManager.getConnection().getDBConfig().isDB2()))
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
			log.error("操作表" + schema.TableCode + "时发生错误!");
			e.printStackTrace();
			return null;
		} finally {
		}
	}
	
	public static boolean deleteByCondition(Schema conditionSchema) {
		return deleteByCondition(conditionSchema, 0);
	}
	
	public static boolean deleteByCondition(Schema conditionSchema, int bConnFlag) {
		SchemaColumn[] columns = conditionSchema.Columns;
		boolean firstFlag = true;
		StringBuffer sb = new StringBuffer(128);
		sb.append("delete from " + conditionSchema.TableCode);
		for (int i = 0; i < columns.length; ++i) {
			SchemaColumn sc = columns[i];
			if (conditionSchema.getV(sc.getColumnOrder()) != null) {
				if (firstFlag) {
					sb.append(" where ");
					sb.append(sc.getColumnName());
					sb.append("=?");
					firstFlag = false;
				} else {
					sb.append(" and ");
					sb.append(sc.getColumnName());
					sb.append("=?");
				}
			}
		}
		Connection conn = ConnectionPoolManager.getConnection();
		PreparedStatement pstmt = null;
		try {
			pstmt = conn.prepareStatement(sb.toString(), 1003, 1007);
			int i = 0;
			for (int j = 0; i < columns.length; ++i) {
				SchemaColumn sc = columns[i];
				Object v = conditionSchema.getV(sc.getColumnOrder());
				if (v != null) {
					if (sc.getColumnType() == 0)
						pstmt.setDate(j + 1, new java.sql.Date(((java.util.Date) v).getTime()));
					else {
						pstmt.setObject(j + 1, v);
					}
					++j;
				}
			}
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		} finally {
			if (pstmt != null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
				pstmt = null;
			}
			if (bConnFlag == 0) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
	public static boolean copyFieldValue(Schema srcSchema, Schema destSchema) {
		try {
			SchemaColumn[] srcSC = srcSchema.Columns;
			SchemaColumn[] destSC = destSchema.Columns;
			for (int i = 0; i < srcSC.length; i++)
				for (int j = 0; j < destSC.length; j++)
					if (srcSC[i].getColumnName().equals(destSC[j].getColumnName())) {
						int order = destSC[j].getColumnOrder();
						Object v = srcSchema.getV(srcSC[i].getColumnOrder());
						if (v == null) {
							destSchema.setV(order, null);
							break;
						}
						if ((v instanceof java.util.Date)) {
							destSchema.setV(order, ((java.util.Date) v).clone());
							break;
						}
						if ((v instanceof Double)) {
							destSchema.setV(order, Double.valueOf(((Double) v).doubleValue()));
							break;
						}
						if ((v instanceof Float)) {
							destSchema.setV(order, Float.valueOf(((Float) v).floatValue()));
							break;
						}
						if ((v instanceof Integer)) {
							destSchema.setV(order, Integer.valueOf(((Integer) v).intValue()));
							break;
						}
						if ((v instanceof Long)) {
							destSchema.setV(order, Long.valueOf(((Long) v).longValue()));
							break;
						}
						if ((v instanceof byte[])) {
							destSchema.setV(order, ((byte[]) v).clone());
							break;
						}
						if (!(v instanceof String))
							break;
						destSchema.setV(order, v);

						break;
					}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public static Schema getZSchemaFromBSchema(Schema bSchema) {
		String TableCode = bSchema.TableCode;
		if (!TableCode.startsWith("BZ"))
			throw new RuntimeException("必须传入B表的Schema");
		try {
			Class c = Class.forName(bSchema.NameSpace + "." + TableCode.substring(1) + "Schema");
			Schema schema = (Schema) c.newInstance();
			for (int i = 0; i < schema.Columns.length; i++) {
				schema.setV(i, bSchema.getV(i));
			}
			return schema;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static SchemaSet getZSetFromBSet(SchemaSet bset) {
		String TableCode = bset.getSchema().TableCode;
		if (!TableCode.startsWith("BZ"))
			throw new RuntimeException("必须传入B表的Set");
		try {
			bset.sort("BackupNo", "asc");

			ArrayList list = new ArrayList();
			for (int i = 0; i < bset.Columns.length; i++) {
				if ((bset.Columns[i].isPrimaryKey()) && (!bset.Columns[i].getColumnName().equalsIgnoreCase("BackupNo"))) {
					list.add(Integer.valueOf(i));
				}
			}
			int[] keys = new int[list.size()];
			for (int i = 0; i < list.size(); i++) {
				keys[i] = ((Integer) list.get(i)).intValue();
			}
			for (int i = 0; i < bset.size(); i++) {
				Object[] ks = new Object[keys.length];
				for (int j = 0; j < ks.length; j++) {
					ks[j] = bset.getObject(i).getV(j);
				}
				for (int j = i + 1; j < bset.size();) {
					boolean flag = true;
					for (int k = 0; k < keys.length; k++) {
						if (!bset.getObject(j).getV(keys[k]).equals(ks[k])) {
							flag = false;
							break;
						}
					}
					if (flag)
						bset.removeRange(j, 1);
					else {
						j++;
					}
				}
			}
			Class c = Class.forName(bset.getSchema().NameSpace + "." + TableCode.substring(1) + "Set");
			Class schemaClass = Class.forName(bset.getSchema().NameSpace + "." + TableCode.substring(1) + "Schema");
			SchemaSet set = (SchemaSet) c.newInstance();
			for (int j = 0; j < bset.size(); j++) {
				Schema schema = (Schema) schemaClass.newInstance();
				Schema bSchema = bset.getObject(j);
				for (int i = 0; i < schema.Columns.length; i++) {
					schema.setV(i, bSchema.getV(i));
				}
				set.add(schema);
			}
			return set;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static synchronized String getBackupNo() {
		return String.valueOf(BackupNoBase++).substring(1);
	}
	public static boolean delete(Schema conditionSchema) {
		return deleteByCondition(conditionSchema, 1);
	}
	public static void setParam(SchemaColumn sc, PreparedStatement pstmt, Connection conn, int i, Object v) throws SQLException {
		if (v == null) {
			if (sc.getColumnType() == 7)
				pstmt.setNull(i + 1, -5);
			else if (sc.getColumnType() == 8)
				pstmt.setNull(i + 1, 4);
			else if (sc.getColumnType() == 10) {
				if (conn.getDBConfig().isSybase())
					DBTypeService.getInstance().get(conn.getDBConfig().getDatabaseType()).setClob(conn, pstmt, i + 1, "");
				else
					pstmt.setNull(i + 1, 2005);
			} else if (sc.getColumnType() == 6)
				pstmt.setNull(i + 1, 8);
			else if (sc.getColumnType() == 5)
				pstmt.setNull(i + 1, 6);
			else if (sc.getColumnType() == 4)
				pstmt.setNull(i + 1, 3);
			else if (sc.getColumnType() == 0)
				pstmt.setNull(i + 1, 91);
			else if (sc.getColumnType() == 11)
				pstmt.setNull(i + 1, -7);
			else if (sc.getColumnType() == 9)
				pstmt.setNull(i + 1, 5);
			else {
				pstmt.setNull(i + 1, 12);
			}
		} else if (sc.getColumnType() == 0) {
			pstmt.setTimestamp(i + 1, new Timestamp(((java.util.Date) v).getTime()));
		} else if (sc.getColumnType() == 10) {
			String str = (String) v;
			if ((conn.getDBConfig().isLatin1Charset()) 
					&& (conn.getDBConfig().isOracle())) {
				try {
					str = new String(str.getBytes(OrmConstant.GlobalCharset), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			DBTypeService.getInstance().get(conn.getDBConfig().getDatabaseType()).setClob(conn, pstmt, i + 1, str);
		} else if (sc.getColumnType() == 2) {
			DBTypeService.getInstance().get(conn.getDBConfig().getDatabaseType()).setBlob(conn, pstmt, i + 1, (byte[]) v);
		} else if (sc.getColumnType() == 1) {
			String str = (String) v;
			if ((conn.getDBConfig().isLatin1Charset()) && (conn.getDBConfig().isOracle())) {
				try {
					str = new String(str.getBytes(OrmConstant.GlobalCharset), "ISO-8859-1");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			}
			if ((conn.getDBConfig().isSybase()) && (str.equals("")))
				pstmt.setNull(i + 1, 12);
			else
				pstmt.setString(i + 1, str);
		} else {
			pstmt.setObject(i + 1, v);
		}
	}

	public static String[] getAllSchemaClassName() {
		Class<?> c = null;
		ArrayList<String> list = new ArrayList<String>();
		try {
			c = Class.forName("com.arkxos.schema.ZDCodeSchema");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		String path = c.getResource("ZDCodeSchema.class").getPath();
		if (System.getProperty("os.name").toLowerCase().indexOf("windows") >= 0) {
			if (path.startsWith("/"))
				path = path.substring(1);
			else if (path.startsWith("file:/")) {
				path = path.substring(6);
			}
		} else if (path.startsWith("file:/")) {
			path = path.substring(5);
		}

		path = path.replaceAll("%20", " ");
		if (path.toLowerCase().indexOf(".jar!") > 0) {// 获取jar包中的schema
			try {
				path = path.substring(0, path.indexOf(".jar!") + ".jar!".length());
				ZipFile z = new ZipFile(path);
				Enumeration<?> all = z.getEntries();
				while (all.hasMoreElements()) {
					String name = all.nextElement().toString();
					if (name.startsWith("com.xdarkness.schema."))
						list.add(name);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {// 获取文件夹中的schema
			File p = new File(path.substring(0, path.toLowerCase().indexOf("zdcodeschema.class")));
			File[] fs = p.listFiles();
			for (int i = 0; i < fs.length; i++) {
				if (fs[i].getName().endsWith("Schema.class")) {
					list.add("com/arkxos/schema/" + fs[i].getName());
				}
			}
		}

		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String name = (String) list.get(i);
			name = name.replaceAll("\\/", ".");
			name = name.substring(0, name.length() - ".class".length());
			arr[i] = name;
		}
		return arr;
	}

	public static Schema findSchema(String tableName) {
		String[] arr = getAllSchemaClassName();
		for (int i = 0; i < arr.length; i++) {
			String name = arr[i].toLowerCase();
			if (!name.endsWith("." + tableName.toLowerCase() + "schema"))
				continue;
			try {
				return (Schema) Class.forName(arr[i]).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}

		return null;
	}

	public static SchemaColumn findColumn(String tableName, String columnName) {
		Schema schema = findSchema(tableName);
		return findColumn(schema.Columns, columnName);
	}

	public static SchemaColumn findColumn(SchemaColumn[] scs, String columnName) {
		for (int i = 0; i < scs.length; i++) {
			if (scs[i].getColumnName().equalsIgnoreCase(columnName)) {
				return scs[i];
			}
		}
		return null;
	}

	
	
	public static String[] getPrimaryKeyColumnNames(SchemaColumn[] scs) {
		SchemaColumn[] columns = getPrimaryKeyColumns(scs);
		String[] names = new String[columns.length];
		for (int i = 0; i < columns.length; i++) {
			names[i] = columns[i].getColumnName();
		}
		return names;
	}
	

	/**
	 * 获取SchemaColumn中的主键列
	 * @author Darkness
	 * @date 2011-12-15 上午09:30:21 
	 * @version V1.0  
	 * @param scs
	 * @return
	 */
	public static SchemaColumn[] getPrimaryKeyColumns(SchemaColumn[] scs) {
		return getPrimaryKeyColumnsList(scs).toArray(new SchemaColumn[]{});
	}
	
	public static List<SchemaColumn> getPrimaryKeyColumnsList(SchemaColumn[] scs) {
		ArrayList<SchemaColumn> list = new ArrayList<SchemaColumn>();
		for (int i = 0; i < scs.length; i++) {
			if (scs[i].isPrimaryKey()) {
				list.add(scs[i]);
			}
		}
		return list;
	}
	public static String getTableCode(Schema schema) {
		return schema.TableCode;
	}

	public static String getNameSpace(Schema schema) {
		return schema.NameSpace;
	}

	public static SchemaColumn[] getColumns(Schema schema) {
		return schema.Columns;
	}

	public static String getTableCode(SchemaSet set) {
		return set.getSchema().TableCode;
	}

	public static String getNameSpace(SchemaSet set) {
		return set.getSchema().NameSpace;
	}

	public static SchemaColumn[] getColumns(SchemaSet set) {
		return set.Columns;
	}
}

