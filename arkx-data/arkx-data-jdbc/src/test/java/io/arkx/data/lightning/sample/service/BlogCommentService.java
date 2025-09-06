package io.arkx.data.lightning.sample.service;

import io.arkx.data.lightning.plugin.treetable.closure.service.SmartClosureTableServiceImpl;
import io.arkx.data.lightning.sample.model.BlogComment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @author Nobody
 * @date 2025-07-28 2:03
 * @since 1.0
 */

@Service
public class BlogCommentService {

	private final SmartClosureTableServiceImpl closureService;

	public BlogCommentService(SmartClosureTableServiceImpl closureService) {
		this.closureService = closureService;
	}

	@Transactional
	public void addBlogComment(BlogComment comment) {
//		closureService.insertNode(comment, "blog_comment");
	}

	public List<BlogComment> getCommentDescendants(Long commentId) {
//		return closureService.findDescendants("blog_comment", commentId, BlogComment.class);
		return null;
	}
}
