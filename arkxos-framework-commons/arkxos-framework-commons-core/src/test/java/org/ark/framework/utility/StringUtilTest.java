package org.ark.framework.utility;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.arkxos.framework.commons.collection.Mapx;
import com.arkxos.framework.commons.util.StringUtil;

/**   
 * 
 * @author Darkness
 * @date 2012-12-2 下午03:12:33 
 * @version V1.0   
 */
public class StringUtilTest {

	@Test
	public void leftPad() {
		
		String str = "1";
		String result = StringUtil.leftPad(str, '0', 4);
		
		assertEquals("0001", result);
	}
	
	@Test
	public void splitToMapx(){
		Mapx<String, String> params = StringUtil.splitToMapx("name=darkness&sex=man", "&", "=");
		assertEquals(2, params.size());
		assertEquals(params.get("name"), "darkness");
		assertEquals(params.get("sex"), "man");
	}
}
