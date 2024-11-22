package com.rapidark.cloud.platform.gateway.framework.dao;

import com.rapidark.cloud.platform.gateway.framework.entity.SecureIp;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Description IP管理Dao数据层操作接口
 * @Author JL
 * @Date 2020/05/28
 * @Version V1.0
 */
public interface SecureIpDao extends JpaRepository<SecureIp, String> {
}
