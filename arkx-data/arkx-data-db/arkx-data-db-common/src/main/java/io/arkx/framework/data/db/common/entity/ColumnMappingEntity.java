package io.arkx.framework.data.db.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 迁移任务字段映射实体类
 * 对应数据库表：dbswitch_column_mapping
 * 用于描述源表字段与目标表字段之间的映射关系
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("DBSWITCH_COLUMN_MAPPING")
public class ColumnMappingEntity {

    /**
     * 主键ID，自动递增
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 目标数据库连接的ID，表示目标数据库的连接标识
     */
    @TableField("target_connection_id")
    private Long targetConnectionId;

    /**
     * 目标模式（Schema），用于精确标识目标数据的位置
     */
    @TableField("target_schema")
    private String targetSchema;

    /**
     * 目标数据表名称，表示字段所属的目标表
     */
    @TableField("target_table")
    private String targetTable;

    /**
     * 目标字段注释，存储目标字段的描述信息
     */
    @TableField("target_column_remarks")
    private String targetColumnRemarks;

    /**
     * 目标字段名称，表示数据在目标系统中的列名
     */
    @TableField("target_column")
    private String targetColumn;

    /**
     * 目标字段类型，记录目标列的数据类型
     */
    @TableField("target_column_type")
    private String targetColumnType;

    /**
     * 目标字段长度/精度，如字符长度、数值精度等
     */
    @TableField("target_column_precisionSize")
    private Long targetColumnPrecisionSize;

    /**
     * 来源数据库连接的ID，表示数据来源的数据库连接标识
     */
    @TableField("source_connection_id")
    private Long sourceConnectionId;

    /**
     * 来源模式（Schema），用于精确标识源数据的位置
     */
    @TableField("source_schema")
    private String sourceSchema;

    /**
     * 来源数据表名称，表示字段所属的源表
     */
    @TableField("source_table")
    private String sourceTable;

    /**
     * 来源字段注释，存储源列的描述信息
     */
    @TableField("source_column_remarks")
    private String sourceColumnRemarks;

    /**
     * 来源字段名称，表示数据在源系统中的列名
     */
    @TableField("source_column")
    private String sourceColumn;

    /**
     * 来源字段类型，记录源列的数据类型
     */
    @TableField("source_column_type")
    private String sourceColumnType;

    /**
     * 来源字段长度/精度，如字符长度、数值精度等
     */
    @TableField("source_column_precisionSize")
    private Long sourceColumnPrecisionSize;

    /**
     * 数据转换器类型
     */
    @TableField("converter_type")
    private String converterType;

    /**
     * 映射默认值
     */
    @TableField("default_value")
    private String defaultValue;

    /**
     * 对应转化器
     */
    @TableField(exist = false)
    private List<ColumnPlusEntity> columnPlusList;

}
