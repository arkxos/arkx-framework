package org.ark.framework.orm.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:58
 * @since 1.0
 */

/**
 * 雪花ID生成器
 * 基于Twitter的Snowflake算法实现分布式唯一ID生成
 *
 * ID结构：
 * 1位符号位(固定为0) + 41位时间戳 + 10位机器ID + 12位序列号
 *
 * @author Zhoulanzhen
 * @date 2025/1/18
 */
public class SnowflakeIdGenerator {

    // 起始时间戳 (2023-01-01 00:00:00)
    private static final long START_TIMESTAMP = 1672531200000L;

    // 各部分占用位数
    private static final long SEQUENCE_BITS = 12L;
    private static final long MACHINE_BITS = 10L;
    private static final long TIMESTAMP_BITS = 41L;

    // 各部分最大值
    private static final long MAX_SEQUENCE = (1L << SEQUENCE_BITS) - 1;
    private static final long MAX_MACHINE_ID = (1L << MACHINE_BITS) - 1;

    // 各部分位移
    private static final long MACHINE_SHIFT = SEQUENCE_BITS;
    private static final long TIMESTAMP_SHIFT = SEQUENCE_BITS + MACHINE_BITS;

    // 机器ID
    private final long machineId;

    // 序列号
    private long sequence = 0L;

    // 上次生成ID的时间戳
    private long lastTimestamp = -1L;

    // 单例实例
    private static volatile SnowflakeIdGenerator instance;

    /**
     * 私有构造函数
     * @param machineId 机器ID (0-1023)
     */
    private SnowflakeIdGenerator(long machineId) {
        if (machineId < 0 || machineId > MAX_MACHINE_ID) {
            throw new IllegalArgumentException("机器ID必须在0到" + MAX_MACHINE_ID + "之间");
        }
        this.machineId = machineId;
    }

    /**
     * 获取单例实例
     * @return SnowflakeIdGenerator实例
     */
    public static SnowflakeIdGenerator getInstance() {
        if (instance == null) {
            synchronized (SnowflakeIdGenerator.class) {
                if (instance == null) {
                    // 使用当前时间戳的后10位作为机器ID
                    long machineId = System.currentTimeMillis() & MAX_MACHINE_ID;
                    instance = new SnowflakeIdGenerator(machineId);
                }
            }
        }
        return instance;
    }

    /**
     * 获取指定机器ID的实例
     * @param machineId 机器ID
     * @return SnowflakeIdGenerator实例
     */
    public static SnowflakeIdGenerator getInstance(long machineId) {
        return new SnowflakeIdGenerator(machineId);
    }

    /**
     * 生成下一个ID
     * @return 雪花ID
     */
    public synchronized long nextId() {
        long currentTimestamp = getCurrentTimestamp();

        // 时钟回拨检查
        if (currentTimestamp < lastTimestamp) {
            throw new RuntimeException("时钟回拨异常，拒绝生成ID");
        }

        if (currentTimestamp == lastTimestamp) {
            // 同一毫秒内，序列号递增
            sequence = (sequence + 1) & MAX_SEQUENCE;
            if (sequence == 0) {
                // 序列号溢出，等待下一毫秒
                currentTimestamp = waitNextMillis(currentTimestamp);
            }
        } else {
            // 不同毫秒，序列号重置
            sequence = 0L;
        }

        lastTimestamp = currentTimestamp;

        // 组装ID
        return ((currentTimestamp - START_TIMESTAMP) << TIMESTAMP_SHIFT)
                | (machineId << MACHINE_SHIFT)
                | sequence;
    }

    /**
     * 获取当前时间戳
     * @return 当前时间戳
     */
    private long getCurrentTimestamp() {
        return System.currentTimeMillis();
    }

    /**
     * 等待下一毫秒
     * @param lastTimestamp 上次时间戳
     * @return 下一毫秒时间戳
     */
    private long waitNextMillis(long lastTimestamp) {
        long timestamp = getCurrentTimestamp();
        while (timestamp <= lastTimestamp) {
            timestamp = getCurrentTimestamp();
        }
        return timestamp;
    }

    /**
     * 解析雪花ID
     * @param id 雪花ID
     * @return ID信息
     */
    public static IdInfo parseId(long id) {
        long timestamp = (id >> TIMESTAMP_SHIFT) + START_TIMESTAMP;
        long machineId = (id >> MACHINE_SHIFT) & MAX_MACHINE_ID;
        long sequence = id & MAX_SEQUENCE;

        return new IdInfo(timestamp, machineId, sequence);
    }

    /**
     * ID信息类
     */
    public static class IdInfo {
        private final long timestamp;
        private final long machineId;
        private final long sequence;

        public IdInfo(long timestamp, long machineId, long sequence) {
            this.timestamp = timestamp;
            this.machineId = machineId;
            this.sequence = sequence;
        }

        public long getTimestamp() {
            return timestamp;
        }

        public long getMachineId() {
            return machineId;
        }

        public long getSequence() {
            return sequence;
        }

        @Override
        public String toString() {
            return String.format("IdInfo{timestamp=%d, machineId=%d, sequence=%d}",
                    timestamp, machineId, sequence);
        }
    }
}