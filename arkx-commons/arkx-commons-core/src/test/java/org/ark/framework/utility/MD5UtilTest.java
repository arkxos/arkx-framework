package org.ark.framework.utility;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.MD5Util;

/**   
 * 
 * @author Darkness
 * @date 2012-3-13 下午1:42:20 
 * @version V1.0   
 */
public class MD5UtilTest {

	@Test
	public void testMd5() {
		String msg = "darkness";
		assertTrue(MD5Util.isSame(msg, MD5Util.getCryptogram(msg)));
	}
}

