package com.xdreamaker.demo.axon.stream;

import com.xdreamaker.framework.ddd.es.continuance.common.DomainEvent;
import com.xdreamaker.framework.ddd.es.continuance.consumer.StreamEventHandler;
import com.xdreamaker.demo.axon.domain.event.ContractCreatedEvent;
import com.xdreamaker.demo.axon.query.service.ContractViewService;
import com.xdreamaker.framework.ddd.stream.channel.ChannelDefinition;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;

/**
 * @author darkness
 * @date 2021/6/27 0:15
 * @version 1.0
 */
@Component
@AllArgsConstructor
@Transactional
public class ContractEventHandler {

    private final ContractViewService contractViewService;

    @StreamEventHandler(types = ChannelDefinition.CONTRACTS_INPUT)
    public void handle(ContractCreatedEvent event, DomainEvent<ContractCreatedEvent, HashMap> domainEvent) {
        contractViewService.updateViewFromAggregateById(event.getIdentifier(), domainEvent.getTimestamp());
    }

}
