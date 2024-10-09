package com.rapidark.cloud.base.server.service.query;

import com.rapidark.common.annotation.Query;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @author darkness
 * @version 1.0
 * @date 2022/6/29 11:29
 */
@Data
@NoArgsConstructor
public class AccountTypeInQueryCriteria {

    @Query
    private String userId;

    @Query(type=Query.Type.IN)
    private List<String> accountType;

    @Query
    private String domain;

}
