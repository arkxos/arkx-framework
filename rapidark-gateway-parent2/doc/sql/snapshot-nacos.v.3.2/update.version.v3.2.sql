-- ######### 此为升级脚本 ##########
-- ######### 用于snapshot-nacos.v.3.1升级到snapshot-nacos.v.3.2

-- 创建groovy动态脚本引擎表
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
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
