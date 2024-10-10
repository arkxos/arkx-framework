package com.xdreamaker.demo.axon.domain.command;

import com.xdreamaker.demo.axon.domain.command.UpdateContractCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author darkness
 * @date 2021/6/26 22:38
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class CreateContractCommand extends UpdateContractCommand {

    public CreateContractCommand(Long identifier, String name, String partyA, String partyB, String industryName) {
        super(identifier, name, partyA, partyB, industryName);
    }

}
