//package com.rapidark.cloud.base.server.service;
//
//import com.baomidou.mybatisplus.core.metadata.IPage;
//import com.rapidark.platform.system.api.entity.GatewayAppRoute;
//import com.rapidark.framework.commons.model.PageParams;
//import com.rapidark.framework.commons.mybatis.base.service.IBaseService;
//
//import java.util.List;
//
///**
// * 路由管理
// *
// * @author liuyadu
// */
//public interface GatewayAppRouteService extends IBaseService<GatewayAppRoute> {
//    /**
//     * 分页查询
//     *
//     * @param pageParams
//     * @return
//     */
//    IPage<GatewayAppRoute> findListPage(PageParams pageParams);
//
//    /**
//     * 查询可用路由列表
//     *
//     * @return
//     */
//    List<GatewayAppRoute> findRouteList();
//
//    /**
//     * 获取路由信息
//     *
//     * @param routeId
//     * @return
//     */
//    GatewayRoute getRoute(String routeId);
//
//    /**
//     * 添加路由
//     *
//     * @param route
//     */
//    void addRoute(GatewayRoute route);
//
//    /**
//     * 更新路由
//     *
//     * @param route
//     */
//    void updateRoute(GatewayRoute route);
//
//    /**
//     * 删除路由
//     *
//     * @param routeId
//     */
//    void removeRoute(String routeId);
//
//    /**
//     * 是否存在
//     *
//     * @param routeName
//     * @return
//     */
//    Boolean isExist(String routeName);
//}
