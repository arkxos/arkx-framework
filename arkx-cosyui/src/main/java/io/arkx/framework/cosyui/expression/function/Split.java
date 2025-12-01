package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

import java.util.StringTokenizer;

/**
 * 将第一个参数按第二个参数分隔成数组。
 * 
 */
public class Split extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) {
		String input = (String) args[0];
		String delimiters = (String) args[1];
		String[] array;
		if (input == null) {
			input = "";
		}
		if (input.length() == 0) {
			array = new String[1];
			array[0] = "";
			return array;
		}

		if (delimiters == null) {
			delimiters = "";
		}

		StringTokenizer tok = new StringTokenizer(input, delimiters);
		int count = tok.countTokens();
		array = new String[count];
		int i = 0;
		while (tok.hasMoreTokens()) {
			array[i++] = tok.nextToken();
		}
		return array;
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
		return "split";
	}
}
