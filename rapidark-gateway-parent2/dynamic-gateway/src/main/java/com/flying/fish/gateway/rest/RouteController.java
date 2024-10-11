package com.flying.fish.gateway.rest;

import com.flying.fish.gateway.event.DataRouteApplicationEventListen;
import com.flying.fish.gateway.service.DynamicRouteService;
import com.flying.fish.gateway.service.LoadRouteService;
import com.flying.fish.gateway.vo.GatewayRouteDefinition;
import com.rapidark.cloud.gateway.formwork.entity.GatewayAppRoute;
import com.rapidark.cloud.gateway.manage.service.GatewayAppRouteService;
import com.rapidark.common.model.ResultBody;

import org.springframework.cloud.gateway.route.RouteDefinition;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

import javax.annotation.Resource;

/**
 * @Description 动态添加路由
 * @Author jianglong
 * @Date 2020/05/11
 * @Version V1.0
 */
@RestController
@RequestMapping("/gateway/route")
public class RouteController {
    @Resource
    private DynamicRouteService dynamicRouteService;
    @Resource
    private LoadRouteService loadRouteService;
    @Resource
    private GatewayAppRouteService gatewayAppRouteService;
    @Resource
    private DataRouteApplicationEventListen redisRouteDefinitionRepository;

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
    public ResultBody add(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.add(definition);
        return ResultBody.ok();
    }

    /**
     * 删除路由
     * @param id
     * @return
     */
    @DeleteMapping("/routes/{id}")
    public ResultBody delete(@PathVariable String id) {
        this.dynamicRouteService.delete(id);
        return ResultBody.ok();
    }

    /**
     * 更新路由
     * @param gwdefinition
     * @return
     */
    @PostMapping("/update")
    public ResultBody update(@RequestBody GatewayRouteDefinition gwdefinition) {
        RouteDefinition definition = loadRouteService.assembleRouteDefinition(gwdefinition);
        this.dynamicRouteService.update(definition);
        return ResultBody.ok();
    }

    /**
     * 获取所有路由信息
     * @return
     */
    @GetMapping(value = "/list")
    public Flux<RouteDefinition> list() {
        return redisRouteDefinitionRepository.getRouteDefinitions();
    }

    /**
     * 从数据库加载指定路由
     * @return
     */
    @GetMapping(value = "/load")
    public ResultBody load(@RequestParam String id) {
        Assert.notNull(id, "路由ID不能为空");
        GatewayAppRoute gatewayAppRoute = gatewayAppRouteService.findById(id);
        RouteDefinition routeDefinition = loadRouteService.loadRouteDefinition(gatewayAppRoute);
        this.dynamicRouteService.add(routeDefinition);
        return ResultBody.ok();
    }

    /**
     * 刷新路由(可刷新：ip,client,gatewayAppRoute)
     * @return
     */
    @GetMapping(value = "/fresh")
    public ResultBody fresh(@RequestParam(required = false) String type) {
        this.dynamicRouteService.fresh(type);
        return ResultBody.ok();
    }

}
