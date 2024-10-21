package com.rapidark.framework;

import com.rapidark.framework.extend.plugin.AbstractPlugin;
import com.rapidark.framework.extend.plugin.PluginException;
import com.rapidark.framework.i18n.LangMapping;

public class FrameworkUtilPlugin extends AbstractPlugin {
	
	public static final String ID = "com.rapidark.frameworkutil";

	public void install() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.InstallFail", new Object[0]));
	}

	public void start() throws PluginException {
	}

	public void stop() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.StopFail", new Object[0]));
	}

	public void uninstall() throws PluginException {
		throw new PluginException(LangMapping.get("Framework.Plugin.UninstallFail", new Object[0]));
	}
	
}
