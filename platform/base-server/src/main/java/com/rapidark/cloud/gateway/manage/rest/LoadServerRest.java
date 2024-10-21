package com.rapidark.cloud.gateway.manage.rest;

import com.rapidark.framework.commons.model.ResultBody;
import com.rapidark.framework.commons.utils.PageData;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.bean.LoadServerReq;
import com.rapidark.cloud.gateway.manage.service.LoadServerService;

import javax.annotation.Resource;

/**
 * @Description 负载服务管理控制器
 * @Author JL
 * @Date 2020/06/28
 * @Version V1.0
 */
@RestController
public class LoadServerRest extends BaseRest {

    @Resource
    private LoadServerService loadServerService;

    /**
     * 查询当前负载网关已加配置路由服务
     * @param loadServerReq
     * @return
     */
    @RequestMapping(value = "/loadServer/regList", method = { RequestMethod.POST })
    public ResultBody regList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, true);
    }

    /**
     * 查询当前负载网关未加配置路由服务
     * @param loadServerReq
     * @return
     */
    @RequestMapping(value = "/loadServer/notRegPageList", method = {RequestMethod.GET, RequestMethod.POST})
    public ResultBody notRegPageList(@RequestBody LoadServerReq loadServerReq) {
        return list(loadServerReq, false);
    }

    /**
     * 查询数据
     * @param loadServerReq
     * @param isReg
     * @return
     */
    private ResultBody list(LoadServerReq loadServerReq, boolean isReg){
        Assert.notNull(loadServerReq, "未获取到对象");
        if (isReg) {
//            Assert.isTrue(StringUtils.isNotBlank(loadServerReq.getBalancedId()), "未获取到对象ID");
            return ResultBody.ok(loadServerService.loadServerList(loadServerReq.getBalancedId()));
        }else {
            int currentPage = getCurrentPage(loadServerReq.getCurrentPage());
            int pageSize = getPageSize(loadServerReq.getPageSize());
            PageData data = loadServerService.notLoadServerPageList(currentPage, pageSize);
            return ResultBody.ok(data);
        }
    }

}
