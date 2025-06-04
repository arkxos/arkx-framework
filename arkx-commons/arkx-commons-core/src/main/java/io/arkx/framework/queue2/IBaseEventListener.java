package io.arkx.framework.queue2;
//package com.arkxos.framework.queue;
//
///** 
// * 事件处理接口，实现此接口并且getEventClasses方法的返回结果条数大于0，方可处理对应的事件 
// */  
//public interface IBaseEventListener {  
//
//	/** 
//     * 事件处理的方法 
//     * 本来onBaseEvent方法也可以去除，直接由注解来定义即可，但由于以下原因，还是要保留：
//     * 1、如果没有任何接口定义onBaseEvent方法，那么SPRING的代理类也不会有此方法，这样就无法使用AOP的种种好处了；
//     * 2、为规范事件处理的方法名和参数，更易于后续维护，所以还是要有个接口定义为好。
//     */  
//    void onBaseEvent(BaseEvent<?> event);  
//    
//    /**
//     * @description
//	 * 在顶层接口IBaseService增加一个方法getRealClass，
//	 * 此方法用于返回真正的业务类字节码引用，此方法在抽象业务类用统一实现即可。
//	 * 本来不需要此方法，但由于使用了SPRING的AOP，一时没有找到取得真正业务类字节码引用的方法，所以才定义这么个接口
//     */
//    Class<? extends IBaseEventListener> getRealClass();
//} 
