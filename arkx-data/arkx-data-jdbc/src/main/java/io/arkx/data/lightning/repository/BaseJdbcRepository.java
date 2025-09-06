package io.arkx.data.lightning.repository;

import io.arkx.framework.data.common.repository.ExtBaseRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.query.ListQueryByExampleExecutor;

/**
 * @author Nobody
 * @date 2025-07-26 16:47
 * @since 1.0
 */
public interface BaseJdbcRepository<T, ID>
		extends ListCrudRepository<T, ID>,
		ListPagingAndSortingRepository<T, ID>,
		ListQueryByExampleExecutor<T>,
		ExtBaseRepository<T, ID> {

}
