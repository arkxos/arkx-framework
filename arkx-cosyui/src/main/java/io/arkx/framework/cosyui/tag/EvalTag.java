package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.i18n.LangUtil;

/**
 * 表达式求值标签，一般不需要使用本标签，可以直接在zhtml中使用表达式。
 *
 */
public class EvalTag extends ParamTag {

    String expression;

    @Override
    public String getTagName() {
        return "eval";
    }

    @Override
    public int doStartTag() throws TemplateRuntimeException {
        try {
            Object value = expression;// 表达式已经改为自动求值
            if (value != null) {
                String v = String.valueOf(value);
                v = LangUtil.get(v);
                pageContext.getOut().write(v);
            } else {
                pageContext.getOut().write("null");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return SKIP_BODY;
    }

    public String getExpression() {
        return expression;
    }

    public void setExpression(String expression) {
        this.expression = expression;
    }

    @Override
    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<TagAttr>();
        list.add(new TagAttr("expression"));
        return list;
    }

    @Override
    public String getExtendItemName() {
        return "@{Framework.Tag.EvalTagName}";
    }

}
