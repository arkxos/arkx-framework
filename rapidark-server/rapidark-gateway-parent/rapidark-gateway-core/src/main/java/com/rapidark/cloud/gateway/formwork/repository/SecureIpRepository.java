package com.rapidark.cloud.gateway.formwork.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapidark.cloud.gateway.formwork.entity.SecureIp;

/**
 * @Description IP管理Dao数据层操作接口
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
public interface SecureIpRepository extends JpaRepository<SecureIp, String> {
}
