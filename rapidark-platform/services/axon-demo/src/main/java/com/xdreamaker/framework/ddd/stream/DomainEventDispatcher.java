package com.xdreamaker.framework.ddd.stream;


import com.xdreamaker.framework.ddd.es.continuance.common.DomainEvent;
import com.xdreamaker.framework.ddd.es.continuance.consumer.StreamDomainEventDispatcher;
import com.xdreamaker.framework.ddd.stream.channel.ChannelDefinition;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
//import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author darkness
 * @date 2021/6/27 0:10
 * @version 1.0
 */
@Slf4j
@Component
@Transactional
@AllArgsConstructor
public class DomainEventDispatcher {

    private final StreamDomainEventDispatcher streamDomainEventDispatcher;

//    @StreamListener(target = ChannelDefinition.CONTRACTS_INPUT, condition = "headers['messageType']=='eventSourcing'")
    public void handleBuilding(@Payload DomainEvent event) {
        streamDomainEventDispatcher.dispatchEvent(event, ChannelDefinition.CONTRACTS_INPUT);
    }

}
