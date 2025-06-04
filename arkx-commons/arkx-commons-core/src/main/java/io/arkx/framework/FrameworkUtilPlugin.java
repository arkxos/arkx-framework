package io.arkx.framework;

import io.arkx.framework.extend.plugin.AbstractPlugin;
import io.arkx.framework.extend.plugin.PluginException;
import io.arkx.framework.i18n.LangMapping;

public class FrameworkUtilPlugin extends AbstractPlugin {
	
	public static final String ID = "com.arkxos.frameworkutil";

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
