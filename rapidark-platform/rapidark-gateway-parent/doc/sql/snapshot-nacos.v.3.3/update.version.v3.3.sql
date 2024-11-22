-- ######### 此为升级脚本 ##########
-- ######### 用于snapshot-nacos.v.3.2升级到snapshot-nacos.v.3.3

-- 新增限流策略、熔断策略字段
ALTER TABLE `gateway`.`route` ADD COLUMN `flowRuleName` VARCHAR(50) NULL COMMENT '限流策略名称' AFTER `fallbackTimeout`;
ALTER TABLE `gateway`.`route` ADD COLUMN `degradeRuleName` VARCHAR(50) NULL COMMENT '熔断策略名称' AFTER `flowRuleName`;

-- 创建sentinelrule限流配置表
CREATE TABLE `sentinelrule` (
    `id` varchar(60) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci NOT NULL COMMENT '主键，同routeId',
    `flowRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '限流规则',
    `degradeRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '熔断规则',
    `systemRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '系统保护规则',
    `authoritRule` varchar(500) COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '访问来源规则',
    `paramFlowRule` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_german2_ci DEFAULT NULL COMMENT '热点参数规则',
    `updateTime` datetime DEFAULT NULL COMMENT '更新时间',
    PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_german2_ci;

