package com.rapidark.framework.extend.plugin;

import com.rapidark.framework.Config;

/**
 * @class org.ark.framework.extend.plugin.spi.JavaPluginProvider
 * @private
 * @author Darkness
 * @date 2012-11-23 下午04:11:01
 * @version V1.0
 */
public class JavaPluginProvider implements IPluginProvider {

	@Override
	public String[] getPluginFolders() {
		return new String[] { Config.getClassesPath() + "/classes/plugins/" };
	}

}
