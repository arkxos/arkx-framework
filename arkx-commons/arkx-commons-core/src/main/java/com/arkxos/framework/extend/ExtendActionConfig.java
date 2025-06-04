package com.arkxos.framework.extend;

import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.commons.util.ObjectUtil;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.extend.exception.CreateExtendActionInstanceException;
import com.arkxos.framework.extend.plugin.ExtendPluginProvider;
import com.arkxos.framework.extend.plugin.PluginConfig;
import com.arkxos.framework.extend.plugin.PluginException;
import com.arkxos.framework.i18n.LangUtil;

/**
 * @class org.ark.framework.extend.plugin.ExtendActionConfig
 * 扩展行为配置
 * @private
 * @author Darkness
 * @date 2012-8-5 下午10:49:17
 * @version V1.0
 */
public class ExtendActionConfig {
	private boolean enable;
	private PluginConfig pluginConfig;
	private String id;
	private String description;
	private String extendPointID;
	private String className;
	private IExtendAction instance = null;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * @param pc
	 * @param extendActionConfigNodeData
	 * <extendAction>
    		<id>org.ark.framework.PrivCheck</id>
    		<class>org.ark.framework.extend.actions.PrivExtendAction</class>
    		<description>@{Framework.Plugin.PrivCheck}</description>
    		<extendPoint>org.ark.framework.PrivCheck</extendPoint>
  		</extendAction>
	 * 
	 * @author Darkness
	 * @date 2012-8-18 下午8:34:20 
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
			if (nd.getQName().equalsIgnoreCase("extendPoint")) {
				extendPointID = nd.getText().trim();
			}
			if (nd.getQName().equalsIgnoreCase("class")) {
				className = nd.getText().trim();
			}
		}
		if (ObjectUtil.isEmpty(id)) {
			throw new PluginException("extendAction's id is empty!");
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

	public String getExtendPointID() {
		return extendPointID;
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

	public void setExtendPointID(String extendPointID) {
		this.extendPointID = extendPointID;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	/**
	 * 
	 * @author Darkness
	 * @date 2012-8-18 下午8:34:00 
	 * @version V1.0
	 */
	public IExtendAction getInstance() {
		try {
			if (instance == null) {
				lock.lock();
				try {
					if (instance == null) {
						Class<?> clazz = Class.forName(className);
						ExtendPointConfig ep = ExtendPluginProvider.getInstance().findExtendPoint(this.extendPointID);
						if (ep.isChild(clazz)) {
							throw new CreateExtendActionInstanceException("ExtendAction " + className + " must extends "
									+ ep.getClassName());
						}
						instance = (IExtendAction) clazz.newInstance();
					}
				} finally {
					lock.unlock();
				}
			}
			return instance;
		} catch (Exception e) {
			throw new CreateExtendActionInstanceException(e);
		}
	}
}
