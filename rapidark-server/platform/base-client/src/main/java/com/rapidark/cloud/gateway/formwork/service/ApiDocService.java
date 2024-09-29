package com.rapidark.cloud.gateway.formwork.service;

import org.springframework.stereotype.Service;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.repository.ApiDocRepository;
import com.rapidark.cloud.gateway.formwork.entity.ApiDoc;

import javax.persistence.EntityManager;

/**
 * @Description 网关路由API接口文档业务实现类
 * @Author JL
 * @Date 2020/11/25
 * @Version V1.0
 */
@Service
public class ApiDocService extends BaseService<ApiDoc, String, ApiDocRepository> {
    private EntityManager entityManager;
}
