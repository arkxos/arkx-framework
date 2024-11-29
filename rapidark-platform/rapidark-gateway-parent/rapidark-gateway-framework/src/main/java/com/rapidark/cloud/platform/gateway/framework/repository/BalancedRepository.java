package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.entity.Balanced;
import com.rapidark.framework.data.jpa.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description 负载管理数据层操作接口
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
public interface BalancedRepository extends BaseRepository<Balanced, Long> {

}
