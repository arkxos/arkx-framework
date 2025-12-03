package io.arkx.framework.cosyui.tag;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import io.arkx.framework.FrameworkPlugin;
import io.arkx.framework.WebCurrent;
import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.core.JsonResult;
import io.arkx.framework.core.method.IMethodLocator;
import io.arkx.framework.core.method.MethodLocatorUtil;
import io.arkx.framework.cosyui.template.TagAttr;
import io.arkx.framework.cosyui.template.exception.TemplateRuntimeException;
import io.arkx.framework.security.PrivCheck;

/**
 * 变量初始化标签，用于为<ark:init>包围的区域中的表达式提供变量
 */
public class InitTag extends ArkTag {

    private String method;

    private String rest;

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    @Override
    public String getTagName() {
        return "init";
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public int doStartTag() throws TemplateRuntimeException {
        if (ObjectUtil.notEmpty(method)) {
            IMethodLocator m = MethodLocatorUtil.find(method);
            PrivCheck.check(m);
            m.execute();
        } else if (StringUtil.isNotEmpty(rest)) {
            JsonResult jsonResult = RestUtil.post(rest);
            if (!jsonResult.isSuccess()) {
                throw new TemplateRuntimeException(jsonResult.getMessage());
            }
            Object data = jsonResult.getData();
            if (data instanceof Map) {
                WebCurrent.getResponse().putAll((Map) data);
            }
            WebCurrent.getResponse().putAll(jsonResult.getExtraData());
        }
        return EVAL_BODY_INCLUDE;
    }

    @Override
    public List<TagAttr> getTagAttrs() {
        List<TagAttr> list = new ArrayList<>();
        list.add(new TagAttr("method", false));
        list.add(new TagAttr("rest", false));
        return list;
    }

    @Override
    public String getExtendItemName() {
        return "@{Framework.Tag.InitTagName}";
    }

    @Override
    public String getDescription() {
        return "";
    }

    @Override
    public String getPluginID() {
        return FrameworkPlugin.ID;
    }

    public String getRest() {
        return rest;
    }

    public void setRest(String rest) {
        this.rest = rest;
    }

}
