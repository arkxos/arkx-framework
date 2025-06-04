package io.arkx.framework.data.oldfastdb;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

import io.arkx.framework.data.fasttable.FastColumn;
import io.arkx.framework.data.fasttable.FastTableHeader;
import io.arkx.framework.data.fasttable.FastTableHeaderDecoder;
import io.arkx.framework.data.fasttable.FastTableHeaderEncoder;
import io.arkx.framework.data.fasttable.MappedFile;

/**
 *  
 * @author Darkness
 * @date 2015年12月19日 下午5:10:11
 * @version V1.0
 * @since infinity 1.0
 */
public class FastTableFile extends MappedFile {

	
	
	public FastTableFile(String path) {
		super(path);
	}

	public FastTableFile(String path, boolean isAppend) {
		super(path, isAppend);
	}
	
	public FastTableHeader readTableInfo(FileChannel fileChannel,FastTableHeaderDecoder tableInfoReader) {
		try {
			int tableInfoLength = readInt(fileChannel);
			ByteBuffer tableInfoByteBuffer = ByteBuffer.allocate(tableInfoLength);
			fileChannel.read(tableInfoByteBuffer);
			tableInfoByteBuffer.flip();
			return tableInfoReader.decode(tableInfoByteBuffer);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	public FastTableHeader readTableInfo() {
		try {
//			openReadFileChannel();
			
			FastTableHeaderDecoder tableInfoReader = new FastTableHeaderDecoder();
			FastTableHeader tableInfo = readTableInfo(fileChannel, tableInfoReader);
			return tableInfo;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return null;
	}

	public long readRowSize() {
		if(!exists()) {
			return 0L;
		}
		
		try {
//			openReadFileChannel();
			
			FastTableHeaderDecoder headerReader = new FastTableHeaderDecoder();
			FastTableHeader header = readTableInfo(fileChannel, headerReader);
			
			return header.getRowSize();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return 0L;
	}
	
	


	// [headerLength][LTF][rowSize][tableNameLength][tableName]
	protected static int rowCountIndex = INT_LENGTH + FastTableHeaderEncoder.FILE_TYPE.length;
	
	
	
	public <T extends IFastTable> void save(T dt) {
		FastColumn[] columns = dt.getLightningColumns();
		FastColumn rowIndexColumn = FastColumn.intColumn("rowIndex");
		FastColumn[] fixColumns = new FastColumn[columns.length+1];
		fixColumns[0] = rowIndexColumn;
		for (int i=0;i<columns.length;i++) {
			fixColumns[i+1] = columns[i];
		}
		
		try {
			RandomAccessFile raf = new RandomAccessFile(path(), "rw");
			FileChannel fileChannel = raf.getChannel();
			long fileLength = raf.length(); 
			if(fileLength==0) {// 文件不存在，写入文件头
				String tableName = dt.getTableName();
				int rowCount = dt.getRowCount();
				
				FastTableHeader header = new FastTableHeader();
				header.setTableName(tableName);
				header.setColumns(fixColumns);
				header.setRowSize(rowCount);
				
				ByteBuffer headerBuffer = FastTableHeaderEncoder.encode(header);
				int headerLength = headerBuffer.limit();
				MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, 0, headerLength);
				mappedByteBuffer.put(headerBuffer);
				
				fileLength = headerLength;
			} else {// 文件存在，修改记录数
				MappedByteBuffer mappedByteBuffer = fileChannel.map(FileChannel.MapMode.READ_WRITE, rowCountIndex, LONG_LENGTH);
				long existRowCount = mappedByteBuffer.getLong();
				mappedByteBuffer.position(0);
				mappedByteBuffer.putLong(existRowCount + dt.getRowCount());
			}
			
			fileChannel.close();
			raf.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
