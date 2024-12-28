package com.rapidark.framework.data.oldfastdb;

import java.nio.ByteBuffer;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.rapidark.framework.commons.collection.DataRow;
import com.rapidark.framework.data.fasttable.FastColumn;
import com.rapidark.framework.data.fasttable.FastColumnType;

public class PkList {

	ByteBuffer pkbuffer;
	FastColumnType columnType;
	String columnName;
	List values;
	
	public PkList(FastColumn column, int typeLength, int rowLength) {
		if(column.getType() == FastColumnType.String) {
			typeLength += 4;
		}
		int totalPkSize = typeLength * rowLength;
		
		pkbuffer = ByteBuffer.allocate(totalPkSize);
		this.columnType = column.getType();
		this.columnName = column.getName();
		values = new ArrayList<>(totalPkSize);
	}

	public void addAll(DataRow[] rows, FastColumn column) {
		for (DataRow row : rows) {
			if(column.getType() == FastColumnType.Int) {
				int value = row.getInt(column.getName());
				pkbuffer.putInt(value);
				values.add(value);
			} else if (column.getType() == FastColumnType.Float) {
				float value = row.getFloat(column.getName());
				pkbuffer.putFloat(value);
				values.add(value);
			} else if (column.getType() == FastColumnType.Double) {
				double value = row.getDouble(column.getName());
				pkbuffer.putDouble(value);
				values.add(value);
			} else if (column.getType() == FastColumnType.Long) {
				long value = row.getLong(column.getName());
				pkbuffer.putLong(value);
				values.add(value);
			} else if (column.getType() == FastColumnType.Date) {
				LocalDate value = row.getLocalDate(column.getName());
				pkbuffer.putLong(value.toEpochDay());
				values.add(value.toEpochDay());
			} else if (column.getType() == FastColumnType.DateTime) {
				Date value = row.getDate(column.getName());
				pkbuffer.putLong(value.getTime());
				values.add(value);
			} else if (column.getType() == FastColumnType.FixedString) {
				String value = row.getString(column.getName());
				byte[] stringBytes = value.getBytes();
				if (stringBytes.length < column.getLength()) {
					pkbuffer.put(stringBytes);
					int emptyCount = column.getLength() - stringBytes.length;
					byte emptyByte = 32;// " ".getBytes()
					for (int i = 0; i < emptyCount; i++) {
						pkbuffer.put(emptyByte);
					}
				} else {
					if (stringBytes.length > column.getLength()) {
						for (int i = 0; i < column.getLength(); i++) {
							pkbuffer.put(stringBytes[i]);
						}
					} else {
						pkbuffer.put(stringBytes);
					}
				}
				values.add(value);
			} else if(column.getType() == FastColumnType.String){
				String value = row.getString(column.getName());
				byte[] stringBytes = value.getBytes();
				if (stringBytes.length < column.getLength()) {
					pkbuffer.putInt(stringBytes.length);
					pkbuffer.put(stringBytes);
					int emptyCount = column.getLength() - stringBytes.length;
					byte emptyByte = 32;// " ".getBytes()
					for (int i = 0; i < emptyCount; i++) {
						pkbuffer.put(emptyByte);
					}
				} else {
					pkbuffer.putInt(column.getLength());
					if (stringBytes.length > column.getLength()) {
						for (int i = 0; i < column.getLength(); i++) {
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
