package com.arkxos.framework.data.lightning;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

public class LightningDatabase {

	private static boolean inited = false;
	private static Object syncObject = new Object();
	private static String dbPath = System.getProperty("user.home") + File.separator + "LightningDb";
	
	private static final String DEFAULT_SECTION = "defaultSection";
	
	private static Multimap<String, String> tableMap = HashMultimap.create();
//	private static Table<String, String, PkList> pkIndexTable = HashBasedTable.create();
	private static Map<String, TableInfo> tableInfoMap = new HashMap<>();
	
	private static String tableFolder(String tableName) {
		return dbPath + File.separator + tableName;
	}
	
	private static String tableFile(String tableName) {
		return dbPath + File.separator + tableName + ".lt";
	}
	
	private static String tableSectionRecordFile(String tableName, String section) {
		return dbPath + File.separator + tableName + File.separator + section + ".ltr";
	}
	
	private static String tableSectionPkIndexFile(String tableName, String section) {
		return dbPath + File.separator + tableName + File.separator + section + ".pk.lti";
	}
	
	public static void insert(LightningDataTable dataTable) {
		insert(dataTable, DEFAULT_SECTION);
	}
	
	public static void insert(LightningDataTable dataTable, String section) {
		if(dataTable.getRowCount() == 0) {
			return;
		}
		
		init();
		
		String tableName = dataTable.getTableName();
		
		tableMap.put(tableName, section);
		
		LightningTableFile tableFile = new LightningTableFile(tableFile(tableName));
		tableFile.save(dataTable);
		
		LightningTableRecordFile dataTableFile = new LightningTableRecordFile(tableSectionRecordFile(tableName, section));
		PkList pksBuffer = dataTableFile.save(dataTable);
		
//		PkList pkSnapshort = pkIndexTable.get(tableName, section);
//		if(pkSnapshort == null) {
//			pkIndexTable.put(tableName, section, pksBuffer);
//		} else {
//			pkSnapshort.union(pksBuffer);
//		}
		
		updatePkIndex(tableSectionPkIndexFile(tableName, section), pksBuffer);
	}
	
	private static void updatePkIndex(String tableFilePath, PkList pkBuffer) {
		
		if(pkBuffer == null || pkBuffer.values == null) {
			return;
		}
		
		try {
			RandomAccessFile raf = new RandomAccessFile(tableFilePath, "rw");
			FileChannel fileChannel = raf.getChannel();
			
			long fileLength = raf.length();
			if(fileLength==0) {// 文件不存在，写入文件头
				int rowCount = pkBuffer.values.size();
				MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
				mappedByteBuffer.putLong(rowCount);
				
				fileLength = 8;
			} else {// 文件存在，修改记录数
				int rowCount = pkBuffer.values.size();
				MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, 8);
				long existRowCount = mappedByteBuffer.getLong();
				mappedByteBuffer.position(0);
				mappedByteBuffer.putLong(existRowCount + rowCount);
			}
			
			
			ByteBuffer Buffer = pkBuffer.pkbuffer;
			int pkLength = Buffer.limit();
			MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileLength, pkLength);
			mappedByteBuffer.put(Buffer);
			
			fileChannel.close();
			raf.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static LightningDataTable load(String tableName) {
		return load(tableName, DEFAULT_SECTION, LightningDataTable.class);
	}
	
	public static <T extends ILightningTable> T load(String tableName, Class<T> tableClass) {
		return load(tableName, DEFAULT_SECTION, tableClass);
	}
	
	public static LightningDataTable load(String tableName, String section) {
		return load(tableName, section, LightningDataTable.class);
	}
	
	public static <T extends ILightningTable> T load(String tableName, String section, Class<T> tableClass) {
		init();
		
		if(!tableMap.containsEntry(tableName, section)) {
			try {
				return tableClass.newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}
		}
		
		LightningTableFile tableFile = new LightningTableFile(tableFile(tableName));
		TableInfo tableInfo = tableFile.readTableInfo();
		
		LightningTableRecordFile dataTableFile = new LightningTableRecordFile(tableSectionRecordFile(tableName, section));
		T dataTable = dataTableFile.readDataTable(tableInfo, tableClass);
		return dataTable;
	}
	
	public static long queryRowSize( String tableName) {
		return queryRowSize(tableName, DEFAULT_SECTION);
	}
	
	public static long queryRowSize(String tableName, String section) {
		init();
		
		if(!tableMap.containsEntry(tableName, section)) {
			return 0L;
		}
		
		LightningTableFile dataTableFile = new LightningTableFile(tableFile(tableName));
		return dataTableFile.readRowSize();
	}
	
	public static void setPath(String path) {
		dbPath = path;
	}
	
