package io.arkx.framework.data.oldfastdb;

import java.io.File;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import io.arkx.framework.commons.util.SystemInfo;

/**
 *  
 * @author Darkness
 * @date 2016年6月17日 下午2:09:10
 * @version V1.0
 */
public class Fastdb {
	
	private static Fastdb instance = new Fastdb();
	
	public static Fastdb instance() {
		return instance;
	}
	
	private String dbPath = SystemInfo.userHome() + File.separator + "fastdb";
	
	private Map<String, FastTable> tableMap = new ConcurrentHashMap<>();
	
	private boolean inited = false;
	private Object syncObject = new Object();
	
	public void start() {
		init();
	}
	
	public Fastdb setPath(String path) {
		dbPath = path;
		return this;
	}
	
	public void insert(FastDataTable dataTable) {
		init();
		
		String tableName = dataTable.getTableName();
		FastTable table = tableMap.get(tableName);
		if(table == null) {// table not exist
			FastTableFile tableFile = new FastTableFile(tableFile(tableName));
			tableFile.save(dataTable);
			
			table = new FastTable(tableFile(tableName));
			tableMap.put(tableName, table);
		}
		
		table.insert(dataTable);
	}
	
	public FastDataTable load(String tableName) {
		return load(tableName, FastDataTable.class);
	}
	
	public <T extends IFastTable> T load(String tableName, Class<T> tableClass) {
		init();
		
		if(!tableMap.containsKey(tableName)) {
			try {
				return tableClass.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
//			throw new RuntimeException("table["+tableName+"] not exist");
		}
		
		FastTable table = tableMap.get(tableName);
		return table.load(tableClass);
	}
	
	public FastTable getTable(String tableName) {
		init();
		
		return tableMap.get(tableName);
	}
	
	public long queryRowSize(String tableName) {
		init();
		
		if(!tableMap.containsKey(tableName)) {
			try {
				return 0;
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		FastTable table = tableMap.get(tableName);
		return table.queryRowSize();
		
	}
	
	public  boolean isTableExist(String tableName) {
		init();
		
		return tableMap.containsKey(tableName);
	}
	
	public  void dropTable(String tableName) {
		init();
		
		FastTable table = tableMap.get(tableName);
		if (table == null) {
			return;
		}
		table.drop();
		
		tableMap.remove(tableName);
	}
	
	public  FastDataTable select(String tableName, String filter) {
		return select(tableName, filter, FastDataTable.class);
	}
	
	public  <T extends IFastTable> T select(String tableName, String filter, Class<T> tableType) {
		init();
		
		if(!tableMap.containsKey(tableName)) {
			try {
				return tableType.newInstance();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		FastTable table = tableMap.get(tableName);
		return table.select(filter, tableType);
	}
	
	public void init() {
		if(inited) {
			return;
		}
		
		synchronized (syncObject) {
			if(inited) {
				return;
			}
			
			File dbRootFile = new File(dbPath);
			if(!dbRootFile.exists()) {
				dbRootFile.mkdirs();
			}
			
			File[] tableFolders = dbRootFile.listFiles();
			if(tableFolders != null) {
				for (File tableFolder : tableFolders) {
					if(tableFolder.isDirectory()) {
						continue;
					}
					if(!FastTable.isTableFile(tableFolder.getName())) {
						continue;
					}
					
					FastTable table = new FastTable(tableFolder);
					tableMap.put(table.getTableName(), table);
				}
			}
			
			inited = true;
		}
	}
	
	private String tableFile(String tableName) {
		return dbPath + File.separator + tableName + "." + FastTable.TABLE_EXT;
	}
	
}
