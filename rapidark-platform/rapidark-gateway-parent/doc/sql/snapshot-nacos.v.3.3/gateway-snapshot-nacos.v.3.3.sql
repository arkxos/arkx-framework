-- ######### 此为数据库脚本 ##########
-- ######### 用于snapshot-nacos.v.3.3版本


/*!40101 SET NAMES utf8 */;

/*!40101 SET SQL_MODE=''*/;

/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
CREATE DATABASE /*!32312 IF NOT EXISTS*/`gateway` /*!40100 DEFAULT CHARACTER SET utf8mb4 */;

/*Table structure for table `apidoc` */

DROP TABLE IF EXISTS `apidoc`;

CREATE TABLE `apidoc` (
  `id` varchar(40) NOT NULL COMMENT '主键，同route_id',
  `content` text COMMENT '内容',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `balanced` */

DROP TABLE IF EXISTS `balanced`;

CREATE TABLE `balanced` (
  `id` varchar(40) NOT NULL COMMENT '主键',
  `name` varchar(40) NOT NULL COMMENT '负载名称',
  `groupCode` varchar(40) NOT NULL COMMENT '分组编码',
  `loadUri` varchar(200) DEFAULT NULL COMMENT '负载地址',
  `status` varchar(2) DEFAULT NULL COMMENT '状态，0启用，1禁用',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `client` */

DROP TABLE IF EXISTS `client`;

CREATE TABLE `client` (
  `id` varchar(40) NOT NULL COMMENT '主键,注册key',
  `systemCode` varchar(40) NOT NULL COMMENT '系统代号',
  `name` varchar(40) NOT NULL COMMENT '客户端名称',
  `groupCode` varchar(40) NOT NULL COMMENT '分组编码',
  `ip` varchar(16) DEFAULT NULL COMMENT '客户端IP',
  `status` varchar(2) DEFAULT NULL COMMENT '状态，0启用，1禁用',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `groovyscript` */

DROP TABLE IF EXISTS `groovyscript`;

CREATE TABLE `groovyscript` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT 'id主键',
  `routeId` varchar(40) DEFAULT NULL COMMENT '网关ID',
  `name` varchar(40) DEFAULT NULL COMMENT '脚本名称',
  `content` text COMMENT '脚本内容',
  `extendInfo` varchar(1000) DEFAULT NULL COMMENT '扩展内容,参数json',
  `event` char(8) DEFAULT NULL COMMENT '执行事件',
  `orderNum` int(4) DEFAULT NULL COMMENT '顺序',
  `status` varchar(2) DEFAULT NULL COMMENT '状态',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  `createTime` datetime DEFAULT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '修改时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `loadserver` */

DROP TABLE IF EXISTS `loadserver`;

CREATE TABLE `loadserver` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `routeId` varchar(40) NOT NULL COMMENT '路由ID',
  `balancedId` varchar(40) NOT NULL COMMENT '负载ID',
  `weight` int(3) NOT NULL COMMENT '权重',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `monitor` */

DROP TABLE IF EXISTS `monitor`;

CREATE TABLE `monitor` (
  `id` varchar(40) NOT NULL COMMENT '主键，同routeId',
  `status` char(2) DEFAULT NULL COMMENT '0启用，1禁用，2告警',
  `emails` varchar(200) DEFAULT NULL COMMENT '通知接收邮箱',
  `topic` varchar(200) DEFAULT NULL COMMENT '告警邮件主题',
  `recover` char(2) DEFAULT NULL COMMENT '告警重试，0开启，1禁用',
  `frequency` char(4) DEFAULT NULL COMMENT '告警频率：30m,1h,5h,12h,24h',
  `alarmTime` datetime DEFAULT NULL COMMENT '告警时间',
  `sendTime` datetime DEFAULT NULL COMMENT '发送告警时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `regserver` */

DROP TABLE IF EXISTS `regserver`;

CREATE TABLE `regserver` (
  `id` bigint(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `clientId` varchar(40) NOT NULL COMMENT '客户端ID',
  `routeId` varchar(40) NOT NULL COMMENT '路由ID',
  `token` varchar(600) DEFAULT NULL COMMENT 'token加密内容',
  `secretKey` varchar(200) DEFAULT NULL COMMENT 'token加密密钥',
  `tokenEffectiveTime` datetime DEFAULT NULL COMMENT 'token令牌有效截止时间',
  `status` varchar(2) NOT NULL COMMENT '创建时间',
  `createTime` datetime NOT NULL COMMENT '更新时间',
  `updateTime` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4;

/*Table structure for table `route` */

DROP TABLE IF EXISTS `route`;

CREATE TABLE `route` (
  `id` varchar(40) NOT NULL COMMENT '主键',
  `systemCode` varchar(40) NOT NULL COMMENT '系统代号',
  `name` varchar(40) NOT NULL COMMENT '名称',
  `groupCode` varchar(40) NOT NULL COMMENT '分组',
  `uri` varchar(200) DEFAULT NULL COMMENT '服务地址',
  `path` varchar(100) DEFAULT NULL COMMENT '断言地址',
  `method` varchar(6) DEFAULT NULL COMMENT '请求类型',
  `host` varchar(100) DEFAULT NULL COMMENT '断言主机',
  `remoteAddr` varchar(20) DEFAULT NULL COMMENT '断言远程地址',
  `header` varchar(200) DEFAULT NULL COMMENT '断言Headers',
  `filterGatewayName` varchar(50) DEFAULT NULL COMMENT '过滤器',
  `filterHystrixName` varchar(50) DEFAULT NULL COMMENT '熔断器',
  `filterRateLimiterName` varchar(50) DEFAULT NULL COMMENT '限流器',
  `filterAuthorizeName` varchar(60) DEFAULT NULL COMMENT '鉴权器',
  `fallbackMsg` varchar(200) DEFAULT NULL COMMENT '熔断返回提示',
  `fallbackTimeout` bigint(8) DEFAULT NULL COMMENT '熔断超时设置',
  `flowRuleName` varchar(50) DEFAULT NULL COMMENT '限流策略名称',
  `degradeRuleName` varchar(50) DEFAULT NULL COMMENT '熔断策略名称',
  `replenishRate` int(6) DEFAULT NULL COMMENT '每秒流量',
  `burstCapacity` int(6) DEFAULT NULL COMMENT '令牌总量',
  `weight` int(6) DEFAULT NULL COMMENT '权重值',
  `status` varchar(2) DEFAULT NULL COMMENT '状态，0启用，1禁用',
  `stripPrefix` tinyint(1) DEFAULT NULL COMMENT '断言截取',
  `requestParameter` varchar(200) DEFAULT NULL COMMENT '请求参数',
  `rewritePath` varchar(200) DEFAULT NULL COMMENT '重写Path路径',
  `accessHeader` varchar(200) DEFAULT NULL COMMENT 'header验证',
  `accessIp` varchar(200) DEFAULT NULL COMMENT 'ip验证',
  `accessParameter` varchar(200) DEFAULT NULL COMMENT '参数验证',
  `accessTime` varchar(40) DEFAULT NULL COMMENT '限行时间段验证',
  `accessCookie` varchar(200) DEFAULT NULL COMMENT 'cookie键值验证',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `secureip` */

DROP TABLE IF EXISTS `secureip`;

CREATE TABLE `secureip` (
  `ip` varchar(16) NOT NULL COMMENT 'IP主键',
  `status` varchar(2) DEFAULT NULL COMMENT '状态：0正常，1无效',
  `createTime` datetime NOT NULL COMMENT '创建时间',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
  `remarks` varchar(200) DEFAULT NULL COMMENT '备注',
  PRIMARY KEY (`ip`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

/*Table structure for table `sentinelrule` */

DROP TABLE IF EXISTS `sentinelrule`;

CREATE TABLE `sentinelrule` (
  `id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '主键，同routeId',
  `flowRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '限流规则',
  `degradeRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '熔断规则',
  `systemRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '系统保护规则',
  `authoritRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '访问来源规则',
  `paramFlowRule` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '热点参数规则',
  `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;


/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;
