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
 * @author Nobody
 * @version 1.0
 * @date 2025-10-20 18:12
 * @since 1.0
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "dict_mapping_info", autoResultMap = true)
public class DictMappingInfoEntity {

	/**
	 * 主键ID，自动递增
	 */
	@TableId(value = "ID", type = IdType.AUTO)
	private Long id;

	/**
	 * 数据字典选项主键Id
	 */
	@TableField("NEW_DICT_ID")
	private Integer newDictId;

	/**
	 * 父级数据字典选项主键Id
	 */
	@TableField("PARENT_NEW_DICT_ID")
	private Integer parentNewDictId;

	/**
	 * 数据字典选项主键Id
	 */
	@TableField("RAW_DICT_ID")
	private Integer rawDictId;

	/**
	 * 父级数据字典选项主键Id
	 */
	@TableField("PARENT_RAW_DICT_ID")
	private String parentRawDictId;

	/**
	 * 数据字典选项名称
	 */
	@TableField("NEW_DICT_NAME")
	private String newDictName;

	/**
	 * 数据字典选项名称
	 */
	@TableField("RAW_DICT_NAME")
	private String rawDictName;

}
