package org.ark.framework.orm.db.util;

/**
 *
 * @author Nobody
 * @version 1.0
 * @date 2025-10-16 17:19
 * @since 1.0
 */

import org.ark.framework.orm.SchemaColumn;

/**
 * 数据库列类型转换工具类 提供数据库列类型转换和处理的通用方法，包括字段类型转换、长度阈值检查等功能
 */
public class ColumnTypeConverter {

    /**
     * VARCHAR类型转TEXT的默认阈值，超过此长度的VARCHAR将被转换为TEXT类型
     */
    private static final int DEFAULT_VARCHAR_TO_TEXT_THRESHOLD = 1000;

    // 存储当前配置的VARCHAR转TEXT阈值
    private int varcharToTextThreshold = DEFAULT_VARCHAR_TO_TEXT_THRESHOLD;

    /**
     * 设置VARCHAR转TEXT的阈值
     *
     * @param threshold
     *            阈值长度，超过此长度的VARCHAR将被转换为TEXT
     */
    public void setVarcharToTextThreshold(int threshold) {
        if (threshold > 0) {
            this.varcharToTextThreshold = threshold;
        }
    }

    /**
     * 获取当前配置的VARCHAR转TEXT阈值
     *
     * @return 当前阈值
     */
    public int getVarcharToTextThreshold() {
        return this.varcharToTextThreshold;
    }

    /**
     * 判断是否需要将VARCHAR转换为TEXT类型
     *
     * @param columnType
     *            列类型代码
     * @param length
     *            列长度
     * @return 如果需要转换为TEXT返回true，否则返回false
     */
    public boolean shouldConvertToText(int columnType, int length) {
        // columnType 1代表VARCHAR类型
        return columnType == 1 && length > varcharToTextThreshold;
    }

    /**
     * 获取适合当前列的SQL类型，自动判断是否需要从VARCHAR转换为TEXT
     *
     * @param column
     *            列信息
     * @param defaultVarcharType
     *            默认VARCHAR类型名称（不同数据库可能不同）
     * @param textType
     *            对应的TEXT类型名称（不同数据库可能不同）
     * @return 适合的SQL类型名称
     */
    public String getSqlTypeWithTextCheck(SchemaColumn column, String defaultVarcharType, String textType) {
        if (shouldConvertToText(column.getColumnType(), column.getLength())) {
            return textType;
        }
        return defaultVarcharType;
    }

    /**
     * 获取适合当前列的SQL类型声明，包括长度/精度等
     *
     * @param column
     *            列信息
     * @param defaultVarcharType
     *            默认VARCHAR类型名称
     * @param textType
     *            对应的TEXT类型名称
     * @return 完整的SQL类型声明
     */
    public String getFullSqlType(SchemaColumn column, String defaultVarcharType, String textType) {
        String baseType = getSqlTypeWithTextCheck(column, defaultVarcharType, textType);

        // 如果是TEXT类型，不需要添加长度
        if (baseType.equalsIgnoreCase(textType)) {
            return baseType;
        }

        // 对于VARCHAR类型，添加长度
        if (column.getColumnType() == 1) { // VARCHAR
            return baseType + "(" + column.getLength() + ")";
        }

        return baseType;
    }

}
