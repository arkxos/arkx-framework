package io.arkx.framework.common.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/7/25 15:49
 */
public class JsonTest {

	@Test
	public void map2Json() throws JsonProcessingException {
		Map<String, Object> data = new HashMap<>();
		data.put("content", new ArrayList<>());
		data.put("size", 10);
		System.out.println(JSON.toJSONString(data));

		ObjectMapper objectMapper = new ObjectMapper();
		System.out.println(objectMapper.writeValueAsString(data));
	}

}
