package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.entity.SentinelRule;
import com.rapidark.framework.data.jpa.BaseRepository;

/**
 * @Description 限流管理Dao数据层操作接口
 * @Author JL
 * @Date 2022/12/04
 * @Version V1.0
 */
public interface SentinelRuleRepository extends BaseRepository<SentinelRule, String> {

}
