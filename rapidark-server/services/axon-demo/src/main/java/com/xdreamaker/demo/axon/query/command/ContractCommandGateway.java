package com.xdreamaker.demo.axon.query.command;

import com.xdreamaker.demo.axon.domain.command.AbstractCommand;
import com.xdreamaker.demo.axon.domain.model.ContractAggregate;
import org.axonframework.commandhandling.gateway.Timeout;
import org.axonframework.messaging.annotation.MetaDataValue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * @author darkness
 * @date 2021/6/26 23:40
 * @version 1.0
 */
public interface ContractCommandGateway {

    // fire and forget
    void sendCommand(AbstractCommand command);

    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    Long sendCommandAndWaitForAResult(AbstractCommand command);

    // method that will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    void sendCommandAndWait(AbstractCommand command);

    // method that attaches meta data and will wait for a result for 10 seconds
    @Timeout(value = 6, unit = TimeUnit.SECONDS)
    ContractAggregate sendCommandAndWaitForAResult(AbstractCommand command,
                                                   @MetaDataValue("userId") String userId);

    // this method will also wait, caller decides how long
    void sendCommandAndWait(AbstractCommand command, long timeout, TimeUnit unit) throws TimeoutException, InterruptedException;
}
