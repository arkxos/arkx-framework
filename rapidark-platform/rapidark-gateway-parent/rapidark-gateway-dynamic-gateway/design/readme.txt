org.springframework.cloud.gateway.filter.GatewayFilter
1 IpGatewayFilter 限制ip
2 ClientIdGatewayFilter 限制客户端访问服务
3 TokenGatewayFilter 限制客户端token跟客户端id、ip一致，每个客户端分配一个token
4 AuthorizeGatewayFilter 鉴权
  authorize FilterHandler
  CookieFilter cookie校验
  ParameterFilter 参数校验
  IpFileter ip校验
  TimeFilter 时间校验，只允许某段时间访问
  HeaderFilter 请求头校验
5 CacheResultGatewayFilter 缓存请求结果

可改
10001 CustomWeightCalculatorWebFilter 路由权重


global
0 LogGlobalFilter
Ordered.HIGHEST_PRECEDENCE CustomSentinelGatewayFilter
Ordered.LOWEST_PRECEDENCE RequestComponentGlobalFilter
-2 ResponseComponentGlobalFilter
