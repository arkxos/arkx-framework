//package com.rapidark.cloud.base.server.controller;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.rapidark.cloud.base.client.constants.BaseConstants;
//import com.rapidark.cloud.base.server.controller.cmd.AddRouteCommand;
//import com.rapidark.cloud.base.server.controller.cmd.UpdateRouteCommand;
//import com.rapidark.cloud.gateway.formwork.service.GatewayAppRouteService;
//import com.rapidark.framework.commons.model.PageParams;
//import com.rapidark.framework.commons.model.ResultBody;
//import com.rapidark.framework.commons.security.http.OpenRestTemplate;
//
//
//
//import io.swagger.v3.oas.annotations.media.Schema;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.bind.annotation.*;
//
//import jakarta.validation.Valid;
//import java.util.Map;
//
///**
// * 网关智能路由
// *
// * @author: liuyadu
// * @date: 2019/3/12 15:12
// * @description:
// */
//@Schema(title = "网关智能路由")
//@RestController
//public class GatewayRouteController {
//    @Autowired
//    private GatewayAppRouteService gatewayRouteService;
//    @Autowired
//    private OpenRestTemplate openRestTemplate;
//
//    /**
//     * 获取分页路由列表
//     *
//     * @return
//     */
////    @Schema(title = "获取分页路由列表", name = "获取分页路由列表")
////    @GetMapping("/gateway/route")
////    public ResultBody<IPage<GatewayAppRoute>> getRouteListPage(@RequestParam(required = false) Map map) {
////        return ResultBody.ok(gatewayRouteService.findListPage(new PageParams(map)));
////    }
//
//
//    /**
//     * 获取路由
//     *
//     * @param routeId
//     * @return
//     */
//    @Schema(title = "获取路由", name = "获取路由")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "routeId", required = true, value = "路由ID", paramType = "path"),
//    })
//    @GetMapping("/gateway/route/{routeId}/info")
//    public ResultBody<GatewayAppRoute> getRoute(@PathVariable("routeId") String routeId) {
//        return ResultBody.ok(gatewayRouteService.getRoute(routeId));
//    }
//
//    /**
//     * 添加路由
//     *
//     * @return
//     */
//    @Schema(title = "添加路由", name = "添加路由")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "path", required = true, value = "路径表达式", paramType = "form"),
//            @ApiImplicitParam(name = "routeName", required = true, value = "路由标识", paramType = "form"),
//            @ApiImplicitParam(name = "routeType", required = false, value = "路由方式", paramType = "form"),
//            @ApiImplicitParam(name = "routeDesc", required = true, value = "路由名称", paramType = "form"),
//            @ApiImplicitParam(name = "serviceId", required = false, value = "服务名方转发", paramType = "form"),
//            @ApiImplicitParam(name = "url", required = false, value = "地址转发", paramType = "form"),
//            @ApiImplicitParam(name = "stripPrefix", required = false, allowableValues = "0,1", defaultValue = "1", value = "忽略前缀", paramType = "form"),
//            @ApiImplicitParam(name = "retryable", required = false, allowableValues = "0,1", defaultValue = "0", value = "支持重试", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, allowableValues = "0,1", defaultValue = "1", value = "是否启用", paramType = "form")
//    })
//    @PostMapping("/gateway/route/add")
//    public ResultBody<Long> addRoute(
//            @RequestBody @Valid AddRouteCommand command
//            ) {
//        GatewayRoute route = new GatewayRoute();
//        route.setPath(command.getPath());
//        route.setRetryable(command.getRetryable());
//        route.setStripPrefix(command.getStripPrefix());
//        route.setStatus(command.getStatus());
//        route.setSystemCode(command.getRouteName());
//        route.setType(command.getRouteType());
//        route.setName(command.getRouteDesc());
//        switch (command.getRouteType()) {
//            case BaseConstants.ROUTE_TYPE_URL:
//                route.setServiceId(null);
//                route.setUri(command.getUrl().trim());
//                break;
//            default:
//                route.setServiceId(command.getServiceId().trim());
//                route.setUri(null);
//        }
//        gatewayRouteService.addRoute(route);
//        // 刷新网关
//        // openRestTemplate.refreshGateway();
//        return ResultBody.ok();
//    }
//
//    /**
//     * 编辑路由
//     *
//     * @return
//     */
//    @Schema(title = "编辑路由", name = "编辑路由")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "routeId", required = true, value = "路由Id", paramType = "form"),
//            @ApiImplicitParam(name = "routeName", required = true, value = "路由标识", paramType = "form"),
//            @ApiImplicitParam(name = "routeType", required = false, value = "路由方式", paramType = "form"),
//            @ApiImplicitParam(name = "routeDesc", required = true, value = "路由名称", paramType = "form"),
//            @ApiImplicitParam(name = "path", required = true, value = "路径表达式", paramType = "form"),
//            @ApiImplicitParam(name = "serviceId", required = false, value = "服务名方转发", paramType = "form"),
//            @ApiImplicitParam(name = "url", required = false, value = "地址转发", paramType = "form"),
//            @ApiImplicitParam(name = "stripPrefix", required = false, allowableValues = "0,1", defaultValue = "1", value = "忽略前缀", paramType = "form"),
//            @ApiImplicitParam(name = "retryable", required = false, allowableValues = "0,1", defaultValue = "0", value = "支持重试", paramType = "form"),
//            @ApiImplicitParam(name = "status", required = false, allowableValues = "0,1", defaultValue = "1", value = "是否启用", paramType = "form")
//    })
//    @PostMapping("/gateway/route/update")
//    public ResultBody updateRoute(
//            @RequestBody @Valid UpdateRouteCommand command
//            ) {
//        GatewayRoute route = new GatewayRoute();
//        route.setId(command.getRouteId());
//        route.setPath(command.getPath());
//        route.setRetryable(command.getRetryable());
//        route.setStripPrefix(command.getStripPrefix());
//        route.setStatus(command.getStatus());
//        route.setSystemCode(command.getRouteName());
//        route.setType(command.getRouteType());
//        route.setName(command.getRouteDesc());
//        switch (command.getRouteType()) {
//            case "url":
//                route.setServiceId(null);
//                route.setUri(command.getUrl().trim());
//                break;
//            default:
//                route.setServiceId(command.getServiceId().trim());
//                route.setUri(null);
//        }
//        gatewayRouteService.updateRoute(route);
//        // 刷新网关
//        // openRestTemplate.refreshGateway();
//        return ResultBody.ok();
//    }
//
//
//    /**
//     * 移除路由
//     *
//     * @param routeId
//     * @return
//     */
//    @Schema(title = "移除路由", name = "移除路由")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "routeId", required = true, value = "routeId", paramType = "form"),
//    })
//    @PostMapping("/gateway/route/remove")
//    public ResultBody removeRoute(
//            @RequestParam("routeId") String routeId
//    ) {
//        gatewayRouteService.removeRoute(routeId);
//        // 刷新网关
//        // openRestTemplate.refreshGateway();
//        return ResultBody.ok();
//    }
//}
