package org.ark.framework.json;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.HashMap;
import java.util.Map;

import org.ark.common.Person;
import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.util.DateUtil;
import com.arkxos.framework.XTest;

/**
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:27:16
 * @version V1.0
 */
public class JSONTest extends XTest {

	@Test
	public void toJson() {
		Map<String, String> map = new HashMap<>();
		map.put("name", "darkness");
		map.put("age", "28");
		String json = JSON.toJSONString(map);
		assertEquals("{\"name\":\"darkness\",\"age\":\"28\"}", json);
	}
	
	public void jsonToMap() {
		String json = "{\"age\":\"28\",\"name\":\"darkness\"}";
		Object object = JSON.parse(json);
		System.out.println(object);
	}

	@Test
	public void objectToJson() {
		Person person = new Person();
		person.setName("darkness");
		person.setAge(28);
		person.setSex("男");
		person.setBornTime(DateUtil.parse("1987-5-28"));
		System.out.println(JSON.toJSONString(person));
	}
}
