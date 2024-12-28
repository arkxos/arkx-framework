package com.rapidark.framework.extend.plugin;

/**
 * @class org.ark.framework.extend.plugin.spi.IPluginProvider
 * @private
 * @author Darkness
 * @date 2012-11-23 下午04:02:21
 * @version V1.0
 */
public interface IPluginProvider {

	public static final int WEB = 0;
	public static final int NO_WEB = 1;
	public static final int TEST = 2;

	/**
	 * 获取插件目录
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午04:02:42
	 * @version V1.0
	 */
	String[] getPluginFolders();
}
