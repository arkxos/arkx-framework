package org.ark.framework.utility;

import com.rapidark.framework.commons.util.DateUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**   
 * 
 * @author Darkness
 * @date 2012-11-27 下午04:35:14 
 * @version V1.0   
 */
public class DateUtilTest {

	/**
	 * 日期格式的转换
	 * 
	 * @author Darkness
	 * @date 2012-11-27 下午04:38:22 
	 * @version V1.0
	 */
	@Test
	public void dateToString() {
		
		String dateTime = "2012-10-12 15:29:30";
		
		assertEquals("2012-10-12", DateUtil.toString(DateUtil.parse(dateTime)));
		
		assertEquals(dateTime, DateUtil.toDateTimeString(DateUtil.parseDateTime(dateTime)));
	}
}
