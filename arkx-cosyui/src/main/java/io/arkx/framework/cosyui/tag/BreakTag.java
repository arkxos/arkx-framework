package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.cosyui.template.AbstractTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

public class BreakTag extends ArkTag {

    public static final String BreakFlagInVariables = "_ARK_BREAK_TAG";

    private String out;

    public String getTagName() {
        return "break";
    }

    public int doStartTag() throws TemplateRuntimeException {
        AbstractTag tag = this.parent;
        while (tag != null) {
            if ((tag instanceof IBreakableTag)) {
                tag.getVariables().put("_ARK_BREAK_TAG", "1");
                break;
            }
            tag = tag.getParent();
        }
        return 0;
    }

    public String getOut() {
        return this.out;
    }

    public void setOut(String out) {
        this.out = out;
    }

    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<>();
        list.add(new TagAttr("out"));
        return list;
    }

    public String getPluginID() {
        return "io.arkx.framework";
    }

    public String getDescription() {
        return "@{Framework.ZElseTagDescription}";
    }

    public String getExtendItemName() {
        return "@{Framework.ZElseTagName}";
    }

}
