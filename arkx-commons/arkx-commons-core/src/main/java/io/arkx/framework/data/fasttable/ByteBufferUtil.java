package io.arkx.framework.data.fasttable;

import java.nio.ByteBuffer;

/**
 *  
 * @author Darkness
 * @date 2016年11月10日 下午3:05:08
 * @version V1.0
 */
public class ByteBufferUtil {
	
	public static String readString(ByteBuffer buffer, int length) {
		byte[] columnNameBytes = new byte[length];
		buffer.get(columnNameBytes);
		
		return new String(columnNameBytes);
	}
	
}
