package com.rapidark.cloud.gateway.manage.service;

import com.rapidark.cloud.gateway.manage.repository.SecureIpRepository;
import com.rapidark.framework.commons.utils.PageData;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;

import com.rapidark.cloud.gateway.formwork.base.BaseService;
import com.rapidark.cloud.gateway.formwork.entity.SecureIp;

/**
 * @Description IP管理业务操作类
 * @Author jianglong
 * @Date 2020/05/28
 * @Version V1.0
 */
@Service
public class SecureIpService extends BaseService<SecureIp, String, SecureIpRepository> {

    /**
     * 分页查询（支持模糊查询）
     * @param secureIp
     * @param currentPage
     * @param pageSize
     * @return
     */
    @Override
    public PageData<SecureIp> pageList(SecureIp secureIp, int currentPage, int pageSize){
        //构造条件查询方式
        ExampleMatcher matcher = ExampleMatcher.matching();
        if (StringUtils.isNotBlank(secureIp.getIp())) {
            //支持模糊条件查询
            matcher = matcher.withMatcher("ip", ExampleMatcher.GenericPropertyMatchers.contains());
        }
        return this.pageList(secureIp, matcher, currentPage, pageSize);
    }

}
