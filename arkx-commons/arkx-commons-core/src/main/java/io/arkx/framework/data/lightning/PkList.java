package io.arkx.framework.data.lightning;

import io.arkx.framework.commons.collection.DataRow;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PkList {

	ByteBuffer pkbuffer;
	LightningColumnType columnType;
	String columnName;
	List values;
	
	public PkList(LightningColumn column, int typeLength, int rowLength) {
		if(column.ColumnType == LightningColumnType.STRING) {
			typeLength += 4;
		}
		int totalPkSize = typeLength * rowLength;
		
		pkbuffer = ByteBuffer.allocate(totalPkSize);
		this.columnType = column.ColumnType;
		this.columnName = column.ColumnName;
		values = new ArrayList<>(totalPkSize);
	}

	public void addAll(DataRow[] rows, LightningColumn column) {
		for (DataRow row : rows) {
			if(column.getColumnType() == LightningColumnType.INT) {
				int value = row.getInt(column.getColumnName());
				pkbuffer.putInt(value);
				values.add(value);
			} else if (column.getColumnType() == LightningColumnType.FLOAT) {
				float value = row.getFloat(column.getColumnName());
				pkbuffer.putFloat(value);
				values.add(value);
			} else if (column.getColumnType() == LightningColumnType.DOUBLE) {
				double value = row.getDouble(column.getColumnName());
				pkbuffer.putDouble(value);
				values.add(value);
			} else if (column.getColumnType() == LightningColumnType.LONG) {
				long value = row.getLong(column.getColumnName());
				pkbuffer.putLong(value);
				values.add(value);
			} else if (column.getColumnType() == LightningColumnType.DATE) {
				LocalDate value = row.getLocalDate(column.getColumnName());
				pkbuffer.putLong(value.toEpochDay());
				values.add(value.toEpochDay());
			} else if (column.getColumnType() == LightningColumnType.DATETIME) {
				Date value = row.getDate(column.getColumnName());
				pkbuffer.putLong(value.getTime());
				values.add(value);
			} else if (column.getColumnType() == LightningColumnType.FIXED_STRING) {
				String value = row.getString(column.getColumnName());
				byte[] stringBytes = value.getBytes();
				if (stringBytes.length < column.length) {
					pkbuffer.put(stringBytes);
					int emptyCount = column.length - stringBytes.length;
					byte emptyByte = 32;// " ".getBytes()
					for (int i = 0; i < emptyCount; i++) {
						pkbuffer.put(emptyByte);
					}
				} else {
					if (stringBytes.length > column.length) {
						for (int i = 0; i < column.length; i++) {
							pkbuffer.put(stringBytes[i]);
						}
					} else {
						pkbuffer.put(stringBytes);
					}
				}
				values.add(value);
			} else if(column.getColumnType() == LightningColumnType.STRING){
				String value = row.getString(column.getColumnName());
				byte[] stringBytes = value.getBytes();
				if (stringBytes.length < column.length) {
					pkbuffer.putInt(stringBytes.length);
					pkbuffer.put(stringBytes);
					int emptyCount = column.length - stringBytes.length;
					byte emptyByte = 32;// " ".getBytes()
					for (int i = 0; i < emptyCount; i++) {
						pkbuffer.put(emptyByte);
					}
				} else {
					pkbuffer.putInt(column.length);
					if (stringBytes.length > column.length) {
						for (int i = 0; i < column.length; i++) {
							pkbuffer.put(stringBytes[i]);
						}
					} else {
						pkbuffer.put(stringBytes);
					}
				}
				values.add(value);
			}
		}
		
	}

	public void add(Object value) {
		values.add(value);
	}

	public void union(PkList pksBuffer) {
		values.addAll(pksBuffer.values);
	}

	public static void main(String[] args) {
		List values = new ArrayList<>();
		values.add(1);
		
		List<Integer> intValues = (List<Integer>)values;
		int intValue = intValues.get(0);
		System.out.println(intValue);
	}
}
