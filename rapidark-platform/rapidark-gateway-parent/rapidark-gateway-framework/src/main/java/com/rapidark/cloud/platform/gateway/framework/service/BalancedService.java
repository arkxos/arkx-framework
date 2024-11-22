package com.rapidark.cloud.platform.gateway.framework.service;

import com.rapidark.cloud.platform.gateway.framework.base.BaseService;
import com.rapidark.cloud.platform.gateway.framework.dao.BalancedDao;
import com.rapidark.cloud.platform.gateway.framework.entity.Balanced;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description 负载管理业务实现类
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@Service
public class BalancedService extends BaseService<Balanced, String, BalancedDao> {

    @Resource
    private LoadServerService loadServerService;

    /**
     * 删除负载以及注册到负载的路由服务
     * @param id
     */
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = {Throwable.class})
    public void deleteAndServer(String id){
        loadServerService.deleteAllByBalancedId(id);
        this.deleteById(id);
    }
}
