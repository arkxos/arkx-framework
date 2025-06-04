package org.ark.framework.utility;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.ObjectUtil;

/**   
 * 
 * @author Darkness
 * @date 2013-3-7 下午02:31:20 
 * @version V1.0   
 */
public class ObjectUtilTest {

	@Test
	public void in(){
		boolean simpleIn = ObjectUtil.in(new Object[]{"a", "a", "b", "c"});
		assertTrue(simpleIn);
		
		boolean in = ObjectUtil.in(new Object[]{"b", "a"}, new Object[]{ "a", "b", "c"});
		assertTrue(in);
		
		boolean notIn = ObjectUtil.in(new Object[]{"a", "e"}, new Object[]{ "a", "b", "c"});
		assertFalse(notIn);
		
		String[] arr1 = { "预约时间", "性别", "姓名", "年龄", "电话", "身份证号码", "诊疗卡号", "订单号", "挂号时间", "科室", "医院", "监护人" };  
		String[] arr2 = { "预约时间", "姓名", "性别", "年龄", "电话", "身份证号码", "诊疗卡号", "订单号", "挂号时间", "科室", "医院", "监护人", "行政" };
		boolean largeIn = ObjectUtil.in(arr1, arr2);
		assertTrue(largeIn);
	}
}
