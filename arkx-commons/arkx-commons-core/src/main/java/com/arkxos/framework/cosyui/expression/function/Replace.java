package com.arkxos.framework.cosyui.expression.function;

import io.arkx.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 字符串替换，如果指定了第四个参数且其值为"regex"则使用正则替换。
 * 
 */
public class Replace extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		String src = (String) args[1];
		String dest = (String) args[2];
		String type = "";
		if (args.length > 3) {
			type = (String) args[3];
		}
		if (input == null) {
			input = "";
		}
		if (src == null) {
			return input;
		}
		if (dest == null) {
			dest = "";
		}
		if ("regex".equalsIgnoreCase(type)) {
			return input.replaceAll(src, dest);
		} else {
			return StringUtil.replaceEx(input, src, dest);
		}
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return new Class<?>[] { String.class, String.class, String.class, String.class };
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public String getFunctionName() {
		return "replace";
	}
}
