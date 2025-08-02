package io.arkx.data.common.treetable.closure.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Nobody
 * @date 2025-07-28 2:04
 * @since 1.0
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessTableMeta {
	private Long id;                  // 主键ID（全局唯一）
	private String businessTable;     // 业务表名（如"dept"、"menu"）
	private boolean useIndependent;   // 是否使用独立闭包表（默认false）
	private String bizTable;          // 公共闭包表中的业务标识（仅useIndependent=false时有效）
	private IdType idType;            // 业务表ID类型（默认LONG）
}
