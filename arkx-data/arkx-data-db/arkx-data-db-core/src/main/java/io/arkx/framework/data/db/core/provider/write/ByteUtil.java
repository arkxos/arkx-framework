package io.arkx.framework.data.db.core.provider.write;

import java.nio.charset.StandardCharsets;
import java.util.List;

public class ByteUtil {

    /**
     * 计算单条记录的数据包大小（单位：字节）
     */
    public static long calculateRecordSize(Object[] record) {
        long size = 0;
        for (Object value : record) {
            if (value == null) {
                size += 4; // NULL 标记约占用4字节
            } else if (value instanceof String string) {
                size += string.getBytes(StandardCharsets.UTF_8).length;
            } else if (value instanceof Number) {
                size += 8; // 数字按8字节估算
            } else if (value instanceof byte[] bytes) {
                size += bytes.length;
            } else {
                size += value.toString().getBytes(StandardCharsets.UTF_8).length;
            }
        }
        return size;
    }

    /**
     * 计算整个批次的数据包大小（单位：字节）
     */
    public static long calculateBatchSize(List<Object[]> records) {
        return records.stream().mapToLong(ByteUtil::calculateRecordSize).sum();
    }

}
