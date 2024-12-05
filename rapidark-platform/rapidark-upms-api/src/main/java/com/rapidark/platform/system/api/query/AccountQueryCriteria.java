package com.rapidark.platform.system.api.query;

import com.rapidark.framework.common.annotation.Query;
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
