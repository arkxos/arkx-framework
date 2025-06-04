package io.arkx.framework.cosyui.expression;

/**
 * 变量查找器
 * 
 */
public interface IVariableResolver {
	/**
	 * @param varName 变量名称
	 * @return 查找到的变量值，如果未找到则返回null
	 * @throws ExpressionException
	 */
	public Object resolveVariable(String varName) throws ExpressionException;
}
