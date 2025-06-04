package com.arkxos.framework.cosyui.expression.function;

import com.arkxos.framework.Account;
import io.arkx.framework.commons.util.ObjectUtil;
import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

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
