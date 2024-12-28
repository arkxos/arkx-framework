package com.rapidark.framework.avatarmq.netty;

import org.springframework.aop.framework.ProxyFactory;
import org.springframework.aop.support.NameMatchMethodPointcutAdvisor;

import com.rapidark.framework.avatarmq.core.HookMessageEvent;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @filename:MessageEventWrapper.java
 * @description:MessageEventWrapper功能模块
 * @author tangjie<https://github.com/tang-jie>
 * @blog http://www.cnblogs.com/jietang/
 * @since 2016-8-11
 */
public class MessageEventWrapper<T> extends ChannelInboundHandlerAdapter implements MessageEventHandler, MessageEventProxy {

	public final static String proxyMappedName = "handleMessage";
	
    protected MessageProcessor processor;
    protected Throwable cause;
    protected HookMessageEvent<T> hook;
    protected MessageConnectFactory factory;
    private MessageEventWrapper<T> wrapper;

    public MessageEventWrapper() {
    }

    public MessageEventWrapper(MessageProcessor processor) {
        this(processor, null);
    }

    public MessageEventWrapper(MessageProcessor processor, HookMessageEvent<T> hook) {
        this.processor = processor;
        this.hook = hook;
        this.factory = processor.getMessageConnectFactory();
    }

    @Override
    public void handleMessage(ChannelHandlerContext ctx, Object msg) {
        return;
    }

    @Override
    public void beforeMessage(Object msg) {
    }

    @Override
    public void afterMessage(Object msg) {
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
    }

    @Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);

        ProxyFactory weaver = new ProxyFactory(wrapper);
        NameMatchMethodPointcutAdvisor advisor = new NameMatchMethodPointcutAdvisor();
        advisor.setMappedName(MessageEventWrapper.proxyMappedName);
        advisor.setAdvice(new MessageEventAdvisor(wrapper, msg));
        weaver.addAdvisor(advisor);

        MessageEventHandler proxyObject = (MessageEventHandler) weaver.getProxy();
        proxyObject.handleMessage(ctx, msg);
    }

	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        this.cause = cause;
        cause.printStackTrace();
    }

    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        super.channelInactive(ctx);
    }

    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        super.channelReadComplete(ctx);
    }

    public Throwable getCause() {
        return cause;
    }

    public void setWrapper(MessageEventWrapper<T> wrapper) {
        this.wrapper = wrapper;
    }
}
