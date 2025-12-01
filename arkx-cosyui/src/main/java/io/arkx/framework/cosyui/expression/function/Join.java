package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.expression.IVariableResolver;

import java.util.Collection;

/**
 * 将数组和集合按第二个参数指定的分隔符拼接成一个字符串。
 * 
 */
public class Join extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) throws ExpressionException {
		String separator = ",";
		if (args.length <= 1) {
			return "";
		}
		separator = (String) args[0];
		if (args.length == 2 && args[1].getClass().isArray()) {
			Object[] arr = ObjectUtil.toObjectArray(args[1]);
			return StringUtil.join(arr, separator);
		} else if (args.length == 2 && args[1] instanceof Collection) {
			String[] arr = ObjectUtil.toStringArray((Collection<?>) args[1]);
			return StringUtil.join(arr, separator);
		} else {// 有多个参数
			args[0] = "";
			String r = StringUtil.join(args, separator);
			return r.substring(separator.length());
		}
	}

	@Override
	public Class<?>[] getArgumentTypes() {
		return new Class<?>[] { String.class, Object.class };
	}

	@Override
	public String getFunctionPrefix() {
		return "";
	}

	@Override
	public String getFunctionName() {
		return "join";
	}
}
