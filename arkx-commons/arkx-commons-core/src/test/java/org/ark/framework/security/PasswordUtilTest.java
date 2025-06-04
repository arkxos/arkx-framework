package org.ark.framework.security;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * 
 * @author Darkness
 * @date 2012-12-16 下午07:26:23
 * @version V1.0
 */
public class PasswordUtilTest {

	@Test
	public void generateAndVerify() {
		String password = PasswordUtil.generate("Ark123");
		System.out.println(" dd "  +password);
		assertTrue(PasswordUtil.verify("Ark123", password));
	}
}
