package io.arkx.framework.annotation;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import io.arkx.framework.annotation.util.AnnotationVisitor;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * @author Darkness
 * @date 2019-10-01 14:26:12
 * @version V1.0
 */
@Component
@Lazy
@Slf4j
public class PluginAnnotationScannerParser implements ApplicationContextAware, BeanFactoryPostProcessor {

	private static final String EVENT_NAME = "插件注解扫描";
	private static final String RESOURCE_PATTERN = "com/arkxos";
	private static final String PATH_DOT = ".";

	private ApplicationContext applicationContext;

	@Getter
	private Map<String, Object> pluginBeansMap;
	
	@Getter
	private static List<String> pluginIds = new ArrayList<>();
	
	/**
	 * Bean加载后置处理
	 */
	@Override
	public void postProcessBeanFactory(ConfigurableListableBeanFactory beanFactory) throws BeansException {
		// 使用自定义扫描类，针对@TestModel进行扫描
		PluginAnnotationScanner scanner = PluginAnnotationScanner.getScanner((BeanDefinitionRegistry) beanFactory,
				Plugin.class);
		// 设置ApplicationContext
		scanner.setResourceLoader(this.applicationContext);
		// 执行扫描
		log.info("开始扫描插件");
		int count = scanner.scan(RESOURCE_PATTERN);
		log.info(EVENT_NAME + ", 扫描类数量:" + count);

		// 取得对应Annotation映射，BeanName -- 实例
		pluginBeansMap = beanFactory.getBeansWithAnnotation(Plugin.class);
		
		log.info("annotationMap:" + pluginBeansMap.size());
		for (String key : pluginBeansMap.keySet()) {
			Object pluginBean = pluginBeansMap.get(key);
			Plugin plugin = pluginBean.getClass().getAnnotation(Plugin.class);
			String pluginId = plugin.value();
			pluginIds.add(pluginId);
		}
		// .... doSomething，根据需要进行设置，
		
		AnnotationVisitor.load();

	}

	/**
	 * 获取ApplicationContext
	 */
	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

}
