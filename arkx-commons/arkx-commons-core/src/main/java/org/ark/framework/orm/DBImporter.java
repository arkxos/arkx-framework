package org.ark.framework.orm;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.ark.framework.messages.LongTimeTask;

import com.alibaba.fastjson.JSON;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.BufferedRandomAccessFile;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.ZipUtil;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.db.dbtype.DBTypeService;
import io.arkx.framework.data.db.dbtype.IDBType;
import io.arkx.framework.data.jdbc.JdbcTemplate;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;

import lombok.extern.slf4j.Slf4j;


/**
 * @class org.ark.framework.orm.DBImporter
 * 
 * @author Darkness
 * @date 2013-1-31 上午11:46:50 
 * @version V1.0
 */
@Slf4j
public class DBImporter {
	
//	private DataAccess da;
//	private LongTimeTask task;
//
//	public void setTask(LongTimeTask task) {
//		this.task = task;
//	}
//
//	public String getSQL(String file, DatabaseType dbtype) {
//		TableCreator tc = new TableCreator(dbtype);
//		try {
//			ZDTParser parser = new ZDTParser(file);
//			parser.parse();
//			for (ZDTParser.ZDTTableInfo ti : parser.getTables()) {
//				tc.createTable(ti.Columns, ti.Name);
//				tc.createIndexes(ti.Name, ti.IndexInfo);
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//		}
//		return tc.getAllSQL();
//	}
//
//	public void importDB(String file) {
//		importDB(file, "", null);
//	}
//	
//	public void importDB(String file, String poolName) {
//		importDB(file, poolName, null);
//	}
//
//	public void importDB(String file, ArrayList<String> tableList) {
//		importDB(file, "", tableList);
//	}
//
//	public boolean importDB(String file, String poolName, ArrayList<String> tableList) {
//		XConnection conn = XConnectionPoolManager.getConnection(poolName);
//		try {
//			boolean bool = importDB(file, conn, true, tableList);
//			return bool;
//		} finally {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
//		}
//	}
//
//	public boolean importDB(String file, XConnection conn, boolean autoCreate, ArrayList<String> tableList) {
//		this.da = new DataAccess(conn);
//		try {
//			ZDTParser parser = new ZDTParser(file);
//			parser.parse();
//			Mapx map = new Mapx();
//			TableCreator tc = new TableCreator(this.da.getConnection().getDBConfig().getDBType());
//			int j = 0;
//			for (ZDTParser.ZDTTableInfo ti : parser.getTables()) {
//				if ((tableList != null) && (!tableList.contains(ti.Name))) {
//					continue;
//				}
//				if (!map.containsKey(ti.Name)) {
//					tc.createTable(ti.Columns, ti.Name, autoCreate);
//					tc.executeAndClear(conn);
//					map.put(ti.Name, "");
//				}
//				j++;
//				this.task.setPercent(Double.valueOf(j * 100.0D / parser.getTables().size()).intValue());
//				for (int i = 0; i < ti.Positions.size();) {
//					DataTable dt = parser.getDataTable(ti, i * 500, (i + 1) * 500);
//					try {
//						if (this.task != null) {
//							this.task.setCurrentInfo("Importing table " + ti.Name);
//						}
//						if (!importDataTable(ti.Columns, dt, ti.Name))
//							return false;
//					} catch (Exception e) {
//						LogUtil.warn("Import table failed:" + ti.Name);
//						e.printStackTrace();
//
//						i++;
//					}
//
//				}
//
//				if ((ObjectUtil.notEmpty(ti.IndexInfo)) && (!ti.IndexInfo.trim().equals(""))) {
//					tc.createIndexes(ti.Name, ti.IndexInfo);
//					tc.executeAndClear(conn);
//				}
//			}
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			return false;
//		}
//		return true;
//	}
//
//	private boolean importDataTable(SchemaColumn[] scs, DataTable dt, String name) throws Exception {
//		QueryBuilder qb = getSession().createQueryBuilder("insert into " + name + " values (", new Object[0]);
//		qb.setBatchMode(true);
//		for (int i = 0; i < scs.length; i++) {
//			if (i != 0) {
//				qb.append(",", new Object[0]);
//			}
//			qb.append("?", new Object[0]);
//		}
//		qb.append(")", new Object[0]);
//		int j;
//		if (this.da.getConnection().getDBConfig().isOracle()) {
//			for (int i = 0; i < dt.getRowCount(); i++) {
//				for (j = 0; j < scs.length; j++) {
//					Object v = dt.get(i, j);
//					if ((scs[j].isMandatory()) && ((v == null) || (v.equals("")))) {
//						LogUtil.warn(name + "'s column " + scs[j].getColumnName() + " can't be empty");
//						dt.deleteRow(i);
//						i--;
//						break;
//					}
//				}
//			}
//		}
//		for (DataRow dr : dt) {
//			for (int i = 0; i < scs.length; i++) {
//				qb.add(dr.get(i));
//			}
//			qb.addBatch();
//		}
//		qb.executeNoQuery();
//		return true;
//	}
	
//private static Logger logger = log.getLogger(DBImporter.class);
	
