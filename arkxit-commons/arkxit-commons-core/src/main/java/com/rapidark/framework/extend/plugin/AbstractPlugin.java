package com.rapidark.framework.extend.plugin;

/**
 * @class org.ark.framework.extend.plugin.AbstractPlugin
 * 插件抽象基类
 * @private
 * @author Darkness
 * @date 2012-8-7 下午9:23:09 
 * @version V1.0
 */
public abstract class AbstractPlugin implements IPlugin {
	
	private PluginConfig config;

	@Override
	public void install() throws PluginException {
	}

	@Override
	public void uninstall() throws PluginException {
	}

	@Override
	public PluginConfig getConfig() {
		return config;
	}

	public void setConfig(PluginConfig config) {
		this.config = config;
	}

	@Override
	public void destory() {
	}
}
