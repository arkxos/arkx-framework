package io.arkx.framework.cosyui.expression.function;

import java.util.Map;

import io.arkx.framework.cosyui.expression.AbstractFunction;
import io.arkx.framework.cosyui.expression.CachedEvaluator;
import io.arkx.framework.cosyui.expression.DefaultFunctionMapper;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.expression.IVariableResolver;
import io.arkx.framework.cosyui.expression.MapVariableResolver;
import io.arkx.framework.cosyui.template.AbstractExecuteContext;

/**
 * 执行一段含有表达式的字符串
 * 
 */
public class Eval extends AbstractFunction {

	@Override
	public Object execute(IVariableResolver resolver, Object... args) throws ExpressionException {
		String input = (String) args[0];
		if (!(resolver instanceof AbstractExecuteContext)) {
			return input;
		}
		AbstractExecuteContext context = (AbstractExecuteContext) resolver;
		return context.evalExpression(input);
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
		return "eval";
	}

	public static Object eval(String expression, Map<?, ?> map) throws ExpressionException {
		MapVariableResolver vr = new MapVariableResolver(map);
		CachedEvaluator ce = new CachedEvaluator();
		return ce.evaluate(expression, Object.class, vr, DefaultFunctionMapper.getInstance());
	}
}
