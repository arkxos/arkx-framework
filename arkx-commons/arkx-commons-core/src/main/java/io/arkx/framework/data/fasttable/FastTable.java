package io.arkx.framework.data.fasttable;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.arkx.framework.commons.collection.FourTuple;
import io.arkx.framework.commons.util.TimeWatch;

/**
 *  
 * @author Darkness
 * @date 2016年11月11日 上午10:43:18
 * @version V1.0
 */
public class FastTable extends MappedFile {
	
	public static FastTable create(String path, String tableName, List<FastColumn> columns) {
		File file = new File(path);
		if(file.exists()) {
			throw new FastTableException("fast table exist, path:" + path);
		}
		
		FastTable fastTable = new FastTable(path, tableName, columns);
		return fastTable;
	}
	
	public static FastTable load(String path) {
		File file = new File(path);
		if(!file.exists()) {
			throw new FastTableException("fast table not exist, path:" + path);
		}
		
		return new FastTable(path);
	}
	
	public static void delete(String path) {
		File file = new File(path);
//		if(!file.exists()) {
//			throw new FastTableException("fast table not exist, path:" + path);
//		}
		file.deleteOnExit();
	}
	
	private FastTableHeader header;
	
	private FastTable(String path, String tableName, List<FastColumn> columns) {
		super(path, true);
		
		this.header = new FastTableHeader();
		header.setTableName(tableName);
		header.setColumns(columns);
		
		ByteBuffer headerBuffer = FastTableHeaderEncoder.encode(header);
		
		try {
			openFileChannel();
			
			writeInt(headerBuffer.limit());
			write(headerBuffer);
		} catch (IOException e) {
			throw new FastTableException(e);
		}
	}
	
	private FastTable(String path) {
		super(path, true);
		
		try {
			openFileChannel();
			
			int headerLength = readInt(fileChannel);
			
			MappedByteBuffer headerBuffer = readBuffer(headerLength);
			this.header = FastTableHeaderDecoder.decode(headerBuffer);
			
			closeDirectBuffer(headerBuffer);
		} catch (IOException e) {
			e.printStackTrace();
			System.out.println("fasttable error:" + path);
		}
	}
	
	private FastTable(String path, boolean isAppend) {
		super(path, isAppend);
	}
	
	public void save(Iterator<ByteBuffer> recordInterator, int recordLength) throws IOException {
		if(recordInterator == null) {
			return;
		}
		
		int i = 0;
		List<ByteBuffer> batch = new ArrayList<>();
		while (recordInterator.hasNext()) {
			i++;
			ByteBuffer recordBuffer = recordInterator.next();
			
			batch.add(recordBuffer);
			
			if(i%100 ==0) {
				ByteBuffer allRowBuffer = ByteBuffer.allocate(batch.size()*recordLength);
				for (ByteBuffer rowBuffer : batch) {
					allRowBuffer.put(rowBuffer);
				}
				
				write(allRowBuffer);
				
				batch.clear();
			}
		}
		
		if(!batch.isEmpty()){
			ByteBuffer allRowBuffer = ByteBuffer.allocate(batch.size()*recordLength);
			for (ByteBuffer rowBuffer : batch) {
				allRowBuffer.put(rowBuffer);
			}
			
			write(allRowBuffer);
		}
		
		close();
	}

	public <T> void save(Collection<T> dataList, RecordConverter<T> converter) throws IOException {
		if(dataList == null || dataList.isEmpty()) {
			return;
		}
		
		Iterator<T> dataIterator = dataList.iterator();
		
		save(new Iterator<ByteBuffer>() {
			
			@Override
			public boolean hasNext() {
				return dataIterator.hasNext();
			}

			@Override
			public ByteBuffer next() {
				return converter.builderBuffer(dataIterator.next());
			}
			
		}, converter.recordLength());
	}