	public static void init() {
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
					if(!tableFolder.isDirectory()) {
						continue;
					} else {// read table info
						
					}
					String tableName = tableFolder.getName();
					
					LightningTableFile tableFile = new LightningTableFile(tableFile(tableName));
					TableInfo tableInfo = tableFile.readTableInfo();
					tableInfoMap.put(tableName, tableInfo);
					
					File[] sectionFiles = tableFolder.listFiles();
					if(sectionFiles != null) {
						for (File sectionFile : sectionFiles) {
							String extension = Files.getFileExtension(sectionFile.getName());
							if("ltr".equals(extension)) {
								String section = sectionFile.getName().replace(".ltr", "");
								tableMap.put(tableName, section);
							}
							if("pk.lti".equals(extension)) {
								String section = sectionFile.getName().replace(".pk.lti", "");
								
								LightningColumn pkColumn = tableInfo.getPkColumn();
								PkList pkList = null;
								
								try {
									RandomAccessFile raf = new RandomAccessFile(sectionFile.getPath(), "rw");
									FileChannel fileChannel = raf.getChannel();
									long length = raf.length();
									MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, length);
									
									long existRowCount = mappedByteBuffer.getLong();
									
									pkList = new PkList(pkColumn, pkColumn.getColumnType().length(), (int)existRowCount);
									
									for (int i = 0; i < existRowCount; i++) {
										if(pkColumn.getColumnType() == LightningColumnType.INT) {
											int value = mappedByteBuffer.getInt();
											pkList.add(value);
										} else if(pkColumn.getColumnType() == LightningColumnType.DATE) {
											long value = mappedByteBuffer.getLong();
											pkList.add(value);
										}
									}
									
									fileChannel.close();
									raf.close();
								} catch (IOException e) {
									e.printStackTrace();
								}
								
//								pkIndexTable.put(tableName, section, pkList);
							}
							
						}
					}
				}
			}
			
			inited = true;
		}
	}
	
	public static boolean isTableExist(String tableName, String section) {
		init();
		
		return tableMap.containsEntry(tableName, section);
	}
	
	public static void dropTable(String tableName) {
		init();
		
		if(tableMap.containsKey(tableName)) {
			File tableFile = new File(tableFile(tableName));
			if(tableFile.exists()) {
				tableFile.delete();
			}
			
			File tableFolderFile = new File(tableFolder(tableName));
			if(tableFolderFile.exists()) {
				FileUtil.delete(tableFolderFile);
			}
			
			tableMap.removeAll(tableName);
		}
	}
	
	public static void dropTable(String tableName, String section) {
		init();
		
		if(tableMap.containsEntry(tableName, section)) {
			File tableFile = new File(tableSectionRecordFile(tableName, section));
			if(tableFile.exists()) {
				tableFile.delete();
			}
			File tableIndexFile = new File(tableSectionPkIndexFile(tableName, section));
			if(tableIndexFile.exists()) {
				tableIndexFile.delete();
			}
			
			tableMap.remove(tableName, section);
		}
	}

	public static LightningDataTable select(String tableName, String filter) {
		return select(tableName, DEFAULT_SECTION, filter);
	}
	
	public static LightningDataTable select(String tableName, String section, String filter) {
		return select(tableName, section, filter, LightningDataTable.class);
	}
	
	public static <T extends ILightningTable> T select(String tableName, String section, String filter, Class<T> tableType) {
		init();
		
		if(!tableMap.containsEntry(tableName, section)) {
			return null;
		}
		long start = System.currentTimeMillis();
		LightningTableFile tableFile = new LightningTableFile(tableFile(tableName));
		TableInfo tableInfo = tableFile.readTableInfo();
		System.out.println("load table file:" + (System.currentTimeMillis()-start) + "ms");
		FilterInfo filterInfo = null;
		if(!StringUtil.isEmpty(filter)) {
			filterInfo = new FilterInfo(filter);
		}
		start = System.currentTimeMillis();
//		PkList pkList = pkIndexTable.get(tableName, section);
		List<Integer> loadRecords = new ArrayList<>();
//		if(filterInfo != null) {
//			loadRecords = filterInfo.filter(pkList);
//		}
		System.out.println("filter table ["+loadRecords.size()+"]:" + (System.currentTimeMillis()-start) + "ms");
		start = System.currentTimeMillis();
		LightningTableRecordFile dataTableFile = new LightningTableRecordFile(tableSectionRecordFile(tableName, section));
		T data = dataTableFile.readDataTable(tableInfo, tableType, filterInfo, loadRecords);
		System.out.println("load filtered table ["+loadRecords.size()+"]:" + (System.currentTimeMillis()-start) + "ms");
		return data;
	}
	
}
