package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

/**
 * 返回第二个参数在第一个参数中最后出现的位置。
 *
 */
public class LastIndexOf extends AbstractFunction {

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
        return input.lastIndexOf(substring);
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
        return "lastIndexOf";
    }
}
