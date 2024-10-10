package com.xdreamaker.demo.axon.query.helper;

import com.xdreamaker.demo.axon.query.entity.ContractView;
import com.xdreamaker.demo.axon.domain.model.ContractAggregate;

public class ContractAggregateViewMapper {

    public static void mapAggregateToView(ContractAggregate aggregate, ContractView view) {
        view.setId(aggregate.getIdentifier());
        view.setPartyA(aggregate.getPartyA());
        view.setPartyB(aggregate.getPartyB());
        view.setDeleted(aggregate.isDeleted());
        view.setName(aggregate.getName());
        view.setIndustryName(aggregate.getIndustryName());
    }
}
