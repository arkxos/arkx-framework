package com.rapidark.cloud.platform.gateway.rest;

import com.rapidark.cloud.platform.common.core.util.ResponseResult;
import com.rapidark.cloud.platform.gateway.framework.entity.GatewayAppRoute;
import com.rapidark.cloud.platform.gateway.framework.service.GatewayAppRouteService;
import com.rapidark.cloud.platform.gateway.service.DynamicRouteService;
import com.rapidark.cloud.platform.gateway.service.LoadRouteService;
import com.rapidark.cloud.platform.gateway.service.load.RouteDefinitionConverter;
import com.rapidark.cloud.platform.gateway.vo.GatewayRouteDefinition;
import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import jakarta.annotation.Resource;

/**
 * @Description 动态添加路由
 * @Author JL
 * @Date 2020/05/11
 * @Version V1.0
 */
//@RestController
@RequestMapping("/gateway/route")
public class RouteController {

    @Resource
    private DynamicRouteService dynamicRouteService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private GatewayAppRouteService gatewayAppRouteService;

    /**
     add-json:
     {
     "filters":[
         {
             "name":"StripPrefix",
             "args":{
             "_genkey_0":"1"
             }
         }
     ],
     "id":"hi_producer",
     "uri":"lb://service-hi",
     "order":1,
     "predicates":[
         {
             "name":"Path",
             "args":{
                "pattern":"/hi/producer/**"
             }
         }
     ]
     }
     */

    /**
     * 增加路由
     * @param gwdefinition
     * @return
     */
    @PostMapping("/add")
    public ResponseResult add(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.add(definition);
        return ResponseResult.ok();
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    @DeleteMapping("/routes/{id}")
    public ResponseResult delete(@PathVariable String id) {
        this.dynamicRouteService.delete(id);
        return ResponseResult.ok();
    }

    /**
     * 更新路由
     * @param gwdefinition
     * @return
     */
    @PostMapping("/update")
    public ResponseResult update(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.update(definition);
        return ResponseResult.ok();
    }

    /**
     * 获取所有路由信息
     * @return
     */
    @GetMapping(value = "/list")
    public Flux<RouteDefinition> list() {
        return dynamicRouteService.getRouteDefinitions();
    }

    /**
     * 从数据库加载指定路由
     * @return
     */
    @GetMapping(value = "/load")
    public ResponseResult load(@RequestParam String id) {
        Assert.notNull(id, "路由ID不能为空");
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteService.findById(id);
        RouteDefinition routeDefinition = RouteDefinitionConverter.converteFrom(gatewayAppRoute);
        this.dynamicRouteService.add(routeDefinition);
        return ResponseResult.ok();
    }

}
