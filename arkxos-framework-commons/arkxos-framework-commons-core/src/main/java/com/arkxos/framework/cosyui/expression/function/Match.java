package com.arkxos.framework.cosyui.expression.function;

import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 第一个参数是否正则匹配第二个参数
 * 
 */
public class Match extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		String regex = (String) args[1];
		if (input == null) {
			input = "";
		}
		if (regex == null) {
			return false;
		}
		return input.matches(regex);
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return AbstractFunction.Arg_String_String;
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public String getFunctionName() {
		return "match";
	}
}
