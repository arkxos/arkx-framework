package io.arkx.framework.data.fasttable;

import java.nio.ByteBuffer;
import java.util.List;

import static io.arkx.framework.data.fasttable.BufferReader.*;

/**
 * @author Darkness
 * @date 2017年7月12日 下午3:10:36
 * @version 1.0
 * @since 1.0 
 */
public class FastTableHeaderEncoder {
	
	public static final byte[] FILE_TYPE = new byte[]{'A','F','T'};
	public static final int VERSION = 1;
	
	/**
	 * [headerLength][AFT][version][自定义备注，128 byte][rowSize][tableNameLength][tableName][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 上午11:38:28
	 * @version V1.0
	 * @since infinity 1.0
	 */
	public static ByteBuffer encode(FastTableHeader header) {
		
		String tableName = header.getTableName();
		List<FastColumn> columns = header.columns();
		long rowCount = header.getRowSize();
		
		int headerLength = caculateHeaderLength(tableName, columns);
		
		ByteBuffer buffer = ByteBuffer.allocate(INT_LENGTH + headerLength);
		// [headerLength]
//		buffer.putInt(headerLength);
		
		// [AFT]
		buffer.put(FILE_TYPE);
		
		// [version]
		buffer.putInt(VERSION);
		
		// [自定义数据区域，128 byte]
		byte[] customData = new byte[128];
		buffer.put(customData);

		// [rowSize]
		buffer.putLong(rowCount);
		
		// [tableNameLength][tableName]
		buffer.putInt(tableName.length());
		buffer.put(tableName.getBytes());

		// [columnSize]
		buffer.putInt(columns.size());
				
		for (FastColumn column : columns) {
			/**
			 * [columnNameLength][columnName][columnType][columnLength][columnIndexType]...)
			 */
			String columnName = column.getName();
			
			//[columnNameLength][columnName]
			buffer.putInt(columnName.length());
			buffer.put(columnName.getBytes());
			
			// [columnType]
			buffer.put(column.getType().code());
			if(column.getType() == FastColumnType.String
					|| column.getType() == FastColumnType.FixedString) {
				buffer.putInt(column.getLength());
			}
			
			// [columnIndexType]
			buffer.put(column.getIndexType().getValue());
		}
		
		buffer.position(0);
		return buffer;
	}
	
	// 自定义数据区域，128字节
	private static int CUSTOM_DATA_LENGTH = 128;
	
	/**
	 * 计算文件头长度
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 下午12:04:21
	 * @version V1.0
	 * @since infinity 1.0
	 */
	private static int caculateHeaderLength(String tableName, List<FastColumn> columns) {
		int headerLength = 0;
		
		// [AFT]
		headerLength += FILE_TYPE.length;
		
		// [version]
		headerLength += INT_LENGTH;

		// [自定义数据区域，128 byte]
		headerLength += CUSTOM_DATA_LENGTH;
		
		// [rowSize]
		headerLength += LONG_LENGTH;

		// [tableNameLength][tableName]
		headerLength += INT_LENGTH + tableName.length();
		
		// [columnSize]
		headerLength += INT_LENGTH;
		
		for (FastColumn column : columns) {
			/**
			 * [columnNameLength][columnName][columnType][columnLength][columnIndexType]...)
			 */
			String columnName = column.getName();
			
			//[columnNameLength][columnName]
			headerLength += INT_LENGTH + columnName.length();
			
			// [columnType]
			headerLength += BYTE_LENGTH;
			if(column.getType() == FastColumnType.String
					|| column.getType() == FastColumnType.FixedString) {
				headerLength += INT_LENGTH;	
			}
			
			// [columnIndexType]
			headerLength += BYTE_LENGTH;
		}
		
		return headerLength;
	}
}
