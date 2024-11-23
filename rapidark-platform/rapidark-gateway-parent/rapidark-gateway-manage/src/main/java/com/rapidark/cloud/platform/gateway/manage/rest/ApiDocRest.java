package com.rapidark.cloud.platform.gateway.manage.rest;

import com.rapidark.cloud.platform.common.core.util.R;
import com.rapidark.cloud.platform.gateway.framework.base.BaseRest;
import com.rapidark.cloud.platform.gateway.framework.entity.ApiDoc;
import com.rapidark.cloud.platform.gateway.framework.entity.Route;
import com.rapidark.cloud.platform.gateway.framework.service.ApiDocService;
import com.rapidark.cloud.platform.gateway.framework.service.RouteService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import jakarta.annotation.Resource;

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
    private RouteService routeService;

    @Resource
    private ApiDocService apiDocService;

    /**
     * 获取接口列表
     * @return
     */
    @RequestMapping(value = "/list", method = {RequestMethod.GET, RequestMethod.POST})
    public R list(){
        return R.ok(routeService.list(new Route()));
    }

    /**
     * 保存API文档
     * @param apiDoc
     * @return
     */
    @RequestMapping(value = "/save", method = {RequestMethod.POST})
    public R save(@RequestBody ApiDoc apiDoc){
        Assert.notNull(apiDoc, "未获取到对象");
        Assert.isTrue(StringUtils.isNotBlank(apiDoc.getId()), "未获取到对象ID");
        apiDocService.save(apiDoc);
        return R.ok();
    }

    /**
     * 查询API文档
     * @param id
     * @return
     */
    @RequestMapping(value = "/findById", method = {RequestMethod.GET, RequestMethod.POST})
    public R findById(@RequestParam String id) {
        Assert.isTrue(StringUtils.isNotBlank(id), "未获取到请求ID");
        return R.ok(apiDocService.findById(id));
    }

}
