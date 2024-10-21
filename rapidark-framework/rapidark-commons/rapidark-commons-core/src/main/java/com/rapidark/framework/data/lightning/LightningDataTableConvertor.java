package com.rapidark.framework.data.lightning;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rapidark.framework.commons.collection.DataColumn;
import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.commons.collection.DataTypes;
import com.rapidark.framework.commons.collection.TwoTuple;

/**
 *  文件头
 *  [IDT][tableNameLength][tableName][rowSize][columnSize]([columnNameLength][columnName][columnType][columnLength]...)
 *  数据
 *  [intValue][floatValue][doubleValue][fixedStringValue]([stringLength][stringValue][empty])...
 *  
 *  数据行加一个标志，删除的时候，设置当前标识为删除状态，数据库引擎定时清除删除状态的数据，重新构建文件
 * @author Darkness
 * @date 2015年11月8日 下午4:14:02
 * @version V1.0
 * @since infinity 1.0
 */
public class LightningDataTableConvertor implements ILightningTableConvertor<LightningDataTable> {
	
	@Override
	public LightningDataTable createTable(Class<LightningDataTable> type, String tableName, LightningColumn[] columns) {
		List<DataColumn> dataColumns = new ArrayList<>();

		for (LightningColumn lightningColumn : columns) {
			DataTypes dataColumnType = DataTypes.valueOf(lightningColumn.getColumnType().code());

			DataColumn dataColumn = new DataColumn(lightningColumn.getColumnName(), dataColumnType, lightningColumn.length);
			dataColumns.add(dataColumn);
		}

		try {
			LightningDataTable instance = type.newInstance();
			instance.init(tableName, dataColumns.toArray(new DataColumn[0]));
			return instance;
		} catch (InstantiationException | IllegalAccessException e) {
			e.printStackTrace();
		}
		
		return null;
	}
	
	@Override
	public void onRecordReaded(List<LightningColumn> columns, LightningDataTable dt, RecordUnit rowBuffer) throws IOException {
		Object[] rowDatas = rowBuffer.readDatas(columns);
				
		if(rowDatas!=null) {
			dt.insertRow(rowDatas);
		}
	}
	
	/**
	 * 计算行长度
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 下午1:31:53
	 * @version V1.0
	 * @since infinity 1.0
	 */
	protected static int caculateRowLength(LightningColumn[] columns) {
		int result = 0;
		for (LightningColumn column : columns) {
			if(column.getColumnType() == LightningColumnType.STRING) {
				result += 4;// INT_LENGTH;	
			}
			result += column.length;
		}
		return result;
	}
	
	protected static String readString(ByteBuffer buffer, int length) {
		byte[] columnNameBytes = new byte[length];
		buffer.get(columnNameBytes);
		
		return new String(columnNameBytes);
	}
	
	/**
	 * [intValue][floatValue][doubleValue][fixedStringValue]([stringLength][stringValue][empty])...
	 *  
	 * @author Darkness
	 * @date 2015年12月5日 下午1:11:15
	 * @version V1.0
	 * @since infinity 1.0
	 */
	@Override
	public TwoTuple<PkList, List<ByteBuffer>> buildRowByteBuffers(LightningColumn[] columns, LightningDataTable dt) {
		DataRow[] rows = dt.getDataRows();
		
		List<ByteBuffer> rowBuffers = new ArrayList<>();
		// 第一列为主键
		
		int typeLength = columns[0].length();
		if(typeLength == -1) {
			String value = dt.getString(0, 0);
			byte[] stringBytes = value.getBytes();
			typeLength = stringBytes.length;
		}
		
		
		
		int rowDataLength = caculateRowLength(columns);
		for (DataRow row : rows) {
			ByteBuffer rowbuffer = ByteBuffer.allocate(rowDataLength);
			rowBuffers.add(rowbuffer);
			
			for (LightningColumn column : columns) {
				if(column.getColumnType() == LightningColumnType.INT) {
					int value = row.getInt(column.getColumnName());
					rowbuffer.putInt(value);
				} else if(column.getColumnType() == LightningColumnType.FLOAT) {
					float value = row.getFloat(column.getColumnName());
					rowbuffer.putFloat(value);
				} else if(column.getColumnType() == LightningColumnType.DOUBLE) {
					double value = row.getDouble(column.getColumnName());
					rowbuffer.putDouble(value);
				} else if(column.getColumnType() == LightningColumnType.LONG) {
					long value = row.getLong(column.getColumnName());
					rowbuffer.putLong(value);
				} else if(column.getColumnType() == LightningColumnType.DATE) {
					LocalDate value = row.getLocalDate(column.getColumnName());
					rowbuffer.putLong(value.toEpochDay());
				} else if(column.getColumnType() == LightningColumnType.DATETIME) {
					Date value = row.getDate(column.getColumnName());
					rowbuffer.putLong(value.getTime());
				} else if(column.getColumnType() == LightningColumnType.FIXED_STRING){
					String value = row.getString(column.getColumnName());
					byte[] stringBytes = value.getBytes();
					if(stringBytes.length<column.length) {
						rowbuffer.put(stringBytes);
						int emptyCount = column.length - stringBytes.length;
						byte emptyByte = 32;//" ".getBytes()
						for (int i = 0; i < emptyCount; i++) {
							rowbuffer.put(emptyByte);
						}
					} else {
						if(stringBytes.length>column.length) {
					        for (int i=0; i<column.length; i++) {
					        		rowbuffer.put(stringBytes[i]);
					        }
						} else {
							rowbuffer.put(stringBytes);
						}
					}
				} else if(column.getColumnType() == LightningColumnType.STRING){
					String value = row.getString(column.getColumnName());
					byte[] stringBytes = value.getBytes();
					if(stringBytes.length<column.length) {
						rowbuffer.putInt(stringBytes.length);
						rowbuffer.put(stringBytes);
						int emptyCount = column.length - stringBytes.length;
						byte emptyByte = 32;//" ".getBytes()
						for (int i = 0; i < emptyCount; i++) {
							rowbuffer.put(emptyByte);
						}
					} else {
						rowbuffer.putInt(column.length);
						if(stringBytes.length>column.length) {
					        for (int i=0; i<column.length; i++) {
					        		rowbuffer.put(stringBytes[i]);
					        }
						} else {
							rowbuffer.put(stringBytes);	
						}
					}
				}
			}
			
			rowbuffer.position(0);
		}
		
		PkList pkList = new PkList(columns[0], typeLength, rows.length);
		pkList.addAll(rows, columns[0]);
		
		return new TwoTuple<>(pkList, rowBuffers);
	}
		
}
