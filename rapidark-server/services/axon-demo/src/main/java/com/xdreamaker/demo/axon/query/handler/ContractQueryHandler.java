package com.xdreamaker.demo.axon.query.handler;

import com.xdreamaker.demo.axon.domain.model.ContractAggregate;
import com.xdreamaker.demo.axon.query.command.QueryContractCommand;
import com.xdreamaker.framework.ddd.es.continuance.producer.jpa.CustomEventSourcingRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.axonframework.eventsourcing.EventSourcedAggregate;
import org.axonframework.modelling.command.LockAwareAggregate;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

/**
 * @author darkness
 * @date 2021/6/26 23:10
 * @version 1.0
 */
@Component
@AllArgsConstructor
@Slf4j
public class ContractQueryHandler {

    private final CustomEventSourcingRepository<ContractAggregate> contractAggregateRepository;

    @QueryHandler
    public ContractAggregate on(QueryContractCommand command) {
        LockAwareAggregate<ContractAggregate, EventSourcedAggregate<ContractAggregate>> lockAwareAggregate = contractAggregateRepository.load(command.getId().toString(), command.getEndDate());
        return lockAwareAggregate.getWrappedAggregate().getAggregateRoot();
    }


}
