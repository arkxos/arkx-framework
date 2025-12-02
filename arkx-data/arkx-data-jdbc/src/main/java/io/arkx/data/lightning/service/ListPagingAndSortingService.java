package io.arkx.data.lightning.service;

/**
 * @author Nobody
 * @date 2025-08-27 0:50
 * @since 1.0
 */
public interface ListPagingAndSortingService<T, ID> extends Service<T, ID> {

	java.util.List<T> findAll(org.springframework.data.domain.Sort sort);

	org.springframework.data.domain.Page<T> findAll(org.springframework.data.domain.Pageable pageable);

}
