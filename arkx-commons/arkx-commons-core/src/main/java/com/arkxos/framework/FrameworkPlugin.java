package com.arkxos.framework;

import io.arkx.framework.annotation.Plugin;
import io.arkx.framework.annotation.fastdb.Comment;
import io.arkx.framework.extend.plugin.AbstractPlugin;
import io.arkx.framework.extend.plugin.PluginException;
import io.arkx.framework.i18n.LangMapping;

/**
 * @class org.ark.framework.extend.plugin.FrameworkPlugin
 * 代表框架本身的插件。<br>
 * 本插件属于特殊插件，不能被安装、启动、停止和卸载。
 * @private
 * @author Darkness
 * @date 2012-8-7 下午9:22:35
 * @version V1.0
 */
@Comment
@Plugin("com.arkxos.framework")
public class FrameworkPlugin extends AbstractPlugin {
	
	public static final String ID = "com.arkxos.framework";

	@Override
	public void install() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.InstallFail"));
	}

	@Override
	public void start() throws PluginException {
		
	}

	@Override
	public void stop() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.StopFail"));
	}

	@Override
	public void uninstall() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.UninstallFail"));
	}

}
