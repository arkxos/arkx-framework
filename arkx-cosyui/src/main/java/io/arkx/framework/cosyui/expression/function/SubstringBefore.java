package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

/**
 * 从第一个字符串中截取从开始到第二个字符串之间的所有字符。如果第一个字符串不包含第二个字符串，则直接返回第一个字符串。
 *
 */
public class SubstringBefore extends AbstractFunction {

    @Override
    public Object execute(IVariableResolver resolver, Object... args) {
        String input = (String) args[0];
        String substring = (String) args[1];
        if (input == null) {
            input = "";
        }
        if (input.length() == 0) {
            return "";
        }
        if (substring == null) {
            substring = "";
        }
        if (substring.length() == 0) {
            return "";
        }

        int index = input.indexOf(substring);
        if (index == -1) {
            return input;
        } else {
            return input.substring(0, index);
        }
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
        return "substringBefore";
    }
}
