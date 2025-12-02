package org.ark.framework.orm;

import org.ark.framework.orm.spi.schemaloader.SchemaLoaderManager;

/**
 * @class org.ark.framework.orm.SchemaFactory
 * @author Darkness
 * @date 2012-10-23 下午09:56:46
 * @version V1.0
 */
public class SchemaFactory {

	/**
	 * 注册schema扫描路径
	 *
	 * @author Darkness
	 * @date 2012-10-24 下午01:06:13
	 * @version V1.0
	 */
	public static void registerSchemaPath(String schemaPath) {
		SchemaLoaderManager.registerSchemaPath(schemaPath);
	}

	/**
	 * 获取所有schema类名
	 *
	 * @author Darkness
	 * @date 2012-10-23 下午10:15:34
	 * @version V1.0
	 */
	public static String[] getAllSchemaClassName() {
		return SchemaLoaderManager.getAllSchemaClassName();
	}

	public static void main(String[] args) {

		String pluginPath = "E:\\workspace\\projects\\xcms\\sources\\xcms-web\\src\\main\\webapp\\WEB-INF\\plugins\\";

		SchemaFactory.registerSchemaPath(pluginPath + "classes");
		SchemaFactory.registerSchemaPath(pluginPath + "lib");

		String[] schemaClassNames = SchemaFactory.getAllSchemaClassName();
		System.out.println(schemaClassNames.length);
		for (String schemaClassName : schemaClassNames) {
			System.out.println(schemaClassName);
		}
	}

}
