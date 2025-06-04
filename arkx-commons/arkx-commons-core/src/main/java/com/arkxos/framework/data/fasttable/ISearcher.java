package com.arkxos.framework.data.fasttable;

/**
 *  
 * @author Darkness
 * @date 2016年12月6日 下午2:53:11
 * @version V1.0
 */
public interface ISearcher<T, K extends Comparable<? super K>, R> {

	K buildSearchValue(T entity);
	
	K getSearchValue();
	
	R search(T foundRecord, long foundRecordIndex, long low, long high, FastTable recordFile, RecordFunction<T> converter, int recordLength);

	default R defaultResult() {
		return null;
	}
}
