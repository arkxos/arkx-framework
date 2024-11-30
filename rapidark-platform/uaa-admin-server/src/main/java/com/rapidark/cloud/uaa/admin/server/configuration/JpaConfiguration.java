package com.rapidark.cloud.uaa.admin.server.configuration;


import com.rapidark.framework.common.annotation.JpaDto;
import com.rapidark.framework.data.jpa.BaseRepositoryFactoryBean;
import com.rapidark.framework.data.jpa.sqltoy.SqlToyJpaRepositoryFactoryBean;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.support.DefaultConversionService;
import org.springframework.core.convert.support.GenericConversionService;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import javax.annotation.PostConstruct;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

/**
 * @author darkness
 * @version 1.0
 * @date 2021/6/1 17:23
 */
@Slf4j
@Configuration
@EnableJpaRepositories(
        basePackages = { "com.rapidark" },
        repositoryFactoryBeanClass = BaseRepositoryFactoryBean.class)
@EntityScan({
        "com.rapidark.cloud.base.client.model",
        "com.rapidark.cloud.base.server.modules",
        "com.rapidark.cloud.gateway.formwork.entity"
})
public class JpaConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 初始化注入@JpaDto对应的Converter
     */
    @PostConstruct
    public void init() {
        Map<String, Object> map = applicationContext.getBeansWithAnnotation(JpaDto.class);
        for (Object o : map.values()) {
            Class c = o.getClass();
            log.info("Jpa添加Converter,class={}", c.getName());
            GenericConversionService genericConversionService = ((GenericConversionService) DefaultConversionService.getSharedInstance());
            genericConversionService.addConverter(Map.class, c, m -> {
                try {
                    Object obj = c.getDeclaredConstructor().newInstance();
                    // 这里可以扩展,注入的converter,实现sql查寻出的结果为数据库中带下划线的字段,通过程序转为驼峰命名再设置到实体中
                    // 也可以做类型转换判断,这里未做类型判断,直接copy到dto中,类型不匹配的时候可能会出错
                    return copyMapToObj(m, obj);
                } catch (Exception e) {
                    throw new FatalBeanException("Jpa结果转换出错,class=" + c.getName(), e);
                }
            });
        }
    }

    /**
     * 将map中的值copy到bean中对应的字段上
     * @author bazhandao
     * @date 2020-03-26
     * @param map
     * @param target
     * @return
     */
    private Object copyMapToObj(Map<String, Object> map, Object target) {
        if(map == null || target == null || map.isEmpty()){
            return target;
        }
        Class<?> actualEditable = target.getClass();
        PropertyDescriptor[] targetPds = BeanUtils.getPropertyDescriptors(actualEditable);
        for (PropertyDescriptor targetPd : targetPds) {
            if(targetPd.getWriteMethod() == null) {
                continue;
            }
            try {
                String key = targetPd.getName();
                Object value = map.get(key);
                if (value == null) {
                    continue;
                }
                Method writeMethod = targetPd.getWriteMethod();
                if (!Modifier.isPublic(writeMethod.getDeclaringClass().getModifiers())) {
                    writeMethod.setAccessible(true);
                }
                writeMethod.invoke(target, value);
            } catch (Exception ex) {
                throw new FatalBeanException("Could not copy properties from source to target", ex);
            }
        }
        return target;
    }
}

