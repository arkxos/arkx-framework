package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.cosyui.template.TagAttr;

/**
 * <ark:choose>标签,用于实现Choose/When支持
 *
 */
public class ChooseTag extends ArkTag {

    private Object variable;

    private boolean matched;// 是否有when标签匹配中了

    @Override
    public String getTagName() {
        return "choose";
    }

    public Object getVariable() {
        return variable;
    }

    public void setVariable(Object variable) {
        this.variable = variable;
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    @Override
    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<TagAttr>();
        list.add(new TagAttr("variable", true));
        return list;
    }

    @Override
    public String getPluginID() {
        return FrameworkPlugin.ID;
    }

    @Override
    public String getExtendItemName() {
        return "@{Framework.ChooseTag.Name}";
    }

    @Override
    public String getDescription() {
        return null;
    }

}
