package io.arkx.data.lightning.plugin.treetable.closure.entity;

import io.arkx.framework.data.common.entity.IdType;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author Nobody
 * @date 2025-07-28 2:04
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BizTableMeta {

	private Long id; // 主键ID（全局唯一）

	private boolean useIndependent; // 是否使用独立闭包表（默认false）

	private IdType idType; // 业务表ID类型（默认LONG）

	private String bizTable; // 业务表名（如"dept"、"menu"）

}
