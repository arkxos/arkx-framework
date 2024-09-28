package com.rapidark.cloud.gateway.formwork.service;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.dao.ApiDocDao;
import com.rapidark.cloud.gateway.formwork.entity.ApiDoc;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;

/**
 * @Description 网关路由API接口文档业务实现类
 * @Author JL
 * @Date 2020/11/25
 * @Version V1.0
 */
@Service
public class ApiDocService extends BaseService<ApiDoc, String, ApiDocDao> {
    private EntityManager entityManager;
}
