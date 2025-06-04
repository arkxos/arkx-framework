package com.arkxos.framework.cosyui.expression.function;

import com.arkxos.framework.commons.util.StringUtil;
import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 对字符串进行java转码，以便于在javascript中输出成字符串。
 * 
 */
public class JavaEncode extends AbstractFunction {
	@Override
	public String getFunctionName() {
		return "javaEncode";
	}

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		if (input == null) {
			return "";
		}
		return StringUtil.javaEncode(input);
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return AbstractFunction.Arg_String;
	}
}
