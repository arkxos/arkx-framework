package com.xdreamaker.demo.axon.domain.command;

import com.xdreamaker.demo.axon.domain.command.AbstractCommand;
import com.xdreamaker.demo.axon.domain.model.ContractInterface;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author darkness
 * @date 2021/6/26 22:37
 * @version 1.0
 */
@Getter
@Setter
@NoArgsConstructor
public class UpdateContractCommand extends AbstractCommand implements ContractInterface {

    private String name;

    private String partyA;

    private String partyB;

    private String industryName;

    public UpdateContractCommand(Long identifier, String name, String partyA, String partyB, String industryName) {
        super(identifier);
        this.name = name;
        this.partyA = partyA;
        this.partyB = partyB;
        this.industryName = industryName;
    }

}
