package org.ark.framework.utility.lang;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.TypeConvertUtil;

/**
 * @author Darkness
 * @date 2012-3-13 下午1:53:43
 * @version V1.0
 */
public class TypeConvertUtilTest {

	@Test
	public void convertValue() {

		assertEquals(0, TypeConvertUtil.convertValue(Integer.class, "0"));
	}

	@Test
	public void convertDate() throws ParseException {

		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		String dataString = "2012-03-15";
		assertEquals(df.parse(dataString), TypeConvertUtil.convertValue(Date.class, dataString));

		assertEquals(null, TypeConvertUtil.convertValue(Date.class, null));

		assertEquals(null, TypeConvertUtil.convertValue(Date.class, "null"));
	}

	@Test
	public void convertBoolean() {

		assertEquals(false, TypeConvertUtil.convertValue(boolean.class, "0"));
		assertEquals(true, TypeConvertUtil.convertValue(boolean.class, "1"));
		assertEquals(true, TypeConvertUtil.convertValue(boolean.class, "yes"));
		assertEquals(false, TypeConvertUtil.convertValue(boolean.class, "no"));
		assertEquals(true, TypeConvertUtil.convertValue(boolean.class, "true"));
		assertEquals(false, TypeConvertUtil.convertValue(boolean.class, "false"));
		assertEquals(true, TypeConvertUtil.convertValue(Boolean.class, "Y"));
		assertEquals(false, TypeConvertUtil.convertValue(boolean.class, "N"));
	}

}
