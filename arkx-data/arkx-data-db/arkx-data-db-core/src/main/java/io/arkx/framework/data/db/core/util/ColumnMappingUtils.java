package io.arkx.framework.data.db.core.util;

import java.util.Map;
import java.util.stream.Collectors;

import io.arkx.framework.data.db.common.entity.ColumnMappingEntity;
import io.arkx.framework.data.db.common.entity.ColumnPlusEntity;
import io.arkx.framework.data.db.common.util.DataDumpCenter;

public class ColumnMappingUtils {

    private static Map<String, Map<String, ColumnMappingEntity>> COLUMN_MAP;

    private static Map<Long, ColumnPlusEntity> COLUMN_PLUS_MAP;

    public static void init() {
        // 缓存所有字段映射信息
        COLUMN_MAP = DataDumpCenter.columnMappingDAO_getAllColumnMapping().get().stream().collect(
                // 根据targetSchema和targetTable进行分组
                Collectors.groupingBy(e -> e.getTargetSchema() + "." + e.getTargetTable(),
                        // 使用使用targetColumn作为key, 将分组的字段映射列表转成map
                        Collectors.toMap(ColumnMappingEntity::getTargetColumn, entity -> entity)));

        // 缓存所有字段映射附加信息
        COLUMN_PLUS_MAP = DataDumpCenter.columnPlusDAO_getAllColumnPlus().get().stream().collect(Collectors
                .toMap(ColumnPlusEntity::getColumnMappingId, entity -> entity, (existing, replacement) -> existing // 遇到重复字段名保留第一个
                ));
    }

    public static ColumnMappingEntity getColumnMappingEntity(String targetSchema, String targetTable,
            String targetColumn) {
        Map<String, ColumnMappingEntity> columnMapping = COLUMN_MAP.get(targetSchema + "." + targetTable);
        if (columnMapping == null)
            return null;
        return columnMapping.get(targetColumn);
    }

    public static ColumnPlusEntity getColumnPlusEntity(Long columnMappingId) {
        if (COLUMN_PLUS_MAP == null || columnMappingId == null)
            return null;
        return COLUMN_PLUS_MAP.get(columnMappingId);
    }

}
