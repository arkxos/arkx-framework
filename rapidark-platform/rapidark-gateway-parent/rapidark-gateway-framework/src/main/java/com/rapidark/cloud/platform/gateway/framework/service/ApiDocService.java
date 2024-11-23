package com.rapidark.cloud.platform.gateway.framework.service;

import com.rapidark.cloud.platform.gateway.framework.base.BaseService;
import com.rapidark.cloud.platform.gateway.framework.repository.ApiDocRepository;
import com.rapidark.cloud.platform.gateway.framework.entity.ApiDoc;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityManager;

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
