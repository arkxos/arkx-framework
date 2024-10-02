package com.rapidark.cloud.gateway.manage.rest;

import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import com.rapidark.cloud.gateway.formwork.base.BaseRest;
import com.rapidark.cloud.gateway.formwork.entity.ApiDoc;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.formwork.service.ApiDocService;
import com.rapidark.cloud.gateway.formwork.service.GatewayAppRouteService;
import com.rapidark.cloud.gateway.formwork.util.ApiResult;

import javax.annotation.Resource;

/**
 * @Description API接口文档控制器
 * @Author JL
 * @Date 2020/11/24
 * @Version V1.0
 */
@RestController
@RequestMapping("/apiDoc")
public class ApiDocRest extends BaseRest {

    @Resource
    private GatewayAppRouteService gatewayAppRouteService;

    @Resource
    private ApiDocService apiDocService;

    /**
     * 获取接口列表
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult list(){
        return new ApiResult(gatewayAppRouteService.list(new GatewayAppRoute()));
    }

    /**
     * 保存API文档
     * @param apiDoc
     * @return
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public ApiResult save(@RequestBody ApiDoc apiDoc){
        Assert.notNull(apiDoc, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(apiDoc.getId()), "未获取到对象ID");
        apiDocService.save(apiDoc);
        return new ApiResult();
    }

    /**
     * 查询API文档
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public ApiResult findById(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到请求ID");
        return new ApiResult(apiDocService.findById(id));
    }

}
