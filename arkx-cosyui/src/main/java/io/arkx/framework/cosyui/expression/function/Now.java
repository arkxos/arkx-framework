package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.expression.IVariableResolver;

import java.util.Date;

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
