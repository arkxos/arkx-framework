package io.arkx.framework.framework.utility;

import io.arkx.framework.commons.util.StringUtil;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 
 * @author Darkness
 * @date 2016年10月10日 下午6:56:49
 * @version V1.0
 */
public class StringUtilTest {

	@Test
	public void clearForXML() {
		String json = StringUtil.clearForXML("{\"UserName\":\"admin\"}");
		assertEquals("{\"UserName\":\"admin\"}", json);
	}
}
