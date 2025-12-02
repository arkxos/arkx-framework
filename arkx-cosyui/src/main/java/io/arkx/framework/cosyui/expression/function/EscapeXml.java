package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

/**
 * 将字符串进行XML转码
 *
 */
public class EscapeXml extends AbstractFunction {

	@Override
	public String getFunctionName() {
		return "escapeXml";
	}

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		if (input == null) {
			return "";
		}
		return StringUtil.htmlEncode(input);
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
