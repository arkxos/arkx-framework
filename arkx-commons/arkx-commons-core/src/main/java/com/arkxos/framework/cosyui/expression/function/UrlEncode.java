package com.arkxos.framework.cosyui.expression.function;

import io.arkx.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 对字符串进行Url转码，以便于在Url中输出成字符串。
 * 
 */
public class UrlEncode extends AbstractFunction {
	@Override
	public String getFunctionName() {
		return "urlEncode";
	}

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		if (input == null) {
			return "";
		}
		if (args.length > 1) {
			String charset = (String) args[1];
			if (StringUtil.isNotEmpty(charset)) {
				return StringUtil.urlEncode(input, charset);
			}
		}
		return StringUtil.urlEncode(input);
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return new Class<?>[] { String.class, String.class };
	}
}
