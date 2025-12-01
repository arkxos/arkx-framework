package io.arkx.framework.extend.plugin;

import io.arkx.framework.commons.util.FileUtil;

import java.io.File;


/**
 * @class org.ark.framework.extend.plugin.spi.FilePluginParser
 * @private
 * @author Darkness
 * @date 2012-11-23 下午03:29:51
 * @version V1.0
 */
public class FilePluginParser implements IPluginParser {

	@Override
	public boolean validate(File f) {
		return (f.isFile()) && f.getName().toLowerCase().endsWith(".plugin");
	}

	@Override
	public PluginConfig[] parse(File f) {
		PluginConfig pc = new PluginConfig();
		try {
			pc.parse(FileUtil.readText(f, "UTF-8"));
			pc.setUpdateSite(f.getAbsolutePath());
			pc.setPackageFile(f.getAbsolutePath());
		} catch (PluginException e) {
			e.printStackTrace();
		}
		return new PluginConfig[] { pc };
	}

}
