package com.xdreamaker.framework.ddd.es.continuance.consumer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;

/**
 * @author darkness
 * @date 2021/6/27 0:05
 * @version 1.0
 */
@Entity
@Table(indexes = @Index(columnList = "aggregateIdentifier,type", unique = true))
@Getter
@Setter
@NoArgsConstructor
public class DomainAggregateSequence {

    @Id
    @GeneratedValue
    private Long id;

    private Long sequenceNumber;

    private Long aggregateIdentifier;

    private String type;

}
