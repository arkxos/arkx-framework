-- 为监控系统优化的MySQL 5.7兼容表结构
CREATE TABLE `monitor_trace` (
                                 `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
                                 `trace_id` CHAR(36) NOT NULL COMMENT '跟踪ID',
                                 `parent_id` CHAR(36) DEFAULT NULL COMMENT '父节点ID',
                                 `node_type` VARCHAR(10) NOT NULL COMMENT '节点类型(METHOD/SQL)',
                                 `class_name` VARCHAR(255) DEFAULT NULL COMMENT '类名',
                                 `method_name` VARCHAR(100) DEFAULT NULL COMMENT '方法名',
                                 `sql` TEXT COMMENT '原始SQL',
                                 `sql_parameters` TEXT COMMENT 'SQL参数',
                                 `full_sql` TEXT COMMENT '完整SQL(带参数)',
                                 `start_time` BIGINT(20) NOT NULL COMMENT '开始时间(纳秒)',
                                 `end_time` BIGINT(20) NOT NULL COMMENT '结束时间(纳秒)',
                                 `duration` BIGINT(20) NOT NULL COMMENT '执行时长(纳秒)',
                                 `success` TINYINT(1) NOT NULL DEFAULT '1' COMMENT '是否成功',
                                 `error_message` TEXT COMMENT '错误信息',
                                 `depth` TINYINT(3) NOT NULL DEFAULT '0' COMMENT '调用深度',
                                 `request_id` CHAR(36) DEFAULT NULL COMMENT '请求ID',
                                 `endpoint` VARCHAR(255) DEFAULT NULL COMMENT 'API端点',
                                 PRIMARY KEY (`id`),
    -- MySQL 5.7有限制的索引
                                 KEY `idx_node_type` (`node_type`),
                                 KEY `idx_class_name` (`class_name`(191)),
                                 KEY `idx_method_name` (`method_name`(100)),
                                 KEY `idx_start_time` (`start_time`),
                                 KEY `idx_trace_id` (`trace_id`),
                                 KEY `idx_request_id` (`request_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='方法监控跟踪表';

-- 慢SQL统计表
CREATE TABLE `monitor_slow_sql` (
                                    `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                    `sql_hash` CHAR(32) NOT NULL COMMENT 'SQL哈希值',
                                    `sql_template` TEXT NOT NULL COMMENT 'SQL模板',
                                    `max_duration` BIGINT(20) NOT NULL COMMENT '最长执行时间',
                                    `avg_duration` BIGINT(20) NOT NULL COMMENT '平均执行时间',
                                    `occurrence_count` INT(11) NOT NULL COMMENT '出现次数',
                                    `last_occurrence` DATETIME NOT NULL COMMENT '最后发生时间',
                                    PRIMARY KEY (`id`),
                                    UNIQUE KEY `uniq_sql_hash` (`sql_hash`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 慢方法统计表
CREATE TABLE `monitor_slow_method` (
                                       `id` BIGINT(20) NOT NULL AUTO_INCREMENT,
                                       `class_name` VARCHAR(255) NOT NULL,
                                       `method_name` VARCHAR(100) NOT NULL,
                                       `max_duration` BIGINT(20) NOT NULL COMMENT '最长执行时间',
                                       `avg_duration` BIGINT(20) NOT NULL COMMENT '平均执行时间',
                                       `occurrence_count` INT(11) NOT NULL COMMENT '出现次数',
                                       `last_occurrence` DATETIME NOT NULL COMMENT '最后发生时间',
                                       PRIMARY KEY (`id`),
                                       UNIQUE KEY `uniq_class_method` (`class_name`(191), `method_name`(100))
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

-- 分区管理存储过程(减少单表数据量)
DELIMITER //
CREATE PROCEDURE `monitor_rotate_partitions`()
BEGIN
    -- 每天执行一次的作业，用于归档旧数据
    DECLARE oldTable VARCHAR(100);
    DECLARE newTable VARCHAR(100);

    -- 创建下个月的新表
    SET newTable = CONCAT('monitor_trace_', DATE_FORMAT(DATE_ADD(NOW(), INTERVAL 1 MONTH), '%Y%m'));
    SET @sql = CONCAT('CREATE TABLE IF NOT EXISTS ', newTable, ' LIKE monitor_trace');
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 归档上个月的数据
SET oldTable = CONCAT('monitor_trace_', DATE_FORMAT(DATE_SUB(NOW(), INTERVAL 1 MONTH), '%Y%m'));
    SET @sql = CONCAT('RENAME TABLE monitor_trace TO ', oldTable);
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 重新创建当前月表
CREATE TABLE monitor_trace LIKE monitor_trace_template;
END//
DELIMITER ;

-- 视图：最新的5000条慢SQL
CREATE VIEW `v_slow_sql_last_5000` AS
SELECT
    trace_id,
    DATE_FORMAT(FROM_UNIXTIME(start_time/1000000000), '%Y-%m-%d %H:%i:%s') AS start_time,
    duration/1000000 AS duration_ms,
    full_sql,
    error_message
FROM monitor_trace
WHERE node_type = 'SQL'
ORDER BY start_time DESC
    LIMIT 5000;

-- 视图：最慢的前100个方法
CREATE VIEW `v_top100_slow_methods` AS
SELECT
    class_name,
    method_name,
    COUNT(*) AS occurrence_count,
    MAX(duration)/1000000 AS max_duration_ms,
    AVG(duration)/1000000 AS avg_duration_ms
FROM monitor_trace
WHERE node_type = 'METHOD'
GROUP BY class_name, method_name
ORDER BY max_duration_ms DESC
    LIMIT 100;