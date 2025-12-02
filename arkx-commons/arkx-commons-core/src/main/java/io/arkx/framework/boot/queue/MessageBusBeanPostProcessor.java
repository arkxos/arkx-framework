package io.arkx.framework.boot.queue;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.stereotype.Component;

import io.arkx.framework.queue2.Consumer;
import io.arkx.framework.queue2.MessageBus;

/**
 *
 * @author Darkness
 * @date 2014-12-8 下午12:45:23
 * @version V1.0
 */
@Component
public class MessageBusBeanPostProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean.getClass().isAnnotationPresent(Consumer.class)) {
            MessageBus.globalInstance().register(bean);
        }
        return bean;
    }

    // @Override
    // public Object postProcessBeforeInitialization(Object bean, String beanName)
    // throws BeansException {
    // return bean;
    // }

}
