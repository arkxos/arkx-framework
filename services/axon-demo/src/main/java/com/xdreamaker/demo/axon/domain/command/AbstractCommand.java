package com.xdreamaker.demo.axon.domain.command;

import lombok.*;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

/**
 * @author darkness
 * @date 2021/6/26 22:37
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AbstractCommand {

    @TargetAggregateIdentifier
    private Long identifier;

}
