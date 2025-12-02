package io.arkx.framework.extend.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.ZipUtil;

/**
 * @class org.ark.framework.extend.plugin.spi.JarPluginParser
 * @private
 * @author Darkness
 * @date 2012-11-23 下午03:11:19
 * @version V1.0
 */
public class JarPluginParser implements IPluginParser {

	@Override
	public boolean validate(File f) {

		if (!f.getName().endsWith(".jar")) {
			return false;
		}

		if (f.getName().endsWith(".plugin.jar")) {
			return true;
		}

		if (f.getName().startsWith("ark-")) {
			return true;
		}

		if (f.getName().startsWith("arkxos-")) {
			return true;
		}

		if (f.getName().indexOf("-plugin-") < 0) {
			return false;
		}

		return true;
	}

	@Override
	public PluginConfig[] parse(File f) {
		List<PluginConfig> result = new ArrayList<>();

		try {
			Mapx<String, Long> files = ZipUtil.getFileListInZip(f.getAbsolutePath());
			for (String fileName : files.keyArray())
				if (fileName.endsWith(".plugin")) {
					byte[] bs = ZipUtil.readFileInZip(f.getAbsolutePath(), fileName);
					PluginConfig pc = new PluginConfig();
					pc.parse(new String(bs, "UTF-8"));
					pc.setUpdateSite(f.getAbsolutePath() + "!" + fileName);
					pc.setPackageFile(f.getAbsolutePath());
					result.add(pc);
				}
		}
		catch (Exception e) {
			e.printStackTrace();
		}

		return result.toArray(new PluginConfig[0]);
	}

}
