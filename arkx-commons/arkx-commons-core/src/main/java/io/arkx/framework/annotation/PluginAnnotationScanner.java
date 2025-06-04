package io.arkx.framework.annotation;

import java.lang.annotation.Annotation;
import java.util.Set;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.beans.factory.config.BeanDefinitionHolder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ClassPathBeanDefinitionScanner;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import lombok.Setter;

/**
 * @author Darkness
 * @date 2019-10-01 14:32:26
 * @version V1.0
 */
public class PluginAnnotationScanner extends ClassPathBeanDefinitionScanner {

    /**
     * 传值使用的临时静态变量
     */
    private static Class<? extends Annotation> staticTempAnnotationClazz = null;

    /**
     * 因构造函数无法传入指定的Annotation类，需使用静态方法来调用
     * @param registry
     * @param clazz
     * @return
     */
    public static synchronized PluginAnnotationScanner getScanner(BeanDefinitionRegistry registry,Class<? extends Annotation> clazz){
        staticTempAnnotationClazz = clazz;
        PluginAnnotationScanner scanner = new PluginAnnotationScanner(registry);
        scanner.setSelfAnnotationClazz(clazz);
        return scanner;
    }
    
	/**
     * 实体类对应的AnnotationClazz
     */
    @Setter
    private Class<? extends Annotation> selfAnnotationClazz;

    private PluginAnnotationScanner(BeanDefinitionRegistry registry) {
        super(registry);
    }

    // 构造函数需调用函数，使用静态变量annotationClazz传值
    @Override
    public void registerDefaultFilters() {
        // 添加需扫描的Annotation Class
        this.addIncludeFilter(new AnnotationTypeFilter(staticTempAnnotationClazz));
    }

    // 以下为初始化后调用的方法
    @Override
    public Set<BeanDefinitionHolder> doScan(String... basePackages) {
        return super.doScan(basePackages);
    }

    @Override
    public boolean isCandidateComponent(AnnotatedBeanDefinition beanDefinition) {
        return super.isCandidateComponent(beanDefinition)
                && beanDefinition.getMetadata().hasAnnotation(this.selfAnnotationClazz.getName());
    }
}
