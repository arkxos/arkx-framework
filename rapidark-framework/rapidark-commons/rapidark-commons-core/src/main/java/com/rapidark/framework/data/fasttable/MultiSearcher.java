package com.rapidark.framework.data.fasttable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rapidark.framework.commons.util.TimeWatch;

/**
 *  
 * @author Darkness
 * @date 2016年12月6日 下午1:01:42
 * @version V1.0
 */
public abstract class MultiSearcher<T, K extends Comparable<? super K>> implements ISearcher<T, K, List<T>> {

	@Override
	public List<T> defaultResult() {
		return new ArrayList<>();
	}
	
	@Override
	public List<T> search(T foundRecord, long foundRecordIndex, long low, long high, FastTable recordFile, RecordFunction<T> converter, int recordLength) {
		List<T> result = new ArrayList<>();
		
		int batchSize = 100;
		long mid = foundRecordIndex;
		
		try {
			if (mid > 0) {
				TimeWatch timeWatch = TimeWatch.create().startWithTaskName("look foward ");
			    
			    //看前一个元素是否＝目标元素
				T preRecord = recordFile.queryOne(mid - 1, converter, recordLength);
			    if (this.buildSearchValue(preRecord).equals(this.getSearchValue())) {
			        for (long i = mid; i >= 0; i=i-batchSize) {
			        	List<T> preRecords = recordFile.queryN(i - batchSize, batchSize, converter, recordLength);
			        	for (T _preRecord : preRecords) {
			        		if (this.buildSearchValue(_preRecord).equals(this.getSearchValue())) {
			                    result.add(_preRecord);
			                } 
						}
			        	if(preRecords.isEmpty()) {
			        		break;
			        	}
			        	if(!this.buildSearchValue(preRecords.get(0)).equals(this.getSearchValue())) {
			        		break;
			        	}
			        }
			    }
			    
			    timeWatch.stopAndPrint();
			}
			
			result.add(foundRecord);
			
			if (mid < high) {
				TimeWatch timeWatch = TimeWatch.create().startWithTaskName("look after ");
			    //看后一个元素是否＝目标元素
				T nextRecord = recordFile.queryOne(mid + 1, converter, recordLength);
			    if (this.buildSearchValue(nextRecord).equals(this.getSearchValue())) {
			        for (long i = mid + 1; i <= high; i=i+batchSize) {
			        	List<T> preRecords = recordFile.queryN(i, batchSize, converter, recordLength);
			        	for (T _preRecord : preRecords) {
			        		if (this.buildSearchValue(_preRecord).equals(this.getSearchValue())) {
			                    result.add(_preRecord);
			                } 
						}
			        	if(preRecords.isEmpty()) {
			        		break;
			        	}
			        	T lastRecord = preRecords.get(preRecords.size()-1);
			        	if(!this.buildSearchValue(lastRecord).equals(this.getSearchValue())) {
			        		break;
			        	}
			        }
			    }
			    
			    timeWatch.stopAndPrint();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
}
