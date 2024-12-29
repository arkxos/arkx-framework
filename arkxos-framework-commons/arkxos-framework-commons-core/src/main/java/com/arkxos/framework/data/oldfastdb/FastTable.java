package com.arkxos.framework.data.oldfastdb;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.arkxos.framework.commons.collection.TwoTuple;
import com.arkxos.framework.commons.util.FileUtil;
import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.data.fasttable.FastColumn;
import com.arkxos.framework.data.fasttable.FastTableHeader;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.io.Files;

/**
 *  
 * @author Darkness
 * @date 2016年6月18日 下午3:50:02
 * @version V1.0
 * @since infinity 1.0
 */
public class FastTable {

	public static final String TABLE_EXT = "ft";
	private static final String TABLE_RECORD_EXT = "ftr";
	private static final String TABLE_INDEX_EXT = "fci";
	private static final String INDEX_GROUP_EXT = "fig";
	
	private String tableName;
	private String tableFilePath;
	private String tableDataFolderPath;
	
	public FastTable(String tableFile) {
		init(new File(tableFile));
	}
	
	public FastTable(File tableFile) {
		init(tableFile);
	}
	
	private void init(File tableFile) {
		this.tableName = tableName(tableFile.getName());
		this.tableFilePath = tableFile.getPath();
		
		FastTableFile tableFileReader = new FastTableFile(this.tableFilePath);
		FastTableHeader tableInfo = tableFileReader.readTableInfo();
//		tableInfoMap.put(tableName, tableInfo);
		
		this.tableDataFolderPath = tableFile.getParent() + File.separator + this.tableName;
		File tableDataFolder = new File(tableDataFolderPath);
		
		File[] files = tableDataFolder.listFiles();
		if(files != null) {
			for (File file : files) {
				String extension = Files.getFileExtension(file.getName());
				if(TABLE_RECORD_EXT.equals(extension)) {
					String recordBolckName = file.getName().replace("." + TABLE_RECORD_EXT, "");
//					tableMap.put(tableName, section);
				}
//				if("pk.lti".equals(extension)) {
//					String section = sectionFile.getName().replace(".pk.lti", "");
//					
//					FastColumn pkColumn = tableInfo.getPkColumn();
//					PkList pkList = null;
//					
//					try {
//						RandomAccessFile raf = new RandomAccessFile(sectionFile.getPath(), "rw");
//						FileChannel fileChannel = raf.getChannel();
//						long length = raf.length();
//						MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, length);
//						
//						long existRowCount = mappedByteBuffer.getLong();
//						
//						pkList = new PkList(pkColumn, pkColumn.getColumnType().length(), (int)existRowCount);
//						
//						for (int i = 0; i < existRowCount; i++) {
//							if(pkColumn.getColumnType() == FastColumnType.INT) {
//								int value = mappedByteBuffer.getInt();
//								pkList.add(value);
//							} else if(pkColumn.getColumnType() == FastColumnType.DATE) {
//								long value = mappedByteBuffer.getLong();
//								pkList.add(value);
//							}
//						}
//						
//						fileChannel.close();
//						raf.close();
//					} catch (IOException e) {
//						e.printStackTrace();
//					}
//					
////					pkIndexTable.put(tableName, section, pkList);
//				}
				
			}
		}
	}

	public String getTableName() {
		return tableName;
	}
	
	private String tableName(String fileName) {
		return fileName.replace("." + TABLE_EXT, "");
	}
	
	public static boolean isTableFile(String fileName) {
		String extension = Files.getFileExtension(fileName);
		if(TABLE_EXT.equals(extension)) {
			return true;
		}
		return false;
	}

	private int currentBlockIndex = 1;
	private int currentBlockRecordSize = 0;
	private int preBlockSize = 10 * 10000 * 10000;// 每个区块数据大小
	
