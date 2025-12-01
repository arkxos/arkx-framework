package io.arkx.framework.data.oldfastdb;

import io.arkx.framework.data.fasttable.MappedFile;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 *  
 * @author Darkness
 * @date 2016年6月22日 下午3:06:06
 * @version V1.0
 */
public class IndexGroupFile extends MappedFile {

	public IndexGroupFile(String path) {
		super(path);
	}

	public long writeIndex(Collection<Integer> valueRowIndexes) throws IOException {
		int size = 4 + valueRowIndexes.size()*4;
		long fileLength = getRealFileLength();
		
		ByteBuffer allRowBuffer = ByteBuffer.allocate(size);
		allRowBuffer.putInt(valueRowIndexes.size());
		for (Integer valueRowIndex : valueRowIndexes) {
			allRowBuffer.putInt(valueRowIndex);
		}
		
		MappedByteBuffer rowMappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, fileLength, size);
		allRowBuffer.flip();
		rowMappedByteBuffer.put(allRowBuffer);	
		
		return fileLength;
	}

	public List<Integer> readValues(List<Long> loadRecords) {
		List<Integer> result = new ArrayList<>();
		
		try {
//			TimeWatch timeWatch = new TimeWatch();
//			timeWatch.startWithTaskName("open file");
			openFileChannel();
//			timeWatch.stopAndPrint();
			
			for (Long index : loadRecords) {
				fileChannel.position(index);
				int size = readInt(fileChannel);
				ByteBuffer rowBuffer = ByteBuffer.allocate(size*4);
				
				fileChannel.read(rowBuffer);
				rowBuffer.position(0);
				
				for (int i=0;i<size;i++) {
					result.add(rowBuffer.getInt());
				}
			}
			
			close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
