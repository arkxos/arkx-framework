package io.arkx.framework.data.fasttable;

import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.StringUtil;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *  
 * @author Darkness
 * @date 2016年11月11日 下午1:24:52
 * @version V1.0
 */
public class FastDatabase {

	private String path;
	private Map<Class<?>, RecordConverter<?>> converters = new HashMap<>();
	
	public FastDatabase(String path) {
		this.path = path;
	}
	
	public void registerConverter(RecordConverter<?> converter) {
		converters.put(converter.acceptEntityClass(), converter);
	}

	private String tableFilePath(String partition, String tableName) {
		return path + File.separator + partition + File.separator+ tableName + ".dat";
	}
	
	public boolean isTableExist(String partition, String tableName) {
		return new File(tableFilePath(partition, tableName)).exists();
	}
	
	private static String DefaultPartition = "partition-default";
	
	public <T> void save(String tableName, List<T> dataList) {
		save(DefaultPartition, tableName, dataList);
	}
	
	@SuppressWarnings("unchecked")
	public <T> void save(String partition, String tableName, Collection<T> dataList) {
		if(dataList == null || dataList.isEmpty()) {
			return;
		}
		
		Class<?> converterClass = dataList.iterator().next().getClass();
		RecordConverter<T> converter = (RecordConverter<T>)converters.get(converterClass);
		
		if (converter == null) {
			throw new RuntimeException("fast db can not found converter class for " + converterClass);
		}
		
		FastTable fastTable = null;
		
		String path = tableFilePath(partition, tableName);
		if(!new File(path).exists()) {
			fastTable = new FastTableBuilder().tableName(tableName).addColumns(converter.getColumns()).create(path);
		} else {
			fastTable = FastTable.load(path);
		}
		
		try {
			fastTable.save(dataList, converter);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public <T> List<T> queryAll(String tableName, Class<T> clazz) {
		return queryAll(DefaultPartition, tableName, clazz);
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> queryAll(String partition, String tableName, Class<T> clazz) {
		if(StringUtil.isEmpty(tableName)) {
			return new ArrayList<>();
		}
		
		RecordConverter<T> converter = (RecordConverter<T>)converters.get(clazz);
		
		String fastTablePath = tableFilePath(partition, tableName);
		if(!new File(fastTablePath).exists()) {
			return new ArrayList<>();
		}
		
		FastTable recordFile = FastTable.load(fastTablePath);
		
		List<T> result = new ArrayList<>();
		
		try {
			result = recordFile.queryAll((recordBuffer)->{
				return converter.builderObject(recordBuffer);
			}, converter.recordLength());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return result;
	}
	
	public <T,K extends Comparable<? super K>, R> R query(String tableName, Class<T> clazz, ISearcher<T, K, R> searcher) {
		return query(DefaultPartition, tableName, clazz, searcher);
	}
	
	@SuppressWarnings("unchecked")
	public <T,K extends Comparable<? super K>, R> R query(String partition, String tableName, Class<T> clazz, ISearcher<T, K, R> searcher) {
		if(StringUtil.isEmpty(tableName)) {
			return searcher.defaultResult();
		}
		
		RecordConverter<T> converter = (RecordConverter<T>)converters.get(clazz);
		
		FastTable recordFile = FastTable.load(tableFilePath(partition, tableName));
		
		if(!recordFile.exists()) {
			return searcher.defaultResult();
		}
		
		try {
			return recordFile.query(searcher, (recordBuffer)->{
				return converter.builderObject(recordBuffer);
			},converter.recordLength());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return searcher.defaultResult();
	}
	
	public boolean dropTable(String tableName) {
		return dropTable(DefaultPartition, tableName);
	}
	
	public boolean dropTable(String partition, String tableName) {
		String tableFilePath = tableFilePath(partition, tableName);
		return FileUtil.delete(tableFilePath);
	}

	public void clear() {
		FileUtil.delete(this.path);
	}
	
}
