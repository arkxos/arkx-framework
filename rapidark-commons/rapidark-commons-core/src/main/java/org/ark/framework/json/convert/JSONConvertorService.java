package org.ark.framework.json.convert;

import com.rapidark.framework.extend.AbstractExtendService;

/**
 * 
 * 
 * @author Darkness
 * @date 2013-3-30 下午03:56:51 
 * @version V1.0
 */
public class JSONConvertorService extends AbstractExtendService<IJSONConvertor> {
	public static JSONConvertorService getInstance() {
		return (JSONConvertorService) AbstractExtendService.findInstance(JSONConvertorService.class);
	}
}