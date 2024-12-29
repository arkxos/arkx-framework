package com.arkxos.framework.extend;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import com.arkxos.framework.commons.util.LogUtil;
import com.arkxos.framework.commons.util.ObjectUtil;
import com.arkxos.framework.core.FrameworkException;
import com.arkxos.framework.data.xml.XMLElement;
import com.arkxos.framework.extend.plugin.ExtendPluginProvider;
import com.arkxos.framework.extend.plugin.PluginConfig;
import com.arkxos.framework.extend.plugin.PluginException;
import com.arkxos.framework.i18n.LangUtil;

/**
 * @class org.ark.framework.extend.plugin.ExtendServiceConfig
 * 扩展服务配置
 * @private
 * @author Darkness
 * @date 2012-8-5 下午10:47:22
 * @version V1.0
 */
public class ExtendServiceConfig {
	private boolean enable;
	private PluginConfig pluginConfig;
	private String id;
	private String description;
	private String className;
	private String itemClassName;

	private IExtendService<?> instance = null;
	private static ReentrantLock lock = new ReentrantLock();

	/**
	 * 
	 * @param pc PluginConfig
	 * @param extendServiceNodeData 
	 * 传过来的extendService节点格式如下：
	 * <extendService>
	    <id>org.ark.framework.cache.CacheService</id>
	    <class>org.ark.framework.cache.CacheService</class>
	    <description>缓存提供器注册</description>
	    <itemClass>org.ark.framework.cache.CacheProvider</itemClass>
	   </extendService>
	  
	 * @author Darkness
	 * @date 2012-8-18 下午6:16:40 
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
			if (nd.getQName().equalsIgnoreCase("class")) {
				className = nd.getText().trim();
			}
			if (nd.getQName().equalsIgnoreCase("itemClass")) {
				itemClassName = nd.getText().trim();
			}
		}
		if (ObjectUtil.isEmpty(id)) {
			throw new PluginException("extendPoint's id is empty!");
		}
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

	public String getClassName() {
		return className;
	}

	public void setID(String id) {
		this.id = id;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setClassName(String className) {
		this.className = className;
	}

	public boolean isEnable() {
		return enable;
	}

	public void setEnable(boolean enable) {
		this.enable = enable;
	}

	public PluginConfig getPluginConfig() {
		return pluginConfig;
	}

	public String getItemClassName() {
		return itemClassName;
	}

	public void setItemClassName(String itemClassName) {
		this.itemClassName = itemClassName;
	}

	/**
	 * 获取该扩展服务的实例，注册系统中该服务的扩展项
	 * 
	 * @author Darkness
	 * @date 2012-8-18 下午6:30:21 
	 * @version V1.0
	 */
	public IExtendService<?> getInstance() {
		try {
			if (instance == null) {
				lock.lock();
				try {
					if (instance == null) {
						Class<?> clazz = Class.forName(className);
						IExtendService<?> tmp = (IExtendService<?>) clazz.newInstance();
						try {
							List<ExtendItemConfig> list = ExtendPluginProvider.getInstance().findItemsByServiceID(this.id);
							if (ObjectUtil.notEmpty(list)) {
								for (ExtendItemConfig item : list) {
									try {
										tmp.register(item.getInstance());
									} catch (Exception e) {
										e.printStackTrace();
										LogUtil.error("Load ExtendItem " + item.getClassName() + " failed!");
									}
								}
							}
							instance = tmp;
						} catch (Exception e) {
							e.printStackTrace();
							LogUtil.error("Load ExtendService " + className + " failed!");
						}
					}
				} finally {
					lock.unlock();
				}
			}
			return instance;
		} catch (Exception e) {
			throw new FrameworkException(e);
		}
	}

	public void destory() {
		if (instance != null) {
			instance.destory();
		}
	}
}
