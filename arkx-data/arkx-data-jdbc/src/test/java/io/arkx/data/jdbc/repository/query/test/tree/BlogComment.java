package io.arkx.data.jdbc.repository.query.test.tree;

import io.arkx.framework.data.common.entity.TreeEntity;
import lombok.*;

/**
 * @author Nobody
 * @date 2025-07-28 2:02
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
// 博客留言实体（独立闭包表）
public class BlogComment extends TreeEntity<Long> {

	private String content;

}
