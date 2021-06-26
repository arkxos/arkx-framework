package com.xdreamaker.demo.axon.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.axonframework.modelling.command.TargetAggregateIdentifier;
import org.axonframework.serialization.Revision;

/**
 * @author darkness
 * @date 2021/6/26 22:39
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Revision("1.0.0")
public class AbstractEvent {

    @TargetAggregateIdentifier
    private Long identifier;
}
