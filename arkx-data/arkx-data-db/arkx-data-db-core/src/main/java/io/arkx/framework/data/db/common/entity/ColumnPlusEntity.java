package io.arkx.framework.data.db.common.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 字段映射扩展实体类 对应数据库表：dbswitch_column_plus 用于存储字段映射的额外信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "DBSWITCH_COLUMN_PLUS", autoResultMap = true)
public class ColumnPlusEntity {

	/**
	 * 主键ID，自动递增
	 */
	@TableId(value = "id", type = IdType.AUTO)
	private Long id;

	/**
	 * 字段映射ID，关联到字段映射表
	 */
	@TableField("column_mapping_id")
	private Long columnMappingId;

	/**
	 * 转换器编码
	 */
	@TableField("handler_code")
	private String handlerCode;

	/**
	 * 转换器具体类名称
	 */
	@TableField("handler_class")
	private String handlerClass;

	/**
	 * 扩展字段值
	 */
	@TableField("value")
	private String value;

	/**
	 * 字典名称
	 */
	@TableField("raw_dict_name")
	private String rawDictName;

	/**
	 * 字典id信息
	 */
	@TableField("raw_dict_id")
	private Integer rawDictId;

	/**
	 * 新系统字典名称
	 */
	@TableField("new_dict_name")
	private String newDictName;

	/**
	 * 新系统字典信息
	 */
	@TableField("new_dict_id")
	private Integer newDictId;

	/**
	 * 是否映射
	 */
	@TableField(exist = false)
	private boolean isMapping;

}
