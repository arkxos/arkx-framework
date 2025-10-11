package org.ark.framework.orm;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.ark.framework.messages.LongTimeTask;

import io.arkx.framework.Constant;
import io.arkx.framework.commons.collection.DataTable;
import io.arkx.framework.commons.util.BufferedRandomAccessFile;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.LogUtil;
import io.arkx.framework.commons.util.NumberUtil;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.commons.util.ZipUtil;
import io.arkx.framework.data.db.connection.Connection;
import io.arkx.framework.data.db.connection.ConnectionPoolManager;
import io.arkx.framework.data.jdbc.JdbcTemplate;
import io.arkx.framework.data.jdbc.Query;
import io.arkx.framework.data.jdbc.Session;
import io.arkx.framework.data.jdbc.SessionFactory;


/**
 * @class org.ark.framework.orm.DBExporter
 * @author Darkness
 * @date 2012-3-8 下午1:58:16
 * @version V1.0
 */
public class DBExporter {
	
	static Logger logger = Logger.getLogger(DBExporter.class);
	
	public static final int PageSize = 500;
	private JdbcTemplate da;
	private BufferedRandomAccessFile braf;
	private LongTimeTask task;
//	private static final String ZDMVersion1 = "1";
	private static String CurrentVersion = "1";
	private ArrayList<ZDTParser.ZDTTableInfo> Tables;

	public void setTask(LongTimeTask task) {
		this.task = task;
	}

	public void setTables(ArrayList<ZDTParser.ZDTTableInfo> tables) {
		this.Tables = tables;
	}

//	public void exportDB(String file) {
//		exportDB(file, SchemaUtil.getAllSchemaClassName());
//	}
//	public void exportDB(String file, String[] arr) {
//		exportDB(file, arr, null);
//	}
	
