package io.arkx.framework.data.lightning;

import io.arkx.framework.data.fasttable.MappedFile;

import java.io.RandomAccessFile;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;

/**
 *  
 * @author Darkness
 * @date 2015年12月19日 下午5:10:11
 * @version V1.0
 * @since infinity 1.0
 */
public class LightningTableFile extends MappedFile {

	protected static final byte[] FILE_TYPE = new byte[]{'L','T','D'};
	
	public LightningTableFile(String path) {
		super(path);
	}

	public LightningTableFile(String path, boolean isAppend) {
		super(path, isAppend);
	}
	
	public TableInfo readTableInfo() {
		try {
//			openReadFileChannel();
			
			TableInfoReader tableInfoReader = new TableInfoReader();
			TableInfo tableInfo = tableInfoReader.readTableInfo(fileChannel);
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
			
			TableInfoReader headerReader = new TableInfoReader();
			TableInfo header = headerReader.readTableInfo(fileChannel);
			
			return header.rowSize;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close();
		}
		
		return 0L;
	}
	
	/**
	 * 计算文件头长度
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 下午12:04:21
	 * @version V1.0
	 * @since infinity 1.0
	 */
	protected static <T extends ILightningTable> int caculateTableInfoLength(String tableName, LightningColumn[] columns) {
		int headerLength = 0;
		
		// [IDT]
		headerLength += FILE_TYPE.length;
		
		// [tableNameLength][tableName]
		
		headerLength += INT_LENGTH + tableName.length();
		
		// [rowSize]
		headerLength += LONG_LENGTH;
		
		// [columnSize]
		headerLength += INT_LENGTH;
			
		
		for (LightningColumn column : columns) {
			/**
			 * [columnNameLength][columnName][columnType][columnLength]...)
			 */
			String columnName = column.getColumnName();
			
			//[columnNameLength][columnName]
			headerLength += INT_LENGTH + columnName.length();
			
			// [columnType]
			headerLength += BYTE_LENGTH;
			if(column.getColumnType() == LightningColumnType.STRING
					|| column.getColumnType() == LightningColumnType.FIXED_STRING) {
				headerLength += INT_LENGTH;	
			}
		}
		
		return headerLength;
	}


	// [headerLength][LTF][rowSize][tableNameLength][tableName]
	protected static int rowCountIndex = INT_LENGTH + FILE_TYPE.length;
	
	/**
	 * [headerLength][IDT][rowSize][tableNameLength][tableName][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 上午11:38:28
	 * @version V1.0
	 * @since infinity 1.0
	 */
	public static <T extends ILightningTable> ByteBuffer buildTableInfoByteBuffer(String tableName, LightningColumn[] columns, int rowCount) {
		
		int headerLength = caculateTableInfoLength(tableName, columns);
		
		ByteBuffer buffer = ByteBuffer.allocate(INT_LENGTH + headerLength);
		// [headerLength]
		buffer.putInt(headerLength);
		
		// [LTF]
		buffer.put(FILE_TYPE);

		// [rowSize]
		buffer.putLong(rowCount);
		
		// [tableNameLength][tableName]
		buffer.putInt(tableName.length());
		buffer.put(tableName.getBytes());

		// [columnSize]
		buffer.putInt(columns.length);
				
		for (LightningColumn column : columns) {
			/**
			 * [columnNameLength][columnName][columnType][columnLength]...)
			 */
			String columnName = column.getColumnName();
			
			//[columnNameLength][columnName]
			buffer.putInt(columnName.length());
			buffer.put(columnName.getBytes());
			
			// [columnType]
			buffer.put(column.getColumnType().code());
			if(column.getColumnType() == LightningColumnType.STRING
					|| column.getColumnType() == LightningColumnType.FIXED_STRING) {
				buffer.putInt(column.length());
			}
		}
		
		buffer.position(0);
		return buffer;
	}
	
	public <T extends ILightningTable> void save(T dt) {
		LightningColumn[] columns = dt.getLightningColumns();
		
		try {
			RandomAccessFile raf = new RandomAccessFile(path(), "rw");
			FileChannel fileChannel = raf.getChannel();
			long fileLength = raf.length(); 
			if(fileLength==0) {// 文件不存在，写入文件头
				String tableName = dt.getTableName();
				int rowCount = dt.getRowCount();
				ByteBuffer headerBuffer = buildTableInfoByteBuffer(tableName, columns, rowCount);
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
