package com.rapidark.framework.cosyui.expression.function;

import com.rapidark.framework.Account;
import com.rapidark.framework.commons.util.ObjectUtil;
import com.rapidark.framework.cosyui.expression.AbstractFunction;
import com.rapidark.framework.cosyui.expression.IVariableResolver;

/**
 * 判断当前用户是否拥有第一个参数指定的权限项
 * 
 */
public class Priv extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		if (ObjectUtil.empty(input)) {
			return false;
		}
		return Account.getPrivilege().hasPriv(input);
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return AbstractFunction.Arg_String;
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public String getFunctionName() {
		return "priv";
	}
}
