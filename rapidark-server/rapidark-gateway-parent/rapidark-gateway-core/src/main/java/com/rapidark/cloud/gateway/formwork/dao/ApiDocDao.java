package com.rapidark.cloud.gateway.formwork.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.rapidark.cloud.gateway.formwork.entity.ApiDoc;

/**
 * @Description 网关路由API接口文档数据层操作接口
 * @Author JL
 * @Date 2020/11/25
 * @Version V1.0
 */
public interface ApiDocDao extends JpaRepository<ApiDoc, String> {

}