	public void exportDB(String file, String[] arr, ClassLoader classLoader) {
		FileUtil.delete(file);
		try {
			this.braf = new BufferedRandomAccessFile(file, "rw");
			
			for (int i = 0; i < arr.length; i++) {
				try {
					if (this.task != null) {
						this.task.setPercent(Double.valueOf(i * 100.0D / arr.length).intValue());
						this.task.setCurrentInfo("正在导出表" + arr[i]);
						
					}
					int totalPercent = Double.valueOf(i * 100.0D / arr.length).intValue();
					System.out.println("【"+totalPercent+"%】正在导出表" + arr[i]);
					transferOneTable(totalPercent, arr[i], classLoader);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			try {
//				DataTable dt = getSession().createQueryBuilder("select Code,ID from ZCCustomTable where Type='Custom'").executeDataTable();
//				for (int i = 0; i < dt.getRowCount(); i++)
//					transferCustomTable(dt.getString(i, "Code"), dt.getString(i, "ID"));
//			} catch (Throwable t) {
//				logger.warn("系统中没有自定义表");
//			}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (this.braf != null)
				try {
					this.braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
		}
	}

//	private void transferCustomTable(String table, String ID) throws Exception {
//		int count = 0;
//		try {
//			String columnName = getSession().createQueryBuilder("select Code from ZCCustomTableColumn where TableID=?", ID).executeString();
//			QueryBuilder qb = getSession().createQueryBuilder("select * from " + table + " order by " + columnName);
//			count = DBUtil.getCount(qb);
//			for (int i = 0; i * PageSize < count || i == 0 && count == 0; i++) {
//				DataTable dt = qb.executePagedDataTable(PageSize, i);
//
//				byte[] bs = table.getBytes();
//				this.braf.write(NumberUtil.toBytes(bs.length));
//				this.braf.write(bs);
//
//				bs = FileUtil.serialize(dt);
//				bs = ZipUtil.zip(bs);
//				this.braf.write(NumberUtil.toBytes(bs.length));
//				this.braf.write(bs);
//			} 
//		} catch (Exception e) {
//			logger.warn("对应的自定义表不存在" + table + ":" + e.getMessage());
//			return;
//		}
//	}
	private void transferOneTable(int totalPercent, String schemaName, ClassLoader classLoader) throws Exception {

		Schema schema = null;

		if (classLoader != null) {
			schema = (Schema) classLoader.loadClass(schemaName).newInstance();
		} else {
			schema = (Schema) Class.forName(schemaName).newInstance();
		}
		transferOneTable(totalPercent, schemaName, schema);
	}
	
	private void transferOneTable(int totalPercent, String schemaName, Schema schema) throws Exception {
		int count = 0;
		try {
			SessionFactory.openSessionInThread();
			SessionFactory.currentSession().beginTransaction();
			count = SessionFactory.currentSession().createQuery("select count(*) from " + schema.TableCode).executeInt();
			int totalByteLength = 0;
			for (int i = 0; i * PageSize < count || i == 0 && count == 0; i++) {
				SchemaSet<? extends Schema> set = schema.querySet(null, PageSize, i);

				byte[] schemaNameBtyes = schemaName.getBytes();
				int schemaNameBytesLength = schemaNameBtyes.length;
				this.braf.write(NumberUtil.toBytes(schemaNameBytesLength));
				this.braf.write(schemaNameBtyes);

				byte[] dataSetBytes = FileUtil.serialize(set);
				byte[] zipedDataSetBytes = ZipUtil.zip(dataSetBytes);
				this.braf.write(NumberUtil.toBytes(zipedDataSetBytes.length));
				this.braf.write(zipedDataSetBytes);
				
				int currentPageByteLength = 4 + schemaNameBtyes.length + 4 + zipedDataSetBytes.length;
				System.out.println("第" + (i + 1) + "页，数据长度：" + currentPageByteLength);
				totalByteLength += currentPageByteLength;
				System.out.println("【"+totalPercent+"%】当前表["+schema.TableCode+"]进度：" + Double.valueOf(i * PageSize * 100.0D / count).intValue() + "%, " + "("+i * PageSize+"/"+count+")");
			} 
			System.out.println("数据总长度：" + totalByteLength);
			SessionFactory.currentSession().close();
			SessionFactory.clearCurrentSession();
		} catch (Exception e) {
			logger.warn("Schema对应的表不存在：" + schemaName);
			e.printStackTrace();
			return;
		}
	}
	
	public void exportDB(String file) {
		exportDB(file, "", null);
	}

	public void exportDB(String file, ArrayList<String> tableList) {
		exportDB(file, "", tableList);
	}

	public void exportDB(String file, String poolName, ArrayList<String> tableList) {
		Connection conn = ConnectionPoolManager.getConnection(poolName);
		try {
			exportDB(file, conn, tableList);
		} finally {
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void exportDB(String file, Connection conn, ArrayList<String> list) {
		String dir = FileUtil.normalizePath(file);
		File dFile = new File(dir.substring(0, dir.lastIndexOf("/") + 1));
		if (!dFile.exists()) {
			dFile.mkdirs();
		}
		FileUtil.delete(file);
		this.da = new JdbcTemplate(conn);
		try {
			this.braf = new BufferedRandomAccessFile(file, "rw");

			byte[] bs = CurrentVersion.getBytes();
			this.braf.write(NumberUtil.toBytes(bs.length));
			this.braf.write(bs);

			if (this.Tables == null) {
				this.Tables = getTableListFromClass();
			}

			for (int i = 0; i < this.Tables.size(); i++)
				try {
					ZDTParser.ZDTTableInfo table = this.Tables.get(i);
					if (this.task != null) {
						this.task.setPercent(Double.valueOf(i * 100.0D / this.Tables.size()).intValue());
						this.task.setCurrentInfo("Exporting table " + table.Name);
					}
					String tableCode = table.Name;
					if ((list == null) || (list.contains(tableCode))) {
						transferOneTable(tableCode, table.IndexInfo, table.Columns);
						//transferOneTable("B" + tableCode, null, SchemaUtil.addBackupColumn(table.Columns));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
		} catch (Exception e1) {
			e1.printStackTrace();
		} finally {
			if (this.braf != null)
				try {
					this.braf.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
//				this.da.close();
		}
	}

	private ArrayList<ZDTParser.ZDTTableInfo> getTableListFromClass() {
		String[] arr = SchemaFactory.getAllSchemaClassName();
		ArrayList<ZDTParser.ZDTTableInfo> list = new ArrayList<ZDTParser.ZDTTableInfo>();
		for (String schemaName : arr) {
			ZDTParser.ZDTTableInfo ti = new ZDTParser.ZDTTableInfo();
			try {
				Schema schema = (Schema) Class.forName(schemaName).newInstance();
				ti.Name = schema.getTableCode();
				//ti.IndexInfo = schema.IndexInfo;
				ti.Columns = schema.Columns;
				list.add(ti);
			} catch (Exception e) {
				LogUtil.warn("Schema's matching table not found:" + schemaName);
			}
		}
		return list;
	}

	private void writeByte(byte[] bs) throws IOException {
		this.braf.write(NumberUtil.toBytes(bs.length));
		this.braf.write(bs);
	}
	
	private void transferOneTable(String tableName, String indexInfo, SchemaColumn[] scs) throws Exception {
		try {
			Query qb = getSession().createQuery("select * from " + tableName);
			int count = SessionFactory.openSession().readOnly().createQuery("select count(1) from " + tableName).executeInt();
			int i = 0;
			do {
				do {
					DataTable dt = (DataTable)qb.executePagedDataTable(PageSize, i).getData();

					if (scs.length != dt.getColumnCount()) {
						throw new RuntimeException("Schema not match table:" + tableName);
					}

					byte[] bs = tableName.getBytes();
					writeByte(bs);

					bs = getColumnString(scs).getBytes();
					writeByte(bs);

					if (ObjectUtil.empty(indexInfo)) {
						indexInfo = Constant.Null;
					}
					bs = indexInfo.getBytes();
					writeByte(bs);

					bs = getDataTableString(scs, dt).getBytes("UTF-8");
					bs = ZipUtil.zip(bs);
					writeByte(bs);

					i++;
				} while (i * PageSize < count);
				if (i != 0)
					break;
			} while (count == 0);
		} catch (Exception e) {
			LogUtil.warn("Table not found:" + tableName + ";" + e.getMessage());
			return;
		}
	}

	private String getColumnString(SchemaColumn[] scs) {
		StringBuilder sb = new StringBuilder();
		for (SchemaColumn sc : scs) {
			sb.append(sc.getColumnName());
			sb.append("\t");
			sb.append(sc.getColumnType());
			sb.append("\t");
			sb.append(sc.getColumnOrder());
			sb.append("\t");
			sb.append(sc.getLength());
			sb.append("\t");
			sb.append(sc.getPrecision());
			sb.append("\t");
			sb.append(sc.isMandatory());
			sb.append("\t");
			sb.append(sc.isPrimaryKey());
			sb.append("\n");
		}
		return sb.toString().trim();
	}

	private String getDataTableString(SchemaColumn[] scs, DataTable dt) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < dt.getRowCount(); i++) {
			for (int j = 0; j < dt.getColumnCount(); j++) {
				if (j != 0) {
					sb.append("\t");
				}
				if (scs[j].getColumnType() == 2) {
					sb.append(StringUtil.javaEncode(StringUtil.base64Encode((byte[]) dt.get(i, j))));
				} else if (ObjectUtil.in(new Object[] { Integer.valueOf(scs[j].getColumnType()), Integer.valueOf(1), Integer.valueOf(10) })) {
					String v = dt.getString(i, j);
					if (v == null) {
						sb.append("null");
					} else {
						sb.append("\"");
						sb.append(StringUtil.javaEncode(v));
						sb.append("\"");
					}
				} else {
					sb.append(dt.getString(i, j));
				}
			}
			sb.append("\n");
		}
		return sb.toString().trim();
	}

	public Session getSession() {
		return SessionFactory.currentSession();
	}
}