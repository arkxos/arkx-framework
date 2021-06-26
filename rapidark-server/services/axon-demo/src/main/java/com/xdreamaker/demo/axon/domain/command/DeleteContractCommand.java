package com.xdreamaker.demo.axon.domain.command;

import com.xdreamaker.demo.axon.domain.command.AbstractCommand;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * @author darkness
 * @date 2021/6/26 22:38
 * @version 1.0
 */
@NoArgsConstructor
@Getter
@Setter
public class DeleteContractCommand extends AbstractCommand {

    public DeleteContractCommand(Long identifier) {
        super(identifier);
    }

}
