-- ######### 此为升级脚本 ##########
-- ######### 用于snapshot-nacos.v.3.3升级到snapshot-nacos.v.3.4

-- 新增缓存时长字段
ALTER TABLE `gateway3`.`route` ADD COLUMN `cacheTtl` BIGINT NULL COMMENT '缓存时长' AFTER `accessCookie`;


