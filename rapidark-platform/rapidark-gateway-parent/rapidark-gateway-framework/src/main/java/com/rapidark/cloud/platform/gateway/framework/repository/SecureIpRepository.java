package com.rapidark.cloud.platform.gateway.framework.repository;

import com.rapidark.cloud.platform.gateway.framework.entity.SecureIp;
import com.rapidark.framework.data.jpa.BaseRepository;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description IP管理Dao数据层操作接口
 * @Author JL
 * @Date 2020/05/28
 * @Version V1.0
 */
public interface SecureIpRepository extends BaseRepository<SecureIp, String> {
}
