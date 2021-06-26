package com.xdreamaker.demo.axon.query.command;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

/**
 * @author darkness
 * @date 2021/6/26 23:09
 * @version 1.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class QueryContractCommand {

    @NotBlank
    @NotNull
    private Long id;

    private Instant endDate;

}
