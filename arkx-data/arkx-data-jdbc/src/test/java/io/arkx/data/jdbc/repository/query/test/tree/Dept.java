package io.arkx.data.jdbc.repository.query.test.tree;

import io.arkx.framework.data.common.entity.TreeEntity;
import lombok.*;

/**
 * @author Nobody
 * @date 2025-07-28 2:01
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 部门实体（公共闭包表）
public class Dept extends TreeEntity<Long> {

	private String name;

}

