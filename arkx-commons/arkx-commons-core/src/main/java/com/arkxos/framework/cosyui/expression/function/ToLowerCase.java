package com.arkxos.framework.cosyui.expression.function;

import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 将字符串转换为小写。
 * 
 */
public class ToLowerCase extends AbstractFunction {
	@Override
	public String getFunctionName() {
		return "toLowerCase";
	}

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		if (input == null) {
			return "";
		}
		return input.toString().toLowerCase();
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
