package com.xdreamaker.demo.axon.rest;

import com.xdreamaker.demo.axon.domain.model.ContractAggregate;
import com.xdreamaker.demo.axon.query.command.ContractCommandGateway;
import com.xdreamaker.demo.axon.domain.command.CreateContractCommand;
import com.xdreamaker.demo.axon.domain.command.DeleteContractCommand;
import com.xdreamaker.demo.axon.query.command.QueryContractCommand;
import com.xdreamaker.demo.axon.domain.command.UpdateContractCommand;
import lombok.AllArgsConstructor;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.time.Instant;

/**
 * @author darkness
 * @date 2021/6/26 22:51
 * @version 1.0
 */
@RestController
@RequestMapping("/contracts")
@AllArgsConstructor
public class ContractController {

    private final ContractCommandGateway contractCommandGateway;
    private final QueryGateway queryGateway;

    @PostMapping
    public Long createContract(@RequestBody @Valid CreateContractCommand command) {
        return contractCommandGateway.sendCommandAndWaitForAResult(command);
    }

    @PutMapping("/{id}")
    public void updateContract(@PathVariable("id") Long id, @RequestBody @Valid UpdateContractCommand command) {
        command.setIdentifier(id);
        contractCommandGateway.sendCommandAndWait(command);
    }

    @DeleteMapping("/{id}")
    public void deleteContract(@PathVariable("id") Long id) {
        contractCommandGateway.sendCommandAndWait(new DeleteContractCommand(id));
    }

    @GetMapping("/{id}")
    public ContractAggregate getContract(@PathVariable("id") Long id) {
        QueryContractCommand command = new QueryContractCommand(id, Instant.now());

        return queryGateway.query(command, ContractAggregate.class).join();
    }
}
