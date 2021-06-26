package com.xdreamaker.demo.axon.domain.event;

import com.xdreamaker.demo.axon.domain.event.AbstractEvent;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author darkness
 * @date 2021/6/26 22:44
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class ContractDeletedEvent extends AbstractEvent {

    public ContractDeletedEvent(Long identifier) {
        super(identifier);
    }
    
}
