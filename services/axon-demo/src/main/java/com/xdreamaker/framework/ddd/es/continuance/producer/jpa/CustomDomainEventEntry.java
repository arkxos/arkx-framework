package com.xdreamaker.framework.ddd.es.continuance.producer.jpa;

import lombok.Getter;
import lombok.Setter;
import org.axonframework.eventhandling.AbstractSequencedDomainEventEntry;
import org.axonframework.eventhandling.DomainEventMessage;
import org.axonframework.serialization.Serializer;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

/**
 * @author darkness
 * @date 2021/6/26 23:54
 * @version 1.0
 */
@Entity(name = "DomainEventEntry")
@Getter
@Setter
@Table(indexes = @Index(columnList = "aggregateIdentifier,sequenceNumber", unique = true))
@EntityListeners(CustomDomainEventEntryListener.class)
public class CustomDomainEventEntry extends AbstractSequencedDomainEventEntry<byte[]> {

    @NotNull
    @Column(columnDefinition = "tinyint(1) default 0")
    private boolean sent = false;

    public CustomDomainEventEntry(DomainEventMessage<?> eventMessage, Serializer serializer) {
        super(eventMessage, serializer, byte[].class);
        this.setSent(false);
    }

    /**
     * Default constructor required by JPA
     */
    protected CustomDomainEventEntry() {
    }

}
