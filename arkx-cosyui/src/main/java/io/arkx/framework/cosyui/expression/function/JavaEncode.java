package io.arkx.framework.cosyui.expression.function;

import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.IVariableResolver;

/**
 * 对字符串进行java转码，以便于在javascript中输出成字符串。
 *
 */
public class JavaEncode extends AbstractFunction {

    @Override
    public String getFunctionName() {
        return "javaEncode";
    }

    @Override
    public Object execute(IVariableResolver resolver, Object... args) {
        String input = (String) args[0];
        if (input == null) {
            return "";
        }
        return StringUtil.javaEncode(input);
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
