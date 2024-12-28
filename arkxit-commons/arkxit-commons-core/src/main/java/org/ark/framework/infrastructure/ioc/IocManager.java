package org.ark.framework.infrastructure.ioc;

import java.io.File;

import com.rapidark.framework.Config;
import com.rapidark.framework.commons.collection.Mapx;
import com.rapidark.framework.commons.util.XMLLoader;
import com.rapidark.framework.commons.util.XMLLoader.NodeData;


/**
 * @class org.ark.framework.infrastructure.ioc.IocManager
 * IOC容器管理器
 * 
 * @author Darkness
 * @date 2012-9-25 下午9:58:12
 * @version V1.0
 */
public class IocManager {

	private static XMLLoader xmlLoader;
	
	private static Mapx<String, String> beansConfig = new Mapx<>();
	
	/**
	 * 加载bean.config.xml配置文件，初始化bean列表
	 * 
	 * @author Darkness
	 * @date 2012-10-27 上午11:33:15 
	 * @version V1.0
	 */
	private static void init() {
		if (xmlLoader == null) {
			xmlLoader = new XMLLoader();
			
			String path = Config.getPluginPath() + "classes/";
			if(!new File(path).exists()) {
				path = Config.getClassesPath();
			} 
			
			xmlLoader.load(path + "beans.config.xml");
			
			NodeData[] beansNodeData = xmlLoader.getNodeDataList("beans.bean");
			if(beansNodeData != null) {
				for (NodeData nodeData : beansNodeData) {
					String className = nodeData.getAttributes().get("class");
	//				Object bean = null;
	//				try {
	//					bean = Class.forName(className).newInstance();
	//				} catch (Exception e) {
	//					e.printStackTrace();
	//				} 
					beansConfig.put(nodeData.getAttributes().get("name"), className);
				}
			}
		}
	}

	/**
	 * 获取bean类名
	 * 
	 * @author Darkness
	 * @date 2012-10-27 上午11:30:00 
	 * @version V1.0
	 * @return 
	 */
	public static String getBeanClass(String beanName) {
		init();
		return beansConfig.get(beanName);
	}
	
}
