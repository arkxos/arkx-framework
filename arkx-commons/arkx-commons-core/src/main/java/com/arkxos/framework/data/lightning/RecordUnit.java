package com.arkxos.framework.data.lightning;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;

import io.arkx.framework.commons.util.DateUtil;
import com.arkxos.framework.data.fasttable.BufferReader;
import com.arkxos.framework.data.fasttable.ByteBufferUtil;

public class RecordUnit extends BufferReader {

	private ByteBuffer buffer;
	
	public RecordUnit(ByteBuffer byteBuffer) {
		this.buffer = byteBuffer;
	}
	
	private int culumnIndex(String columnName, TableInfo tableInfo) {
		int columnDataIndex = tableInfo.getColumnDataIndex(columnName);
		return columnDataIndex;
	}
	
	private void position(String columnName, TableInfo tableInfo) {
		buffer.position(culumnIndex(columnName, tableInfo));
	}
	
	private int readInt(String columnName, TableInfo tableInfo) {
		position(columnName, tableInfo);
		return buffer.getInt();
	}
	
	private float readFloat(String columnName, TableInfo tableInfo) {
		position(columnName, tableInfo);
		return buffer.getFloat();
	}
	
	private double readDouble(String columnName, TableInfo tableInfo) {
		position(columnName, tableInfo);
		return buffer.getDouble();
	}
	
	private long readLong(String columnName, TableInfo tableInfo) {
		position(columnName, tableInfo);
		return buffer.getLong();
	}
	
	public String readFixedString(String columnName, int columnLength, TableInfo tableInfo) {
		position(columnName, tableInfo);
		
		byte[] stringBytes = new byte[columnLength];
		buffer.get(stringBytes);
		
		String value = new String(stringBytes);
		return value;
	}
	
	public String readString(String columnName, TableInfo tableInfo) {
		position(columnName, tableInfo);
		
		int actureStringLength = this.buffer.getInt();
		
		byte[] stringBytes = new byte[actureStringLength];
		buffer.get(stringBytes);
		
		String value = new String(stringBytes);
		return value;
	}

	public ByteBuffer getBytes() {
		this.buffer.position(0);
		return this.buffer;
	}
//
//	public static void main(String[] args) {
//		String line = "600789,2015-09-10,0,21.649,22.073,20.993,20.929,37328468.000,377767680.000,2.118";
//		String[] strings = line.split(",");

