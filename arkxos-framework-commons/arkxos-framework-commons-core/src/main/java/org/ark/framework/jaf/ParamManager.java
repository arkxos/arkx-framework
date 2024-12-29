package org.ark.framework.jaf;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import com.arkxos.framework.commons.util.StringUtil;
import com.rapidark.framework.Constant;


/**   
 * 
 * @author Darkness
 * @date 2013-3-6 下午03:58:13 
 * @version V1.0   
 */
public class ParamManager {

	/**
	 * 提取内容的参数
	 * 示例：
	 * String content = "index.html?name=${name}&password=${password}";
	 * 
	 * List<String> params = ParamManager.extractParam(content);
	 * 
	 * Assert.assertEquals(2, params.size());
	 * 
	 * Assert.assertEquals("name", params.get(0));
	 * Assert.assertEquals("password", params.get(1));
	 * 
	 * @author Darkness
	 * @date 2013-3-6 下午04:04:03 
	 * @version V1.0
	 */
	public static List<String> extractParam(String content){
		List<String> result = new ArrayList<String>();
		
		if(StringUtil.isEmpty(content)) {
			return result;
		}
		
		Matcher matcher = Constant.PatternField.matcher(content);
		int lastEndIndex = 0;
		while (matcher.find(lastEndIndex)) {
			result.add(matcher.group(1));
			lastEndIndex = matcher.end();
		}
		
		return result;
	}

	/**
	 * 替换内容中的参数
	 * 
	 * @author Darkness
	 * @date 2013-3-6 下午04:14:04 
	 * @version V1.0
	 */
	public static String replaceParam(String content, Map<String, String> paramValues) {
		List<String> params = extractParam(content);
		
		for (String param : params) {
			content = StringUtil.replaceEx(content, "${" + param + "}", paramValues.get(param));
		}
		
		return content;
	}
	
}