	public <T> List<T> queryAll(RecordFunction<T> converter, int recordLength) throws IOException {
		List<T> result = new ArrayList<>();
//		openReadFileChannel();
		
		int batchSize = 200;
		while(!isReadEnd()) {
			ByteBuffer recordBuffer = readBuffer(recordLength * batchSize);
			if(recordBuffer == null) {// read end
				break;
			}
			
			List<T> records = builderObjects(recordBuffer, converter);
			result.addAll(records);
			
			closeDirectBuffer(recordBuffer);
		}
		close();
		return result;
	}
	
	private <T> List<T> builderObjects(ByteBuffer recordBuffer, RecordFunction<T> converter) {
		List<T> result = new ArrayList<>();
		
		while(recordBuffer.hasRemaining()) {
			T entity = converter.apply(recordBuffer);
			result.add(entity);
		}
		
		return result;
	}
	
	
	
	public <T,K extends Comparable<? super K>, R> R query(ISearcher<T, K, R> searchWarpper, RecordFunction<T> converter, int recordLength) throws IOException {
		TimeWatch timeWatch = TimeWatch.create().startWithTaskName("openReadFileChannel ");
//		openReadFileChannel();
		timeWatch.stopAndPrint();
		
		long rowSize = getFileLength() / recordLength;
		
		TimeWatch timeWatch2 = TimeWatch.create().startWithTaskName("binarySearch ");
		FourTuple<T, Long, Long, Long> result = binarySearch(searchWarpper, rowSize, converter, recordLength);
		
		R r = searchWarpper.search(result.first, result.second, result.third, result.fourth, this, converter, recordLength);
		
		timeWatch2.stopAndPrint();
		
		TimeWatch timeWatch3 = TimeWatch.create().startWithTaskName("close ");
		close();
		timeWatch3.stopAndPrint();
		
		return r;
	}
	
	private <T,K extends Comparable<? super K>, R> FourTuple<T, Long, Long, Long> binarySearch(ISearcher<T, K, R> searchWarpper, long total, RecordFunction<T> converter, int recordLength) throws IOException {
        
        long low = 0, high = total - 1;
        while (low <= high) {
            long mid = (low + high) / 2;
            
            T record = queryOne(mid, converter, recordLength);
            
            K searchValue = searchWarpper.getSearchValue();
            K buildedValue = searchWarpper.buildSearchValue(record);
            
            if (searchWarpper.buildSearchValue(record).equals(searchWarpper.getSearchValue())) {
                return new FourTuple<T, Long, Long, Long>(record, mid, low, high);
            } else if (searchValue.compareTo(buildedValue) < 0) {
                high = mid - 1;
            } else {
                low = mid + 1;
            }
        }
        
        return new FourTuple<T, Long, Long, Long>(null, -1L, -1L, -1L);
    }
	
	public <T> List<T> queryN(long index, long count , RecordFunction<T> converter, int recordLength) throws IOException {
		if(index < 0) {
			index = 0;
		}
		long start = index * recordLength;
		
		ByteBuffer recordBuffer = readBuffer(start, count * recordLength);
		if(recordBuffer == null) {// read end
			return new ArrayList<>();
		}
		
		List<T> records = builderObjects(recordBuffer, converter);
		
		closeDirectBuffer(recordBuffer);
		
		return records;
	}
	
	public <T> T queryOne(long index, RecordFunction<T> converter, int recordLength) throws IOException {
//		TimeWatch timeWatch = TimeWatch.create().startWithTaskName("query one " + index);
		ByteBuffer recordBuffer = readBuffer(index * recordLength, recordLength);
		if (recordBuffer == null) {// read end
			return null;
		}
		// ByteBuffer recordBuffer =
		// ByteBuffer.allocate(converter.recordLength());

		T entity = converter.apply(recordBuffer);
		
		closeDirectBuffer(recordBuffer);
		
//		timeWatch.stopAndPrint();
		return entity;
		// System.out.println(personFromBuffer);
	}

}
