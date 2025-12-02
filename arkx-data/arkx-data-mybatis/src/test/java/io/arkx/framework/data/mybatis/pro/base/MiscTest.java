package io.arkx.framework.data.mybatis.pro.base;

import java.util.EnumSet;

import org.junit.jupiter.api.Test;

class MiscTest {

	@Test
	void enumSetTest() {
		EnumSet<Gender> genders = EnumSet.allOf(Gender.class);
		System.err.println(genders);
		EnumSet<Gender> female = EnumSet.of(Gender.FEMALE);
		System.err.println(female);
	}

}
