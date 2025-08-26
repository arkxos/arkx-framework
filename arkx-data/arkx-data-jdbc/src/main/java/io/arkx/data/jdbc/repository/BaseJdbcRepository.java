package io.arkx.data.jdbc.repository;

import io.arkx.framework.data.common.repository.ExtBaseRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.ListPagingAndSortingRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.ListQueryByExampleExecutor;
import org.springframework.data.repository.query.QueryByExampleExecutor;

import java.io.Serializable;

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
