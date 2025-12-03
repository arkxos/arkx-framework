package org.ark.framework.orm.schema;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:23
 * @since 1.0
 */

import java.util.*;

import org.apache.log4j.Logger;

/**
 * Schema字段工具类，用于处理Schema字段的通用操作
 *
 * @author AI
 * @date 2023-04-16
 * @version V1.0
 */
public class SchemaColumnUtils {

    private static final Logger logger = Logger.getLogger(SchemaColumnUtils.class);

    /**
     * 处理重复的字段，忽略大小写，保留最后一个，并在Comment中添加重复提示
     *
     * @param columns
     *            原始字段列表
     * @param tableName
     *            表名（用于日志输出）
     * @return 处理后的字段列表
     */
    public static SchemaGenerator.SchemaColumn[] filterDuplicateColumns(SchemaGenerator.SchemaColumn[] columns,
            String tableName) {
        if (columns == null || columns.length == 0) {
            return columns;
        }

        // 使用LinkedHashMap保持插入顺序
        Map<String, SchemaGenerator.SchemaColumn> uniqueColumns = new LinkedHashMap<>();
        Map<String, Integer> duplicateCount = new HashMap<>();

        // 第一遍扫描，统计重复字段
        for (SchemaGenerator.SchemaColumn column : columns) {
            String columnNameLower = column.Name.toLowerCase();

            if (uniqueColumns.containsKey(columnNameLower)) {
                // 增加重复计数
                int count = duplicateCount.getOrDefault(columnNameLower, 1);
                duplicateCount.put(columnNameLower, count + 1);
            } else {
                uniqueColumns.put(columnNameLower, column);
                // 初始计数为1
                duplicateCount.put(columnNameLower, 1);
            }
        }

        // 第二遍扫描，处理重复字段，保留最后一个
        uniqueColumns.clear();
        for (SchemaGenerator.SchemaColumn column : columns) {
            String columnNameLower = column.Name.toLowerCase();
            int count = duplicateCount.get(columnNameLower);

            if (count > 1) {
                // 是重复字段
                String originalComment = column.Comment != null ? column.Comment : "";
                column.Comment = originalComment + " [警告: 字段名重复(" + count + ")次，忽略大小写]";

                logger.warn("表[" + tableName + "]包含重复字段: " + column.Name + "，共出现" + count + "次(忽略大小写)");
            }

            // 总是替换，这样最终会保留最后一个重复项
            uniqueColumns.put(columnNameLower, column);
        }

        // 将Map转换回数组
        List<SchemaGenerator.SchemaColumn> resultList = new ArrayList<>(uniqueColumns.values());
        return resultList.toArray(new SchemaGenerator.SchemaColumn[0]);
    }

}
