package io.arkx.data.jdbc.repository.query.test.tree;

import io.arkx.framework.data.common.entity.TreeEntity;
import lombok.*;

/**
 * @author Nobody
 * @date 2025-07-28 2:49
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Menu extends TreeEntity<String> {

	private String name;

}
