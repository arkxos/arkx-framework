package io.arkx.framework;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import io.arkx.framework.commons.collection.Mapx;
import io.arkx.framework.commons.util.FileUtil;
import io.arkx.framework.commons.util.StringUtil;
import io.arkx.framework.data.xml.XMLElement;
import io.arkx.framework.data.xml.XMLMultiLoader;

/**
 * 全局配置文件加载器，会加载WEB-INF/plugins/classes下的所有xml文件中的配置信息
 *
 * @author Darkness
 * @date 2012-8-5 下午2:46:36
 * @version V1.0
 *
 **/
public class ConfigLoader {

	private boolean loaded = false;

	private ReentrantLock lock = new ReentrantLock();

	private XMLMultiLoader loader = new XMLMultiLoader();

	private String[] paths;

	/**
	 * 载入配置文件
	 */
	ConfigLoader(String... paths) {
		this.paths = paths;
	}

	public void load() {
		if (!loaded) {
			lock.lock();
			try {
				if (!loaded) {// 只启动时加载一次
					loader.clear();
					try {
						load(paths);
					}
					catch (Exception e) {
						e.printStackTrace();
						throw new ConfigLoadException(e.getMessage());
					}
				}
				loaded = true;
			}
			finally {
				lock.unlock();
			}
		}
	}

	public void load(InputStream inputStream) {
		loader.load(inputStream);
	}

	/**
	 * 载入指定路径下的所有xml文件
	 */
	private void load(String[] paths) {
		if (paths == null || paths.length == 0) {
			return;
		}

		String file = paths[0] + "charset.config";
		if (new File(file).exists()) {
			String txt = FileUtil.readText(file, "UTF-8");// 必须指定字符集，否则会导致Config.getGlobalCharset()死循环
			Mapx<String, String> map = StringUtil.splitToMapx(txt, "\n", "=");
			Config.globalCharset = "GBK".equalsIgnoreCase(map.getString("global")) ? "GBK" : "UTF-8";
		}
		else {
			// throw new FrameworkException("File "+file+" not found!");
			Config.globalCharset = "UTF-8";
		}
		for (String path : paths) {
			loader.load(path);
		}
		XMLElement data = loader.elements("framework.application.config", "name", "ComplexDeployMode");
		Config.isComplexDepolyMode = data != null && "true".equals(data.getText());
	}

	/**
	 * 重新载入配置文件
	 */
	public void reload() {
		loaded = false;
		load();
	}

	/**
	 * 获取指定的节点数据，如：ConfigLoader.getElements("*.mapping.method")
	 *
	 * @author Darkness
	 * @date 2012-8-5 下午6:07:06
	 * @version V1.0
	 * @param path XML路径
	 * @return 指定XML路径下的所有XML元素
	 */
	public List<XMLElement> getElements(String path) {
		return loader.elements(path);
	}

}
