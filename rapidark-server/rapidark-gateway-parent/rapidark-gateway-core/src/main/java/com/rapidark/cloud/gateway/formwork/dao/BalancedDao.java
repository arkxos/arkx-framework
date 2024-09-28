package com.rapidark.cloud.gateway.formwork.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapidark.cloud.gateway.formwork.entity.Balanced;

/**
 * @Description 负载管理数据层操作接口
 * @Author jianglong
 * @Date 2020/06/28
 * @Version V1.0
 */
public interface BalancedDao extends JpaRepository<Balanced, Long> {

}