	public void insert(FastDataTable dataTable) {
		int totalRecordCount = dataTable.getRowCount();
		int sourceRecordIndex = 0;
		int freeCount = preBlockSize - currentBlockRecordSize;
		
		IndexGroupFile indexGroupFile = new IndexGroupFile(this.tableDataFolderPath + File.separator + "index." + INDEX_GROUP_EXT);
		try {
			indexGroupFile.openFileChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		if(totalRecordCount > freeCount) {
			{
				FastDataTable dataTable2 = dataTable.copy();
				dataTable2.insertRows(Arrays.asList(dataTable.getDataRows(sourceRecordIndex, freeCount)));
				
				FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(currentBlockIndex));
				TwoTuple<PkList, Map<String, Multimap<Object, Integer>>> info = dataTableFile.save(dataTable2);
				PkList pksBuffer = info.first;
				Map<String, Multimap<Object, Integer>> indexs = info.second;
				for (String columnName : indexs.keySet()) {
					FastColumn fastColumn = dataTable2.getFastColumn(columnName);
					Multimap<Object, Integer> indexValues = indexs.get(columnName);
					ColumnIndexFile indexFile = new ColumnIndexFile(tableIndexFile(columnName));
					indexFile.save(fastColumn, indexValues, indexGroupFile);
				}
				
				sourceRecordIndex = freeCount;
				currentBlockIndex++;
				
				currentBlockRecordSize = 0;
			}
			int utilRecordCount = totalRecordCount-freeCount;
			int utilBlockSize = utilRecordCount/preBlockSize;
			for (int i=0;i<utilBlockSize;i++) {
				FastDataTable dataTable2 = dataTable.copy();
				dataTable2.insertRows(Arrays.asList(dataTable.getDataRows(sourceRecordIndex, preBlockSize)));
				
				FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(currentBlockIndex));
				TwoTuple<PkList, Map<String, Multimap<Object, Integer>>> info = dataTableFile.save(dataTable2);
				PkList pksBuffer = info.first;
				Map<String, Multimap<Object, Integer>> indexs = info.second;
				for (String columnName : indexs.keySet()) {
					FastColumn fastColumn = dataTable2.getFastColumn(columnName);
					Multimap<Object, Integer> indexValues = indexs.get(columnName);
					ColumnIndexFile indexFile = new ColumnIndexFile(tableIndexFile(columnName));
					indexFile.save(fastColumn, indexValues, indexGroupFile);
				}
				sourceRecordIndex += preBlockSize;
				currentBlockIndex++;
			}
			
			int lastCount = utilRecordCount%preBlockSize; 
			if(lastCount!=0) {//最后剩余的数量
				FastDataTable dataTable2 = dataTable.copy();
				dataTable2.insertRows(Arrays.asList(dataTable.getDataRows(sourceRecordIndex, lastCount)));
				
				FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(currentBlockIndex));
				TwoTuple<PkList, Map<String, Multimap<Object, Integer>>> info = dataTableFile.save(dataTable2);
				PkList pksBuffer = info.first;
				Map<String, Multimap<Object, Integer>> indexs = info.second;
				for (String columnName : indexs.keySet()) {
					FastColumn fastColumn = dataTable2.getFastColumn(columnName);
					Multimap<Object, Integer> indexValues = indexs.get(columnName);
					ColumnIndexFile indexFile = new ColumnIndexFile(tableIndexFile(columnName));
					indexFile.save(fastColumn, indexValues, indexGroupFile);
				}
				
				sourceRecordIndex += lastCount;
				
				currentBlockRecordSize = lastCount;
//				currentBlockIndex++;
			}
		} else {
//			FastDataTable dataTable2 = new FastDataTable(dataTable.getTableName(), dataTable.getDataColumns());
//			dataTable2.insertRows(Arrays.asList(dataTable.getDataRows(sourceRecordIndex, lastCount)));
			
			FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(currentBlockIndex));
			TwoTuple<PkList, Map<String, Multimap<Object, Integer>>> info = dataTableFile.save(dataTable);
			PkList pksBuffer = info.first;
			Map<String, Multimap<Object, Integer>> indexs = info.second;
			for (String columnName : indexs.keySet()) {
				FastColumn fastColumn = dataTable.getFastColumn(columnName);
				Multimap<Object, Integer> indexValues = indexs.get(columnName);
				ColumnIndexFile indexFile = new ColumnIndexFile(tableIndexFile(columnName));
				indexFile.save(fastColumn, indexValues, indexGroupFile);
			}
			
//			sourceRecordIndex += lastCount;
			
			currentBlockRecordSize += totalRecordCount;
		}
		
		indexGroupFile.close();
		
//		PkList pkSnapshort = pkIndexTable.get(tableName, section);
//		if(pkSnapshort == null) {
//			pkIndexTable.put(tableName, section, pksBuffer);
//		} else {
//			pkSnapshort.union(pksBuffer);
//		}
		