	private JdbcTemplate da;
	private LongTimeTask task;

	public void setTask(LongTimeTask task) {
		this.task = task;
	}

	public void importDB(String file) {
		importDB(file, "");
	}

	public String getSQL(String file, String dbtype) {
		TableCreator tc = new TableCreator(DBTypeService.getInstance().get(dbtype));
		BufferedRandomAccessFile braf = null;
		try {
			braf = new BufferedRandomAccessFile(file, "r");
			HashMap<String, String> map = new HashMap<String, String>();
			while (braf.getFilePointer() != braf.length()) {
				byte[] bs = new byte[4];
				braf.read(bs);
				int len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);

				bs = new byte[4];
				braf.read(bs);
				len = NumberUtil.toInt(bs);
				bs = new byte[len];
				braf.read(bs);
				bs = ZipUtil.unzip(bs);
				Object obj = FileUtil.unserialize(bs);
				if (obj == null) {
					continue;
				}
				if ((obj instanceof SchemaSet)) {
					SchemaSet<? extends Schema> set = (SchemaSet<? extends Schema>) obj;
					if ((set != null) && (!map.containsKey(set.getSchema().getTableCode()))) {
						tc.createTable(set.getSchema().getColumns(), set.getSchema().TableComment, set.getSchema().getTableCode());
						map.put(set.getSchema().getTableCode(), "");
					}
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();

			if (braf != null)
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		} finally {
			if (braf != null) {
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return tc.getAllSQL();
	}

	public boolean importDB(String file, String poolName) {
		
//		Connection conn = ConnectionPoolManager.getConnection(poolName);
		try {
			boolean bool = importDB(file, poolName, true);
			return bool;
		} finally {
//			try {
//				conn.close();
//			} catch (SQLException e) {
//				e.printStackTrace();
//			}
		}
	}

	@SuppressWarnings("rawtypes")
	public boolean importDB(String file,String poolName,  boolean autoCreate) {
		BufferedRandomAccessFile braf = null;
		try {
			braf = new BufferedRandomAccessFile(file, "r");
			HashMap<String, String> map = new HashMap<>();
			map.put("BidProduct", "");
			
			int i = 0;
			
			
			String databaseTypeString = ConnectionPoolManager.getDBConnConfig(poolName).getDatabaseType();
			IDBType databaseType = DBTypeService.getInstance().get(databaseTypeString);
			TableCreator tc = new TableCreator(databaseType);
			int total = 0;
			long filePointer = 0;
			long fileLength = braf.length();
			int pageIndex = 1;
			while ((filePointer = braf.getFilePointer()) != fileLength) {
				byte[] bs = new byte[4];
				// read schemaName's length
				braf.read(bs);
				int len = NumberUtil.toInt(bs);
				bs = new byte[len];
				// read schemaName
				braf.read(bs);
				String currentImportTableName = new String(bs);

				bs = new byte[4];
				// read SchemaSet's length
				braf.read(bs);
				int dataLen = NumberUtil.toInt(bs);
				bs = new byte[dataLen];
				// read SchemaSet
				braf.read(bs);
				
				int currentPageByteLength = 4 + len + 4 + dataLen;
				System.out.println("filePointer: " + filePointer + ", fileLength: " + fileLength);
				System.out.println("第" + (pageIndex++) + "页，数据长度：" + currentPageByteLength);
				
				// unzip SchemaSet
				bs = ZipUtil.unzip(bs);
				try {
					// unserialize SchemaSet
					Object obj = FileUtil.unserialize(bs);
					if (obj == null)
						continue; /* Loop/switch isn't completed */
					
					if ((obj instanceof SchemaSet)) {
						SchemaSet set = (SchemaSet) obj;
						total += set.size();
						System.out.println("total: " + total);
						try {
							if (!map.containsKey(set.getSchema().getTableCode())) {
								Session session = SessionFactory.openSessionInThread(poolName);
								session.beginTransaction();
								
								tc.createTable(set.getSchema().getColumns(), set.getSchema().TableComment,
										set.getSchema().getTableCode(), autoCreate);
								
								tc.writeSqlToFile("d:/arkxos-oracle.sql");
								try {
									tc.executeAndClear(session);
									map.put(set.getSchema().getTableCode(), "");
									
									session.commit();
									SessionFactory.clearCurrentSession();
									
								} catch (Exception e) {
									session.rollback();
									session.close();
									SessionFactory.clearCurrentSession();
									e.printStackTrace();
								}
								
							}
							if (this.task != null) {
								this.task.setPercent(Double.valueOf(i++ * 100.0D / 600.0D).intValue());
								this.task.setCurrentInfo("正在导入表" + set.getSchema().getTableCode());
							}
							importOneSet(set, poolName);
						} catch (Exception e) {
							log.warn("未成功导入表" + set.getSchema().getTableCode());
							e.printStackTrace();
						}
					}
					if (!(obj instanceof DataTable))
						continue;
					try {
						Query insertQB = null;
						if (false) {//!map.containsKey(currentImportTableName)) {
							DataTable cdt = getSession().createQuery("select * from ZCCustomTableColumn where TableID in (select ID from ZCCustomTable where Code=? and Type='Custom')",
									currentImportTableName).executeDataTable();
							SchemaColumn[] scs = new SchemaColumn[cdt
									.getRowCount()];
							for (int j = 0; j < scs.length; j++) {
								DataRow cdr = cdt.getDataRow(j);
								int type = Integer.parseInt(cdr.getString("DataType"));
								SchemaColumn sc = new SchemaColumn(
										cdr.getString("Code"), 
										type, 
										j, 
										cdr.getInt("Length"), 
										0, 
										"Y".equals(cdr.getString("isMandatory")), 
										"Y".equals(cdr.getString("isPrimaryKey")), "");
								scs[j] = sc;
							}
							tc.createTable(scs, "", currentImportTableName, autoCreate);
							tc.executeAndClear();
							map.put(currentImportTableName, "");
							StringBuffer sb = new StringBuffer("insert into "
									+ currentImportTableName + "(");
							for (int j = 0; j < cdt.getRowCount(); j++) {
								if (j != 0) {
									sb.append(",");
								}
								sb.append(cdt.get(j, "Code"));
							}
							sb.append(") values (");
							for (int j = 0; j < cdt.getRowCount(); j++) {
								if (j != 0) {
									sb.append(",");
								}
								sb.append("?");
							}
							sb.append(")");
							insertQB = getSession().createQuery(sb.toString());
							insertQB.setBatchMode(true);
							
							importOneTable(currentImportTableName, (DataTable) obj, insertQB);
						}
						if (this.task != null) {
							this.task.setPercent(Double.valueOf(
									i++ * 100.0D / 600.0D).intValue());
							this.task.setCurrentInfo("正在导入表" + currentImportTableName);
						}
						
					} catch (Exception e) {
						log.warn("未成功导入表" + currentImportTableName);
						e.printStackTrace();
					}
				} catch (Exception e) {
//					private static final long serialVersionUID = -4218492595750785904L;
					log.warn("导入数据时发生错误:" + e.getMessage());
					
					//ResumeSerialVersion.resume(e.getMessage());
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			return false;
		} finally {
			if (braf != null)
				try {
					braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}

		return true;
	}


	private boolean importOneSet(SchemaSet set, String poolName) throws Exception {
		Session session = SessionFactory.openSessionInThread(poolName);
		session.beginTransaction();
		
		if (session.getConnection().getDBConfig().isOracle()) {
			for (int i = 0; i < set.size(); i++) {
				Schema schema = set.getObject(i);
				for (int j = 0; j < schema.getColumns().length; j++) {
					Object v = schema.getV(j);
					if ((schema.getColumns()[j].isMandatory())
							&& ((v == null) || (v.equals("")))) {
						log.warn("表" + schema.getTableCode() + "的"
								+ schema.getColumns()[j].getColumnName() + "列不能为空!");
//						System.out.println("表" + schema.getTableCode() + "的"
//								+ schema.getColumns()[j].getColumnName() + "列不能为空!");
//						set.remove(schema);
//						i--;
//						break;
					}
				}
//				System.out.println((Double.valueOf(i * 100.0D / set.size()).intValue()) + ", " + i + "/" + set.size());
			}
		}
		
		set.setSession(session);
		boolean success = false;
		try {
			success = set.insert();
			
			session.commit();
		} catch (Exception e) {
			e.printStackTrace();
			session.rollback();
			session.close();
			
			for (int k=0;k<set.size();k++) {
				Schema schema = set.getObject(k);
				Session session2 = null;
				try {
					session2 = SessionFactory.openSessionInThread(poolName);
					session2.beginTransaction();
					schema.setSession(session2);
					schema.insert();
					session2.commit();
				} catch (Exception e1) {
					System.out.println("===========data==========");
//					System.out.println(JSON.toJSONString(schema));
					e1.printStackTrace();
					session2.rollback();
					session2.close();
				}
			}
			
			Map<String, Integer> columnMaxLengthMap = new HashMap<String, Integer>();
			for (int i = 0; i < set.size(); i++) {
				Schema schema = set.getObject(i);
				for (int j = 0; j < schema.getColumns().length; j++) {
					Object v = schema.getV(j);
					String value = v + "";
					int valueLength = value.length();
					String columnName = schema.getColumns()[j].getColumnName();
					Integer columnMaxLength = columnMaxLengthMap.get(columnName);
					if (columnMaxLength == null) {
						columnMaxLengthMap.put(columnName, valueLength);
					} else {
						if (valueLength > columnMaxLength) {
							columnMaxLengthMap.put(columnName, valueLength);
						}
					}
				}	
			}
			System.out.println("======================================");
			System.out.println(JSON.toJSONString(columnMaxLengthMap));
			System.out.println("======================================");
		}
		SessionFactory.clearCurrentSession();
		
		return success;
	}

	private void importOneTable(String code, DataTable dt, Query qb) throws Exception {
		try {
			qb.getParams().clear();
			for (int i = 0; i < dt.getRowCount(); i++) {
				for (int j = 0; j < dt.getColumnCount(); j++) {
					if ((j == dt.getColumnCount() - 1) && ((dt.getDataColumn(j).getColumnName().equalsIgnoreCase("RNM")) || (dt.getDataColumn(j).getColumnName().equalsIgnoreCase("_RowNumber")))) {
						break;
					}
					String v = dt.getString(i, j);
					if (StringUtil.isEmpty(v)) {
						v = null;
					}
					if ((v != null) && (dt.getDataColumn(j).getColumnType().code() == 8))
						qb.add(Integer.parseInt(v));
					else {
						qb.add(v);
					}
				}
				qb.addBatch();
			}
			qb.executeNoQuery();
		} catch (Throwable t) {
			t.printStackTrace();
			return;
		}
	}

	public Session getSession() {
		return SessionFactory.currentSession();
	}
}