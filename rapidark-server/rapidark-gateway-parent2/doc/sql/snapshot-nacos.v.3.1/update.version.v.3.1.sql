-- ######### 此为升级脚本 ##########
-- ######### 用于snapshot-nacos.v.3.0升级到snapshot-nacos.v.3.1

-- 添加host\remoteAddr\rewritePath\header字段
ALTER TABLE `gatewayAppRoute` ADD COLUMN `host` VARCHAR(100) NULL COMMENT '断言主机' AFTER `method`;
ALTER TABLE `gatewayAppRoute` ADD COLUMN `remoteAddr` VARCHAR(20) NULL COMMENT '断言远程地址' AFTER `host`;
ALTER TABLE `gatewayAppRoute` ADD COLUMN `rewritePath` VARCHAR(200) NULL COMMENT '重写Path路径' AFTER `requestParameter`;
ALTER TABLE `gatewayAppRoute` ADD COLUMN `header` VARCHAR(200) NULL COMMENT '断言Headers' AFTER `remoteAddr`;

-- 添加systemCode系统代号
ALTER TABLE `gatewayAppRoute` ADD COLUMN `systemCode` VARCHAR(40) NOT NULL COMMENT '系统代号' AFTER `id`;
ALTER TABLE `client` ADD COLUMN `systemCode` VARCHAR(40) NOT NULL COMMENT '系统代号' AFTER `id`;

-- 添加token\secretKey\tokenEffectiveTime字段
ALTER TABLE `regserver` ADD COLUMN `token` VARCHAR(600) NULL COMMENT 'token加密内容' AFTER `routeId`;
ALTER TABLE `regserver` ADD COLUMN `secretKey` VARCHAR(200) NULL COMMENT 'token加密密钥' AFTER `token`;
ALTER TABLE `regserver` ADD COLUMN `tokenEffectiveTime` DATETIME NULL COMMENT 'token令牌有效截止时间' AFTER `secretKey`;