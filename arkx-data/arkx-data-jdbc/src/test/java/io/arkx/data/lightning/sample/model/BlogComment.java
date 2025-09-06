package io.arkx.data.lightning.sample.model;

import io.arkx.framework.data.common.entity.TreeEntity;
import lombok.*;
import org.springframework.data.relational.core.mapping.Table;

/**
 * @author Nobody
 * @date 2025-07-28 2:02
 * @since 1.0
 */
@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@Table("TEST_BLOG_COMMENT")
// 博客留言实体（独立闭包表）
public class BlogComment extends TreeEntity<Long> {

	private String content;

}
