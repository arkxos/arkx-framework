package com.arkxos.framework.data.fasttable;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 *  
 * @author Darkness
 * @date 2016年11月10日 下午3:21:02
 * @version V1.0
 */
public class RecordConverter<T> implements RecordFunction<T> {
	
	public Class<T> acceptEntityClass() {
		return null;
	}
	
	public List<FastColumn> getColumns() {
		return new ArrayList<>();
	}
	
	public int recordLength() {
		int recordLength = 0;
		for (FastColumn fastColumn : getColumns()) {
			recordLength += fastColumn.getLength();
		}
		return recordLength;
	}
	
	public void writeString(ByteBuffer recordBuffer, String value, int length) {
		if(value != null) {
			byte[] valueBytes = value.getBytes();
			if (valueBytes.length < length) {// 值长度小于列长度，补齐空格
				recordBuffer.put(valueBytes);
				int emptyCount = length - valueBytes.length;
				writeEmptyString(recordBuffer, emptyCount);
			} else {// 值长度大于列长度，截断
				for (int i = 0; i < length; i++) {
					recordBuffer.put(valueBytes[i]);
				}
			}
		} else {
			writeEmptyString(recordBuffer, length);
		}
	}
	
	private void writeEmptyString(ByteBuffer recordBuffer, int length) {
		int emptyCount = length;
		byte emptyByte = 32;// " ".getBytes()
		for (int j = 0; j < emptyCount; j++) {
			recordBuffer.put(emptyByte);
		}
	}
	
	public String readString(ByteBuffer recordBuffer, int length) {
		byte[] dst = new byte[length];
		recordBuffer.get(dst);
		String value = new String(dst).trim();
		return value;
	}
	
	public ByteBuffer builderBuffer(T engity) {
		
		int recordLength = recordLength();
		
		ByteBuffer recordBuffer = ByteBuffer.allocate(recordLength);
		
		writeEntity2Buffer(engity, recordBuffer);
		
		recordBuffer.flip();
		
		return recordBuffer;
	}
	
	public T builderObject(ByteBuffer recordBuffer) {
		return null;
	}
	
	protected void writeEntity2Buffer(T engity, ByteBuffer recordBuffer) {
	}

	@Override
	public T apply(ByteBuffer recordBuffer) {
		return builderObject(recordBuffer);
	}
	
}
