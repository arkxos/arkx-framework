package io.arkx.framework.data.oldfastdb;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;

import io.arkx.framework.data.fasttable.FastColumn;
import io.arkx.framework.data.fasttable.FastColumnType;
import io.arkx.framework.data.fasttable.MappedFile;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

/**
 *  
 * @author Darkness
 * @date 2016年6月22日 下午1:23:17
 * @version V1.0
 */
public class ColumnIndexFile extends MappedFile{

	public ColumnIndexFile(String path) {
		super(path);
	}

	public ColumnIndexFile(String path, boolean isAppend) {
		super(path, isAppend);
	}
	
	public Multimap<Object, Long> readValueRowIndexes(FastColumn column) {
		try {
			Multimap<Object, Long> result = HashMultimap.create();
			
//			openReadFileChannel();
			
			while(!isReadEnd()) {
				if(column.getType() == FastColumnType.Date) {
					long date = readLong();
					result.put(date, readLong());
				} else if(column.getType() == FastColumnType.FixedString) {
					String value = readString(column.getLength());
					result.put(value, readLong());
				}
			}
			
			close();
			
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public void save(FastColumn fastColumn, Multimap<Object, Integer> indexValues, IndexGroupFile indexGroupFile) {
		try {
			RandomAccessFile raf = new RandomAccessFile(path(), "rw");
			FileChannel fileChannel = raf.getChannel();
			long fileLength = raf.length(); 
			// [data value][record length]{[index]...}
			// date long   int 4
			Map<Object, Long> groupIndex = new LinkedHashMap<>();
			
			for (Object columnValue : indexValues.keySet()) {
				Collection<Integer> valueRowIndexes = indexValues.get(columnValue);
				
				long filePosition = indexGroupFile.writeIndex(valueRowIndexes);
				groupIndex.put(columnValue, filePosition);
			}
			
			int count = groupIndex.size();
			int valueLength = fastColumn.getType().length();
			if(valueLength == -1) {
				valueLength = fastColumn.getLength();
			}
			int buffLength = (valueLength+8)*count;
			ByteBuffer buffer = ByteBuffer.allocate(buffLength);
			
			for (Object columnValue : groupIndex.keySet()) {
				long filePosition = groupIndex.get(columnValue);
				if(fastColumn.getType() == FastColumnType.Date) {
					buffer.putLong((Long)columnValue);
				} else if(fastColumn.getType() == FastColumnType.FixedString) {
//					allRowBuffer.put
					String value = (String)columnValue;
					byte[] stringBytes = value.getBytes();
					if(stringBytes.length<fastColumn.getLength()) {
						buffer.put(stringBytes);
						int emptyCount = fastColumn.getLength() - stringBytes.length;
						byte emptyByte = 32;//" ".getBytes()
						for (int i = 0; i < emptyCount; i++) {
							buffer.put(emptyByte);
						}
					} else {
						if(stringBytes.length>fastColumn.getLength()) {
					        for (int i=0; i<fastColumn.getLength(); i++) {
					        	buffer.put(stringBytes[i]);
					        }
						} else {
							buffer.put(stringBytes);
						}
					}
				}
				
				buffer.putLong(filePosition);
			}
			
			MappedByteBuffer rowMappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileLength, buffLength);
			buffer.flip();
			rowMappedByteBuffer.put(buffer);	
			
			fileChannel.close();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
