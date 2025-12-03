package io.arkx.framework.extend;

import io.arkx.framework.commons.util.ObjectUtil;
import io.arkx.framework.data.xml.XMLElement;
import io.arkx.framework.extend.exception.CreateExtendItemInstanceException;
import io.arkx.framework.extend.plugin.PluginConfig;
import io.arkx.framework.extend.plugin.PluginException;
import io.arkx.framework.i18n.LangUtil;

/**
 * @class org.ark.framework.extend.plugin.ExtendItemConfig 扩展项配置
 * @private
 * @author Darkness
 * @date 2012-8-5 下午10:48:34
 * @version V1.0
 */
public class ExtendItemConfig {

    private boolean enable;

    private PluginConfig pluginConfig;

    private String id;

    private String description;

    private String extendServiceID;

    private String className;

    private IExtendItem instance = null;

    /**
     * @param pc
     * @param extendPointConfig
     *            <extendItem> <id>org.ark.framework.PrivCheck</id>
     *            <class>org.ark.framework.extend.actions.PrivExtendAction</class>
     *            <description>@{Framework.Plugin.PrivCheck}</description>
     *            <extendServiceID>org.ark.framework.cache.CacheService</extendServiceID>
     *            </extendItem>
     *
     * @author Darkness
     * @date 2012-8-18 下午8:19:32
     * @version V1.0
     */
    public void init(PluginConfig pc, XMLElement parent) throws PluginException {
        pluginConfig = pc;
        for (XMLElement nd : parent.elements()) {
            if (nd.getQName().equalsIgnoreCase("id")) {
                id = nd.getText().trim();
            }
            if (nd.getQName().equalsIgnoreCase("description")) {
                description = nd.getText().trim();
            }
            if (nd.getQName().equalsIgnoreCase("extendService")) {
                extendServiceID = nd.getText().trim();
            }
            if (nd.getQName().equalsIgnoreCase("class")) {
                className = nd.getText().trim();
            }
        }
        if (ObjectUtil.isEmpty(id)) {
            throw new PluginException("extendItem's id is empty!");
        }
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public boolean isEnable() {
        return enable;
    }

    public PluginConfig getPluginConfig() {
        return pluginConfig;
    }

    public String getID() {
        return id;
    }

    public String getDescription() {
        return description;
    }

    public String getDescription(String language) {
        if (description == null) {
            return null;
        }
        return LangUtil.get(description, language);
    }

    public String getExtendServiceID() {
        return extendServiceID;
    }

    public String getClassName() {
        return className;
    }

    public void setID(String id) {
        this.id = id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setExtendServiceID(String extendServiceID) {
        this.extendServiceID = extendServiceID;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public IExtendItem getInstance() {
        try {
            if (instance == null) {
                Class<?> clazz = Class.forName(className);
                try {
                    instance = (IExtendItem) clazz.newInstance();
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new CreateExtendItemInstanceException(
                            "ExtendItem " + className + " must implements IExtendItem");
                }
            }
            return instance;
        } catch (Exception e) {
            throw new CreateExtendItemInstanceException(e);
        }
    }

}
