package com.arkxos.framework.cosyui.expression.function;

import java.util.Date;

import com.arkxos.framework.cosyui.expression.AbstractFunction;
import com.arkxos.framework.cosyui.expression.ExpressionException;
import com.arkxos.framework.cosyui.expression.IVariableResolver;

/**
 * 返回表示当前时间的Date对象。
 * 用法：${now().time}返回毫秒数,${format(now(),'yyyy-MM-dd')}返回当前日期
 * 
 */
public class Now extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) throws ExpressionException {
		return new Date();
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return new Class<?>[] {};
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public String getFunctionName() {
		return "now";
	}
}
