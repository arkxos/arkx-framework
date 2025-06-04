package com.arkxos.framework.avatarmq.netty;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

/**
 * @filename:MessageEventAdvisor.java
 * @description:MessageEventAdvisor功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageEventAdvisor implements MethodInterceptor {

    private MessageEventProxy proxy;
    private Object msg;

    public MessageEventAdvisor(MessageEventProxy proxy, Object msg) {
        this.proxy = proxy;
        this.msg = msg;
    }

    public Object invoke(MethodInvocation invocation) throws Throwable {
        proxy.beforeMessage(msg);
        
        Object obj = invocation.proceed();
        
        proxy.afterMessage(msg);
        
        return obj;
    }
}
