package com.arkxos.framework.extend.plugin;

import com.rapidark.framework.Config;

/**
 * @class org.ark.framework.extend.plugin.spi.JavaTestPluginProvider
 * @private
 * @author Darkness
 * @date 2012-11-23 下午04:11:01
 * @version V1.0
 */
public class JavaTestPluginProvider implements IPluginProvider {

	@Override
	public String[] getPluginFolders() {
		return new String[] { Config.getClassesPath().replace("test-classes", "classes") + "/plugins/" };
//		return new String[] { Config.getClassesPath().replace("test-classes", "classes") };
	}

}
