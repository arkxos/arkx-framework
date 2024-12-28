package com.rapidark.framework.json.convert;

import com.rapidark.framework.extend.AbstractExtendService;

/**
 * JSON转换器扩展服务类
 * 
 */
public class JSONConvertorService extends AbstractExtendService<IJSONConvertor> {
	public static JSONConvertorService getInstance() {
		return AbstractExtendService.findInstance(JSONConvertorService.class);
	}

}
