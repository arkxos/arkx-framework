package org.ark.framework.core.castor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Date;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.DateUtil;
import io.arkx.framework.XTest;

/**
 * 
 * @author Darkness
 * @date 2013-3-26 下午07:03:34
 * @version V1.0
 */
public class CastorServiceTest extends XTest {

	/**
	 * 测试boolean转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午07:36:13 
	 * @version V1.0
	 */
	@Test
	public void castBoolean() {
		Object bool = CastorService.toType(1, boolean.class);
		assertTrue((Boolean) bool);
		
		Object bool2 = CastorService.toType("true", boolean.class);
		assertTrue((Boolean) bool2);
		
		Object bool3 = CastorService.toType(true, boolean.class);
		assertTrue((Boolean) bool3);

		boolean[] boolArray = (boolean[])CastorService.toType(new Integer[] { 1, 0, -1 }, boolean[].class);
		assertTrue(boolArray[0]);
		assertFalse(boolArray[1]);
		assertFalse(boolArray[2]);
		
		boolean[] boolArray2 = (boolean[])CastorService.toType(new Boolean[] { true, false }, boolean[].class);
		assertTrue(boolArray2[0]);
		assertFalse(boolArray2[1]);
		
		boolean[] boolArray3 = (boolean[])CastorService.toType(new Object[] { "true", "false", 1, -1.9, "null" }, boolean[].class);
		assertTrue(boolArray3[0]);
		assertFalse(boolArray3[1]);
		assertTrue(boolArray3[2]);
		assertFalse(boolArray3[3]);
		assertFalse(boolArray3[4]);
	}
	
	/**
	 * 测试int转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午07:45:02 
	 * @version V1.0
	 */
	@Test
	public void castInt() {
		int int1 = (Integer)CastorService.toType(1, int.class);
		assertEquals(1, int1);
		
		int int2 = (Integer)CastorService.toType("2", Integer.class);
		assertEquals(2, int2);
		
		int[] intArray = (int[])CastorService.toType(new Object[]{"1", 5, 5.8}, Integer[].class);
		assertEquals(1, intArray[0]);
		assertEquals(5, intArray[1]);
		assertEquals(5, intArray[2]);
	}
	
	/**
	 * 测试long转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午08:11:11 
	 * @version V1.0
	 */
	@Test
	public void castLong() {
		long long1 = (Long)CastorService.toType(1, Long.class);
		assertEquals(1, long1);
		
		long long2 = (Long)CastorService.toType("2", Long.class);
		assertEquals(2, long2);
		
		long[] intArray = (long[])CastorService.toType(new Object[]{"1", 5, 5.8}, Long[].class);
		assertEquals(1, intArray[0]);
		assertEquals(5, intArray[1]);
		assertEquals(5, intArray[2]);
	}
	
	/**
	 * 测试float转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午08:15:47 
	 * @version V1.0
	 */
	@Test
	public void castFloat() {
		float float1 = (Float)CastorService.toType(1.2, Float.class);
		assertEquals(1.2, float1, 0.0001);
		
		float float2 = (Float)CastorService.toType("2.5", Float.class);
		assertEquals(2.5, float2, 0.0001);
		
		float[] intArray = (float[])CastorService.toType(new Object[]{"1", "2.5", 5, 5.8}, Float[].class);
		assertEquals(1, intArray[0], 0.0001);
		assertEquals(2.5, intArray[1], 0.0001);
		assertEquals(5, intArray[2], 0.0001);
		assertEquals(5.8, intArray[3], 0.0001);
	}
	
	/**
	 * 测试double类型转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午08:27:20 
	 * @version V1.0
	 */
	@Test
	public void castDouble() {
		double float1 = (Double)CastorService.toType(1.2, double.class);
		assertEquals(1.2, float1, 0.0001);
		
		double float2 = (Double)CastorService.toType("2.5", Double.class);
		assertEquals(2.5, float2, 0.0001);
		
		double[] intArray = (double[])CastorService.toType(new Object[]{"1", "2.5", 5, 5.8}, double[].class);
		assertEquals(1, intArray[0], 0.0001);
		assertEquals(2.5, intArray[1], 0.0001);
		assertEquals(5, intArray[2], 0.0001);
		assertEquals(5.8, intArray[3], 0.0001);
	}
	
	/**
	 * 测试string类型转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午08:32:13 
	 * @version V1.0
	 */
	@Test
	public void castString() {
		String ids = "1,2,3,4,5";
		String[] strArray = (String[])CastorService.toType(ids, String[].class);
		assertEquals(strArray.length, 5);
	}
	
	/**
	 * 测试泛型数组类型转换器
	 * 
	 * @author Darkness
	 * @date 2013-3-26 下午08:38:08 
	 * @version V1.0
	 */
	@Test
	public void castDate() {
		String dateStr = "2012-03-26 12:42:36";
		Date date = (Date)CastorService.toType(dateStr, Date.class);
		assertEquals(dateStr, DateUtil.toDateTimeString(date));
	}

}