	public boolean isMatch(TableInfo tableInfo, FilterInfo filterInfo) {
		for (String columnName : filterInfo.needFilterColumnNames) {
			LightningColumn column = tableInfo.getColumn(columnName);
			if (column.getColumnType() == LightningColumnType.INT) {
				int value = readInt(columnName, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (value != Integer.parseInt(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					if (value <= Integer.parseInt(filterInfo.gtMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					if (value >= Integer.parseInt(filterInfo.ltMap.get(columnName))) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.FLOAT) {
				float value = readFloat(columnName, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (value != Float.parseFloat(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					if (value <= Float.parseFloat(filterInfo.gtMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					if (value >= Float.parseFloat(filterInfo.ltMap.get(columnName))) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.DOUBLE) {
				double value = readDouble(columnName, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (value != Double.parseDouble(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					if (value <= Double.parseDouble(filterInfo.gtMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					if (value >= Double.parseDouble(filterInfo.ltMap.get(columnName))) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.LONG) {
				long value = readLong(columnName, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (value != Long.parseLong(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					if (value <= Long.parseLong(filterInfo.gtMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					if (value >= Long.parseLong(filterInfo.ltMap.get(columnName))) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.DATE) {
				long value = readLong(columnName, tableInfo);

				if (filterInfo.equalMap.containsKey(columnName)) {
					long comparevalue = filterInfo.getEqualValue(columnName);
					if (value != comparevalue) {

						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					long comparevalue = LocalDate.parse(filterInfo.gtMap.get(columnName)).toEpochDay();
					if (value <= comparevalue) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					long comparevalue = LocalDate.parse(filterInfo.ltMap.get(columnName)).toEpochDay();
					if (value >= comparevalue) {
						return false;
					}
				}

			} else if (column.getColumnType() == LightningColumnType.DATETIME) {
				long value = readLong(columnName, tableInfo);

				if (filterInfo.equalMap.containsKey(columnName)) {
					long comparevalue = DateUtil.parse(filterInfo.equalMap.get(columnName)).getTime();
					if (value != comparevalue) {
						return false;
					}
				}
				if (filterInfo.gtMap.containsKey(columnName)) {
					long comparevalue = DateUtil.parse(filterInfo.gtMap.get(columnName)).getTime();
					if (value <= comparevalue) {
						return false;
					}
				}
				if (filterInfo.ltMap.containsKey(columnName)) {
					long comparevalue = DateUtil.parse(filterInfo.ltMap.get(columnName)).getTime();
					if (value >= comparevalue) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.FIXED_STRING) {
				String value = readFixedString(columnName, column.length, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (!value.equals(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.likeMap.containsKey(columnName)) {
					if (value.indexOf(filterInfo.likeMap.get(columnName)) == -1) {
						return false;
					}
				}
			} else if (column.getColumnType() == LightningColumnType.STRING) {
				// int emptyLength = column.length - actureStringLength;
				String value = readString(columnName, tableInfo);
				if (filterInfo.equalMap.containsKey(columnName)) {
					if (!value.equals(filterInfo.equalMap.get(columnName))) {
						return false;
					}
				}
				if (filterInfo.likeMap.containsKey(columnName)) {
					if (value.indexOf(filterInfo.likeMap.get(columnName)) == -1) {
						return false;
					}
				}
			}
		}

		return true;
	}

	public Object[] readDatas(List<LightningColumn> columns) {
		buffer.position(0);
		
		Object[] rowDatas = new Object[columns.size()];
		for (int j = 0; j < columns.size(); j++) {
			LightningColumn column = columns.get(j);
			Object value = null;
			if (column.getColumnType() == LightningColumnType.INT) {
				value = buffer.getInt();
			} else if (column.getColumnType() == LightningColumnType.FLOAT) {
				value = buffer.getFloat();
			} else if (column.getColumnType() == LightningColumnType.DOUBLE) {
				value = buffer.getDouble();
			} else if (column.getColumnType() == LightningColumnType.LONG) {
				value = buffer.getLong();
			} else if (column.getColumnType() == LightningColumnType.DATE) {
				value = LocalDate.ofEpochDay(buffer.getLong());
			} else if (column.getColumnType() == LightningColumnType.DATETIME) {
				value = new Date(buffer.getLong());
			} else if (column.getColumnType() == LightningColumnType.FIXED_STRING) {
				value = ByteBufferUtil.readString(buffer, column.length);
			} else if (column.getColumnType() == LightningColumnType.STRING) {
				int actureStringLength = buffer.getInt();
				int emptyLength = column.length - actureStringLength;
				value = ByteBufferUtil.readString(buffer, actureStringLength);
				buffer.position(buffer.position()+emptyLength);
			}
	
			rowDatas[j] = value;
		}
		
		return rowDatas;
	}
		
//		RecordUnit unit = new RecordUnit(strings[0], strings[1], strings[2], strings[3], strings[4], strings[5], strings[6], strings[7],
//				strings[8], strings[9]);
//
//		
//		
//		ByteBuffer eodQuoteUnitBuffer = RecordUnit.prepareBuffer();
//		eodQuoteUnitBuffer.put(unit.getBytes());
//		
//		RecordUnit newUnit = new RecordUnit(eodQuoteUnitBuffer);

//		EodQuote eodQuote = builde(newUnit, false);
//		System.out.println(eodQuote);
//		
//		System.out.println(unit.readSymbol());
//		System.out.println(unit.readSymbol());
//		System.out.println(unit.readDate());
//		System.out.println(unit.readYesterdayClose());
//		unit.setYesterdayClose(12.57f);
//		System.out.println(unit.readYesterdayClose());
//
//		eodQuoteUnitBuffer = EodQuoteUnit.prepareBuffer();
//		eodQuoteUnitBuffer.put(unit.getBytes());
//
//		newUnit = new EodQuoteUnit(eodQuoteUnitBuffer);
//
//		eodQuote = builde(newUnit, false);
//		System.out.println(eodQuote);
//	}
	
}
