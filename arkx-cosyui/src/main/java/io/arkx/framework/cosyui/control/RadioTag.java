package io.arkx.framework.cosyui.control;

import io.arkx.framework.cosyui.expression.ExpressionException;

/**
 * 单选框标签
 *
 */
public class RadioTag extends CheckboxTag {

    @Override
    public void init() throws ExpressionException {
        super.init();
        type = "radio";
    }

    @Override
    public String getTagName() {
        return "radio";
    }

    @Override
    public String getDescription() {
        return "@{Framework.RadioTag.Desc}";
    }

    @Override
    public String getExtendItemName() {
        return "@{Framework.RadioTag.Name}";
    }

}
