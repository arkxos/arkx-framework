CREATE TABLE `route` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(40) NOT NULL COMMENT '名称',
  `routeId` varchar(40) NOT NULL COMMENT '路由ID',
  `groupCode` varchar(40) NOT NULL COMMENT '分组',
  `uri` varchar(200) DEFAULT NULL COMMENT '服务地址',
  `path` varchar(100) DEFAULT NULL COMMENT '断言地址',
  `method` varchar(6) DEFAULT NULL COMMENT '请求类型',
  `filterGatewaName` varchar(50) DEFAULT NULL COMMENT '过滤器',
  `filterHystrixName` varchar(50) DEFAULT NULL COMMENT '熔断器',
  `filterRateLimiterName` varchar(50) DEFAULT NULL COMMENT '限流器',
  `filterAuthorizeName` varchar(60) DEFAULT NULL COMMENT '鉴权器',
  `fallbackMsg` varchar(200) DEFAULT NULL COMMENT '熔断返回提示',
  `fallbackTimeout` bigint(8) DEFAULT NULL COMMENT '熔断超时设置',
  `replenishRate` int(6) DEFAULT NULL COMMENT '每秒流量',
  `burstCapacity` int(6) DEFAULT NULL COMMENT '令牌总量',
  `weight` int(6) DEFAULT NULL COMMENT '权重值',
  `status` varchar(2) DEFAULT NULL COMMENT '状态',
  `stripPrefix` tinyint(1) DEFAULT NULL COMMENT '断言截取',
  `requestParameter` varchar(200) DEFAULT NULL COMMENT '请求参数',
  `accessHeader` varchar(200) DEFAULT NULL COMMENT 'header验证',
  `accessIp` varchar(200) DEFAULT NULL COMMENT 'ip验证',
  `accessParameter` varchar(200) DEFAULT NULL COMMENT '参数验证',
  `accessTime` varchar(40) DEFAULT NULL COMMENT '限行时间段验证',
  `accessCookie` varchar(200) DEFAULT NULL COMMENT 'cookie键值验证',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `client` (
  `id` varchar(40) NOT NULL COMMENT '主键,注册key',
  `name` varchar(40) NOT NULL COMMENT '客户端名称',
  `groupCode` varchar(40) NOT NULL COMMENT '分组编码',
  `ip` varchar(16) DEFAULT NULL COMMENT '客户端IP',
  `status` varchar(2) DEFAULT NULL COMMENT '状态，0启用，1禁用',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `regserver` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clientId` varchar(40) NOT NULL COMMENT '客户端ID',
  `routeId` varchar(40) NOT NULL COMMENT '路由ID',
  `status` varchar(2) NOT NULL COMMENT '创建时间',
  `createTime` datetime NOT NULL COMMENT '更新时间',
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `secureip` (
  `ip` varchar(16) NOT NULL COMMENT 'IP主键',
  `status` varchar(2) DEFAULT NULL COMMENT '状态：0正常，1无效',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `balanced` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `name` varchar(40) NOT NULL COMMENT '负载名称',
  `groupCode` varchar(40) NOT NULL COMMENT '分组编码',
  `loadUri` varchar(200) DEFAULT NULL COMMENT '负载地址',
  `status` varchar(2) DEFAULT NULL COMMENT '状态，0启用，1禁用',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

CREATE TABLE `loadserver` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `routePrimaryId` bigint(8) NOT NULL COMMENT '路由ID',
  `balancedId` bigint(8) NOT NULL COMMENT '负载ID',
  `weight` int(3) NOT NULL COMMENT '权重',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB  DEFAULT CHARSET=utf8mb4;



