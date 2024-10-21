//package com.rapidark.framework.boot.spring.axon;
//
//import org.axonframework.eventhandling.EventListener;
//import org.axonframework.eventhandling.EventMessage;
//import org.axonframework.eventhandling.ListenerInvocationErrorHandler;
//
///**
// * @author Darkness
// * @date 2019-08-18 13:06:30
// * @version V1.0
// */
//public class AxonListenerExceptionHandler implements ListenerInvocationErrorHandler {
//
//	@Override
//	public void onError(Exception exception, EventMessage<?> event, EventListener eventListener) throws Exception {
//		System.out.println("axon listener exception");
//		exception.printStackTrace();
//		throw exception;
//	}
//
//}
