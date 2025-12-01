package org.ark.framework.orm.spi.schemaloader;

import io.arkx.framework.Config;

import java.util.ArrayList;
import java.util.List;


/**   
 * @class org.ark.framework.orm.spi.schemaloader.SchemaLoaderManager
 * @author Darkness
 * @date 2012-10-24 下午01:42:37 
 * @version V1.0   
 */
public class SchemaLoaderManager {

	// schema扫描路径列表
	private static List<String> schemaPaths = new ArrayList<String>();
	
	// schema扫描器
	private static List<ISchemaLoader> schemaLoaders = new ArrayList<ISchemaLoader>();
	
	static {
		
		initSchemaLoader();
		
		registerOldStyle();
	}
	
	/**
	 * 注册schema扫描路径
	 * 
	 * @author Darkness
	 * @date 2012-10-24 下午01:06:13 
	 * @version V1.0
	 */
	public static void registerSchemaPath(String schemaPath) {
		schemaPaths.add(schemaPath);
	}
	
	/**
	 * 初始化schema loader
	 * 
	 * @author Darkness
	 * @date 2012-10-24 下午01:56:54 
	 * @version V1.0
	 */
	private static void initSchemaLoader() {
		schemaLoaders.add(new SchemaClassLoader());
		schemaLoaders.add(new SchemaJarLoader());
	}

	/**
	 * 获取所有schema类名
	 * 
	 * @author Darkness
	 * @date 2012-10-23 下午10:15:34 
	 * @version V1.0
	 */
	public static String[] getAllSchemaClassName() {
		
		ArrayList<String> list = new ArrayList<String>();
		
		for (String path : schemaPaths) {
			for (ISchemaLoader schemaLoader : schemaLoaders) {
				list.addAll(schemaLoader.load(path));
			}
		}
		
		String[] arr = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			String name = list.get(i);
			name = name.replaceAll("\\/", ".");
			name = name.substring(0, name.length() - 6);
			arr[i] = name;
		}
		return arr;
	}
	
	/**
	 * 兼容旧的使用方式
	 * 
	 * @author Darkness
	 * @date 2012-10-24 下午01:47:54 
	 * @version V1.0
	 */
	private static void registerOldStyle() {
		registerSchemaPath(Config.getWEBINFPath() + "/classes/");
		registerSchemaPath(Config.getWEBINFPath() + "/lib/");
		registerSchemaPath(Config.getPluginPath() + "/classes/");
		registerSchemaPath(Config.getPluginPath() + "/lib/");
	}
}
