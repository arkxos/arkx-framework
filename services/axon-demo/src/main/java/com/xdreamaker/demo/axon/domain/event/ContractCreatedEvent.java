package com.xdreamaker.demo.axon.domain.event;

import com.xdreamaker.demo.axon.domain.event.ContractUpdatedEvent;
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
public class ContractCreatedEvent extends ContractUpdatedEvent {

    public ContractCreatedEvent(Long identifier, String name, String partyA, String partyB, String industryName) {
        super(identifier, name, partyA, partyB, industryName);
    }

}
