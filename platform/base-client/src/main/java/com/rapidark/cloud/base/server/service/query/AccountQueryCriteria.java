package com.rapidark.cloud.base.server.service.query;

import com.rapidark.framework.commons.annotation.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:29
 */
@Data
@NoArgsConstructor
public class AccountQueryCriteria {

    @Query
    private String account;

    @Query
    private String accountType;

    @Query
    private String domain;

}
