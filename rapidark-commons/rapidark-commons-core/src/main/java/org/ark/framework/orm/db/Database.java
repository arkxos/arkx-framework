package org.ark.framework.orm.db;
//package org.ark.framework.orm.db;
//package org.ark.framework.orm.schema;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//import org.ark.framework.orm.connection.ConnectionManager;
//
///**   
// * 
// * @author Darkness
// * @date 2012-9-19 下午10:27:07 
// * @version V1.0   
// */
//public class Database {
//
//	private DatabaseConfig config;
//	private List<Table> tables = new ArrayList<Table>();
//	
//	public DatabaseConfig getConfig() {
//		return config;
//	}
//
//	public void setConfig(DatabaseConfig config) {
//		this.config = config;
//	}
//
//	public void initTables() {
//		try {
//			initTables(ConnectionManager.mysql(config));
//		} catch (ClassNotFoundException e) {
//			e.printStackTrace();
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}
//
//	/**
//	 * 初始化数据库中的所有表信息
//	 * 
//	 * @author Darkness
//	 * @date 2012-9-18 下午5:36:27
//	 * @version V1.0
//	 */
//	public void initTables(Connection conn) {
//
//		tables.addAll(parseAllTable(conn));
//	}
//	
//	/**
//	 * 开始处理生成所有表 如果不传入表名，表示将数据库中所有表生成bean; 可以指定表名生成bean;
//	 */
//	public List<Table> parseAllTable(Connection conn) { // *1.3*
//
//		String sql = "show tables";
//		ResultSet rs = ConnectionManager.query(conn, sql);
//
//		List<Table> tables = new ArrayList<Table>();
//
//		try {
//			while (rs.next()) {
//				String tablename = rs.getString(1);
//				Table table = parseTableByShowCreate(conn, tablename);
//				tables.add(table);
//			}
//			ConnectionManager.close(conn, null, rs);
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//
//		return tables;
//	}
//
//	/**
//	 * 通过 mysql的 show create table TABLE_NAME逆向生成Bean;
//	 * 
//	 * @param conn
//	 * @param tname
//	 * @param outputdir
//	 * @param packname
//	 */
//	public Table parseTableByShowCreate(Connection conn, String tablename) {
//
//		boolean shouldCloseConn = false;
//
//		String sql = "show create table " + tablename;
//		ResultSet rs = null;
//
//		try {
//			rs = ConnectionManager.query(conn, sql);
//			Table table = new Table();
//			table.setTableName(tablename);
//
//			// 用查詢結果生成bean
//			if (rs.next()) {
//
//				String sqlstr = rs.getString(2);
//				String lines[] = sqlstr.split("\r\n");
//
//				for (int i = 0; i < lines.length; i++) {
//					String line = lines[i];
//
//					String regex = "\\s*`([^`]*)`\\s*(\\w+[^ ]*)\\s*(NOT\\s+NULL\\s*)?(AUTO_INCREMENT\\s*)?(DEFAULT\\s*([^ ]*|NULL|'0'|''|CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP)\\s*)?(COMMENT\\s*'([^']*)')?\\s*,\\s*";
//					Pattern patt = Pattern.compile(regex);
//					Matcher mat = patt.matcher(line);
//
//					while (mat.find()) {
//						Column column = new Column();
//						column.setColumnName(mat.group(1));
//						column.setType(mat.group(2));
//						column.setComment(mat.group(8));
//						table.addColumn(column);
//					}
//
//					if (i == lines.length - 1) {
//
//						int index = line.indexOf("COMMENT=");
//						String comment = "无备注信息";
//						if (index != -1) {
//							String tmp = line.substring(index + 8);
//							comment = tmp.replace("'", "");
//						}
//						table.setComment(comment);
//					}
//
//				}
//
//			}
//			return table;
//		} catch (SQLException e) {
//			e.printStackTrace();
//			return null;
//		} finally {
//			ConnectionManager.close(shouldCloseConn ? conn : null, null, rs);
//		}
//
//	}
//	
//	/**
//	 * 获取数据库中的所有表
//	 * 
//	 * @author Darkness
//	 * @date 2012-9-18 下午4:37:50
//	 * @version V1.0
//	 */
//	public Table[] getTables() {
//		if (tables == null) {
//			return new Table[0];
//		}
//		return tables.toArray(new Table[0]);
//	}
//	
//	/**
//	 * 获取指定的表
//	 * 
//	 * @author Darkness
//	 * @date 2012-9-19 下午10:48:43 
//	 * @version V1.0
//	 */
//	public Table[] getTables(String... tableNames) {
//		if (tables == null) {
//			return new Table[0];
//		}
//		
//		if(tableNames != null) {
//			List<Table> result = new ArrayList<Table>();
//			for (Table table : tables) {
//				for (int i = 0; i < tableNames.length; i++) {
//					if(table.getTableName().equalsIgnoreCase(tableNames[i])) {
//						result.add(table);
//						break;
//					}
//				}
//			}
//			return result.toArray(new Table[0]);
//		}
//		
//		return tables.toArray(new Table[0]);
//	}
//}
