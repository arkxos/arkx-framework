package com.rapidark.cloud.base.server.service.dto;

import lombok.Data;
import com.rapidark.common.annotation.Query;

/**
 * @website http://rapidark.com
 * @author Darkness
 * @date 2022-05-25
 **/
@Data
public class OpenClientQueryCriteria {

    /** 精确 */
    @Query
    private String appId;

    /** 模糊 */
    @Query(type = Query.Type.INNER_LIKE)
    private String appName;
}
