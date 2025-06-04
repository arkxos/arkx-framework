package com.arkxos.framework.extend.plugin;

import java.io.File;


/**   
 * @class org.ark.framework.extend.plugin.spi.IPluginParser
 * 插件解析器
 * @private
 * @author Darkness
 * @date 2012-11-23 下午03:11:42 
 * @version V1.0   
 */
public interface IPluginParser {

	/**
	 * 符合格式的插件
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午03:13:10 
	 * @version V1.0
	 * @param f 
	 */
	boolean validate(File f);

	/**
	 * 解析插件配置
	 * 
	 * @author Darkness
	 * @date 2012-11-23 下午03:17:19 
	 * @version V1.0
	 */
	PluginConfig[] parse(File f);

}
