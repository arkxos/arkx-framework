package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.service.LoadServerService;
import com.rapidark.cloud.platform.common.core.util.R;
import com.rapidark.cloud.platform.gateway.framework.bean.LoadServerReq;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import jakarta.annotation.Resource;

/**
 * @Description 负载服务管理控制器
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@RestController
@RequestMapping("/loadServer")
public class LoadServerRest extends BaseRest {

    @Resource
    private LoadServerService loadServerService;

    /**
     * 查询当前负载网关已加配置路由服务
     * @param loadServerReq
     * @return
     */
    @RequestMapping(value = "/regList", method = {RequestMethod.GET, RequestMethod.POST})
    public R regList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, true);
    }

    /**
     * 查询当前负载网关未加配置路由服务
     * @param loadServerReq
     * @return
     */
    @RequestMapping(value = "/notRegPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public R notRegPageList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, false);
    }

    /**
     * 查询数据
     * @param loadServerReq
     * @param isReg
     * @return
     */
    private R list(LoadServerReq loadServerReq, boolean isReg){
        Assert.notNull(loadServerReq, "未获取到对象");
        if (isReg) {
            Assert.isTrue(StringUtils.isNotBlank(loadServerReq.getBalancedId()), "未获取到对象ID");
            return R.ok(loadServerService.loadServerList(loadServerReq.getBalancedId()));
        }else {
            int currentPage = getCurrentPage(loadServerReq.getCurrentPage());
            int pageSize = getPageSize(loadServerReq.getPageSize());
            return R.ok(loadServerService.notLoadServerPageList(currentPage, pageSize));
        }
    }

}
