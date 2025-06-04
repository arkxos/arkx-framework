package io.arkx.framework.framework.collection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.arkx.framework.commons.collection.CacheMapx;
import io.arkx.framework.commons.collection.Mapx;

/**
 *  
 * @author Rapidark
 * @date 2016年5月6日 下午2:45:59
 * @version V1.0
 */
public class MapxTest {

	@Test
	public void put() {
		Mapx<String, String> mapx = new Mapx<>();
		
		mapx.put("a", "a");
		assertEquals(mapx.get("a"), "a");
		
		mapx = new CacheMapx<>();
		mapx.put("a", "a");
		assertEquals(mapx.get("a"), "a");
	}
}
