//package io.arkx.framework.boot.spring.axon;
//
//import java.time.Instant;
//import java.time.LocalDateTime;
//
//import org.axonframework.eventhandling.EventMessage;
//import org.axonframework.messaging.InterceptorChain;
//import org.axonframework.messaging.MessageHandlerInterceptor;
//import org.axonframework.messaging.unitofwork.UnitOfWork;
//
///**
// * @author Darkness
// * @date 2019-08-18 11:29:06
// * @version V1.0
// */
//public class MyEventHandlerInterceptor implements MessageHandlerInterceptor<EventMessage<?>> {
//
//    @Override
//    public Object handle(UnitOfWork<? extends EventMessage<?>> unitOfWork, InterceptorChain interceptorChain) throws Exception {
//        EventMessage<?> event = unitOfWork.getMessage();
//        Instant timestamp = event.getTimestamp();
//        LocalDateTime eventOccurredOn = AxonUtil.instant2LocalDateTime(timestamp);
//        Class<?> eventType = event.getPayloadType();
//        if(Auditor.class.isAssignableFrom(eventType)) {
//        	Auditor auditor = (Auditor)event.getPayload();
//        	if(auditor.getOccurredOn() == null) {
//        		auditor.setOccurredOn(eventOccurredOn);
//        	}
//        	CurrentAuditor.set(auditor);
//        }
//        
//        return interceptorChain.proceed();
//    }
//}
//
package io.arkx.framework.boot.spring.axon;


