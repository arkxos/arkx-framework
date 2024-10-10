package com.xdreamaker.framework.ddd.es.continuance.producer.jpa;

public interface AggregateSequenceNumberInterface {

    String getAggregateIdentifier();

    Long getSequenceNumber();
}
