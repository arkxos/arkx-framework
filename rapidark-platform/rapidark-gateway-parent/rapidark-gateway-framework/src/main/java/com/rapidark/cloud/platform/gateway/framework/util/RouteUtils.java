package com.rapidark.cloud.platform.gateway.framework.util;

/**
 * @Description
 * @Author JL
 * @Date 2022/12/25
 * @Version V1.0
 */
public class RouteUtils {

    /**
     * 正常组件负载网关路由ID为 balanced-UUID-路由ID，如：balanced-bcd1ed61eaee40eda56f128372f3b683-userCenter-getUser
     * @param balancedRouteId 负载均衡网关ID
     * @return 子路由ID
     */
    public static String getBalancedToRouteId(String balancedRouteId){
        return balancedRouteId.contains(RouteConstants.BALANCED) ?
                balancedRouteId.substring(balancedRouteId.indexOf("-", RouteConstants.BALANCED.length() + 1) + 1) :
                balancedRouteId;
    }

    public static void main(String[] args) {
        String str = getBalancedToRouteId("balanced-bcd1ed61eaee40eda56f128372f3b683-userCenter-getUser");
        System.out.println(str);
    }
}
