package io.arkx.framework.cosyui.expression;

import java.util.Map;

/**
 * Map变量查找器，将Map中的键当成变量
 *
 */
public class MapVariableResolver implements IVariableResolver {// NO_UCD
    private Map<?, ?> map;

    /**
     * 构造器
     *
     * @param map
     *            变量所在的Map
     */
    public MapVariableResolver(Map<?, ?> map) {
        this.map = map;
    }

    @Override
    public Object resolveVariable(String varName) throws ExpressionException {
        return map.get(varName);
    }
}