//		updatePkIndex(tableSectionPkIndexFile(tableName, section), pksBuffer);
	}
	
	private String tableRecordFile(int blockIndex) {
		return this.tableDataFolderPath + File.separator + "block" + blockIndex + "." + TABLE_RECORD_EXT;
	}
	
	private String tableIndexFile(String columnName) {
		return this.tableDataFolderPath + File.separator + columnName + "." + TABLE_INDEX_EXT;
	}
	
	private  void updatePkIndex(String tableFilePath, PkList pkBuffer) {
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

	public <T extends IFastTable> T load(Class<T> tableClass) {
		FastTableFile tableFile = new FastTableFile(this.tableFilePath);
		FastTableHeader tableInfo = tableFile.readTableInfo();
		
		FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(1));
		T dataTable = dataTableFile.readDataTable(tableInfo, tableClass);
		return dataTable;
	}

	public long queryRowSize() {
		FastTableFile dataTableFile = new FastTableFile(this.tableFilePath);
		return dataTableFile.readRowSize();
	}

	public void drop() {
		File tableFile = new File(this.tableFilePath);
		tableFile.delete();
			
		File tableFolderFile = new File(this.tableDataFolderPath);
		if (tableFolderFile.exists()) {
			FileUtil.delete(tableFolderFile);
		}
	}
	
	public void rebuildIndex() {
		FastTableFile tableFile = new FastTableFile(this.tableFilePath);
		FastTableHeader tableInfo = tableFile.readTableInfo();
		
		IndexGroupFile indexGroupFile = new IndexGroupFile(this.tableDataFolderPath + File.separator + "index." + INDEX_GROUP_EXT);
		
		try {
			indexGroupFile.openFileChannel();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (FastColumn column : tableInfo.getIndexColumns()) {
			String filePath = this.tableDataFolderPath + File.separator + column.getName() + "." + TABLE_INDEX_EXT;
			File file = new File(filePath);
			if(!file.exists()) {
				continue;
			}
			ColumnIndexFile columnIndexFile = new ColumnIndexFile(filePath);
			Multimap<Object,Integer> newValues = HashMultimap.create();
			Multimap<Object, Long> indexPositions = columnIndexFile.readValueRowIndexes(column);
			int total = indexPositions.keySet().size();
			int i =0;
			for (Object value : indexPositions.keySet()) {
				Collection<Long> xx = indexPositions.get(value);
				List<Integer> needReadRecords = indexGroupFile.readValues(new ArrayList<>(xx));
				newValues.putAll(value, needReadRecords);
				System.out.println(total + ":" + i++);
			}
			System.out.println("finish");
			file.delete();
			
			columnIndexFile = new ColumnIndexFile(filePath);
//			indexGroupFile = new IndexGroupFile(this.tableDataFolderPath + File.separator + "index." + INDEX_GROUP_EXT);
			indexGroupFile = new IndexGroupFile(this.tableDataFolderPath + File.separator + "index." + INDEX_GROUP_EXT);
			
			try {
				indexGroupFile.openFileChannel();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			columnIndexFile.save(column, newValues, indexGroupFile);
			
			indexGroupFile.close();
		}
	}

	public  <T extends IFastTable> T select(String filter, Class<T> tableType) {
		long start = System.currentTimeMillis();
		FastTableFile tableFile = new FastTableFile(this.tableFilePath);
		FastTableHeader tableInfo = tableFile.readTableInfo();
//		System.out.println("load table file:" + (System.currentTimeMillis()-start) + "ms");
		FilterInfo filterInfo = null;
		if(!StringUtil.isEmpty(filter)) {
			filterInfo = new FilterInfo(filter);
		}
		start = System.currentTimeMillis();
//		PkList pkList = pkIndexTable.get(tableName, section);
		List<Integer> loadRecords = new ArrayList<>();
		
		IndexGroupFile indexGroupFile = new IndexGroupFile(this.tableDataFolderPath + File.separator + "index." + INDEX_GROUP_EXT);
		
		if(filterInfo != null) {
			for (FastColumn column : tableInfo.getIndexColumns()) {
				if(filterInfo.needFilter(column.getName())) {
					start = System.currentTimeMillis();
					ColumnIndexFile columnIndexFile = new ColumnIndexFile(this.tableDataFolderPath + File.separator + column.getName() + "." + TABLE_INDEX_EXT);
					
					Multimap<Object, Long> indexPositions = columnIndexFile.readValueRowIndexes(column);
					System.out.println("load index ["+column.getName()+"]:" + (System.currentTimeMillis()-start) + "ms");
					start = System.currentTimeMillis();
					
					List<Long> temp = filterInfo.filter(column, indexPositions);
					List<Integer> needReadRecords = indexGroupFile.readValues(temp);
					
					if(loadRecords.isEmpty()) {
						loadRecords.addAll(needReadRecords);	
					} else {
						loadRecords.retainAll(needReadRecords);
					}
					
					System.out.println("filter index ["+column.getName()+"]:" + (System.currentTimeMillis()-start) + "ms");
				}
			}
			
			if(loadRecords.isEmpty()) {
				try {
					return tableType.newInstance();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
		start = System.currentTimeMillis();
		
		Collections.sort(loadRecords);
		
		
//		System.out.println("filter table ["+loadRecords.size()+"]:" + (System.currentTimeMillis()-start) + "ms");
		start = System.currentTimeMillis();
		FastTableRecordFile dataTableFile = new FastTableRecordFile(tableRecordFile(1));
		T data = dataTableFile.readDataTable(tableInfo, tableType, filterInfo, loadRecords);
//		System.out.println("load filtered table ["+loadRecords.size()+"]:" + (System.currentTimeMillis()-start) + "ms");
		return data;
	}
}
