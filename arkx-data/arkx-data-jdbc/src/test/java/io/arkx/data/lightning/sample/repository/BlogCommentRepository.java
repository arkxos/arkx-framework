package io.arkx.data.lightning.sample.repository;

import io.arkx.data.lightning.repository.BaseJdbcRepository;
import io.arkx.data.lightning.sample.model.BlogComment;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogCommentRepository extends BaseJdbcRepository<BlogComment, Long> {
}
