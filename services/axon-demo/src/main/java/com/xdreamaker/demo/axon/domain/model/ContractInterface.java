package com.xdreamaker.demo.axon.domain.model;

import javax.validation.constraints.NotBlank;

/**
 * @author darkness
 * @date 2021/6/26 22:35
 * @version 1.0
 */
public interface ContractInterface {
    
    @NotBlank
    String getName();

    @NotBlank
    String getPartyA();

    @NotBlank
    String getPartyB();

    @NotBlank
    String getIndustryName();
}
