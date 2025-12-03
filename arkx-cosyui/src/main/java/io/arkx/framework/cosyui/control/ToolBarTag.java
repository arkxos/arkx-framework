package io.arkx.framework.cosyui.control;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.cosyui.tag.ArkTag;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.cosyui.util.TagUtil;

/**
 * 工具栏标签
 *
 */
public class ToolBarTag extends ArkTag {

    private String theme;

    private String fixed = "true";

    private String id;

    @Override
    public String getTagName() {
        return "toolbar";
    }

    @Override
    public int doStartTag() throws TemplateRuntimeException {
        if (ObjectUtil.empty(id)) {
            id = TagUtil.getTagID(pageContext, "ToolBar");
        }
        StringBuilder sb = new StringBuilder();
        sb.append("<div id=\"" + id + "_outer\" class=\"z-toolbar-anchor");
        if ("flat".equals(theme)) {
            sb.append(" z-toolbar-flat");
        }
        if ("true".equals(fixed)) {
            sb.append(" z-toolbar-fixed");
        }
        sb.append("\">");
        sb.append("<div class=\"z-toolbar\" id=\"" + id + "\">");
        sb.append("<div class=\"z-toolbar-ct\">");
        sb.append("<div class=\"z-toolbar-overflow\">");
        sb.append("<div class=\"z-toolbar-nowrap\" id=\"" + id + "_body\">");
        pageContext.getOut().write(sb.toString());
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public int doAfterBody() throws TemplateRuntimeException {
        pageContext.getOut().write("</div></div></div></div></div>");
        pageContext.getOut().write("<script>");
        pageContext.getOut().write(
                "Ark.Page.onReady(function(){new Ark.Toolbar({el:Ark.getDom('" + id + "'),fixed:" + fixed + "});});");
        pageContext.getOut().write("</script>");
        return EVAL_PAGE;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getFixed() {
        return fixed;
    }

    public void setFixed(String fixed) {
        this.fixed = fixed;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<>();
        list.add(new TagAttr("id", true));
        list.add(new TagAttr("theme"));
        list.add(new TagAttr("fixed"));
        return list;
    }

    @Override
    public String getExtendItemName() {
        return "@{Framework.UIControl.ToolBarTagName}";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getPluginID() {
        return FrameworkPlugin.ID;
    }

}
