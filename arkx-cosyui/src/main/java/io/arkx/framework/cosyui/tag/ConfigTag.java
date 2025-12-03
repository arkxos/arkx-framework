package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.commons.collection.DataTypes;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;

/**
 * 模板配置标签。<br>
 * 标明模板的类型、名称、作者等，某些应用场景只需要指定类型的模板。 本标签不对页面输出产生实际效果，只作标明某些应用中的模板类别之用。
 *
 */
public class ConfigTag extends ArkTag {

    public static final String PREFIX = "ark";

    public static final String TAGNAME = "config";

    private String type;

    private String name;

    private String author;

    private double version;

    private String description;

    private String scriptStart;

    private String scriptEnd;

    @Override
    public String getTagName() {
        return TAGNAME;
    }

    @Override
    public int doStartTag() throws TemplateRuntimeException {
        return SKIP_BODY;// 什么都不干
    }

    @Override
    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<TagAttr>();
        list.add(new TagAttr("type", DataTypes.STRING.code(), "@{Framework.ConfigTag.Type}"));
        list.add(new TagAttr("name", DataTypes.STRING.code(), "@{Framework.ConfigTag.Name}"));
        list.add(new TagAttr("author", DataTypes.STRING.code(), "@{Framework.ConfigTag.Author}"));
        list.add(new TagAttr("description", DataTypes.STRING.code(), "@{Framework.ConfigTag.Description}"));
        list.add(new TagAttr("version", DataTypes.DOUBLE.code(), "@{Framework.ConfigTag.Version}"));
        list.add(new TagAttr("scriptStart", DataTypes.STRING.code(), "@{Framework.ConfigTag.ScriptStart}"));
        list.add(new TagAttr("scriptEnd", DataTypes.STRING.code(), "@{Framework.ConfigTag.ScriptEnd}"));
        return list;
    }

    @Override
    public String getPluginID() {
        return FrameworkPlugin.ID;
    }

    /**
     * 注意本方法有两种应用场景： 1、作为IExtendItem.getName()被插件机制调用 2、在模板中要求返回name属性时调用
     * 两种场景下返回的值会有所不同。
     */
    @Override
    public String getDescription() {
        if (context != null) {
            return description;
        }
        return "@{Framework.ZVarTagDescription}";
    }

    @Override
    public String getExtendItemName() {
        if (context != null) {
            return name;
        }
        return "@{Framework.ZVarTagName}";
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public double getVersion() {
        return version;
    }

    public void setVersion(double version) {
        this.version = version;
    }

    public String getScriptStart() {
        return scriptStart;
    }

    public void setScriptStart(String scriptStart) {
        this.scriptStart = scriptStart;
    }

    public String getScriptEnd() {
        return scriptEnd;
    }

    public void setScriptEnd(String scriptEnd) {
        this.scriptEnd = scriptEnd;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

}
