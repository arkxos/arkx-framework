package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

/**
 * 判断第一个字符串是否以第二个字符串结束。
 *
 */
public class EndsWith extends AbstractFunction {

    @Override
    public Object execute(IVariableResolver resolver, Object... args) {
        String input = (String) args[0];
        String substring = (String) args[1];
        if (input == null) {
            input = "";
        }
        if (substring == null) {
            substring = "";
        }
        return input.endsWith(substring);
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
        return "endsWith";
    }

}
