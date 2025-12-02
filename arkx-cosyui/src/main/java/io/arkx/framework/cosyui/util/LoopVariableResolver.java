package io.arkx.framework.cosyui.util;

import java.util.Map;

import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.collection.CaseIgnoreMapx;
import io.arkx.framework.commons.collection.DataRow;
import io.arkx.framework.cosyui.expression.ExpressionException;
import io.arkx.framework.cosyui.expression.IVariableResolver;
import io.arkx.framework.i18n.LangUtil;

/**
 * 循环变量查找器，用于在for/while等循环代码中将DataRow和Map加入到变量查找序列中，以实现同样的<br>
 * 表达式每次循环查找到的变量有不同的值。 表达式引擎在查找变量时会优先使用加入的DataRow和Map，<br>
 * 如果从中未找到变量， 则会使用当前线程中存在HttpVariableResolver实例查找实变量，如果实例不存<br>
 * 在或未从实例中查找到变量，则返回null
 *
 */
public class LoopVariableResolver implements IVariableResolver {
    protected DataRow dr;
    protected IVariableResolver vr;
    protected Map<?, ?> map;

    public LoopVariableResolver() {
        if (WebCurrent.getExecuteContext() != null) {
            vr = WebCurrent.getExecuteContext();
        }
    }

    public void setDataRow(DataRow dr) {
        this.dr = dr;
    }

    public void setVariableResolver(IVariableResolver vr) {
        this.vr = vr;
    }

    public void setMap(Map<?, ?> map) {
        if (map instanceof CaseIgnoreMapx) {
            this.map = map;
        } else {
            this.map = new CaseIgnoreMapx<Object, Object>(map);
        }
    }

    @Override
    public Object resolveVariable(String holder) {
        Object v = null;
        if (map != null) {
            v = map.get(holder);
        }
        if (v == null && dr != null) {
            v = dr.get(holder);
        }
        if (v == null && vr != null) {
            try {
                v = vr.resolveVariable(holder);
            } catch (ExpressionException e) {
                e.printStackTrace();
            }
        }
        if (v != null && v instanceof String) {
            v = LangUtil.get((String) v);
        }
        return v;
    }
}
