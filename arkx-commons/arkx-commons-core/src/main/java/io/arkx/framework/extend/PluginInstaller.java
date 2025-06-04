package io.arkx.framework.extend;

import org.apache.commons.fileupload2.core.FileItem;

/**
 * @class org.ark.framework.extend.plugin.PluginInstaller
 * 插件安装器
 * @private
 * @author Darkness
 * @date 2012-8-7 下午9:24:09 
 * @version V1.0
 */
public class PluginInstaller {
	
	public static void verify(FileItem file) {
	}

	public static int install(FileItem file) {
		return 0;
	}

	public static int uninstall(String pluginID) {
		return 0;
	}
}