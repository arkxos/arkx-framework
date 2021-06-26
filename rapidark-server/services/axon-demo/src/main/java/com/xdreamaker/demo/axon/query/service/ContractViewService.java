package com.xdreamaker.demo.axon.query.service;

import com.xdreamaker.demo.axon.query.entity.ContractView;
import com.xdreamaker.demo.axon.query.entity.ContractViewRepository;
import com.xdreamaker.demo.axon.domain.model.ContractAggregate;
import com.xdreamaker.demo.axon.query.command.QueryContractCommand;
import com.xdreamaker.demo.axon.query.helper.ContractAggregateViewMapper;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class ContractViewService {

    private final QueryGateway queryGateway;
    private final ContractViewRepository contractViewRepository;

    public void updateViewFromAggregateById(Long aggregateIdentifier, Instant time) {

        QueryContractCommand command = new QueryContractCommand(aggregateIdentifier, time);
        ContractAggregate aggregate = queryGateway.query(command, ContractAggregate.class).join();
        ContractView view = contractViewRepository.findById(aggregateIdentifier).orElse(new ContractView());

        ContractAggregateViewMapper.mapAggregateToView(aggregate, view);
        contractViewRepository.save(view);
    }
}
