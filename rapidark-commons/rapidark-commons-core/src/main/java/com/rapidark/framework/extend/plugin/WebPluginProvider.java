package com.rapidark.framework.extend.plugin;

import com.rapidark.framework.Config;

/**
 * @class org.ark.framework.extend.plugin.spi.WebPluginProvider
 * @private
 * @author Darkness
 * @date 2012-11-23 下午02:53:53
 * @version V1.0
 */
public class WebPluginProvider implements IPluginProvider {

	@Override
	public String[] getPluginFolders() {

		String path = Config.getWEBINFPath();

		return new String[] { 
				// 首先读取应用下的插件配置文件
				path + "/classes/plugins/", 
//				path + "/plugins/classes/plugins/",  
				// 读取jar文件中的资源文件
				path + "/lib/"
//				path + "/plugins/lib/"
			};
	}

}
