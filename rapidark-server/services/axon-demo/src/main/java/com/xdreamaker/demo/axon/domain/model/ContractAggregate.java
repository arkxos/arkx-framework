package com.xdreamaker.demo.axon.domain.model;

import com.xdreamaker.demo.axon.domain.command.CreateContractCommand;
import com.xdreamaker.demo.axon.domain.command.DeleteContractCommand;
import com.xdreamaker.demo.axon.domain.command.UpdateContractCommand;
import com.xdreamaker.framework.ddd.es.continuance.producer.uuid.UIDGenerator;
import com.xdreamaker.demo.axon.domain.event.ContractCreatedEvent;
import com.xdreamaker.demo.axon.domain.event.ContractDeletedEvent;
import com.xdreamaker.demo.axon.domain.event.ContractUpdatedEvent;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.messaging.MetaData;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.apply;

/**
 * @author darkness
 * @date 2021/6/26 22:36
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Aggregate
public class ContractAggregate implements ContractInterface {

    @AggregateIdentifier
    private Long identifier;

    private String name;

    private String partyA;

    private String partyB;

    private String industryName;

    private boolean deleted = false;

    @CommandHandler
    public ContractAggregate(CreateContractCommand command, MetaData metaData, UIDGenerator generator) {
        if (null == command.getIdentifier()) {
            command.setIdentifier(generator.getId());
        }
        apply(new ContractCreatedEvent(command.getIdentifier(),
            command.getName(),
            command.getPartyA(),
            command.getPartyB(),
            command.getIndustryName()), metaData);
    }

    @CommandHandler
    private void on(UpdateContractCommand command, MetaData metaData) {
        apply(new ContractUpdatedEvent(command.getIdentifier(),
                command.getName(),
                command.getPartyA(),
                command.getPartyB(),
                command.getIndustryName()),
            metaData);
    }

    @CommandHandler
    private void on(DeleteContractCommand command, MetaData metaData) {
        apply(new ContractDeletedEvent(command.getIdentifier()), metaData);
    }

    @EventSourcingHandler
    private void on(ContractCreatedEvent event) {
        this.setIdentifier(event.getIdentifier());
        this.onUpdate(event);
    }

    @EventSourcingHandler
    private void onUpdate(ContractUpdatedEvent event) {
        this.setName(event.getName());
        this.setPartyA(event.getPartyA());
        this.setPartyB(event.getPartyB());
        this.setIndustryName(event.getIndustryName());
    }

    @EventSourcingHandler(payloadType = ContractDeletedEvent.class)
    private void on() {
        this.setDeleted(true);
    }

}
